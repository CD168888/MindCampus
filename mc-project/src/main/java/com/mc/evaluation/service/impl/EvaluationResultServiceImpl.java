package com.mc.evaluation.service.impl;

import com.mc.common.utils.DateUtils;
import com.mc.evaluation.domain.EvaluationResult;
import com.mc.evaluation.mapper.EvaluationResultMapper;
import com.mc.evaluation.service.IEvaluationResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 心理测评结果Service业务层处理
 *
 * @author caidu
 * @date 2025-09-25
 */
@Service
public class EvaluationResultServiceImpl implements IEvaluationResultService {
    @Autowired
    private EvaluationResultMapper evaluationResultMapper;

    /**
     * 查询心理测评结果
     *
     * @param resultId 心理测评结果主键
     * @return 心理测评结果
     */
    @Override
    public EvaluationResult selectEvaluationResultByResultId(Long resultId) {
        return evaluationResultMapper.selectEvaluationResultByResultId(resultId);
    }

    /**
     * 查询心理测评结果列表
     *
     * @param evaluationResult 心理测评结果
     * @return 心理测评结果
     */
    @Override
    public List<EvaluationResult> selectEvaluationResultList(EvaluationResult evaluationResult) {
        return evaluationResultMapper.selectEvaluationResultList(evaluationResult);
    }

    /**
     * 新增心理测评结果
     *
     * @param evaluationResult 心理测评结果
     * @return 结果
     */
    @Override
    public int insertEvaluationResult(EvaluationResult evaluationResult) {
        evaluationResult.setCreateTime(DateUtils.getNowDate());
        return evaluationResultMapper.insertEvaluationResult(evaluationResult);
    }

    /**
     * 修改心理测评结果
     *
     * @param evaluationResult 心理测评结果
     * @return 结果
     */
    @Override
    public int updateEvaluationResult(EvaluationResult evaluationResult) {
        evaluationResult.setUpdateTime(DateUtils.getNowDate());
        return evaluationResultMapper.updateEvaluationResult(evaluationResult);
    }

    /**
     * 批量删除心理测评结果
     *
     * @param resultIds 需要删除的心理测评结果主键
     * @return 结果
     */
    @Override
    public int deleteEvaluationResultByResultIds(Long[] resultIds) {
        return evaluationResultMapper.deleteEvaluationResultByResultIds(resultIds);
    }

    /**
     * 删除心理测评结果信息
     *
     * @param resultId 心理测评结果主键
     * @return 结果
     */
    @Override
    public int deleteEvaluationResultByResultId(Long resultId) {
        return evaluationResultMapper.deleteEvaluationResultByResultId(resultId);
    }
}
