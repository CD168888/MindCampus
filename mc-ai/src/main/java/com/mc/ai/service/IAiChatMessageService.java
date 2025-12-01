package com.mc.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.ai.domain.AiChatMessage;

import java.util.List;

/**
 * AI聊天消息服务接口
 */
public interface IAiChatMessageService extends IService<AiChatMessage> {
    /**
     * 保存消息
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @param messageType 消息类型
     * @param content 消息内容
     * @return 保存的消息对象
     */
    AiChatMessage saveMessage(Long sessionId, Long userId, Integer messageType, String content);
    
    /**
     * 从Redis获取消息列表
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<AiChatMessage> getMessagesFromRedis(Long sessionId);
    
    /**
     * 从数据库获取消息列表
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<AiChatMessage> getMessagesFromDB(Long sessionId);
    
    /**
     * 获取会话消息列表（优先从Redis获取，如果没有则从数据库获取）
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<AiChatMessage> getSessionMessages(Long sessionId);
    
    /**
     * 删除会话消息
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 删除是否成功
     */
    boolean deleteSessionMessages(Long sessionId, Long userId);
}
