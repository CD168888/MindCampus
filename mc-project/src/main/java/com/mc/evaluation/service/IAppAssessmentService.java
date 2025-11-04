package com.mc.evaluation.service;

import com.mc.evaluation.domain.EvaluationResult;
import com.mc.evaluation.domain.dto.SubmitAnswerDTO;
import com.mc.evaluation.domain.vo.AssessmentStatisticsVO;
import com.mc.evaluation.domain.vo.EvaluationResultVO;
import com.mc.evaluation.domain.vo.QuestionnaireListVO;
import com.mc.questionnaire.domain.vo.QuestionnaireVO;

import java.util.List;

/**
 * APP端心理测评Service接口
 *
 * @author caidu
 * @date 2025-11-04
 */
public interface IAppAssessmentService {

    /**
     * 获取测评统计数据
     *
     * @param userId 用户ID
     * @return 统计数据
     */
    AssessmentStatisticsVO getStatistics(Long userId);

    /**
     * 查询问卷列表（待填+已完成）
     *
     * @param userId 用户ID
     * @return 问卷列表
     */
    QuestionnaireListVO listQuestionnaires(Long userId);

    /**
     * 查询问卷详情（包含题目）
     *
     * @param userId          用户ID
     * @param questionnaireId 问卷ID
     * @return 问卷详情
     */
    QuestionnaireVO getQuestionnaireDetail(Long userId, Long questionnaireId);

    /**
     * 提交答题
     *
     * @param userId 用户ID
     * @param dto    答题数据
     * @return 结果ID
     */
    Long submitAnswer(Long userId, SubmitAnswerDTO dto);

    /**
     * 查询测评结果详情
     *
     * @param userId   用户ID
     * @param resultId 结果ID
     * @return 结果详情
     */
    EvaluationResultVO getResultDetail(Long userId, Long resultId);

    /**
     * 查询我的测评历史记录
     *
     * @param userId 用户ID
     * @return 测评记录列表
     */
    List<EvaluationResult> listMyResults(Long userId);

    /**
     * 检查问卷是否已完成
     *
     * @param userId          用户ID
     * @param questionnaireId 问卷ID
     * @return 是否已完成
     */
    boolean checkCompleted(Long userId, Long questionnaireId);
}
