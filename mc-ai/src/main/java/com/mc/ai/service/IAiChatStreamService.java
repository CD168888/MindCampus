package com.mc.ai.service;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

/**
 * AI 聊天流式服务接口
 * 封装所有异步逻辑和流式 SSE 响应处理
 */
public interface IAiChatStreamService {

    /**
     * 验证或创建会话
     * @param sessionId 会话ID（可为null）
     * @param userId 用户ID
     * @return 有效的会话ID，如果验证失败返回null
     */
    Long validateOrCreateSession(Long sessionId, Long userId);

    /**
     * 生成流式对话响应
     * @param message 用户消息
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return Flux<ServerSentEvent<String>> 流式响应
     */
    Flux<ServerSentEvent<String>> generateStreamResponse(String message, Long sessionId, Long userId);

    /**
     * 中断流式输出
     * @param sessionId 会话ID
     * @return 是否成功中断
     */
    boolean cancelStream(Long sessionId);

    /**
     * 验证会话归属
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 是否有权访问
     */
    boolean validateSessionAccess(Long sessionId, Long userId);
}