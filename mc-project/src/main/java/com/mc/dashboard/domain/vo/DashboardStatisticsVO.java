package com.mc.dashboard.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "首页统计数据VO")
public class DashboardStatisticsVO {

    @Schema(description = "学生总数")
    private Long totalStudents;

    @Schema(description = "已完成测评数")
    private Long completedEvaluations;

    @Schema(description = "待关注学生数")
    private Long attentionStudents;

    @Schema(description = "高风险预警数")
    private Long highRiskCount;

    @Schema(description = "低风险人数")
    private Long lowRiskCount;

    @Schema(description = "中风险人数")
    private Long mediumRiskCount;

    @Schema(description = "本周新增测评数")
    private Long weekNewEvaluations;

    @Schema(description = "上周测评数")
    private Long lastWeekEvaluations;

    public Long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public Long getCompletedEvaluations() {
        return completedEvaluations;
    }

    public void setCompletedEvaluations(Long completedEvaluations) {
        this.completedEvaluations = completedEvaluations;
    }

    public Long getAttentionStudents() {
        return attentionStudents;
    }

    public void setAttentionStudents(Long attentionStudents) {
        this.attentionStudents = attentionStudents;
    }

    public Long getHighRiskCount() {
        return highRiskCount;
    }

    public void setHighRiskCount(Long highRiskCount) {
        this.highRiskCount = highRiskCount;
    }

    public Long getLowRiskCount() {
        return lowRiskCount;
    }

    public void setLowRiskCount(Long lowRiskCount) {
        this.lowRiskCount = lowRiskCount;
    }

    public Long getMediumRiskCount() {
        return mediumRiskCount;
    }

    public void setMediumRiskCount(Long mediumRiskCount) {
        this.mediumRiskCount = mediumRiskCount;
    }

    public Long getWeekNewEvaluations() {
        return weekNewEvaluations;
    }

    public void setWeekNewEvaluations(Long weekNewEvaluations) {
        this.weekNewEvaluations = weekNewEvaluations;
    }

    public Long getLastWeekEvaluations() {
        return lastWeekEvaluations;
    }

    public void setLastWeekEvaluations(Long lastWeekEvaluations) {
        this.lastWeekEvaluations = lastWeekEvaluations;
    }
}
