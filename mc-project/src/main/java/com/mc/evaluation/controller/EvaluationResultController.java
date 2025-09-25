package com.mc.evaluation.controller;

import com.mc.common.annotation.Log;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.AjaxResult;
import com.mc.common.core.page.TableDataInfo;
import com.mc.common.enums.BusinessType;
import com.mc.common.utils.poi.ExcelUtil;
import com.mc.evaluation.domain.EvaluationResult;
import com.mc.evaluation.service.IEvaluationResultService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 心理测评结果Controller
 *
 * @author caidu
 * @date 2025-09-25
 */
@RestController
@RequestMapping("/evaluation/evaluationResult")
public class EvaluationResultController extends BaseController {
    @Autowired
    private IEvaluationResultService evaluationResultService;

    /**
     * 查询心理测评结果列表
     */
    @PreAuthorize("@ss.hasPermi('evaluation:evaluationResult:list')")
    @GetMapping("/list")
    public TableDataInfo list(EvaluationResult evaluationResult) {
        startPage();
        List<EvaluationResult> list = evaluationResultService.selectEvaluationResultList(evaluationResult);
        return getDataTable(list);
    }

    /**
     * 导出心理测评结果列表
     */
    @PreAuthorize("@ss.hasPermi('evaluation:evaluationResult:export')")
    @Log(title = "心理测评结果", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EvaluationResult evaluationResult) {
        List<EvaluationResult> list = evaluationResultService.selectEvaluationResultList(evaluationResult);
        ExcelUtil<EvaluationResult> util = new ExcelUtil<EvaluationResult>(EvaluationResult.class);
        util.exportExcel(response, list, "心理测评结果数据");
    }

    /**
     * 获取心理测评结果详细信息
     */
    @PreAuthorize("@ss.hasPermi('evaluation:evaluationResult:query')")
    @GetMapping(value = "/{resultId}")
    public AjaxResult getInfo(@PathVariable("resultId") Long resultId) {
        return success(evaluationResultService.selectEvaluationResultByResultId(resultId));
    }

    /**
     * 新增心理测评结果
     */
    @PreAuthorize("@ss.hasPermi('evaluation:evaluationResult:add')")
    @Log(title = "心理测评结果", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EvaluationResult evaluationResult) {
        return toAjax(evaluationResultService.insertEvaluationResult(evaluationResult));
    }

    /**
     * 修改心理测评结果
     */
    @PreAuthorize("@ss.hasPermi('evaluation:evaluationResult:edit')")
    @Log(title = "心理测评结果", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EvaluationResult evaluationResult) {
        return toAjax(evaluationResultService.updateEvaluationResult(evaluationResult));
    }

    /**
     * 删除心理测评结果
     */
    @PreAuthorize("@ss.hasPermi('evaluation:evaluationResult:remove')")
    @Log(title = "心理测评结果", businessType = BusinessType.DELETE)
    @DeleteMapping("/{resultIds}")
    public AjaxResult remove(@PathVariable Long[] resultIds) {
        return toAjax(evaluationResultService.deleteEvaluationResultByResultIds(resultIds));
    }
}
