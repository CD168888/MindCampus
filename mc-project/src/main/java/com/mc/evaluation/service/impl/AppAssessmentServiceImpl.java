package com.mc.evaluation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mc.common.exception.ServiceException;
import com.mc.common.utils.StringUtils;
import com.mc.evaluation.domain.EvaluationResult;
import com.mc.evaluation.domain.QuestionnaireAnswer;
import com.mc.evaluation.domain.dto.SubmitAnswerDTO;
import com.mc.evaluation.domain.vo.AssessmentStatisticsVO;
import com.mc.evaluation.domain.vo.EvaluationResultVO;
import com.mc.evaluation.domain.vo.QuestionnaireItemVO;
import com.mc.evaluation.domain.vo.QuestionnaireListVO;
import com.mc.evaluation.mapper.EvaluationResultMapper;
import com.mc.evaluation.mapper.QuestionnaireAnswerMapper;
import com.mc.evaluation.service.IAppAssessmentService;
import com.mc.questionnaire.domain.Question;
import com.mc.questionnaire.domain.Questionnaire;
import com.mc.questionnaire.mapper.QuestionMapper;
import com.mc.questionnaire.mapper.QuestionnaireMapper;
import com.mc.student.domain.Student;
import com.mc.student.mapper.StudentInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * APP端心理测评Service实现
 *
 * @author caidu
 * @date 2025-11-04
 */
@Service
@RequiredArgsConstructor
public class AppAssessmentServiceImpl implements IAppAssessmentService {

    private final QuestionnaireMapper questionnaireMapper;
    private final QuestionMapper questionMapper;
    private final EvaluationResultMapper evaluationResultMapper;
    private final QuestionnaireAnswerMapper answerMapper;
    private final StudentInfoMapper studentInfoMapper;

    @Override
    public AssessmentStatisticsVO getStatistics(Long userId) {
        // 通过userId查询studentId
        Long studentId = getStudentIdByUserId(userId);

        AssessmentStatisticsVO statistics = new AssessmentStatisticsVO();

        // 查询该学生所有测评记录
        EvaluationResult queryParam = new EvaluationResult();
        queryParam.setStudentId(studentId);
        List<EvaluationResult> allResults = evaluationResultMapper.selectEvaluationResultList(queryParam);

        // 按问卷ID分组，每个问卷只取最新的一条记录（resultId最大的）
        Map<Long, EvaluationResult> latestResultMap = new HashMap<>();
        for (EvaluationResult result : allResults) {
            Long qId = result.getQuestionnaireId();
            if (!latestResultMap.containsKey(qId) ||
                    result.getResultId() > latestResultMap.get(qId).getResultId()) {
                latestResultMap.put(qId, result);
            }
        }

        // 统计待完成和已完成数量
        int pending = 0;
        int completed = 0;

        for (EvaluationResult result : latestResultMap.values()) {
            if ("1".equals(result.getCompletionStatus())) {
                completed++;
            } else {
                pending++;
            }
        }

        statistics.setPending(pending);
        statistics.setCompleted(completed);
        statistics.setTotal(latestResultMap.size());

        return statistics;
    }

