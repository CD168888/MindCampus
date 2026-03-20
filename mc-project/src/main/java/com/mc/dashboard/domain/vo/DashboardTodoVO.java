package com.mc.dashboard.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "待办事项VO")
public class DashboardTodoVO {

    @Schema(description = "通知ID")
    private Long notificationId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "问卷标题")
    private String questionnaireTitle;

    @Schema(description = "风险等级")
    private String riskLevel;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "优先级")
    private String priority;

    @Schema(description = "待办内容")
    private String content;

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
