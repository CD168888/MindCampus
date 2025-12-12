package com.mc.ai.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 心理健康指标类
 * 用于存储心理健康各个维度的指标数据，用于前端雷达图展示
 *
 * @author caidu
 */
public class MentalHealthIndicator {
    /**
     * 焦虑维度得分（0-100）
     */
    @JsonProperty("anxiety_score")
    private int anxietyScore;

    /**
     * 抑郁维度得分（0-100）
     */
    @JsonProperty("depression_score")
    private int depressionScore;

    /**
     * 压力维度得分（0-100）
     */
    @JsonProperty("stress_score")
    private int stressScore;

    /**
     * 社交功能维度得分（0-100）
     */
    @JsonProperty("social_score")
    private int socialScore;

    /**
     * 睡眠质量维度得分（0-100）
     */
    @JsonProperty("sleep_score")
    private int sleepScore;

    /**
     * 情绪稳定性维度得分（0-100）
     */
    @JsonProperty("emotion_score")
    private int emotionScore;

    /**
     * 自我效能感维度得分（0-100）
     */
    @JsonProperty("self_efficacy_score")
    private int selfEfficacyScore;

    // getter 和 setter 方法
    public int getAnxietyScore() {
        return anxietyScore;
    }

    public void setAnxietyScore(int anxietyScore) {
        this.anxietyScore = anxietyScore;
    }

    public int getDepressionScore() {
        return depressionScore;
    }

    public void setDepressionScore(int depressionScore) {
        this.depressionScore = depressionScore;
    }

    public int getStressScore() {
        return stressScore;
    }

    public void setStressScore(int stressScore) {
        this.stressScore = stressScore;
    }

    public int getSocialScore() {
        return socialScore;
    }

    public void setSocialScore(int socialScore) {
        this.socialScore = socialScore;
    }

    public int getSleepScore() {
        return sleepScore;
    }

    public void setSleepScore(int sleepScore) {
        this.sleepScore = sleepScore;
    }

    public int getEmotionScore() {
        return emotionScore;
    }

    public void setEmotionScore(int emotionScore) {
        this.emotionScore = emotionScore;
    }

    public int getSelfEfficacyScore() {
        return selfEfficacyScore;
    }

    public void setSelfEfficacyScore(int selfEfficacyScore) {
        this.selfEfficacyScore = selfEfficacyScore;
    }
}