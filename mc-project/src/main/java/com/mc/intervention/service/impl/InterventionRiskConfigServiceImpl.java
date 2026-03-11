package com.mc.intervention.service.impl;

import com.mc.intervention.domain.InterventionRiskConfig;
import com.mc.intervention.mapper.InterventionRiskConfigMapper;
import com.mc.intervention.service.IInterventionRiskConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
     * 获取所有风险等级配置（低、中、高），如果不存在则创建默认配置
     */
    @Override
    public List<InterventionRiskConfig> getOrCreateAllConfig() {
        List<InterventionRiskConfig> result = new ArrayList<>();
        String[] levels = {"低", "中", "高"};
        for (String level : levels) {
            InterventionRiskConfig config = getOrCreateConfigByLevel(level);
            result.add(config);
        }
        return result;
    }

    /**
     * 根据风险等级获取配置，如果不存在则创建默认配置
     */
    @Override
    public InterventionRiskConfig getOrCreateConfigByLevel(String riskLevel) {
        InterventionRiskConfig query = new InterventionRiskConfig();
        query.setRiskLevel(riskLevel);
        List<InterventionRiskConfig> configs = configMapper.selectConfigList(query);

        if (configs != null && !configs.isEmpty()) {
            return configs.get(0);
        }

        // 创建默认配置
        InterventionRiskConfig config = new InterventionRiskConfig();
        config.setRiskLevel(riskLevel);

        if ("低".equals(riskLevel)) {
            config.setMinScore(0);
            config.setMaxScore(60);
            config.setNotificationTemplate("学生测评分数为${score}分，风险等级为低，请关注学生心理健康。");
        } else if ("中".equals(riskLevel)) {
            config.setMinScore(61);
            config.setMaxScore(80);
            config.setNotificationTemplate("学生测评分数为${score}分，风险等级为中，建议关注并适时干预。");
        } else if ("高".equals(riskLevel)) {
            config.setMinScore(81);
            config.setMaxScore(100);
            config.setNotificationTemplate("学生测评分数为${score}分，风险等级为高，请及时采取干预措施！");
        }

        config.setStatus("0");
        config.setCreateTime(new Date());
        configMapper.insertConfig(config);
        return config;
    }

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
