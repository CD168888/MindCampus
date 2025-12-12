package com.mc.ai.service.impl;

import com.mc.ai.domain.AiChatSession;
import com.mc.ai.mapper.AiChatSessionMapper;
import com.mc.ai.service.IAiChatSessionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI聊天会话服务
 */
@Service
@Slf4j
public class IAiChatSessionServiceImpl extends ServiceImpl<AiChatSessionMapper, AiChatSession> implements IAiChatSessionService {
    /**
     * 创建新会话
     */
    public AiChatSession createSession(Long userId, String sessionName) {
        AiChatSession session = new AiChatSession();
        session.setUserId(userId);
        session.setSessionName(sessionName);

        save(session);
        log.info("创建会话成功 - 用户ID: {}, 会话ID: {}", userId, session.getSessionId());
        return session;
    }

    /**
     * 获取用户的所有会话列表
     */
    public List<AiChatSession> getUserSessions(Long userId) {
        LambdaQueryWrapper<AiChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatSession::getUserId, userId)
                .orderByDesc(AiChatSession::getUpdateTime);
        return list(wrapper);
    }

    /**
     * 更新会话时间
     */
//    public void updateSessionTime(Long sessionId) {
//        AiChatSession session = getById(sessionId);
//        if (session != null) {
//            session.setUpdateTime(LocalDateTime.now());
//            updateById(session);
//        }
//    }

    /**
     * 更新会话名称
     */
    public void updateSessionName(Long sessionId, String sessionName) {
        AiChatSession session = getById(sessionId);
        if (session != null) {
            session.setSessionName(sessionName);
            updateById(session);
            log.info("更新会话名称 - 会话ID: {}, 新名称: {}", sessionId, sessionName);
        }
    }

    /**
     * 删除会话
     */
    public boolean deleteSession(Long sessionId, Long userId) {
        LambdaQueryWrapper<AiChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatSession::getSessionId, sessionId)
                .eq(AiChatSession::getUserId, userId);
        return remove(wrapper);
    }
}
