package com.mc.evaluation.controller;

import com.mc.common.annotation.Log;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.R;
import com.mc.common.enums.BusinessType;
import com.mc.common.utils.SecurityUtils;
import com.mc.evaluation.domain.EvaluationResult;
import com.mc.evaluation.domain.dto.SubmitAnswerDTO;
import com.mc.evaluation.domain.vo.AssessmentStatisticsVO;
import com.mc.evaluation.domain.vo.EvaluationResultVO;
import com.mc.evaluation.domain.vo.QuestionnaireListVO;
import com.mc.evaluation.service.IAppAssessmentService;
import com.mc.questionnaire.domain.vo.QuestionnaireVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * APP端心理测评Controller
 *
 * @author caidu
 * @date 2025-11-04
 */
@Tag(name = "APP端心理测评")
@RestController
@RequestMapping("/app/assessment")
@RequiredArgsConstructor
public class AppAssessmentController extends BaseController {

    private final IAppAssessmentService appAssessmentService;

    /**
     * 获取测评统计数据
     */
    @Operation(summary = "获取测评统计数据")
    @GetMapping("/statistics")
    public R<AssessmentStatisticsVO> getStatistics() {
        Long userId = SecurityUtils.getUserId();
        AssessmentStatisticsVO statistics = appAssessmentService.getStatistics(userId);
        return R.ok(statistics);
    }

    /**
     * 查询可用问卷列表（待填+已完成）
     */
    @Operation(summary = "查询问卷列表")
    @GetMapping("/questionnaires")
    public R<QuestionnaireListVO> listQuestionnaires() {
        Long userId = SecurityUtils.getUserId();
        QuestionnaireListVO result = appAssessmentService.listQuestionnaires(userId);
        return R.ok(result);
    }

    /**
     * 查询问卷详情（包含题目）
     */
    @Operation(summary = "查询问卷详情")
    @GetMapping("/questionnaire/{questionnaireId}")
    public R<QuestionnaireVO> getQuestionnaireDetail(@PathVariable Long questionnaireId) {
        Long userId = SecurityUtils.getUserId();
        QuestionnaireVO detail = appAssessmentService.getQuestionnaireDetail(userId, questionnaireId);
        return R.ok(detail);
    }

    /**
     * 提交答题
     */
    @Operation(summary = "提交答题")
    @Log(title = "提交测评答题", businessType = BusinessType.INSERT)
    @PostMapping("/submit")
    public R<Long> submitAnswer(@RequestBody SubmitAnswerDTO dto) {
        Long userId = SecurityUtils.getUserId();
        Long resultId = appAssessmentService.submitAnswer(userId, dto);
        return R.ok(resultId);
    }

    /**
     * 查询测评结果详情
     */
    @Operation(summary = "查询测评结果详情")
    @GetMapping("/result/{resultId}")
    public R<EvaluationResultVO> getResultDetail(@PathVariable Long resultId) {
        Long userId = SecurityUtils.getUserId();
        EvaluationResultVO detail = appAssessmentService.getResultDetail(userId, resultId);
        return R.ok(detail);
    }

    /**
     * 查询我的测评历史记录
     */
    @Operation(summary = "查询我的测评历史记录")
    @GetMapping("/my-results")
    public R<List<EvaluationResult>> listMyResults() {
        Long userId = SecurityUtils.getUserId();
        List<EvaluationResult> list = appAssessmentService.listMyResults(userId);
        return R.ok(list);
    }

    /**
     * 检查问卷是否已完成
     */
    @Operation(summary = "检查问卷是否已完成")
    @GetMapping("/check/{questionnaireId}")
    public R<Boolean> checkCompleted(@PathVariable Long questionnaireId) {
        Long userId = SecurityUtils.getUserId();
        boolean completed = appAssessmentService.checkCompleted(userId, questionnaireId);
        return R.ok(completed);
    }
}
