package com.mc.recommend.service.impl;

import com.mc.common.utils.DateUtils;
import com.mc.recommend.domain.CourseLike;
import com.mc.recommend.domain.RecommendCourse;
import com.mc.recommend.mapper.CourseLikeMapper;
import com.mc.recommend.mapper.RecommendCourseMapper;
import com.mc.recommend.service.IRecommendCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 心理课程推荐Service业务层处理
 *
 * @author caidu
 * @date 2025-11-08
 */
@Service
public class RecommendCourseServiceImpl implements IRecommendCourseService {
    @Autowired
    private RecommendCourseMapper recommendCourseMapper;

    @Autowired
    private CourseLikeMapper courseLikeMapper;

    /**
     * 查询心理课程推荐列表
     *
     * @param recommendCourse 心理课程推荐
     * @return 心理课程推荐
     */
    @Override
    public List<RecommendCourse> selectRecommendCourseList(RecommendCourse recommendCourse) {
        return recommendCourseMapper.selectRecommendCourseList(recommendCourse);
    }

    /**
     * 查询心理课程推荐
     *
     * @param courseId 心理课程推荐主键
     * @return 心理课程推荐
     */
    @Override
    public RecommendCourse selectRecommendCourseByCourseId(Long courseId) {
        return recommendCourseMapper.selectRecommendCourseByCourseId(courseId);
    }

    /**
     * 新增心理课程推荐
     *
     * @param recommendCourse 心理课程推荐
     * @return 结果
     */
    @Override
    public int insertRecommendCourse(RecommendCourse recommendCourse) {
        recommendCourse.setCreateTime(DateUtils.getNowDate());
        return recommendCourseMapper.insertRecommendCourse(recommendCourse);
    }

    /**
     * 修改心理课程推荐
     *
     * @param recommendCourse 心理课程推荐
     * @return 结果
     */
    @Override
    public int updateRecommendCourse(RecommendCourse recommendCourse) {
        recommendCourse.setUpdateTime(DateUtils.getNowDate());
        return recommendCourseMapper.updateRecommendCourse(recommendCourse);
    }

    /**
     * 批量删除心理课程推荐
     *
     * @param courseIds 需要删除的心理课程推荐主键
     * @return 结果
     */
    @Override
    public int deleteRecommendCourseByCourseIds(Long[] courseIds) {
        return recommendCourseMapper.deleteRecommendCourseByCourseIds(courseIds);
    }

    /**
     * 删除心理课程推荐信息
     *
     * @param courseId 心理课程推荐主键
     * @return 结果
     */
    @Override
    public int deleteRecommendCourseByCourseId(Long courseId) {
        return recommendCourseMapper.deleteRecommendCourseByCourseId(courseId);
    }

    @Override
    public boolean likeCourse(Long courseId, Long userId) {
        // 检查是否已点赞
        int count = courseLikeMapper.checkLike(courseId, userId);
        if (count > 0) {
            // 已点赞，取消点赞
            courseLikeMapper.deleteLike(courseId, userId);
            return false;
        } else {
            // 未点赞，添加点赞
            CourseLike courseLike = new CourseLike();
            courseLike.setCourseId(courseId);
            courseLike.setUserId(userId);
            courseLike.setCreateTime(new Date());
            courseLikeMapper.insertLike(courseLike);
            return true;
        }
    }

    @Override
    public boolean checkCourseLiked(Long courseId, Long userId) {
        return courseLikeMapper.checkLike(courseId, userId) > 0;
    }

    @Override
    public int getCourseLikeCount(Long courseId) {
        return courseLikeMapper.selectLikeCount(courseId);
    }
}
