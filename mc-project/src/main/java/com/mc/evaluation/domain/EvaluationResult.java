package com.mc.evaluation.domain;

import com.mc.common.annotation.Excel;
import com.mc.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 心理测评结果对象 evaluation_result
 *
 * @author caidu
 * @date 2025-09-25
 */
public class EvaluationResult extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 测评结果ID
     */
    private Long resultId;

    /**
     * 学生ID
     */
    @Excel(name = "学生ID")
    private Long studentId;

    /**
     * 学生姓名
     */
    @Excel(name = "学生姓名")
    private String studentName;

    /**
     * 问卷ID
     */
    @Excel(name = "问卷ID")
    private Long questionnaireId;

    /**
     * 问卷标题
     */
    @Excel(name = "问卷标题")
    private String questionnaireTitle;

    /**
     * 总得分
     */
    @Excel(name = "总得分")
    private Long totalScore;

    /**
     * 风险等级（低/中/高）
     */
    @Excel(name = "风险等级")
    private String riskLevel;

    /**
     * AI 分析结果（JSON格式）
     */
    @Excel(name = "AI 分析结果")
    private String aiAnalysis;

    /**
     * AI分析状态（0未完成 1已完成）
     */
    @Excel(name = "AI分析状态", readConverterExp = "0=未完成,1=已完成")
    private String aiStatus;

    /**
     * 已读标识（0未读 1已读）
     */
    @Excel(name = "已读标识", readConverterExp = "0=未读,1=已读")
    private String readStatus;

    /**
     * 完成标识（0未完成 1已完成）
     */
    @Excel(name = "完成标识", readConverterExp = "0=未完成,1=已完成")
    private String completionStatus;

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public Long getResultId() {
        return resultId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setQuestionnaireId(Long questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public Long getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireTitle(String questionnaireTitle) {
        this.questionnaireTitle = questionnaireTitle;
    }

    public String getQuestionnaireTitle() {
        return questionnaireTitle;
    }

    public void setTotalScore(Long totalScore) {
        this.totalScore = totalScore;
    }

    public Long getTotalScore() {
        return totalScore;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setAiAnalysis(String aiAnalysis) {
        this.aiAnalysis = aiAnalysis;
    }

    public String getAiAnalysis() {
        return aiAnalysis;
    }

    public void setAiStatus(String aiStatus) {
        this.aiStatus = aiStatus;
    }

    public String getAiStatus() {
        return aiStatus;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }

    public String getReadStatus() {
        return readStatus;
    }

    public void setCompletionStatus(String completionStatus) {
        this.completionStatus = completionStatus;
    }

    public String getCompletionStatus() {
        return completionStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("resultId", getResultId())
                .append("studentId", getStudentId())
                .append("studentName", getStudentName())
                .append("questionnaireId", getQuestionnaireId())
                .append("questionnaireTitle", getQuestionnaireTitle())
                .append("totalScore", getTotalScore())
                .append("riskLevel", getRiskLevel())
                .append("aiAnalysis", getAiAnalysis())
                .append("aiStatus", getAiStatus())
                .append("readStatus", getReadStatus())
                .append("completionStatus", getCompletionStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
