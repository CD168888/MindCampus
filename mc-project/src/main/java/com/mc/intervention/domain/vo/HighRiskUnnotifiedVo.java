package com.mc.intervention.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 高风险未通知评测结果VO
 *
 * @author mc
 */
@Data
@Schema(description = "高风险未通知评测结果")
public class HighRiskUnnotifiedVo {

    @Schema(description = "评测结果ID")
    private Long resultId;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "学号")
    private String studentNo;

    @Schema(description = "年级")
    private String grade;

    @Schema(description = "专业")
    private String major;

    @Schema(description = "班级")
    private String className;

    @Schema(description = "风险等级")
    private String riskLevel;

    @Schema(description = "问卷标题")
    private String questionnaireTitle;

    @Schema(description = "评测时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date evaluationTime;
}
