package com.mc.evaluation.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 问卷列表项VO
 *
 * @author caidu
 * @date 2025-11-04
 */
@Data
@Schema(description = "问卷列表项VO")
public class QuestionnaireItemVO {

    @Schema(description = "问卷ID")
    private Long questionnaireId;

    @Schema(description = "问卷标题")
    private String title;

    @Schema(description = "问卷描述")
    private String description;

    @Schema(description = "题目数量")
    private Integer questionCount;

    @Schema(description = "预计时长（分钟）")
    private Integer duration;

    @Schema(description = "发布者")
    private String publisher;

    @Schema(description = "截止时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deadline;

    @Schema(description = "完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date completedTime;

    @Schema(description = "结果ID")
    private Long resultId;

    @Schema(description = "问卷类型（0常规测评 1临时测评 2专项测评 3学业压力 4情绪状态 5人格特质）")
    private String type;

    @Schema(description = "状态（pending待填写/expired已截止/completed已完成/analyzing待分析）")
    private String status;

    @Schema(description = "AI分析状态（0未完成 1已完成）")
    private String aiStatus;
}
