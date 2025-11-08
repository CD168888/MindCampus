package com.mc.questionnaire.service;

import com.mc.questionnaire.domain.QuestionnaireAnswer;

import java.util.List;

/**
 * 心理测评答题记录Service接口
 *
 * @author caidu
 * @date 2025-11-08
 */
public interface IQuestionnaireAnswerService {
    /**
     * 查询心理测评答题记录
     *
     * @param answerId 心理测评答题记录主键
     * @return 心理测评答题记录
     */
    public QuestionnaireAnswer selectQuestionnaireAnswerByAnswerId(Long answerId);

    /**
     * 查询心理测评答题记录列表
     *
     * @param questionnaireAnswer 心理测评答题记录
     * @return 心理测评答题记录集合
     */
    public List<QuestionnaireAnswer> selectQuestionnaireAnswerList(QuestionnaireAnswer questionnaireAnswer);

    /**
     * 新增心理测评答题记录
     *
     * @param questionnaireAnswer 心理测评答题记录
     * @return 结果
     */
    public int insertQuestionnaireAnswer(QuestionnaireAnswer questionnaireAnswer);

    /**
     * 修改心理测评答题记录
     *
     * @param questionnaireAnswer 心理测评答题记录
     * @return 结果
     */
    public int updateQuestionnaireAnswer(QuestionnaireAnswer questionnaireAnswer);

    /**
     * 批量删除心理测评答题记录
     *
     * @param answerIds 需要删除的心理测评答题记录主键集合
     * @return 结果
     */
    public int deleteQuestionnaireAnswerByAnswerIds(Long[] answerIds);

    /**
     * 删除心理测评答题记录信息
     *
     * @param answerId 心理测评答题记录主键
     * @return 结果
     */
    public int deleteQuestionnaireAnswerByAnswerId(Long answerId);
}