    @Override
    public QuestionnaireListVO listQuestionnaires(Long userId) {
        // 通过userId查询studentId
        Long studentId = getStudentIdByUserId(userId);

        QuestionnaireListVO result = new QuestionnaireListVO();

        // 查询该学生所有测评记录
        EvaluationResult queryParam = new EvaluationResult();
        queryParam.setStudentId(studentId);
        List<EvaluationResult> allResults = evaluationResultMapper.selectEvaluationResultList(queryParam);

        // 按问卷ID分组，每个问卷只取最新的一条记录（resultId最大的）
        Map<Long, EvaluationResult> latestResultMap = new HashMap<>();
        for (EvaluationResult result1 : allResults) {
            Long qId = result1.getQuestionnaireId();
            if (!latestResultMap.containsKey(qId) ||
                    result1.getResultId() > latestResultMap.get(qId).getResultId()) {
                latestResultMap.put(qId, result1);
            }
        }

        // 分离待完成和已完成
        List<QuestionnaireItemVO> pendingList = new ArrayList<>();
        List<QuestionnaireItemVO> completedList = new ArrayList<>();
        Date now = new Date();

        for (EvaluationResult evalResult : latestResultMap.values()) {
            // 查询问卷信息
            Questionnaire questionnaire = questionnaireMapper.selectById(evalResult.getQuestionnaireId());
            if (questionnaire == null) {
                continue;
            }

            QuestionnaireItemVO item = convertToItemVO(questionnaire);
            item.setResultId(evalResult.getResultId()); // 设置评测结果ID
            item.setAiStatus(evalResult.getAiStatus()); // 设置AI分析状态

            if ("1".equals(evalResult.getCompletionStatus())) {
                // 已完成
                item.setCompletedTime(
                        evalResult.getUpdateTime() != null ? evalResult.getUpdateTime() : evalResult.getCreateTime());
                item.setScore(evalResult.getTotalScore());
                item.setRiskLevel(evalResult.getRiskLevel());

                // 判断AI分析状态
                if ("1".equals(evalResult.getAiStatus())) {
                    // AI已分析完成
                    item.setStatus("completed");
                    completedList.add(item);
                } else {
                    // AI未分析，待分析
                    item.setStatus("analyzing");
                    completedList.add(item); // 待分析也放在已完成列表中，但状态不同
                }
            } else {
                // 未完成，判断是否已截止
                item.setDeadline(questionnaire.getEndTime());

                if (questionnaire.getEndTime() != null && now.after(questionnaire.getEndTime())) {
                    // 已超过截止时间
                    item.setStatus("expired");
                    pendingList.add(item);
                } else {
                    // 未截止，待填写
                    item.setStatus("pending");
                    pendingList.add(item);
                }
            }
        }

        result.setPendingList(pendingList);
        result.setCompletedList(completedList);

        return result;
    }

    @Override
    public com.mc.questionnaire.domain.vo.QuestionnaireVO getQuestionnaireDetail(Long userId, Long questionnaireId) {
        // 通过userId查询studentId
        Long studentId = getStudentIdByUserId(userId);

        // 查询问卷基本信息
        Questionnaire questionnaire = questionnaireMapper.selectById(questionnaireId);
        if (questionnaire == null) {
            throw new ServiceException("问卷不存在");
        }

        // 标记为已读（如果该学生有此问卷的测评记录）
        EvaluationResult queryResult = new EvaluationResult();
        queryResult.setStudentId(studentId);
        queryResult.setQuestionnaireId(questionnaireId);
        List<EvaluationResult> existingResults = evaluationResultMapper.selectEvaluationResultList(queryResult);

        if (!existingResults.isEmpty()) {
            // 如果有多条记录，取最新的一条（resultId最大的）
            EvaluationResult result = existingResults.stream()
                    .max((r1, r2) -> Long.compare(r1.getResultId(), r2.getResultId()))
                    .orElse(existingResults.get(0));
            // 只有未完成的才需要标记已读
            if (!"1".equals(result.getCompletionStatus()) && !"1".equals(result.getReadStatus())) {
                result.setReadStatus("1");
                evaluationResultMapper.updateEvaluationResult(result);
            }
        }

        // 查询问卷题目
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getQuestionnaireId, questionnaireId);
        wrapper.orderByAsc(Question::getOrderNum);
        List<Question> questions = questionMapper.selectList(wrapper);

