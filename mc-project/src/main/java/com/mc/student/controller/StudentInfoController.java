package com.mc.student.controller;

import com.mc.common.annotation.Log;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.R;
import com.mc.common.core.page.TableDataInfo;
import com.mc.common.enums.BusinessType;
import com.mc.common.utils.poi.ExcelUtil;
import com.mc.student.domain.Student;
import com.mc.student.service.IStudentInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public TableDataInfo<List<Student>> list(
            @Parameter(description = "学生信息查询条件") Student student) {
        startPage();
        List<Student> list = studentInfoService.selectStudentInfoList(student);
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
            @Parameter(description = "学生信息查询条件") Student student) {
        List<Student> list = studentInfoService.selectStudentInfoList(student);
        ExcelUtil<Student> util = new ExcelUtil<Student>(Student.class);
        util.exportExcel(response, list, "学生信息数据");
    }

    /**
     * 获取学生信息详细信息
     */
    @Operation(summary = "获取学生信息详细信息")
    @PreAuthorize("@ss.hasPermi('student:info:query')")
    @GetMapping(value = "/{studentId}")
    public R<Student> getInfo(
            @Parameter(description = "学生ID") @PathVariable("studentId") Long studentId) {
        return R.ok(studentInfoService.selectStudentInfoByStudentId(studentId));
    }

    /**
     * 新增学生信息
     */
    @Operation(summary = "新增学生信息")
    @PreAuthorize("@ss.hasPermi('student:info:add')")
    @Log(title = "学生信息", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Integer> add(
            @Parameter(description = "学生信息实体") @RequestBody Student student) {
        return R.ok(studentInfoService.insertStudentInfo(student));
    }

    /**
     * 修改学生信息
     */
    @Operation(summary = "修改学生信息")
    @PreAuthorize("@ss.hasPermi('student:info:edit')")
    @Log(title = "学生信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Integer> edit(
            @Parameter(description = "学生信息实体") @RequestBody Student student) {
        return R.ok(studentInfoService.updateStudentInfo(student));
    }

    /**
     * 删除学生信息
     */
    @Operation(summary = "删除学生信息")
    @PreAuthorize("@ss.hasPermi('student:info:remove')")
    @Log(title = "学生信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{studentIds}")
    public R<Integer> remove(
            @Parameter(description = "学生ID数组") @PathVariable Long[] studentIds) {
        return R.ok(studentInfoService.deleteStudentInfoByStudentIds(studentIds));
    }

    /**
     * 查询未绑定的用户ID/昵称
     */
    @Operation(summary = "查询未绑定的用户ID/昵称列表")
    @PreAuthorize("@ss.hasPermi('student:info:listUnbindUsers')")
    @GetMapping("/listUnbindUsers")
    public R<List<Map<String, Object>>> listUnbindUserIds() {
        List<Map<String, Object>> maps = studentInfoService.listUnbindUserInfos();
        return R.ok(maps);
    }
}
