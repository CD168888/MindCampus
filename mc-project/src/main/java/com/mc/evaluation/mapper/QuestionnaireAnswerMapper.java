package com.mc.evaluation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mc.evaluation.domain.QuestionnaireAnswer;

import java.util.List;

/**
 * 答题记录Mapper接口
 *
 * @author caidu
 * @date 2025-11-04
 */
public interface QuestionnaireAnswerMapper extends BaseMapper<QuestionnaireAnswer> {

    /**
     * 批量插入答题记录
     *
     * @param answerList 答题记录列表
     * @return 结果
     */
    int batchInsert(List<QuestionnaireAnswer> answerList);

    /**
     * 根据结果ID查询答题记录
     *
     * @param resultId 结果ID
     * @return 答题记录列表
     */
    List<QuestionnaireAnswer> selectByResultId(Long resultId);

    /**
     * 根据结果ID删除答题记录
     *
     * @param resultId 结果ID
     * @return 结果
     */
    int deleteByResultId(Long resultId);
}
