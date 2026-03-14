package com.mc.recommend.controller;

import com.mc.common.annotation.Anonymous;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.AjaxResult;
import com.mc.common.core.domain.R;
import com.mc.common.core.page.TableDataInfo;
import com.mc.common.utils.SecurityUtils;
import com.mc.recommend.domain.RecommendCourse;
import com.mc.recommend.service.IRecommendCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  /**
   * 点赞/取消点赞课程
   */
  @PostMapping("/{courseId}/like")
  public R<Map<String, Object>> likeCourse(@PathVariable Long courseId) {
    Long userId = SecurityUtils.getUserId();
    boolean liked = recommendCourseService.likeCourse(courseId, userId);
    int likeCount = recommendCourseService.getCourseLikeCount(courseId);

    Map<String, Object> result = new HashMap<>();
    result.put("liked", liked);
    result.put("likeCount", likeCount);
    return R.ok(result);
  }

  /**
   * 获取课程点赞状态和数量
   */
  @GetMapping("/{courseId}/like/status")
  public R<Map<String, Object>> getLikeStatus(@PathVariable Long courseId) {
    Long userId = SecurityUtils.getUserId();
    boolean liked = recommendCourseService.checkCourseLiked(courseId, userId);
    int likeCount = recommendCourseService.getCourseLikeCount(courseId);

    Map<String, Object> result = new HashMap<>();
    result.put("liked", liked);
    result.put("likeCount", likeCount);
    return R.ok(result);
  }

  /**
   * 获取用户点赞的课程列表
   */
  @GetMapping("/like/list")
  public R<List<RecommendCourse>> getLikedCourses() {
    Long userId = SecurityUtils.getUserId();
    List<RecommendCourse> list = recommendCourseService.getLikedCourses(userId);
    return R.ok(list);
  }
}

