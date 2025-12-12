package com.mc.evaluation.service;

/**
 * 心理健康评估服务接口
 * 定义心理健康评估的核心方法
 *
 * @author caidu
 */
public interface IMentalHealthEvaluationService {
    /**
     * 异步评估学生的心理健康状况
     *
     * @param resultId 测评结果ID
     */
    void asyncEvaluateMentalHealth(Long resultId);
}
