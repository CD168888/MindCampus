package com.mc.intervention.domain;

import com.mc.common.annotation.Excel;
import com.mc.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 风险等级配置表
 *
 * @author mc
 * @date 2026-02-03
 */
public class InterventionRiskConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    private Long configId;

    /**
     * 风险等级（低/中/高）
     */
    @Excel(name = "风险等级", readConverterExp = "低=低,中=中,高=高")
    private String riskLevel;

    /**
     * 最低分数
     */
    @Excel(name = "最低分数")
    private Integer minScore;

    /**
     * 最高分数
     */
    @Excel(name = "最高分数")
    private Integer maxScore;

    /**
     * 通知模板
     */
    @Excel(name = "通知模板")
    private String notificationTemplate;

    /**
     * 状态（0正常 1停用）
     */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public Long getConfigId() {
        return configId;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setMinScore(Integer minScore) {
        this.minScore = minScore;
    }

    public Integer getMinScore() {
        return minScore;
    }

    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }

    public Integer getMaxScore() {
        return maxScore;
    }

    public void setNotificationTemplate(String notificationTemplate) {
        this.notificationTemplate = notificationTemplate;
    }

    public String getNotificationTemplate() {
        return notificationTemplate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("configId", getConfigId())
                .append("riskLevel", getRiskLevel())
                .append("minScore", getMinScore())
                .append("maxScore", getMaxScore())
                .append("notificationTemplate", getNotificationTemplate())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
