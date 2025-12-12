package com.mc.ai.service;

import com.mc.ai.domain.AiChatSession;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * AI聊天会话服务接口
 * 提供管理AI聊天会话的操作
 */
public interface IAiChatSessionService extends IService<AiChatSession> {
    
    /**
     * 创建新的聊天会话
     * 
     * @param userId 创建会话的用户ID
     * @param sessionName 会话名称
     * @return 创建的AiChatSession对象
     */
    AiChatSession createSession(Long userId, String sessionName);
    
    /**
     * 获取指定用户的所有聊天会话
     * 
     * @param userId 用户ID
     * @return 属于该用户的AiChatSession对象列表
     */
    List<AiChatSession> getUserSessions(Long userId);
    
    /**
     * 更新现有会话的名称
     * 
     * @param sessionId 要更新的会话ID
     * @param sessionName 会话的新名称
     */
    void updateSessionName(Long sessionId, String sessionName);
    
    /**
     * 删除聊天会话
     * 
     * @param sessionId 要删除的会话ID
     * @param userId 请求删除的用户ID
     * @return 删除成功返回true，否则返回false
     */
    boolean deleteSession(Long sessionId, Long userId);
}
