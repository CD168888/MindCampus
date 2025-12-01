package com.mc.ai.controller;

import com.mc.ai.domain.AiChatMessage;
import com.mc.ai.domain.AiChatSession;
import com.mc.ai.service.IAiChatMessageService;
import com.mc.ai.service.IAiChatSessionService;
import com.mc.ai.service.IAiChatStreamService;
import com.mc.common.core.domain.R;
import com.mc.common.utils.SecurityUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AI 聊天控制器（重构版）
 * 所有异步逻辑已迁移到 AiChatStreamService
 */
@RestController
@RequestMapping("/ai")
@Slf4j
public class AiChatController {

    @Resource
    private IAiChatStreamService chatStreamService;

    @Resource
    private IAiChatSessionService chatSessionService;

    @Resource
    private IAiChatMessageService chatMessageService;

    /**
     * 流式对话接口（2.0版本 - 需要登录，支持消息持久化）
     * @param message 用户输入的消息
     * @param sessionId 会话ID
     * @return Flux<ServerSentEvent<String>> 流式响应数据
     */
    @GetMapping(value = "/chatStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> generate(
            @RequestParam(value = "message", required = true) String message,
            @RequestParam(value = "sessionId", required = false) Long sessionId,
            HttpServletResponse response) {

        // 验证用户登录
        Long userId = SecurityUtils.getUserId();

        log.info("接收到 AI 对话请求 - 用户ID: {}, 会话ID: {}, 消息: {}", userId, sessionId, message);

        // 验证或创建会话
        Long validSessionId = chatStreamService.validateOrCreateSession(sessionId, userId);
        if (validSessionId == null) {
            return Flux.just(ServerSentEvent.<String>builder()
                    .event("error")
                    .data("会话不存在或无权访问")
                    .build());
        }

        // 设置响应头，禁止缓存，否则无法流式输出？
        response.setHeader("Cache-Control", "no-cache, no-transform");
        response.setHeader("X-Accel-Buffering", "no");

        // 委托给 Service 层处理流式响应
        return chatStreamService.generateStreamResponse(message, validSessionId, userId);
    }

    /**
     * 中断流式输出接口
     * @param sessionId 会话ID
     */
    @PostMapping("/cancelStream")
    public R<String> cancelStream(@RequestParam(value = "sessionId") Long sessionId) {
        boolean success = chatStreamService.cancelStream(sessionId);
        if (success) {
            return R.ok("已中断流式输出");
        } else {
            return R.fail("未找到活跃的流式对话");
        }
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
        if (!chatStreamService.validateSessionAccess(sessionId, userId)) {
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
        if (!chatStreamService.validateSessionAccess(sessionId, userId)) {
            return R.fail("会话不存在或无权访问");
        }

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
        if (!chatStreamService.validateSessionAccess(sessionId, userId)) {
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
}
