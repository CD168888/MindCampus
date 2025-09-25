package com.mc.evaluation.mapper;

import com.mc.evaluation.domain.EvaluationResult;

import java.util.List;

/**
 * 心理测评结果Mapper接口
 *
 * @author caidu
 * @date 2025-09-25
 */
public interface EvaluationResultMapper {
    /**
     * 查询心理测评结果
     *
     * @param resultId 心理测评结果主键
     * @return 心理测评结果
     */
    public EvaluationResult selectEvaluationResultByResultId(Long resultId);

    /**
     * 查询心理测评结果列表
     *
     * @param evaluationResult 心理测评结果
     * @return 心理测评结果集合
     */
    public List<EvaluationResult> selectEvaluationResultList(EvaluationResult evaluationResult);

    /**
     * 新增心理测评结果
     *
     * @param evaluationResult 心理测评结果
     * @return 结果
     */
    public int insertEvaluationResult(EvaluationResult evaluationResult);

    /**
     * 修改心理测评结果
     *
     * @param evaluationResult 心理测评结果
     * @return 结果
     */
    public int updateEvaluationResult(EvaluationResult evaluationResult);

    /**
     * 删除心理测评结果
     *
     * @param resultId 心理测评结果主键
     * @return 结果
     */
    public int deleteEvaluationResultByResultId(Long resultId);

    /**
     * 批量删除心理测评结果
     *
     * @param resultIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEvaluationResultByResultIds(Long[] resultIds);
}
