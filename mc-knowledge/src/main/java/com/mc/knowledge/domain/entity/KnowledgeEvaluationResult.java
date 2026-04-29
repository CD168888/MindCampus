package com.mc.knowledge.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评估结果实体
 */
@Data
@TableName("evaluation_result")
public class KnowledgeEvaluationResult {

    @TableId(type = IdType.AUTO)
    private Long resultId;

    private Long studentId;

    private Long questionnaireId;

    private Integer totalScore;

    private String riskLevel;

    private String aiAnalysis;

    private String aiStatus;

    private String readStatus;

    private String completionStatus;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    // 关联查询字段（非数据库字段）
    private String questionnaireTitle;
}
