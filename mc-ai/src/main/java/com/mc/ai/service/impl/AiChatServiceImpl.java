package com.mc.ai.service.impl;

import com.mc.ai.prompt.AiPrompts;
import com.mc.ai.service.IAiChatService;
import com.mc.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * AI 统一对话服务实现
 * <p>
 * 负责与 DashScope API 的底层网络交互，提供流式 SSE 和单次非流式两种调用方式。
 * 完全不涉及数据库写操作，所有写库逻辑由 mc-project 的 Controller 层完成。
 * <p>
 * 运行期通过 Spring ApplicationContext 动态扫描（AiConfig.getToolNames()）获取业务工具 Bean，
 * 实现 mc-project（业务层）-> mc-ai（AI 能力层）的单向依赖，不产生编译期循环。
 *
 * @author caidu
 */
@Service
@Slf4j
public class AiChatServiceImpl implements IAiChatService {

    /**
     * 基础文本模型 ChatClient（由 AiConfig 注册，支持业务工具 + ChatMemory）
     */
    private final ChatClient chatClient;

    /**
     * 多模态模型 ChatClient（由 AiConfig 注册，支持图片输入）
     */
    private final ChatClient multiModalChatClient;

    /**
     * 心理健康评估专用 ChatClient（由 MentalHealthChatClientConfig 注册，无记忆无工具）
     */
    private final ChatClient mentalHealthChatClient;

    /**
     * 题目生成专用 ChatClient（由 QuestionGenerationChatClientConfig 注册，无记忆无工具）
     */
    private final ChatClient questionGenerationChatClient;

    /**
     * 对话记忆（由 AiConfig 通过 RedisChatMemoryRepository 构建）
     */
    private final ChatMemory chatMemory;

    /**
     * 学生端专用 ChatClient（由 StudentChatClientConfig 配置，仅注册学生端工具）
     */
    private final ChatClient studentChatClient;

    private final ConcurrentHashMap<String, AtomicBoolean> cancelFlags = new ConcurrentHashMap<>();

    private ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public AiChatServiceImpl(
            ChatClient chatClient,
            @Qualifier("multiModalChatClient") ChatClient multiModalChatClient,
            @Qualifier("mentalHealthChatClient") ChatClient mentalHealthChatClient,
            @Qualifier("questionGenerationChatClient") ChatClient questionGenerationChatClient,
            @Qualifier("studentChatClient") ChatClient studentChatClient,
            ChatMemory chatMemory) {
        this.chatClient = chatClient;
        this.multiModalChatClient = multiModalChatClient;
        this.mentalHealthChatClient = mentalHealthChatClient;
        this.questionGenerationChatClient = questionGenerationChatClient;
        this.studentChatClient = studentChatClient;
        this.chatMemory = chatMemory;
    }

