package com.mc.recommend.controller;

import com.mc.common.annotation.Anonymous;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.AjaxResult;
import com.mc.common.core.page.TableDataInfo;
import com.mc.recommend.domain.RecommendCourse;
import com.mc.recommend.service.IRecommendCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 心理课程推荐App接口
 *
 * @author caidu
 * @date 2025-11-09
 */
@RestController
@RequestMapping("/app/course")
public class AppCourseController extends BaseController {
  @Autowired
  private IRecommendCourseService recommendCourseService;

  /**
   * 查询心理课程推荐列表
   */
  @Anonymous
  @GetMapping("/list")
  public TableDataInfo list(RecommendCourse recommendCourse) {
    startPage();
    // 只查询正常状态的课程
    recommendCourse.setStatus("0");
    List<RecommendCourse> list = recommendCourseService.selectRecommendCourseList(recommendCourse);
    return getDataTable(list);
  }

  /**
   * 获取心理课程推荐详细信息
   */
  @Anonymous
  @GetMapping("/{courseId}")
  public AjaxResult getInfo(@PathVariable("courseId") Long courseId) {
    // 查询课程详情
    RecommendCourse course = recommendCourseService.selectRecommendCourseByCourseId(courseId);
    return success(course);
  }

  /**
   * 获取推荐课程（首页推荐3个）
   */
  @Anonymous
  @GetMapping("/recommended")
  public AjaxResult getRecommended() {
    RecommendCourse query = new RecommendCourse();
    query.setStatus("0");
    List<RecommendCourse> list = recommendCourseService.selectRecommendCourseList(query);
    // 取前3个
    if (list.size() > 3) {
      list = list.subList(0, 3);
    }
    return success(list);
  }
}

