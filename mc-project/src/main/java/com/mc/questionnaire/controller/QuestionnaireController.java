package com.mc.questionnaire.controller;

import com.mc.common.annotation.Log;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.R;
import com.mc.common.core.domain.entity.SysUser;
import com.mc.common.core.page.TableDataInfo;
import com.mc.common.enums.BusinessType;
import com.mc.questionnaire.domain.Question;
import com.mc.questionnaire.domain.Questionnaire;
import com.mc.questionnaire.domain.dto.QuestionnaireDTO;
import com.mc.questionnaire.service.IQuestionnaireService;
import com.mc.student.domain.Student;
import com.mc.student.service.IStudentInfoService;
import com.mc.system.service.ISysUserService;
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

    private final ISysUserService sysUserService;

    private final IStudentInfoService studentInfoService;

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
    public R<Void> save(@RequestBody QuestionnaireDTO questionnaireDTO) {
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
    public R<Void> remove(@PathVariable Long[] questionnaireIds) {
        questionnaireService.deleteQuestionnaire(questionnaireIds);
        return R.ok();
    }

    /**
     * 发送问卷给指定部门学生
     */
    @Operation(summary = "发送问卷给指定部门学生")
    @PreAuthorize("@ss.hasPermi('questionnaire:questionnaireinfo:send')")
    @Log(title = "心理测评问卷管理", businessType = BusinessType.INSERT)
    @PostMapping("/send")
    public R<java.util.Map<String, Object>> send(@RequestParam(value = "questionnaireId") Long questionnaireId,
            @RequestParam(value = "deptId") Long deptId) {
        List<SysUser> sysUsers = sysUserService.selectUserListByDeptId(deptId);

        // 统计发送结果
        int totalUsers = sysUsers.size();
        int sentCount = 0;
        int skippedCount = 0;
        int noStudentCount = 0;

        for (SysUser sysUser : sysUsers) {
            // 通过用户ID查询学生信息
            Student student = new Student();
            student.setUserId(sysUser.getUserId());
            List<Student> students = studentInfoService.selectStudentInfoList(student);

            if (!students.isEmpty()) {
                Student studentInfo = students.get(0);
                boolean isSent = questionnaireService.sendQuestionnaire(questionnaireId, studentInfo.getStudentId());
                if (isSent) {
                    sentCount++;
                } else {
                    // 跳过的原因可能是：1.已存在记录 2.用户被禁用
                    // 这里统一计入skippedCount，包含了禁用用户
                    skippedCount++;
                }
            } else {
                noStudentCount++; // 用户未关联学生信息
            }
        }

        // 构建返回数据
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("totalUsers", totalUsers);
        result.put("sentCount", sentCount);
        result.put("skippedCount", skippedCount);
        result.put("noStudentCount", noStudentCount);
        result.put("message", String.format("发送完成：成功 %d 人，跳过 %d 人（已存在记录或用户被禁用），无学生信息 %d 人",
                sentCount, skippedCount, noStudentCount));

        // 返回详细的发送结果
        return R.ok(result);
    }
}