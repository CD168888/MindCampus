package com.mc.dashboard.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "最新动态VO")
public class DashboardActivityVO {

    @Schema(description = "结果ID")
    private Long resultId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "问卷标题")
    private String questionnaireTitle;

    @Schema(description = "风险等级")
    private String riskLevel;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "动态类型")
    private String activityType;

    @Schema(description = "动态标题")
    private String title;

    @Schema(description = "动态描述")
    private String description;

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getQuestionnaireTitle() {
        return questionnaireTitle;
    }

    public void setQuestionnaireTitle(String questionnaireTitle) {
        this.questionnaireTitle = questionnaireTitle;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
