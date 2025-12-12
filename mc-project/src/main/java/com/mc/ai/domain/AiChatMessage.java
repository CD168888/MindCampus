package com.mc.ai.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.common.annotation.Excel;
import com.mc.common.core.domain.BaseEntityPlus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * AI 聊天消息对象 ai_chat_message
 *
 * @author caidu
 * @date 2025-11-27
 */
@Data
@TableName("ai_chat_message")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AiChatMessage extends BaseEntityPlus {

    private static final long serialVersionUID = 1L;

    /** 消息ID */
    @TableId(type = IdType.AUTO)
    @Excel(name = "消息ID")
    private Long messageId;

    /** 会话ID */
    @Excel(name = "会话ID")
    private Long sessionId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 消息类型（0 AI消息 1 用户消息） */
    @Excel(name = "消息类型", readConverterExp = "0=AI消息,1=用户消息")
    private Integer messageType;

    /** 消息内容 */
    @Excel(name = "消息内容")
    private String content;
}
