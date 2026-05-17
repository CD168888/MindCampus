package com.mc.intervention.listener;

import com.mc.evaluation.event.EvaluationCompletedEvent;
import com.mc.intervention.service.IInterventionNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 评测结果监听器
 * 用于监听AI分析完成事件，自动生成干预通知
 *
 * @author mc
 */
@Component
@Slf4j
public class EvaluationResultListener {
    @Autowired
    private IInterventionNotificationService notificationService;

    /**
     * 监听评测完成事件，在事务提交后执行
     * 使用 @TransactionalEventListener 确保数据已持久化后再触发干预通知
     *
     * @param event 评测完成事件
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEvaluationCompleted(EvaluationCompletedEvent event) {
        try {
            // 判断是否为高风险
            if ("高".equals(event.getRiskLevel())) {
                log.info("检测到高风险评估结果，生成干预通知 - 结果ID: {}, 学生ID: {}",
                        event.getResultId(), event.getStudentId());
                // 生成干预通知
                notificationService.generateNotification(event.getResultId(), event.getStudentId());
            }
        } catch (Exception e) {
            log.error("生成干预通知失败 - 结果ID: {}", event.getResultId(), e);
        }
    }
}
