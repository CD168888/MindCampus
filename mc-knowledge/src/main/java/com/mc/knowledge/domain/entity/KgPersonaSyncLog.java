package com.mc.knowledge.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.common.core.domain.BaseEntity;
import com.mc.common.core.domain.BaseEntityPlus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

/**
 * 知识图谱同步日志对象 kg_persona_sync_log
 *
 * @author MindCampus
 */
@Data
@TableName("kg_persona_sync_log")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "知识图谱同步日志实体")
public class KgPersonaSyncLog extends BaseEntityPlus {
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private Map<String, Object> params;

    @Schema(description = "日志ID", accessMode = Schema.AccessMode.READ_ONLY)
    @com.baomidou.mybatisplus.annotation.TableId(value = "log_id", type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Long logId;

    @Schema(description = "用户ID", required = true)
    private Long userId;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "同步类型（full/incremental）", maxLength = 20)
    private String syncType;

    @Schema(description = "同步内容（JSON）")
    private String syncContent;

    @Schema(description = "状态（0成功 1失败）", allowableValues = {"0", "1"})
    private String syncStatus;

    @Schema(description = "错误信息", maxLength = 500)
    private String errorMsg;
}
