package com.mc.ai.service.impl;

import com.mc.ai.domain.AiChatMessage;
import com.mc.ai.domain.AiChatSession;
import com.mc.ai.service.IAiChatMessageService;
import com.mc.ai.service.IAiChatSessionService;
import com.mc.ai.service.IAiChatStreamService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.mc.ai.prompt.AiPrompts.STUDENT_WELL_BEING_PROMPT;

/**
 * AI 聊天流式服务
 * 封装所有异步逻辑和流式 SSE 响应处理
 */
@Service
@Slf4j
public class AiChatStreamServiceImpl implements IAiChatStreamService {

    private final ChatClient dashScopeChatClient;
    
    @Resource
    private IAiChatSessionService chatSessionService;
    
    @Resource
    private IAiChatMessageService chatMessageService;

    // 存储每个会话的流式状态标识
    private final ConcurrentHashMap<String, AtomicBoolean> streamingStates = new ConcurrentHashMap<>();

    private static final String SYSTEM_PROMPT = STUDENT_WELL_BEING_PROMPT;

    public AiChatStreamServiceImpl(ChatClient.Builder chatClientBuilder, ChatMemoryRepository chatMemoryRepository) {
        this.dashScopeChatClient = chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(
                                MessageWindowChatMemory.builder().chatMemoryRepository(chatMemoryRepository).build())
                        .build())
                .build();
    }

    /**
     * 验证或创建会话
     * @param sessionId 会话ID（可为null）
     * @param userId 用户ID
     * @return 有效的会话ID，如果验证失败返回null
     */
    public Long validateOrCreateSession(Long sessionId, Long userId) {
        // 如果 sessionId 为 null，自动创建新会话
        if (sessionId == null) {
            AiChatSession newSession = chatSessionService.createSession(userId, "新会话");
            log.info("自动创建新会话 - 用户ID: {}, 会话ID: {}", userId, newSession.getSessionId());
            return newSession.getSessionId();
        }

        // 验证会话归属
        AiChatSession session = chatSessionService.getById(sessionId);
        if (session == null || !session.getUserId().equals(userId)) {
            log.warn("会话验证失败 - 用户ID: {}, 会话ID: {}", userId, sessionId);
            return null;
        }

        return sessionId;
    }

    /**
     * 生成流式对话响应
     * @param message 用户消息
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return Flux<ServerSentEvent<String>> 流式响应
     */
    public Flux<ServerSentEvent<String>> generateStreamResponse(String message, Long sessionId, Long userId) {
        log.info("开始生成流式响应 - 用户ID: {}, 会话ID: {}, 消息: {}", userId, sessionId, message);

        // 保存用户消息
        chatMessageService.saveMessage(sessionId, userId, 1, message);

        // 创建会话标识
        String chatId = String.valueOf(sessionId);

        // 为当前会话创建流式状态标识
        AtomicBoolean isStreaming = streamingStates.computeIfAbsent(chatId, k -> new AtomicBoolean(true));
        isStreaming.set(true);

        // 用于累积AI回复内容
        StringBuilder aiResponse = new StringBuilder();

        // 生成流式对话
        return dashScopeChatClient.prompt()
                .user(message)
                .system(SYSTEM_PROMPT)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
                .stream()
                .content()
                .takeWhile(data -> isStreaming.get())
                .map(content -> {
                    aiResponse.append(content);
                    return ServerSentEvent.<String>builder()
                            .data(content)
                            .build();
                })
                .concatWith(Flux.just(ServerSentEvent.<String>builder()
                        .data("\u0003")
                        .build()))
                .doOnComplete(() -> handleStreamComplete(sessionId, userId, message, aiResponse.toString(), chatId))
                .doOnCancel(() -> handleStreamCancel(sessionId, userId, aiResponse.toString(), chatId))
                .doOnError(error -> handleStreamError(sessionId, userId, chatId, error))
                .onErrorResume(error -> handleStreamErrorResponse(userId, sessionId, error));
    }

    /**
     * 处理流式响应完成
     */
    private void handleStreamComplete(Long sessionId, Long userId, String userMessage, String aiResponse, String chatId) {
        // 保存AI回复消息
        if (aiResponse.length() > 0) {
            chatMessageService.saveMessage(sessionId, userId, 0, aiResponse);
        }

        // 如果是会话的第一次对话，生成会话标题
        List<AiChatMessage> sessionMessages = chatMessageService.getSessionMessages(sessionId);
        if (sessionMessages.size() == 2) {
            generateSessionTitleAsync(sessionId, userMessage, aiResponse);
        }

        streamingStates.remove(chatId);
        log.info("流式对话完成 [用户ID: {}, 会话ID: {}]", userId, sessionId);
    }

    /**
     * 处理流式响应取消
     */
    private void handleStreamCancel(Long sessionId, Long userId, String aiResponse, String chatId) {
        // 即使取消也保存部分回复
        if (aiResponse.length() > 0) {
            chatMessageService.saveMessage(sessionId, userId, 0, aiResponse);
        }
        streamingStates.remove(chatId);
        log.info("流式对话被取消 [用户ID: {}, 会话ID: {}]", userId, sessionId);
    }

    /**
     * 处理流式响应错误
     */
    private void handleStreamError(Long sessionId, Long userId, String chatId, Throwable error) {
        streamingStates.remove(chatId);
        log.error("流式对话出错 [用户ID: {}, 会话ID: {}]", userId, sessionId, error);
    }

    /**
     * 处理流式响应错误返回
     */
    private Flux<ServerSentEvent<String>> handleStreamErrorResponse(Long userId, Long sessionId, Throwable error) {
        String errorMsg = "AI服务暂时不可用，请稍后重试";
        log.error("AI对话异常 [用户ID: {}, 会话ID: {}]: {}", userId, sessionId, error.getMessage(), error);
        return Flux.just(ServerSentEvent.<String>builder()
                .event("error")
                .data(errorMsg)
                .build());
    }

    /**
     * 生成会话标题（异步）
     * 根据用户问题和AI回复，生成一个5-6字的简短标题
     */
    private void generateSessionTitleAsync(Long sessionId, String userMessage, String aiResponse) {
        // 异步执行，不阻塞主流程
        new Thread(() -> {
            try {
                String prompt = String.format(
                    "请根据以下对话内容，生成一个5-6个字的简短标题，直接输出标题，不要其他说明。\n\n用户：%s\n\nAI：%s",
                    userMessage.length() > 100 ? userMessage.substring(0, 100) : userMessage,
                    aiResponse.length() > 200 ? aiResponse.substring(0, 200) : aiResponse
                );

                String title = dashScopeChatClient.prompt()
                    .user(prompt)
                    .call()
                    .content()
                    .trim();

                // 限制长度为5-8字
                title = title.replaceAll("[^\\p{L}\\p{N}]", "");
                if (title.length() > 8) {
                    title = title.substring(0, 8);
                }

                // 更新会话标题
                chatSessionService.updateSessionName(sessionId, title);
                log.info("会话标题生成成功 - 会话ID: {}, 标题: {}", sessionId, title);

            } catch (Exception e) {
                log.error("生成会话标题失败 - 会话ID: {}", sessionId, e);
            }
        }).start();
    }

    /**
     * 中断流式输出
     * @param sessionId 会话ID
     * @return 是否成功中断
     */
    public boolean cancelStream(Long sessionId) {
        String chatId = String.valueOf(sessionId);
        AtomicBoolean isStreaming = streamingStates.get(chatId);

        if (isStreaming != null) {
            isStreaming.set(false);
            log.info("已触发流式输出中断 [会话ID: {}]", sessionId);
            return true;
        } else {
            log.warn("未找到活跃的流式对话 [会话ID: {}]", sessionId);
            return false;
        }
    }

    /**
     * 验证会话归属
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 是否有权访问
     */
    public boolean validateSessionAccess(Long sessionId, Long userId) {
        AiChatSession session = chatSessionService.getById(sessionId);
        return session != null && session.getUserId().equals(userId);
    }
}