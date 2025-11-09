package com.mc.recommend.mapper;

import com.mc.recommend.domain.RecommendCourse;

import java.util.List;

/**
 * 心理课程推荐Mapper接口
 *
 * @author caidu
 * @date 2025-11-08
 */
public interface RecommendCourseMapper {
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
     * 删除心理课程推荐
     *
     * @param courseId 心理课程推荐主键
     * @return 结果
     */
    public int deleteRecommendCourseByCourseId(Long courseId);

    /**
     * 批量删除心理课程推荐
     *
     * @param courseIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRecommendCourseByCourseIds(Long[] courseIds);
}
