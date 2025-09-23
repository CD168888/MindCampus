package com.mc.questionnaire.controller;

import com.mc.common.annotation.Log;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.AjaxResult;
import com.mc.common.core.page.TableDataInfo;
import com.mc.common.enums.BusinessType;
import com.mc.common.utils.poi.ExcelUtil;
import com.mc.questionnaire.domain.QuestionBank;
import com.mc.questionnaire.service.IQuestionBankService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题库管理Controller
 *
 * @author caidu
 * @date 2025-09-23
 */
@RestController
@RequestMapping("/questionnaire/questionnairebank")
public class QuestionBankController extends BaseController {
    @Autowired
    private IQuestionBankService questionBankService;

    /**
     * 查询题库管理列表
     */
    @PreAuthorize("@ss.hasPermi('questionnaire:questionnairebank:list')")
    @GetMapping("/list")
    public TableDataInfo list(QuestionBank questionBank) {
        startPage();
        List<QuestionBank> list = questionBankService.selectQuestionBankList(questionBank);
        return getDataTable(list);
    }

    /**
     * 导出题库管理列表
     */
    @PreAuthorize("@ss.hasPermi('questionnaire:questionnairebank:export')")
    @Log(title = "题库管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, QuestionBank questionBank) {
        List<QuestionBank> list = questionBankService.selectQuestionBankList(questionBank);
        ExcelUtil<QuestionBank> util = new ExcelUtil<QuestionBank>(QuestionBank.class);
        util.exportExcel(response, list, "题库管理数据");
    }

    /**
     * 获取题库管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('questionnaire:questionnairebank:query')")
    @GetMapping(value = "/{bankId}")
    public AjaxResult getInfo(@PathVariable("bankId") Long bankId) {
        return success(questionBankService.selectQuestionBankByBankId(bankId));
    }

    /**
     * 新增题库管理
     */
    @PreAuthorize("@ss.hasPermi('questionnaire:questionnairebank:add')")
    @Log(title = "题库管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody QuestionBank questionBank) {
        return toAjax(questionBankService.insertQuestionBank(questionBank));
    }

    /**
     * 修改题库管理
     */
    @PreAuthorize("@ss.hasPermi('questionnaire:questionnairebank:edit')")
    @Log(title = "题库管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody QuestionBank questionBank) {
        return toAjax(questionBankService.updateQuestionBank(questionBank));
    }

    /**
     * 删除题库管理
     */
    @PreAuthorize("@ss.hasPermi('questionnaire:questionnairebank:remove')")
    @Log(title = "题库管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{bankIds}")
    public AjaxResult remove(@PathVariable Long[] bankIds) {
        return toAjax(questionBankService.deleteQuestionBankByBankIds(bankIds));
    }
}
