package com.mc.intervention.mapper;

import com.mc.intervention.domain.InterventionRiskConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 风险等级配置表 数据层
 *
 * @author mc
 */
@Mapper
public interface InterventionRiskConfigMapper {
    /**
     * 查询风险等级配置表数据
     *
     * @param config 风险等级配置表
     * @return 风险等级配置表数据
     */
    public List<InterventionRiskConfig> selectConfigList(InterventionRiskConfig config);

    /**
     * 查询风险等级配置表信息
     *
     * @param configId 风险等级配置表ID
     * @return 风险等级配置表信息
     */
    public InterventionRiskConfig selectConfigById(Long configId);

    /**
     * 根据风险等级查询配置
     *
     * @param riskLevel 风险等级
     * @return 风险等级配置表信息
     */
    public InterventionRiskConfig selectConfigByRiskLevel(String riskLevel);

    /**
     * 根据分数查询风险等级配置
     *
     * @param score 分数
     * @return 风险等级配置表信息
     */
    public InterventionRiskConfig selectConfigByScore(Integer score);

    /**
     * 新增风险等级配置表
     *
     * @param config 风险等级配置表
     * @return 结果
     */
    public int insertConfig(InterventionRiskConfig config);

    /**
     * 修改风险等级配置表
     *
     * @param config 风险等级配置表
     * @return 结果
     */
    public int updateConfig(InterventionRiskConfig config);

    /**
     * 删除风险等级配置表
     *
     * @param configId 风险等级配置表ID
     * @return 结果
     */
    public int deleteConfigById(Long configId);

    /**
     * 批量删除风险等级配置表
     *
     * @param configIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteConfigByIds(Long[] configIds);
}
