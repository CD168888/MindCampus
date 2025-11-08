package com.mc.questionnaire.service.impl;

import com.mc.common.utils.DateUtils;
import com.mc.common.utils.SecurityUtils;
import com.mc.questionnaire.domain.QuestionnaireAnswer;
import com.mc.questionnaire.mapper.QuestionnaireAnswerMapper;
import com.mc.questionnaire.service.IQuestionnaireAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 心理测评答题记录Service业务层处理
 *
 * @author caidu
 * @date 2025-11-08
 */
@Service
public class QuestionnaireAnswerServiceImpl implements IQuestionnaireAnswerService {
    @Autowired
    @Qualifier("questionnaireAnswerRecordMapper")
    private QuestionnaireAnswerMapper questionnaireAnswerMapper;

    /**
     * 查询心理测评答题记录
     *
     * @param answerId 心理测评答题记录主键
     * @return 心理测评答题记录
     */
    @Override
    public QuestionnaireAnswer selectQuestionnaireAnswerByAnswerId(Long answerId) {
        return questionnaireAnswerMapper.selectQuestionnaireAnswerByAnswerId(answerId);
    }

    /**
     * 查询心理测评答题记录列表
     *
     * @param questionnaireAnswer 心理测评答题记录
     * @return 心理测评答题记录
     */
    @Override
    public List<QuestionnaireAnswer> selectQuestionnaireAnswerList(QuestionnaireAnswer questionnaireAnswer) {
        return questionnaireAnswerMapper.selectQuestionnaireAnswerList(questionnaireAnswer);
    }

    /**
     * 新增心理测评答题记录
     *
     * @param questionnaireAnswer 心理测评答题记录
     * @return 结果
     */
    @Override
    public int insertQuestionnaireAnswer(QuestionnaireAnswer questionnaireAnswer) {
        questionnaireAnswer.setCreateBy(SecurityUtils.getUsername());
        questionnaireAnswer.setCreateTime(DateUtils.getNowDate());
        return questionnaireAnswerMapper.insertQuestionnaireAnswer(questionnaireAnswer);
    }

    /**
     * 修改心理测评答题记录
     *
     * @param questionnaireAnswer 心理测评答题记录
     * @return 结果
     */
    @Override
    public int updateQuestionnaireAnswer(QuestionnaireAnswer questionnaireAnswer) {
        questionnaireAnswer.setUpdateBy(SecurityUtils.getUsername());
        questionnaireAnswer.setUpdateTime(DateUtils.getNowDate());
        return questionnaireAnswerMapper.updateQuestionnaireAnswer(questionnaireAnswer);
    }

    /**
     * 批量删除心理测评答题记录
     *
     * @param answerIds 需要删除的心理测评答题记录主键
     * @return 结果
     */
    @Override
    public int deleteQuestionnaireAnswerByAnswerIds(Long[] answerIds) {
        return questionnaireAnswerMapper.deleteQuestionnaireAnswerByAnswerIds(answerIds);
    }

    /**
     * 删除心理测评答题记录信息
     *
     * @param answerId 心理测评答题记录主键
     * @return 结果
     */
    @Override
    public int deleteQuestionnaireAnswerByAnswerId(Long answerId) {
        return questionnaireAnswerMapper.deleteQuestionnaireAnswerByAnswerId(answerId);
    }
}

