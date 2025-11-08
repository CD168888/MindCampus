package com.mc.evaluation.service.impl;

import com.mc.common.utils.DateUtils;
import com.mc.evaluation.domain.EvaluationResult;
import com.mc.evaluation.mapper.EvaluationResultMapper;
import com.mc.evaluation.mapper.QuestionnaireAnswerMapper;
import com.mc.evaluation.service.IEvaluationResultService;
import com.mc.questionnaire.domain.Questionnaire;
import com.mc.questionnaire.mapper.QuestionnaireMapper;
import com.mc.student.domain.Student;
import com.mc.student.mapper.StudentInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private StudentInfoMapper studentInfoMapper;
    @Autowired
    private QuestionnaireMapper questionnaireMapper;
    @Autowired
    @Qualifier("questionnaireAnswerMapper")  // 明确指定使用 evaluation 包下的 Mapper
    private QuestionnaireAnswerMapper questionnaireAnswerMapper;

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
        List<EvaluationResult> evaluationResults = evaluationResultMapper.selectEvaluationResultList(evaluationResult);

        return evaluationResults.stream()
                .peek(result -> {
                    Student student = studentInfoMapper.selectStudentInfoByStudentId(result.getStudentId());
                    if (student != null) {
                        result.setStudentName(student.getName());
                    }
                    Questionnaire questionnaire = questionnaireMapper.selectById(result.getQuestionnaireId());
                    if (questionnaire != null) {
                        result.setQuestionnaireTitle(questionnaire.getTitle());
                    }
                })
                .toList();
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
     * 同时级联删除关联的答题记录
     *
     * @param resultIds 需要删除的心理测评结果主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteEvaluationResultByResultIds(Long[] resultIds) {
        // 先删除关联的答题记录
        for (Long resultId : resultIds) {
            questionnaireAnswerMapper.deleteByResultId(resultId);
        }
        // 再删除测评结果
        return evaluationResultMapper.deleteEvaluationResultByResultIds(resultIds);
    }

    /**
     * 删除心理测评结果信息
     * 同时级联删除关联的答题记录
     *
     * @param resultId 心理测评结果主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteEvaluationResultByResultId(Long resultId) {
        // 先删除关联的答题记录
        questionnaireAnswerMapper.deleteByResultId(resultId);
        // 再删除测评结果
        return evaluationResultMapper.deleteEvaluationResultByResultId(resultId);
    }
}