    /**
     * 流式对话核心实现
     * <p>
     * 接收用户消息和系统提示词，构建 AI 请求并返回流式响应。
     * 返回 StreamChatResult 同时携带 SSE Flux（供 Controller 返回前端）和
     * StringBuilder 引用（供 Controller 在 .doOnComplete() 回调中读取完整回复）。
     * <p>
     * 注意：写库操作（用户消息保存、AI 消息保存）由调用方（mc-project Controller）负责，
     * 本方法不执行任何数据库写操作。
     *
     * @param userMessage   用户消息内容
     * @param files         附件列表（可为 null 或空列表）
     * @param systemPrompt  系统提示词（由 mc-project 传入，如 AiPrompts.GENERAL_ASSISTANT）
     * @param conversationId 会话 ID（用于 ChatMemory）
     * @return 包含 SSE 流和完整内容累积器的结果对象
     */
    @Override
    public StreamChatResult streamChat(String userMessage, List<MultipartFile> files,
                                       String systemPrompt, String conversationId) {
        boolean hasFiles = files != null && !files.isEmpty();
        ChatClient clientToUse = hasFiles ? multiModalChatClient : chatClient;

        log.info("AI 流式对话 - 是否多模态: {}, 系统提示词长度: {}, 会话ID: {}",
                hasFiles, systemPrompt != null ? systemPrompt.length() : 0, conversationId);

        List<org.springframework.ai.content.Media> memoryResources =
                hasFiles ? convertMultipartFilesToMedia(files) : null;

        UserMessage userMsg;
        if (hasFiles && memoryResources != null && !memoryResources.isEmpty()) {
            userMsg = UserMessage.builder()
                    .text(userMessage)
                    .media(memoryResources)
                    .build();
        } else {
            userMsg = UserMessage.builder()
                    .text(userMessage)
                    .build();
        }

        // 用于累积 AI 完整回复
        StringBuilder fullContentHolder = new StringBuilder();

        // 取消标志（用于支持 mc-project 的 cancelStream 功能）
        AtomicBoolean cancelFlag = cancelFlags.computeIfAbsent(conversationId, k -> new AtomicBoolean(false));
        cancelFlag.set(false);

        Flux<String> contentFlux = clientToUse.prompt()
                .messages(userMsg)
                .system(systemPrompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()
                .content()
                .takeWhile(data -> !cancelFlag.get());

        Flux<ServerSentEvent<String>> sseFlux = contentFlux
                .map(chunk -> {
                    fullContentHolder.append(chunk);
                    return ServerSentEvent.<String>builder()
                            .data(chunk)
                            .build();
                })
                .concatWith(Flux.just(ServerSentEvent.<String>builder()
                        .data("\u0003")
                        .build()));

        return new StreamChatResult(sseFlux, fullContentHolder);
    }

    /**
     * 将 MultipartFile 数组转换为 Spring AI Media 列表
     */
    private List<org.springframework.ai.content.Media> convertMultipartFilesToMedia(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }
        return files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .map(file -> {
                    String contentType = file.getContentType();
                    MimeType mimeType = MimeType.valueOf(
                            contentType != null ? contentType : "image/jpeg");
                    return org.springframework.ai.content.Media.builder()
                            .mimeType(mimeType)
                            .data(file.getResource())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 心理健康评估（单次非流式）
     * <p>
     * 使用专用的 mentalHealthChatClient，该 Client 使用 MENTAL_HEALTH_EVALUATION 系统提示词，
     * 无对话记忆、无业务工具，确保评估的独立性和结果稳定性。
     *
     * @param questionnaireContent 问卷答案字符串
     * @return 心理健康评估结果（Spring AI 自动反序列化为 record）
     */
    @Override
    public MentalHealthEvaluationResult evaluateMentalHealth(String questionnaireContent) {
        String prompt = buildEvaluationPrompt(questionnaireContent);
        log.info("开始心理健康评估 - 问卷内容长度: {}", questionnaireContent.length());

        MentalHealthEvaluationResult result = mentalHealthChatClient
                .prompt(prompt)
                .call()
                .entity(MentalHealthEvaluationResult.class);

        log.info("心理健康评估完成 - 风险等级: {}, 总得分: {}",
                result != null ? result.riskLevel() : "null",
                result != null ? result.totalScore() : -1);
        return result;
    }

    /**
     * 构建心理健康评估 Prompt
     */
    private String buildEvaluationPrompt(String questionnaireContent) {
        return "请根据以下学生的问卷回答，进行全面的心理健康评估。\n\n" +
                "问卷内容：\n" + questionnaireContent + "\n\n" +
                "请严格按照心理健康评估专家的要求进行评估，输出标准 JSON 格式。";
    }

    /**
     * 生成心理测评题目（单次非流式）
     * <p>
     * 使用专用的 questionGenerationChatClient，该 Client 使用 QUESTION_GENERATION 系统提示词，
     * 无对话记忆、无业务工具，专注于单次题目生成任务。
     *
     * @param userPrompt 用户的生成需求描述
     * @return AI 生成的题目 JSON 字符串
     */
    @Override
    public String generateQuestion(String userPrompt) {
        log.info("AI 生成题目请求 - 需求: {}", userPrompt);

        String result = questionGenerationChatClient
                .prompt()
                .user(userPrompt)
                .call()
                .content();

        log.info("AI 题目生成完成 - 结果长度: {}", result != null ? result.length() : 0);
        return result;
    }

    /**
     * 生成会话标题（异步，生成后通过回调通知）
     * <p>
     * 纯 AI 调用，不涉及任何数据库写操作。
     * 标题通过 TitleCallback 回调给调用方，由 mc-project Controller 层更新会话标题。
     *
     * @param conversationId 会话 ID
     * @param userMessage    用户消息（截取前 100 字）
     * @param aiResponse     AI 回复（截取前 200 字）
     * @param callback       生成完成后的回调
     */
    @Override
    public void generateSessionTitleAsync(String conversationId, String userMessage,
                                           String aiResponse, TitleCallback callback) {
        new Thread(() -> {
            try {
                String prompt = String.format(
                        "请根据以下对话内容，生成一个5-6个字的简短标题，直接输出标题，不要其他说明。\n\n用户：%s\n\nAI：%s",
                        userMessage.length() > 100 ? userMessage.substring(0, 100) : userMessage,
                        aiResponse.length() > 200 ? aiResponse.substring(0, 200) : aiResponse
                );

                String title = chatClient.prompt()
                        .user(prompt)
                        .call()
                        .content()
                        .trim();

                // 限制长度为 5-8 字
                title = title.replaceAll("[^\\p{L}\\p{N}]", "");
                if (title.length() > 8) {
                    title = title.substring(0, 8);
                }
                if (title.isEmpty()) {
                    title = "新会话";
                }

                log.info("会话标题生成成功 - 会话ID: {}, 标题: {}", conversationId, title);

                // 通过回调返回标题
                if (callback != null) {
                    callback.onTitleGenerated(title);
                }

            } catch (Exception e) {
                log.error("生成会话标题失败 - 会话ID: {}", conversationId, e);
            }
        }).start();
    }

    /**
     * 取消指定会话的流式输出
     * <p>
     * 通过设置 AtomicBoolean 标志，使 takeWhile 条件为 false，从而中断 Flux 流。
     *
     * @param conversationId 会话 ID
     */
    public void cancelStream(String conversationId) {
        AtomicBoolean flag = cancelFlags.get(conversationId);
        if (flag != null) {
            flag.set(true);
            log.info("已触发流式输出中断 - 会话ID: {}", conversationId);
        } else {
            log.warn("未找到活跃的流式对话 - 会话ID: {}", conversationId);
        }
    }

    /**
     * 清空指定会话的 AI 对话记忆
     *
     * @param conversationId 会话 ID
     */
    public void clearChatMemory(String conversationId) {
        try {
            chatMemory.clear(conversationId);
            log.info("清空会话 AI 记忆 - 会话ID: {}", conversationId);
        } catch (Exception e) {
            log.error("清空会话 AI 记忆失败 - 会话ID: {}", conversationId, e);
        }
    }

    // ==================== 学生端专用对话 ====================

    /**
     * 学生端专用流式对话实现
     * <p>
     * 使用 studentChatClient（具有学生端心理健康工具），为学生提供
     * 个性化的心理陪伴对话体验。AI 可主动调用工具查询学生的心理健康状态、
     * 评估历史、推荐文章等，提供更精准的陪伴和疏导。
     *
     * @param userMessage    用户消息内容
     * @param files          附件列表（可为 null 或空列表）
     * @param conversationId 会话 ID
     * @return 流式结果对象
     */
    @Override
    public StreamChatResult streamStudentChat(String userMessage, List<MultipartFile> files,
                                               String conversationId) {
        log.info("[StudentChat] 学生端流式对话 - 会话ID: {}, 消息长度: {}",
                conversationId, userMessage != null ? userMessage.length() : 0);

        List<org.springframework.ai.content.Media> memoryResources =
                (files != null && !files.isEmpty()) ? convertMultipartFilesToMedia(files) : null;

        UserMessage userMsg;
        if (memoryResources != null && !memoryResources.isEmpty()) {
            userMsg = UserMessage.builder()
                    .text(userMessage)
                    .media(memoryResources)
                    .build();
        } else {
            userMsg = UserMessage.builder()
                    .text(userMessage)
                    .build();
        }

        StringBuilder fullContentHolder = new StringBuilder();
        AtomicBoolean cancelFlag = cancelFlags.computeIfAbsent(conversationId, k -> new AtomicBoolean(false));
        cancelFlag.set(false);

        // 这里是 Tomcat 同步线程，100%能拿到 userId，永不丢失！
        Long userId = SecurityUtils.getUserId();
        Map<String, Object> toolContext = new HashMap<>();
        toolContext.put("userId", userId);

        Flux<String> contentFlux = studentChatClient.prompt()
                .messages(userMsg)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .toolContext(toolContext)
                .stream()
                .content()
                .takeWhile(data -> !cancelFlag.get());

        Flux<ServerSentEvent<String>> sseFlux = contentFlux
                .map(chunk -> {
                    fullContentHolder.append(chunk);
                    return ServerSentEvent.<String>builder()
                            .data(chunk)
                            .build();
                })
                .concatWith(Flux.just(ServerSentEvent.<String>builder()
                        .data("\u0003")
                        .build()));

        return new StreamChatResult(sseFlux, fullContentHolder);
    }

    // ==================== RAG + 知识图谱增强对话 ====================

    /**
     * 运行时获取知识图谱服务（避免循环依赖）
     */
    private Object getKnowledgeService(String serviceName) {
        try {
            return applicationContext.getBean(serviceName);
        } catch (Exception e) {
            log.warn("[RAG] 未找到知识服务 Bean: {}", serviceName);
            return null;
        }
    }

    /**
     * 学生端 RAG + 知识图谱增强流式对话
     */
    @Override
    public StreamChatResult streamStudentChatRag(String userMessage, List<MultipartFile> files,
                                                 String conversationId, Long userId,
                                                 boolean enableRag, boolean enableKg) {
        log.info("[RAG-Chat] 增强对话开始 - 会话ID: {}, userId: {}, enableRag: {}, enableKg: {}",
            conversationId, userId, enableRag, enableKg);

        // 阶段1：知识图谱检索
        String kgContext = "";
        if (enableKg && userId != null) {
            try {
                Object kgService = getKnowledgeService("studentProfileKGService");
                if (kgService != null) {
                    Method buildMethod = kgService.getClass().getMethod("buildKgContext", Long.class);
                    kgContext = (String) buildMethod.invoke(kgService, userId);
                    log.info("[RAG-Chat] 知识图谱上下文获取成功 - 长度: {}", kgContext.length());
                }
            } catch (Exception e) {
                log.warn("[RAG-Chat] 知识图谱检索失败: {}", e.getMessage());
            }
        }
        if (kgContext.isEmpty()) {
            kgContext = "【用户画像】暂无该用户的画像数据。";
        }

        // 阶段2：RAG 向量检索
        String ragContext = "";
        if (enableRag && userMessage != null && !userMessage.isBlank()) {
            try {
                Object kgService = getKnowledgeService("studentProfileKGService");
                if (kgService != null) {
                    Method ragMethod = kgService.getClass().getMethod("ragRetrieve",
                        String.class, Long.class, int.class);
                    @SuppressWarnings("unchecked")
                    List<?> ragResults = (List<?>) ragMethod.invoke(kgService, userMessage, null, 5);
                    if (ragResults != null && !ragResults.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        int idx = 1;
                        for (Object result : ragResults) {
                            Method contentMethod = result.getClass().getMethod("getContent");
                            Method scoreMethod = result.getClass().getMethod("getScore");
                            Method kbNameMethod = result.getClass().getMethod("getKbName");
                            String content = (String) contentMethod.invoke(result);
                            Object score = scoreMethod.invoke(result);
                            String kbName = (String) kbNameMethod.invoke(result);
                            if (content != null && !content.isEmpty()) {
                                sb.append(String.format("[参考%d - %s (相似度: %.2f)] %s\n",
                                    idx++, kbName != null ? kbName : "知识库",
                                    score instanceof Number ? ((Number) score).doubleValue() : 0.0,
                                    content.length() > 200 ? content.substring(0, 200) + "..." : content));
                            }
                        }
                        ragContext = sb.toString();
                        log.info("[RAG-Chat] RAG 检索结果 - {} 条", ragResults.size());
                    }
                }
            } catch (Exception e) {
                log.warn("[RAG-Chat] RAG 检索失败: {}", e.getMessage());
            }
        }
        if (ragContext.isEmpty()) {
            ragContext = "【知识库参考】暂无相关知识库内容。";
        }

        // 阶段3：构建增强系统提示词
        String enhancedPrompt = String.format(AiPrompts.STUDENT_WELL_BEING_PROMPT_RAG_TEMPLATE,
            kgContext, ragContext, AiPrompts.STUDENT_WELL_BEING_PROMPT);

        // 构建消息
        List<Media> memoryResources =
            (files != null && !files.isEmpty()) ? convertMultipartFilesToMedia(files) : null;

        UserMessage userMsg;
        if (memoryResources != null && !memoryResources.isEmpty()) {
            userMsg = UserMessage.builder().text(userMessage).media(memoryResources).build();
        } else {
            userMsg = UserMessage.builder().text(userMessage).build();
        }

        StringBuilder fullContentHolder = new StringBuilder();
        AtomicBoolean cancelFlag = cancelFlags.computeIfAbsent(conversationId, k -> new AtomicBoolean(false));
        cancelFlag.set(false);

        Map<String, Object> toolContext = new HashMap<>();
        toolContext.put("userId", userId);

        Flux<String> contentFlux = chatClient.prompt()
            .messages(userMsg)
            .system(enhancedPrompt)
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
            .toolContext(toolContext)
            .stream()
            .content()
            .takeWhile(data -> !cancelFlag.get());

        Flux<ServerSentEvent<String>> sseFlux = contentFlux
            .map(chunk -> {
                fullContentHolder.append(chunk);
                return ServerSentEvent.<String>builder().data(chunk).build();
            })
            .concatWith(Flux.just(ServerSentEvent.<String>builder().data("\u0003").build()));

        log.info("[RAG-Chat] 增强对话返回 SSE 流 - 系统提示词长度: {}", enhancedPrompt.length());
        return new StreamChatResult(sseFlux, fullContentHolder);
    }
}
