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
 * AI 会话对象 ai_chat_session
 *
 * @author caidu
 * @date 2025-11-27
 */
@Data
@TableName("ai_chat_session")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AiChatSession extends BaseEntityPlus {

    private static final long serialVersionUID = 1L;

    /** 会话ID */
    @TableId(type = IdType.AUTO)
    @Excel(name = "会话ID")
    private Long sessionId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 会话名称 */
    @Excel(name = "会话名称")
    private String sessionName;
}
