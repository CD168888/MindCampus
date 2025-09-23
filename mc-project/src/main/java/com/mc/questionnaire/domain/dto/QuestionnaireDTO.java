package com.mc.questionnaire.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.common.core.domain.BaseEntityPlus;
import com.mc.questionnaire.domain.Question;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 问卷 DTO（包含题目）
 */
@Data
@Schema(description = "问卷新增/修改 DTO")
public class QuestionnaireDTO extends BaseEntityPlus {

    @Schema(description = "问卷ID，新增时可为空")
    private Long questionnaireId;

    @Schema(description = "问卷标题")
    private String title;

    @Schema(description = "问卷说明")
    private String description;

    @Schema(description = "问卷状态（0正常 1下架）")
    private String status;

    @Schema(description = "问卷类型（0常规测评 1临时测评 2专项测评 3学业压力 4情绪状态 5人格特质）")
    private String type;

    @Schema(description = "问卷总分")
    private Integer totalScore;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "问卷开始时间")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "问卷结束时间")
    private Date endTime;

    @Schema(description = "题目列表")
    private List<Question> questions;
}