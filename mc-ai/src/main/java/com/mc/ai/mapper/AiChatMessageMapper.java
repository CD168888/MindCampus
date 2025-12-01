package com.mc.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mc.ai.domain.AiChatMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 聊天消息 Mapper 接口
 *
 * @author caidu
 * @date 2025-11-27
 */
@Mapper
public interface AiChatMessageMapper extends BaseMapper<AiChatMessage> {
}
