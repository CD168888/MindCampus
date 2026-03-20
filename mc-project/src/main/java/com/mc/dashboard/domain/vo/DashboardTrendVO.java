package com.mc.dashboard.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "测评趋势数据VO")
public class DashboardTrendVO {

    @Schema(description = "日期")
    private String date;

    @Schema(description = "测评人数")
    private Long evaluationCount;

    @Schema(description = "完成人数")
    private Long completedCount;

    @Schema(description = "高风险人数")
    private Long highRiskCount;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getEvaluationCount() {
        return evaluationCount;
    }

    public void setEvaluationCount(Long evaluationCount) {
        this.evaluationCount = evaluationCount;
    }

    public Long getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(Long completedCount) {
        this.completedCount = completedCount;
    }

    public Long getHighRiskCount() {
        return highRiskCount;
    }

    public void setHighRiskCount(Long highRiskCount) {
        this.highRiskCount = highRiskCount;
    }
}
