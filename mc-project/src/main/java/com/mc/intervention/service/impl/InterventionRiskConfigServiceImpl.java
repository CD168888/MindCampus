package com.mc.intervention.service.impl;

import com.mc.intervention.domain.InterventionRiskConfig;
import com.mc.intervention.mapper.InterventionRiskConfigMapper;
import com.mc.intervention.service.IInterventionRiskConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 风险等级配置表 服务实现
 *
 * @author mc
 */
@Service
public class InterventionRiskConfigServiceImpl implements IInterventionRiskConfigService {
    @Autowired
    private InterventionRiskConfigMapper configMapper;

    /**
     * 查询风险等级配置表列表
     */
    @Override
    public List<InterventionRiskConfig> selectConfigList(InterventionRiskConfig config) {
        return configMapper.selectConfigList(config);
    }

    /**
     * 查询风险等级配置表信息
     */
    @Override
    public InterventionRiskConfig selectConfigById(Long configId) {
        return configMapper.selectConfigById(configId);
    }

    /**
     * 根据风险等级查询配置
     */
    @Override
    public InterventionRiskConfig selectConfigByRiskLevel(String riskLevel) {
        return configMapper.selectConfigByRiskLevel(riskLevel);
    }

    /**
     * 根据分数查询风险等级配置
     */
    @Override
    public InterventionRiskConfig selectConfigByScore(Integer score) {
        return configMapper.selectConfigByScore(score);
    }

    /**
     * 新增风险等级配置表
     */
    @Override
    public int insertConfig(InterventionRiskConfig config) {
        config.setCreateTime(new Date());
        return configMapper.insertConfig(config);
    }

    /**
     * 修改风险等级配置表
     */
    @Override
    public int updateConfig(InterventionRiskConfig config) {
        config.setUpdateTime(new Date());
        return configMapper.updateConfig(config);
    }

    /**
     * 删除风险等级配置表
     */
    @Override
    public int deleteConfigById(Long configId) {
        return configMapper.deleteConfigById(configId);
    }

    /**
     * 批量删除风险等级配置表
     */
    @Override
    public int deleteConfigByIds(Long[] configIds) {
        return configMapper.deleteConfigByIds(configIds);
    }

    /**
     * 获取所有风险等级配置
     */
    @Override
    public List<InterventionRiskConfig> selectAllConfig() {
        InterventionRiskConfig config = new InterventionRiskConfig();
        config.setStatus("0");
        return configMapper.selectConfigList(config);
    }

    /**
     * 根据分数判断风险等级
     */
    @Override
    public String judgeRiskLevel(Integer score) {
        InterventionRiskConfig config = configMapper.selectConfigByScore(score);
        return config != null ? config.getRiskLevel() : "低";
    }
}