        // 组装VO
        com.mc.questionnaire.domain.vo.QuestionnaireVO vo = new com.mc.questionnaire.domain.vo.QuestionnaireVO();
        vo.setQuestionnaireId(questionnaire.getQuestionnaireId());
        vo.setTitle(questionnaire.getTitle());
        vo.setDescription(questionnaire.getDescription());
        vo.setStatus(questionnaire.getStatus());
        vo.setType(questionnaire.getType());
        vo.setTotalScore(questionnaire.getTotalScore());
        vo.setStartTime(questionnaire.getStartTime());
        vo.setEndTime(questionnaire.getEndTime());
        vo.setQuestions(questions);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitAnswer(Long userId, SubmitAnswerDTO dto) {
        // 通过userId查询studentId
        Long studentId = getStudentIdByUserId(userId);

        // 检查是否已经提交过
        if (checkCompleted(userId, dto.getQuestionnaireId())) {
            throw new ServiceException("该问卷已经提交过，不能重复提交");
        }

        // 查询问卷信息
        Questionnaire questionnaire = questionnaireMapper.selectById(dto.getQuestionnaireId());
        if (questionnaire == null) {
            throw new ServiceException("问卷不存在");
        }

        // 查询学生信息
        Student student = studentInfoMapper.selectById(studentId);
        if (student == null) {
            throw new ServiceException("学生信息不存在");
        }

        // 查询问卷题目
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getQuestionnaireId, dto.getQuestionnaireId());
        List<Question> questions = questionMapper.selectList(wrapper);

