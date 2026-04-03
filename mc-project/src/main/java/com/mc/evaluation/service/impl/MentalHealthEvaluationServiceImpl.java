package com.mc.evaluation.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mc.ai.service.IAiChatService;
import com.mc.common.utils.DateUtils;
import com.mc.evaluation.domain.EvaluationResult;
import com.mc.evaluation.domain.QuestionnaireAnswer;
import com.mc.evaluation.mapper.EvaluationResultMapper;
import com.mc.evaluation.mapper.QuestionnaireAnswerMapper;
import com.mc.evaluation.service.IMentalHealthEvaluationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 心理健康评测服务实现类
 */
@Service
@Slf4j
public class MentalHealthEvaluationServiceImpl implements IMentalHealthEvaluationService {

    @Resource
    private IAiChatService aiChatService;

    @Resource
    private EvaluationResultMapper evaluationResultMapper;

    @Resource
    private QuestionnaireAnswerMapper questionnaireAnswerMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    /**
     * 异步评估心理健康状态
     *
     * @param resultId 测评结果ID
     */
    @Async("threadPoolTaskExecutor")
    @Override
    public void asyncEvaluateMentalHealth(Long resultId) {
        log.info("开始心理健康评估 - 测评结果ID: {}", resultId);

        try {
            // 获取问卷答案
            List<QuestionnaireAnswer> answers = questionnaireAnswerMapper.selectByResultId(resultId);
            if (answers == null || answers.isEmpty()) {
                log.warn("没有找到问卷 - 测评结果ID: {}", resultId);
                return;
            }

            // 构建问卷答案字符串
            String questionnaireContent = buildQuestionnaireContent(answers);
            log.info("问卷内容构建完成 - 共 {} 道题目", answers.size());

            // 调用AI进行评估（通过 mc-ai 模块的 IAiChatService 统一接口）
            IAiChatService.MentalHealthEvaluationResult evaluationResult = aiChatService
                    .evaluateMentalHealth(questionnaireContent);

            log.info("AI评估完成 - 测评结果ID: {}, 风险等级: {}", resultId, evaluationResult.riskLevel());

            // 更新数据库中的测评结果
            updateEvaluationResult(resultId, evaluationResult);
        } catch (Exception e) {
            log.error("心理健康评估失败 - 测评结果ID: {}", resultId, e);
        }
    }

    /**
     * 构建问卷内容字符串
     *
     * @param answers 问卷答案列表
     * @return 问卷内容字符串
     */
    private String buildQuestionnaireContent(List<QuestionnaireAnswer> answers) {
        return answers.stream()
                .map(answer -> {
                    String question = "题目: " + answer.getContent();
                    String type = "类型: " + ("choice".equals(answer.getType()) ? "选择题" : "简答题");
                    String userAnswer = "用户回答: " + answer.getUserAnswer();
                    return String.format("%s\n%s\n%s\n", question, type, userAnswer);
                })
                .collect(Collectors.joining("\n"));
    }

    /**
     * 更新评估结果到数据库
     *
     * @param resultId           测评结果ID
     * @param evaluationResult   AI评估结果（来自 mc-ai 模块的 record）
     */
    private void updateEvaluationResult(Long resultId, IAiChatService.MentalHealthEvaluationResult evaluationResult) {
        try {
            EvaluationResult dbResult = evaluationResultMapper.selectEvaluationResultByResultId(resultId);
            if (dbResult != null) {
                // 更新AI分析结果（包含总分、各指标分值、评估建议等完整信息）
                String aiAnalysisJson = objectMapper.writeValueAsString(evaluationResult);
                dbResult.setAiAnalysis(aiAnalysisJson);
                
                // 更新风险等级（从AI评估结果中获取）
                dbResult.setRiskLevel(evaluationResult.riskLevel());
                // 同步综合得分到表字段（便于列表/统计/SQL 查询；详情仍以 ai_analysis JSON 为准）
                dbResult.setTotalScore(evaluationResult.totalScore());
                
                // 更新AI分析状态为已完成
                dbResult.setAiStatus("1");
                if (dbResult.getCreateTime() == null) {
                    dbResult.setCreateTime(DateUtils.getNowDate());
                }
                dbResult.setUpdateTime(DateUtils.getNowDate());

                evaluationResultMapper.updateEvaluationResult(dbResult);
                log.info("更新评估结果到数据库 - 测评结果ID: {}, 总得分: {}, 风险等级: {}",
                        resultId, evaluationResult.totalScore(), evaluationResult.riskLevel());
                
                // 发布评测结果更新事件，触发干预通知生成
                eventPublisher.publishEvent(resultId);
            } else {
                log.warn("未找到对应的测评结果记录 - 测评结果ID: {}", resultId);
            }
        } catch (Exception e) {
            log.error("更新评估结果到数据库失败 - 测评结果ID: {}", resultId, e);
        }
    }
}
