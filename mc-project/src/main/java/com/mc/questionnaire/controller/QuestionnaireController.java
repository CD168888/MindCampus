package com.mc.questionnaire.controller;

import com.mc.common.annotation.Log;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.R;
import com.mc.common.core.page.TableDataInfo;
import com.mc.common.enums.BusinessType;
import com.mc.questionnaire.domain.Question;
import com.mc.questionnaire.domain.Questionnaire;
import com.mc.questionnaire.domain.dto.QuestionnaireDTO;
import com.mc.questionnaire.service.IQuestionnaireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生信息Controller
 *
 * @author caidu
 * @date 2025-09-21
 */
@Tag(name = "问卷信息管理")
@RestController
@RequestMapping("/questionnaire/info")
@RequiredArgsConstructor
public class QuestionnaireController extends BaseController {

    private final IQuestionnaireService questionnaireService;

    /**
     * 查询问卷列表
     */
    @Operation(summary = "查询问卷列表")
    @PreAuthorize("@ss.hasPermi('questionnaire:questionnaireinfo:list')")
    @GetMapping("/list")
    public TableDataInfo<List<Questionnaire>> list(Questionnaire questionnaire) {
        startPage();
        List<Questionnaire> list = questionnaireService.selectQuestionnaireList(questionnaire);
        return getDataTable(list);
    }

    /**
     * 新增/修改问卷
     */
    @Operation(summary = "新增/修改问卷")
    @PreAuthorize("@ss.hasPermi('questionnaire:questionnaireinfo:edit')")
    @Log(title = "心理测评问卷管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R save(@RequestBody QuestionnaireDTO questionnaireDTO) {
        questionnaireService.saveQuestionnaire(questionnaireDTO);
        return R.ok();
    }

    /**
     * 查询问卷题目列表
     */
    @Operation(summary = "查询问卷题目列表")
    @PreAuthorize("@ss.hasPermi('questionnaire:questionnaireinfo:query')")
    @GetMapping("/questions/{questionnaireId}")
    public R<List<Question>> questionList(@PathVariable Long questionnaireId) {
        List<Question> list = questionnaireService.selectQuestionByQuestionnaireId(questionnaireId);
        return R.ok(list);
    }

    /**
     * 删除问卷及题目
     */
    @Operation(summary = "删除问卷及题目")
    @PreAuthorize("@ss.hasPermi('questionnaire:questionnaireinfo:remove')")
    @Log(title = "心理测评问卷管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{questionnaireIds}")
    public R remove(@PathVariable Long[] questionnaireIds) {
        questionnaireService.deleteQuestionnaire(questionnaireIds);
        return R.ok();
    }
}