        // 构建题目映射
        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getQuestionId, q -> q));

        // 计算得分
        int totalScore = 0;
        List<QuestionnaireAnswer> answerList = new ArrayList<>();

        for (SubmitAnswerDTO.AnswerItem answerItem : dto.getAnswers()) {
            Question question = questionMap.get(answerItem.getQuestionId());
            if (question == null) {
                continue;
            }

            QuestionnaireAnswer answer = new QuestionnaireAnswer();
            answer.setQuestionnaireId(dto.getQuestionnaireId());
            answer.setQuestionId(answerItem.getQuestionId());

            // 冗余字段
            answer.setType(question.getType());
            answer.setContent(question.getContent());
            answer.setOptions(question.getOptions());
            answer.setStandardAnswer(question.getStandardAnswer());
            answer.setScore(question.getScore());

            // 用户作答
            answer.setUserAnswer(answerItem.getUserAnswer());

            // 判断是否正确（仅选择题）
            if ("choice".equals(question.getType()) && StringUtils.isNotEmpty(question.getStandardAnswer())) {
                boolean isCorrect = question.getStandardAnswer().equalsIgnoreCase(answerItem.getUserAnswer());
                answer.setIsCorrect(isCorrect ? 1 : 2);
                answer.setObtainScore(isCorrect ? question.getScore() : 0);
                totalScore += answer.getObtainScore();
            } else {
                // 简答题默认给分
                answer.setIsCorrect(3);
                answer.setObtainScore(question.getScore());
                totalScore += question.getScore();
            }

            answerList.add(answer);
        }

        // 评估风险等级
        String riskLevel = calculateRiskLevel(totalScore, questionnaire.getTotalScore());

        // 查询该学生的测评记录（发送时已创建）
        EvaluationResult queryResult = new EvaluationResult();
        queryResult.setStudentId(studentId);
        queryResult.setQuestionnaireId(dto.getQuestionnaireId());
        List<EvaluationResult> existingResults = evaluationResultMapper.selectEvaluationResultList(queryResult);

        EvaluationResult result;
        if (!existingResults.isEmpty()) {
            // 如果有多条记录，取最新的一条（resultId最大的）
            result = existingResults.stream()
                    .max((r1, r2) -> Long.compare(r1.getResultId(), r2.getResultId()))
                    .orElse(existingResults.get(0));
            result.setStudentName(student.getName());
            result.setQuestionnaireTitle(questionnaire.getTitle());
            result.setTotalScore((long) totalScore);
            result.setRiskLevel(riskLevel);
            result.setCompletionStatus("1"); // 1已完成
            result.setAiStatus("0"); // 0未完成
            result.setReadStatus("1"); // 1已读（提交时标记为已读）
            evaluationResultMapper.updateEvaluationResult(result);
        } else {
            // 如果没有记录（异常情况），则插入新记录
            result = new EvaluationResult();
            result.setStudentId(studentId);
            result.setStudentName(student.getName());
            result.setQuestionnaireId(dto.getQuestionnaireId());
            result.setQuestionnaireTitle(questionnaire.getTitle());
            result.setTotalScore((long) totalScore);
            result.setRiskLevel(riskLevel);
            result.setCompletionStatus("1"); // 1已完成
            result.setAiStatus("0"); // 0未完成
            result.setReadStatus("1"); // 1已读（提交时标记为已读）
            evaluationResultMapper.insertEvaluationResult(result);
        }

        // 批量保存答题记录
        for (QuestionnaireAnswer answer : answerList) {
            answer.setResultId(result.getResultId());
        }
        answerMapper.batchInsert(answerList);

        return result.getResultId();
    }

    @Override
    public EvaluationResultVO getResultDetail(Long userId, Long resultId) {
        // 通过userId查询studentId
        Long studentId = getStudentIdByUserId(userId);

        // 查询测评结果
        EvaluationResult result = evaluationResultMapper.selectEvaluationResultByResultId(resultId);
        if (result == null || !result.getStudentId().equals(studentId)) {
            throw new ServiceException("测评结果不存在或无权限查看");
        }

        // 查询答题记录
        List<QuestionnaireAnswer> answers = answerMapper.selectByResultId(resultId);

        // 计算统计信息
        int totalQuestions = answers.size();
        int correctCount = (int) answers.stream()
                .filter(a -> a.getIsCorrect() != null && a.getIsCorrect() == 1)
                .count();

        String accuracy = totalQuestions > 0
                ? String.format("%.2f%%", (correctCount * 100.0 / totalQuestions))
                : "0%";

        // 组装VO
        EvaluationResultVO vo = new EvaluationResultVO();
        vo.setResult(result);
        vo.setAnswers(answers);
        vo.setTotalQuestions(totalQuestions);
        vo.setCorrectCount(correctCount);
        vo.setAccuracy(accuracy);

        return vo;
    }

    @Override
    public List<EvaluationResult> listMyResults(Long userId) {
        // 通过userId查询studentId
        Long studentId = getStudentIdByUserId(userId);

        EvaluationResult queryParam = new EvaluationResult();
        queryParam.setStudentId(studentId);
        queryParam.setCompletionStatus("1"); // 只查询已完成的
        List<EvaluationResult> list = evaluationResultMapper.selectEvaluationResultList(queryParam);
        // 按创建时间倒序排列
        list.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));
        return list;
    }

    @Override
    public boolean checkCompleted(Long userId, Long questionnaireId) {
        // 通过userId查询studentId
        Long studentId = getStudentIdByUserId(userId);

        EvaluationResult queryParam = new EvaluationResult();
        queryParam.setStudentId(studentId);
        queryParam.setQuestionnaireId(questionnaireId);
        List<EvaluationResult> list = evaluationResultMapper.selectEvaluationResultList(queryParam);

        // 检查是否有已完成的记录
        return list.stream().anyMatch(r -> "1".equals(r.getCompletionStatus()));
    }

    /**
     * 通过userId查询studentId
     */
    private Long getStudentIdByUserId(Long userId) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getUserId, userId);
        Student student = studentInfoMapper.selectOne(wrapper);

        if (student == null) {
            throw new ServiceException("学生信息不存在，请先完善学生档案");
        }

        return student.getStudentId();
    }

    /**
     * 转换为列表项VO
     */
    private QuestionnaireItemVO convertToItemVO(Questionnaire q) {
        QuestionnaireItemVO item = new QuestionnaireItemVO();
        item.setQuestionnaireId(q.getQuestionnaireId());
        item.setTitle(q.getTitle());
        item.setDescription(q.getDescription());
        item.setType(q.getType());
        item.setPublisher(q.getCreateBy());

        // 查询题目数量
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getQuestionnaireId, q.getQuestionnaireId());
        Long count = questionMapper.selectCount(wrapper);
        item.setQuestionCount(count.intValue());

        // 预估时长：平均每题1分钟
        item.setDuration(count.intValue());

        return item;
    }

    /**
     * 计算风险等级
     */
    private String calculateRiskLevel(int score, Integer totalScore) {
        if (totalScore == null || totalScore == 0) {
            return "低";
        }

        double percentage = (double) score / totalScore;

        if (percentage >= 0.8) {
            return "低";
        } else if (percentage >= 0.5) {
            return "中";
        } else {
            return "高";
        }
    }
}
