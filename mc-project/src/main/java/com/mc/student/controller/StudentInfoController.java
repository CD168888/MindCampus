package com.mc.student.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mc.common.annotation.Log;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.AjaxResult;
import com.mc.common.enums.BusinessType;
import com.mc.student.domain.StudentInfo;
import com.mc.student.service.IStudentInfoService;
import com.mc.common.utils.poi.ExcelUtil;
import com.mc.common.core.page.TableDataInfo;

/**
 * 学生信息Controller
 *
 * @author caidu
 * @date 2025-09-21
 */
@Tag(name = "学生信息管理")
@RestController
@RequestMapping("/student/info")
public class StudentInfoController extends BaseController {
    @Autowired
    private IStudentInfoService studentInfoService;

    /**
     * 查询学生信息列表
     */
    @Operation(summary = "查询学生信息列表")
    @PreAuthorize("@ss.hasPermi('student:info:list')")
    @GetMapping("/list")
    public TableDataInfo list(
            @Parameter(description = "学生信息查询条件") StudentInfo studentInfo) {
        startPage();
        List<StudentInfo> list = studentInfoService.selectStudentInfoList(studentInfo);
        return getDataTable(list);
    }

    /**
     * 导出学生信息列表
     */
    @Operation(summary = "导出学生信息列表")
    @PreAuthorize("@ss.hasPermi('student:info:export')")
    @Log(title = "学生信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(
            @Parameter(description = "HTTP响应对象") HttpServletResponse response,
            @Parameter(description = "学生信息查询条件") StudentInfo studentInfo) {
        List<StudentInfo> list = studentInfoService.selectStudentInfoList(studentInfo);
        ExcelUtil<StudentInfo> util = new ExcelUtil<StudentInfo>(StudentInfo.class);
        util.exportExcel(response, list, "学生信息数据");
    }

    /**
     * 获取学生信息详细信息
     */
    @Operation(summary = "获取学生信息详细信息")
    @PreAuthorize("@ss.hasPermi('student:info:query')")
    @GetMapping(value = "/{studentId}")
    public AjaxResult getInfo(
            @Parameter(description = "学生ID") @PathVariable("studentId") Long studentId) {
        return success(studentInfoService.selectStudentInfoByStudentId(studentId));
    }

    /**
     * 新增学生信息
     */
    @Operation(summary = "新增学生信息")
    @PreAuthorize("@ss.hasPermi('student:info:add')")
    @Log(title = "学生信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(
            @Parameter(description = "学生信息实体") @RequestBody StudentInfo studentInfo) {
        return toAjax(studentInfoService.insertStudentInfo(studentInfo));
    }

    /**
     * 修改学生信息
     */
    @Operation(summary = "修改学生信息")
    @PreAuthorize("@ss.hasPermi('student:info:edit')")
    @Log(title = "学生信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(
            @Parameter(description = "学生信息实体") @RequestBody StudentInfo studentInfo) {
        return toAjax(studentInfoService.updateStudentInfo(studentInfo));
    }

    /**
     * 删除学生信息
     */
    @Operation(summary = "删除学生信息")
    @PreAuthorize("@ss.hasPermi('student:info:remove')")
    @Log(title = "学生信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{studentIds}")
    public AjaxResult remove(
            @Parameter(description = "学生ID数组") @PathVariable Long[] studentIds) {
        return toAjax(studentInfoService.deleteStudentInfoByStudentIds(studentIds));
    }

    /**
     * 查询未绑定的用户ID列表
     */
    @Operation(summary = "查询未绑定的用户ID列表")
    @PreAuthorize("@ss.hasPermi('student:info:listUnbindUserIds')")
    @GetMapping("/listUnbindUserIds")
    public AjaxResult listUnbindUserIds() {
        List<String> list = studentInfoService.listUnbindUserIds();
        return success(list);
    }
}
