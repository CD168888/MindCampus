package com.mc.intervention.listener;

import com.mc.evaluation.domain.EvaluationResult;
import com.mc.evaluation.service.IEvaluationResultService;
import com.mc.intervention.service.IInterventionNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 评测结果监听器
 * 用于监听AI分析完成事件，自动生成干预通知
 *
 * @author mc
 */
@Component
public class EvaluationResultListener {
    @Autowired
    private IEvaluationResultService evaluationResultService;

    @Autowired
    private IInterventionNotificationService notificationService;

    /**
     * 监听评测结果更新事件
     *
     * @param resultId 评测结果ID
     */
    @Async
    @EventListener(value = Long.class, condition = "#resultId != null")
    public void onEvaluationResultUpdate(Long resultId) {
        try {
            // 查询评测结果
            EvaluationResult result = evaluationResultService.selectEvaluationResultByResultId(resultId);
            if (result == null) {
                return;
            }

            // 判断是否为高风险且AI分析已完成
            if ("1".equals(result.getAiStatus()) && "高".equals(result.getRiskLevel())) {
                // 生成干预通知
                notificationService.generateNotification(resultId, result.getStudentId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
