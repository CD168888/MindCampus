package com.mc.ai.controller;

import com.mc.ai.domain.AiChatMessage;
import com.mc.ai.domain.AiChatSession;
import com.mc.ai.prompt.AiPrompts;
import com.mc.ai.service.IAiChatMessageService;
import com.mc.ai.service.IAiChatSessionService;
import com.mc.ai.service.IAiChatService;
import com.mc.common.core.domain.R;
import com.mc.common.utils.SecurityUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AI 聊天控制器
 * <p>
 * HTTP 入口层，负责会话管理、消息 CRUD 及与前端 SSE 流式交互。
 * 所有写库操作（用户消息、AI 消息持久化）在 Controller 层完成，
 * AI 底层交互委托给 mc-ai 模块的 IAiChatService。
 * <p>
 * 职责划分：
 * - mc-project.controller：HTTP 入口 + 会话/消息 CRUD + 流式 SSE 组装
 * - mc-ai.service：与 DashScope API 的网络交互、流式发送
 * - mc-project.tool / mc-project.config：业务工具 Bean（Supplier/Function）
 *
 * @author caidu
 */
@RestController
@RequestMapping("/ai")
@Slf4j
public class AiChatController {

    @Resource
    private IAiChatService aiChatService;

    @Resource
    private IAiChatSessionService chatSessionService;

    @Resource
    private IAiChatMessageService chatMessageService;

    /**
     * 流式对话接口（需要登录，支持消息持久化，支持多模态）
     * <p>
     * 流程：
     * 1. 验证/创建会话
     * 2. 保存用户消息（写库）
     * 3. 调用 AI 服务获取流式响应
     * 4. 流式返回给前端，同时在流结束时保存 AI 回复（写库）
     *
     * @param message   用户输入的消息
     * @param files     附件列表
     * @param fileUrls  附件URL列表
     * @param sessionId 会话ID
     * @return Flux<ServerSentEvent<String>> 流式响应数据
     */
    @PostMapping(value = "/chatStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> generate(
            @RequestParam(value = "message", required = true) String message,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @RequestParam(value = "fileUrls", required = false) List<String> fileUrls,
            @RequestParam(value = "sessionId", required = false) Long sessionId,
            HttpServletResponse response) {

        // 验证用户登录
        Long userId = SecurityUtils.getUserId();

        log.info("接收到 AI 对话请求 - 用户ID: {}, 会话ID: {}, 消息: {}", userId, sessionId, message);

        // 验证或创建会话
        Long validSessionId = validateOrCreateSession(sessionId, userId);
        if (validSessionId == null) {
            return Flux.just(ServerSentEvent.<String>builder()
                    .event("error")
                    .data("会话不存在或无权访问")
                    .build());
        }

        // 设置响应头，禁止缓存，否则无法流式输出
        response.setHeader("Cache-Control", "no-cache, no-transform");
        response.setHeader("X-Accel-Buffering", "no");

        List<MultipartFile> fileList = (files != null) ? Arrays.asList(files) : Collections.emptyList();
        String urlsString = (fileUrls != null) ? String.join(",", fileUrls) : "";

        // 保存用户消息（写库）
        chatMessageService.saveMessage(validSessionId, userId, 1, message, urlsString);

        // 用于在流结束后判断是否已保存 AI 回复（防止 doOnComplete 和 doOnCancel 重复保存）
        AtomicBoolean messageSaved = new AtomicBoolean(false);

        // 调用 AI 服务获取流式响应（AI 底层交互由 mc-ai 模块完成）
        IAiChatService.StreamChatResult result = aiChatService.streamChat(
                message, fileList, AiPrompts.GENERAL_ASSISTANT, String.valueOf(validSessionId));

        // 获取会话消息数量，用于判断是否需要生成标题
        List<AiChatMessage> sessionMessages = chatMessageService.getSessionMessages(validSessionId);
        int messageCountBefore = sessionMessages.size();

        // 异步生成会话标题（仅在第一次 Q&A 时触发）
        boolean shouldGenerateTitle = (messageCountBefore == 1); // 刚存了用户消息，此时应有 2 条

        return result.sseFlux()
                .doOnComplete(() -> handleStreamComplete(validSessionId, userId, message,
                        result.fullContentHolder(), messageSaved, shouldGenerateTitle))
                .doOnCancel(() -> handleStreamCancel(validSessionId, userId,
                        result.fullContentHolder(), messageSaved))
                .doOnError(error -> handleStreamError(validSessionId, userId, error));
    }

    /**
     * 处理流式响应完成
     */
    private void handleStreamComplete(Long sessionId, Long userId, String userMessage,
                                     StringBuilder fullContentHolder, AtomicBoolean messageSaved,
                                     boolean shouldGenerateTitle) {
        if (messageSaved.compareAndSet(false, true)) {
            String aiResponse = fullContentHolder.toString();
            if (aiResponse.length() > 0) {
                chatMessageService.saveMessage(sessionId, userId, 0, aiResponse, null);
                log.info("流式对话完成，已保存AI消息 [sessionId: {}, 长度: {}]", sessionId, aiResponse.length());
            }

            // 生成会话标题
            if (shouldGenerateTitle) {
                aiChatService.generateSessionTitleAsync(
                        String.valueOf(sessionId), userMessage, aiResponse,
                        title -> chatSessionService.updateSessionName(sessionId, title));
            }
        }
        log.info("流式对话完成 [用户ID: {}, 会话ID: {}]", userId, sessionId);
    }

