package com.mc.questionnaire.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.common.core.domain.BaseEntityPlus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * 问卷实体类
 *
 * @author caidu
 * @date 2025-09-22
 */
@Data
@TableName("questionnaire")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "问卷实体")
public class Questionnaire extends BaseEntityPlus {
    private static final long serialVersionUID = 1L;

    @Schema(description = "问卷ID")
    @TableId(type = IdType.AUTO)
    private Long questionnaireId;

    @Schema(description = "问卷标题")
    private String title;

    @Schema(description = "问卷描述")
    private String description;

    @Schema(description = "问卷状态（0正常 1下架）")
    private String status;

    @Schema(description = "问卷类型（0常规测评 1临时测评 2专项测评 3学业压力 4情绪状态 5人格特质）")
    private String type;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "问卷开始时间")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "问卷结束时间")
    private Date endTime;
}