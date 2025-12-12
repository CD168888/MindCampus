package com.mc.ai.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 心理健康评估结果类
 * 用于接收AI评估的结构化输出
 *
 * @author caidu
 */
public class MentalHealthEvaluationResult {
    /**
     * 总得分（0-100）
     */
    @JsonProperty("total_score")
    private int totalScore;

    /**
     * 风险等级（低/中/高）
     */
    @JsonProperty("risk_level")
    private String riskLevel;

    /**
     * 心理健康各个维度的指标数据
     */
    @JsonProperty("indicators")
    private MentalHealthIndicator indicators;

    /**
     * 主要问题分析
     */
    @JsonProperty("main_issues")
    private List<String> mainIssues;

    /**
     * 建议措施
     */
    @JsonProperty("suggestions")
    private List<String> suggestions;

    /**
     * 详细分析报告
     */
    @JsonProperty("detailed_analysis")
    private String detailedAnalysis;

    // getter 和 setter 方法
    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public MentalHealthIndicator getIndicators() {
        return indicators;
    }

    public void setIndicators(MentalHealthIndicator indicators) {
        this.indicators = indicators;
    }

    public List<String> getMainIssues() {
        return mainIssues;
    }

    public void setMainIssues(List<String> mainIssues) {
        this.mainIssues = mainIssues;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public String getDetailedAnalysis() {
        return detailedAnalysis;
    }

    public void setDetailedAnalysis(String detailedAnalysis) {
        this.detailedAnalysis = detailedAnalysis;
    }
}