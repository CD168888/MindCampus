package com.mc.ai.service;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AI 统一对话服务接口
 * <p>
 * 定义 AI 模块对外提供的核心 AI 能力接口，完全脱离具体业务与数据库访问层。
 * 所有写库逻辑（如消息持久化）由调用方（mc-project）在 Controller 层自行处理。
 * <p>
 * 依赖方向：mc-project（业务层） -> mc-ai（AI 能力层），单向引入，无循环依赖。
 *
 * @author caidu
 */
public interface IAiChatService {

    // ==================== 流式对话（通用） ====================

    /**
     * 发送消息并获取流式响应
     * <p>
     * AI 底层流式交互的核心方法，支持普通文本模型和多模态模型。
     * 该方法仅负责与 DashScope API 的网络交互及流式返回，
     * 不处理任何写库操作（写库由 mc-project 的 Controller 层完成）。
     * <p>
     * 运行期通过 Spring ApplicationContext 动态扫描并注册业务工具 Bean（Supplier/Function），
     * 由 AiConfig 配置类统一完成装配，无需业务层感知。
     *
     * @param userMessage  用户消息内容（不含媒体文件时为纯文本）
     * @param files        附件列表（图片等），无附件时传 null 或空列表
     * @param systemPrompt 系统提示词（由 mc-project 拼接后传入，如 AiPrompts.GENERAL_ASSISTANT）
     * @param conversationId 会话 ID（用于 ChatMemory 对话记忆）
     * @return 携带流式数据块（String）和完整内容累积器的结果对象
     */
    StreamChatResult streamChat(String userMessage, List<MultipartFile> files,
                                String systemPrompt, String conversationId);

    /**
     * 流式对话结果携带器
     * <p>
     * Flux&lt;ServerSentEvent&lt;String&gt;&gt; 提供给前端 SSE 流式响应；
     * StringBuilder 引用由 mc-project 在 .doOnComplete() / .doOnCancel() 回调中读取完整 AI 回复。
     */
    record StreamChatResult(
            /** SSE 流，供 Controller 层直接返回给前端 */
            Flux<ServerSentEvent<String>> sseFlux,
            /** 完整 AI 回复内容累积器，供调用方在流结束后读取 */
            StringBuilder fullContentHolder
    ) {}

    // ==================== 心理健康评估（单次非流式） ====================

    /**
     * 心理健康评估
     * <p>
     * 将问卷答案内容发送给 AI，接收结构化的评估结果。
     * 使用专用的 mentalHealthChatClient（无对话记忆、无业务工具），
     * 确保评估过程的独立性和结果稳定性。
     *
     * @param questionnaireContent 问卷答案字符串（由 mc-project 构建）
     * @return 心理健康评估结果（JSON 结构化输出，由 Spring AI 自动反序列化）
     */
    MentalHealthEvaluationResult evaluateMentalHealth(String questionnaireContent);

    /**
     * 心理健康评估结果（迁移自 mc-project.domain）
     */
    record MentalHealthEvaluationResult(
            int totalScore,
            String riskLevel,
            MentalHealthIndicator indicators,
            List<String> mainIssues,
            List<String> suggestions,
            String detailedAnalysis
    ) {}

    /**
     * 心理健康指标维度
     */
    record MentalHealthIndicator(
            int anxietyScore,
            int depressionScore,
            int stressScore,
            int socialScore,
            int sleepScore,
            int emotionScore,
            int selfEfficacyScore
    ) {}

    // ==================== 题目生成（单次非流式） ====================

    /**
     * 生成心理测评题目（单次非流式）
     * <p>
     * 使用专用的 questionGenerationChatClient，基于管理员的需求生成题目 JSON。
     *
     * @param userPrompt 用户的生成需求描述（如"生成一道关于学业压力的选择题"）
     * @return AI 生成的题目内容（JSON 字符串，由调用方自行反序列化）
     */
    String generateQuestion(String userPrompt);

    // ==================== 辅助：会话标题生成 ====================

    /**
     * 根据对话内容生成简短会话标题（异步，生成后通过回调通知）
     * <p>
     * 纯文本调用，不涉及业务写库。
     *
     * @param conversationId 会话 ID
     * @param userMessage    用户消息（截取前 100 字）
     * @param aiResponse     AI 回复（截取前 200 字）
     * @param callback       生成完成后的回调，参数为标题文本
     */
    void generateSessionTitleAsync(String conversationId, String userMessage,
                                    String aiResponse, TitleCallback callback);

    /**
     * 会话标题生成回调接口
     */
    @FunctionalInterface
    interface TitleCallback {
        void onTitleGenerated(String title);
    }

    // ==================== 辅助：流取消与记忆管理 ====================

    /**
     * 取消指定会话的流式输出
     * <p>
     * 通过设置 AtomicBoolean 标志，使 takeWhile 条件为 false，从而中断 Flux 流。
     * 用于支持 mc-project 前端用户主动停止 AI 回复的场景。
     *
     * @param conversationId 会话 ID
     */
    void cancelStream(String conversationId);

    /**
     * 清空指定会话的 AI 对话记忆
     * <p>
     * 调用 ChatMemory.clear() 清除 Redis 中存储的该会话历史对话记录。
     *
     * @param conversationId 会话 ID
     */
    void clearChatMemory(String conversationId);

    // ==================== 学生端专用对话（带工具） ====================

    /**
     * 学生端专用流式对话
     * <p>
     * 使用学生端专用 ChatClient（studentChatClient），具备以下特性：
     * - 系统提示词：STUDENT_WELL_BEING_PROMPT（大学生心理陪伴伙伴）
     * - 对话记忆：Redis 持久化 ChatMemory
     * - 业务工具：MentalHealthTools 中的学生端查询工具（@Tool 注解方法）
     * <p>
     * 适用于学生用户在移动端 App 与 AI 进行的心理陪伴对话场景。
     *
     * @param userMessage     用户消息内容
     * @param files           附件列表（图片等），无附件时传 null 或空列表
     * @param conversationId  会话 ID（用于 ChatMemory）
     * @return 携带流式数据块（String）和完整内容累积器的结果对象
     */
    StreamChatResult streamStudentChat(String userMessage, List<MultipartFile> files,
                                        String conversationId);
}
