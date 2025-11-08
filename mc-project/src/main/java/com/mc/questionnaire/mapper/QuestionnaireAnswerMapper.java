package com.mc.questionnaire.mapper;

import com.mc.questionnaire.domain.QuestionnaireAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 心理测评答题记录Mapper接口
 *
 * @author caidu
 * @date 2025-11-08
 */
@Mapper
@Repository("questionnaireAnswerRecordMapper")
public interface QuestionnaireAnswerMapper {
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
   * 删除心理测评答题记录
   *
   * @param answerId 心理测评答题记录主键
   * @return 结果
   */
  public int deleteQuestionnaireAnswerByAnswerId(Long answerId);

  /**
   * 批量删除心理测评答题记录
   *
   * @param answerIds 需要删除的数据主键集合
   * @return 结果
   */
  public int deleteQuestionnaireAnswerByAnswerIds(Long[] answerIds);
}
