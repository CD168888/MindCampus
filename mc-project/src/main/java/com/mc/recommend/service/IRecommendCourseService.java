package com.mc.recommend.service;

import com.mc.recommend.domain.RecommendCourse;

import java.util.List;

/**
 * 心理课程推荐Service接口
 *
 * @author caidu
 * @date 2025-11-08
 */
public interface IRecommendCourseService {
    /**
     * 查询心理课程推荐列表
     *
     * @param recommendCourse 心理课程推荐
     * @return 心理课程推荐集合
     */
    public List<RecommendCourse> selectRecommendCourseList(RecommendCourse recommendCourse);

    /**
     * 查询心理课程推荐
     *
     * @param courseId 心理课程推荐主键
     * @return 心理课程推荐
     */
    public RecommendCourse selectRecommendCourseByCourseId(Long courseId);

    /**
     * 新增心理课程推荐
     *
     * @param recommendCourse 心理课程推荐
     * @return 结果
     */
    public int insertRecommendCourse(RecommendCourse recommendCourse);

    /**
     * 修改心理课程推荐
     *
     * @param recommendCourse 心理课程推荐
     * @return 结果
     */
    public int updateRecommendCourse(RecommendCourse recommendCourse);

    /**
     * 批量删除心理课程推荐
     *
     * @param courseIds 需要删除的心理课程推荐主键集合
     * @return 结果
     */
    public int deleteRecommendCourseByCourseIds(Long[] courseIds);

    /**
     * 删除心理课程推荐信息
     *
     * @param courseId 心理课程推荐主键
     * @return 结果
     */
    public int deleteRecommendCourseByCourseId(Long courseId);

    /**
     * 点赞/取消点赞课程
     *
     * @param courseId 课程ID
     * @param userId 用户ID
     * @return 结果(true=点赞,false=取消点赞)
     */
    boolean likeCourse(Long courseId, Long userId);

    /**
     * 检查用户是否已点赞
     *
     * @param courseId 课程ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    boolean checkCourseLiked(Long courseId, Long userId);

    /**
     * 获取课程点赞数
     *
     * @param courseId 课程ID
     * @return 点赞数
     */
    int getCourseLikeCount(Long courseId);

    /**
     * 获取用户点赞的课程列表
     *
     * @param userId 用户ID
     * @return 点赞的课程列表
     */
    List<RecommendCourse> getLikedCourses(Long userId);
}