    /**
     * 处理流式响应取消
     */
    private void handleStreamCancel(Long sessionId, Long userId,
                                   StringBuilder fullContentHolder, AtomicBoolean messageSaved) {
        if (messageSaved.compareAndSet(false, true)) {
            String aiResponse = fullContentHolder.toString();
            if (aiResponse.length() > 0) {
                chatMessageService.saveMessage(sessionId, userId, 0, aiResponse, null);
                log.info("流式对话被取消，已保存部分AI消息 [sessionId: {}, 长度: {}]", sessionId, aiResponse.length());
            }
        }
        log.info("流式对话被取消 [用户ID: {}, 会话ID: {}]", userId, sessionId);
    }

    /**
     * 处理流式响应错误
     */
    private void handleStreamError(Long sessionId, Long userId, Throwable error) {
        log.error("AI对话异常 [用户ID: {}, 会话ID: {}]: {}", userId, sessionId, error.getMessage(), error);
    }

    /**
     * 中断流式输出接口
     *
     * @param sessionId 会话ID
     */
    @PostMapping("/cancelStream")
    public R<String> cancelStream(@RequestParam(value = "sessionId") Long sessionId) {
        Long userId = SecurityUtils.getUserId();

        // 验证会话归属
        if (!validateSessionAccess(sessionId, userId)) {
            return R.fail("会话不存在或无权访问");
        }

        // 调用 AI 服务的取消方法
        aiChatService.cancelStream(String.valueOf(sessionId));
        log.info("已触发流式输出中断 [会话ID: {}]", sessionId);
        return R.ok("已中断流式输出");
    }

    /**
     * 创建新会话
     */
    @PostMapping("/createSession")
    public R<AiChatSession> createSession(@RequestParam(value = "sessionName") String sessionName) {
        Long userId = SecurityUtils.getUserId();
        AiChatSession session = chatSessionService.createSession(userId, sessionName);
        return R.ok(session);
    }

    /**
     * 获取用户的所有会话列表
     */
    @GetMapping("/sessions")
    public R<List<AiChatSession>> getSessions() {
        Long userId = SecurityUtils.getUserId();
        List<AiChatSession> sessions = chatSessionService.getUserSessions(userId);
        return R.ok(sessions);
    }

    /**
     * 获取会话的历史消息（优先从Redis获取，不存在则从数据库加载）
     */
    @GetMapping("/messages/{sessionId}")
    public R<List<AiChatMessage>> getMessages(@PathVariable Long sessionId) {
        Long userId = SecurityUtils.getUserId();

        // 验证会话归属
        if (!validateSessionAccess(sessionId, userId)) {
            return R.fail("会话不存在或无权访问");
        }

        List<AiChatMessage> messages = chatMessageService.getSessionMessages(sessionId);
        return R.ok(messages);
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/session/{sessionId}")
    public R<String> deleteSession(@PathVariable Long sessionId) {
        Long userId = SecurityUtils.getUserId();

        // 验证会话归属
        if (!validateSessionAccess(sessionId, userId)) {
            return R.fail("会话不存在或无权访问");
        }

        // 清空 AI 记忆（由 mc-ai 提供）
        aiChatService.clearChatMemory(String.valueOf(sessionId));

        // 删除会话的所有消息
        chatMessageService.deleteSessionMessages(sessionId, userId);

        // 删除会话
        boolean success = chatSessionService.deleteSession(sessionId, userId);
        if (success) {
            return R.ok("删除成功");
        } else {
            return R.fail("删除失败");
        }
    }

    /**
     * 更新会话标题
     */
    @PutMapping("/session/{sessionId}/title")
    public R<String> updateSessionTitle(@PathVariable Long sessionId,
                                        @RequestParam(value = "title", required = true) String title) {
        Long userId = SecurityUtils.getUserId();

        // 验证会话归属
        if (!validateSessionAccess(sessionId, userId)) {
            return R.fail("会话不存在或无权访问");
        }

        // 验证标题长度
        if (title == null || title.trim().isEmpty()) {
            return R.fail("标题不能为空");
        }
        if (title.length() > 50) {
            return R.fail("标题长度不能超过50个字符");
        }

        // 更新标题
        chatSessionService.updateSessionName(sessionId, title.trim());
        return R.ok("标题更新成功");
    }

    /**
     * 验证或创建会话
     *
     * @param sessionId 会话ID（可为null）
     * @param userId    用户ID
     * @return 有效的会话ID，如果验证失败返回null
     */
    private Long validateOrCreateSession(Long sessionId, Long userId) {
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
     * 验证会话归属
     *
     * @param sessionId 会话ID
     * @param userId    用户ID
     * @return 是否有权访问
     */
    private boolean validateSessionAccess(Long sessionId, Long userId) {
        AiChatSession session = chatSessionService.getById(sessionId);
        return session != null && session.getUserId().equals(userId);
    }
}
