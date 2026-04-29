package com.mc.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mc.knowledge.domain.entity.KnowledgeEvaluationResult;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 评估结果Mapper接口
 */
@Mapper
@Component("knowledgeEvaluationResultMapper")
public interface EvaluationResultMapper extends BaseMapper<KnowledgeEvaluationResult> {

    /**
     * 查询学生最近的评估结果
     */
    @Select("SELECT r.result_id as resultId, r.student_id as studentId, r.questionnaire_id as questionnaireId, " +
            "r.total_score as totalScore, r.risk_level as riskLevel, r.create_time as createTime, " +
            "q.title as questionnaireTitle " +
            "FROM evaluation_result r " +
            "LEFT JOIN questionnaire q ON r.questionnaire_id = q.questionnaire_id " +
            "WHERE r.student_id = #{studentId} " +
            "ORDER BY r.create_time DESC LIMIT #{limit}")
    List<KnowledgeEvaluationResult> selectRecentByStudentId(@Param("studentId") Long studentId, @Param("limit") int limit);

    /**
     * 查询所有评估结果（用于批量同步）
     */
    @Select("SELECT r.result_id as resultId, r.student_id as studentId, r.questionnaire_id as questionnaireId, " +
            "r.total_score as totalScore, r.risk_level as riskLevel, r.create_time as createTime, " +
            "q.title as questionnaireTitle " +
            "FROM evaluation_result r " +
            "LEFT JOIN questionnaire q ON r.questionnaire_id = q.questionnaire_id " +
            "ORDER BY r.create_time DESC")
    List<KnowledgeEvaluationResult> selectAllResults();
}
