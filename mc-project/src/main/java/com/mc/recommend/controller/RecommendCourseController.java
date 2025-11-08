package com.mc.recommend.controller;

import com.mc.common.annotation.Log;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.AjaxResult;
import com.mc.common.core.page.TableDataInfo;
import com.mc.common.enums.BusinessType;
import com.mc.common.utils.poi.ExcelUtil;
import com.mc.recommend.domain.RecommendCourse;
import com.mc.recommend.service.IRecommendCourseService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 心理课程推荐Controller
 *
 * @author caidu
 * @date 2025-11-08
 */
@RestController
@RequestMapping("/recommend/recommendCourse")
public class RecommendCourseController extends BaseController {
    @Autowired
    private IRecommendCourseService recommendCourseService;

    /**
     * 查询心理课程推荐列表
     */
    @PreAuthorize("@ss.hasPermi('recommendCourse:recommendCourse:list')")
    @GetMapping("/list")
    public TableDataInfo<List<RecommendCourse>> list(RecommendCourse recommendCourse) {
        startPage();
        List<RecommendCourse> list = recommendCourseService.selectRecommendCourseList(recommendCourse);
        return getDataTable(list);
    }

    /**
     * 导出心理课程推荐列表
     */
    @PreAuthorize("@ss.hasPermi('recommendCourse:recommendCourse:export')")
    @Log(title = "心理课程推荐", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RecommendCourse recommendCourse) {
        List<RecommendCourse> list = recommendCourseService.selectRecommendCourseList(recommendCourse);
        ExcelUtil<RecommendCourse> util = new ExcelUtil<RecommendCourse>(RecommendCourse.class);
        util.exportExcel(response, list, "心理课程推荐数据");
    }

    /**
     * 获取心理课程推荐详细信息
     */
    @PreAuthorize("@ss.hasPermi('recommendCourse:recommendCourse:query')")
    @GetMapping(value = "/{courseId}")
    public AjaxResult getInfo(@PathVariable("courseId") Long courseId) {
        return success(recommendCourseService.selectRecommendCourseByCourseId(courseId));
    }

    /**
     * 新增心理课程推荐
     */
    @PreAuthorize("@ss.hasPermi('recommendCourse:recommendCourse:add')")
    @Log(title = "心理课程推荐", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RecommendCourse recommendCourse) {
        return toAjax(recommendCourseService.insertRecommendCourse(recommendCourse));
    }

    /**
     * 修改心理课程推荐
     */
    @PreAuthorize("@ss.hasPermi('recommendCourse:recommendCourse:edit')")
    @Log(title = "心理课程推荐", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RecommendCourse recommendCourse) {
        return toAjax(recommendCourseService.updateRecommendCourse(recommendCourse));
    }

    /**
     * 删除心理课程推荐
     */
    @PreAuthorize("@ss.hasPermi('recommendCourse:recommendCourse:remove')")
    @Log(title = "心理课程推荐", businessType = BusinessType.DELETE)
    @DeleteMapping("/{courseIds}")
    public AjaxResult remove(@PathVariable Long[] courseIds) {
        return toAjax(recommendCourseService.deleteRecommendCourseByCourseIds(courseIds));
    }
}

