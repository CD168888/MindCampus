package com.mc.evaluation.event;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 评估完成事件
 * 用于替代直接发布 Long 类型的 resultId，提高类型安全性
 *
 * @author mc
 */
@Getter
@AllArgsConstructor
public class EvaluationCompletedEvent {
    private final Long resultId;
    private final Long studentId;
    private final String riskLevel;
}
