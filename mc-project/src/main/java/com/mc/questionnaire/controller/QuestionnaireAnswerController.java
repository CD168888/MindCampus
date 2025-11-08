package com.mc.questionnaire.controller;

import com.mc.common.annotation.Log;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.AjaxResult;
import com.mc.common.core.page.TableDataInfo;
import com.mc.common.enums.BusinessType;
import com.mc.common.utils.poi.ExcelUtil;
import com.mc.questionnaire.domain.QuestionnaireAnswer;
import com.mc.questionnaire.service.IQuestionnaireAnswerService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 心理测评答题记录Controller
 *
 * @author caidu
 * @date 2025-11-08
 */
@RestController
@RequestMapping("/questionnaireAnswer/questionnaireAnswer")
public class QuestionnaireAnswerController extends BaseController {
    @Autowired
    private IQuestionnaireAnswerService questionnaireAnswerService;

    /**
     * 查询心理测评答题记录列表
     */
    @PreAuthorize("@ss.hasPermi('questionnaireAnswer:questionnaireAnswer:list')")
    @GetMapping("/list")
    public TableDataInfo list(QuestionnaireAnswer questionnaireAnswer) {
        startPage();
        List<QuestionnaireAnswer> list = questionnaireAnswerService.selectQuestionnaireAnswerList(questionnaireAnswer);
        return getDataTable(list);
    }

    /**
     * 导出心理测评答题记录列表
     */
    @PreAuthorize("@ss.hasPermi('questionnaireAnswer:questionnaireAnswer:export')")
    @Log(title = "心理测评答题记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, QuestionnaireAnswer questionnaireAnswer) {
        List<QuestionnaireAnswer> list = questionnaireAnswerService.selectQuestionnaireAnswerList(questionnaireAnswer);
        ExcelUtil<QuestionnaireAnswer> util = new ExcelUtil<QuestionnaireAnswer>(QuestionnaireAnswer.class);
        util.exportExcel(response, list, "心理测评答题记录数据");
    }

    /**
     * 获取心理测评答题记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('questionnaireAnswer:questionnaireAnswer:query')")
    @GetMapping(value = "/{answerId}")
    public AjaxResult getInfo(@PathVariable("answerId") Long answerId) {
        return success(questionnaireAnswerService.selectQuestionnaireAnswerByAnswerId(answerId));
    }

    /**
     * 新增心理测评答题记录
     */
    @PreAuthorize("@ss.hasPermi('questionnaireAnswer:questionnaireAnswer:add')")
    @Log(title = "心理测评答题记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody QuestionnaireAnswer questionnaireAnswer) {
        return toAjax(questionnaireAnswerService.insertQuestionnaireAnswer(questionnaireAnswer));
    }

    /**
     * 修改心理测评答题记录
     */
    @PreAuthorize("@ss.hasPermi('questionnaireAnswer:questionnaireAnswer:edit')")
    @Log(title = "心理测评答题记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody QuestionnaireAnswer questionnaireAnswer) {
        return toAjax(questionnaireAnswerService.updateQuestionnaireAnswer(questionnaireAnswer));
    }

    /**
     * 删除心理测评答题记录
     */
    @PreAuthorize("@ss.hasPermi('questionnaireAnswer:questionnaireAnswer:remove')")
    @Log(title = "心理测评答题记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{answerIds}")
    public AjaxResult remove(@PathVariable Long[] answerIds) {
        return toAjax(questionnaireAnswerService.deleteQuestionnaireAnswerByAnswerIds(answerIds));
    }
}

