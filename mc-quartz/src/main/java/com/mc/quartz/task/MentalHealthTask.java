package com.mc.quartz.task;

import com.mc.evaluation.domain.EvaluationResult;
import com.mc.evaluation.mapper.EvaluationResultMapper;
import com.mc.evaluation.service.IMentalHealthEvaluationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 心理健康定时任务
 * 
 * @author caidu
 */
@Component("mentalHealthTask")
public class MentalHealthTask {
    private static final Logger log = LoggerFactory.getLogger(MentalHealthTask.class);

    @Autowired
    private EvaluationResultMapper evaluationResultMapper;

    @Autowired
    private IMentalHealthEvaluationService mentalHealthEvaluationService;

    /**
     * 定期处理待评估的测评结果
     */
    public void processPendingEvaluations() {
        log.info("开始处理待评估的测评结果");
        
        try {
            // 创建查询条件，查找ai_status为0（待评估）的测评结果
            EvaluationResult query = new EvaluationResult();
            query.setAiStatus("0");
            
            // 查询待评估的测评结果
            List<EvaluationResult> pendingResults = evaluationResultMapper.selectEvaluationResultList(query);
            log.info("找到 {} 个待评估的测评结果", pendingResults.size());
            
            // 逐个处理待评估的结果
            for (EvaluationResult result : pendingResults) {
                log.info("处理测评结果ID: {}", result.getResultId());
                mentalHealthEvaluationService.asyncEvaluateMentalHealth(result.getResultId());
            }
            
            log.info("待评估的测评结果处理完成");
        } catch (Exception e) {
            log.error("处理待评估的测评结果失败", e);
        }
    }
}