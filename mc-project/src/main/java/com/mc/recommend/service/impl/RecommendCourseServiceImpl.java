package com.mc.recommend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.common.utils.DateUtils;
import com.mc.common.utils.SecurityUtils;
import com.mc.recommend.domain.RecommendCourse;
import com.mc.recommend.mapper.RecommendCourseMapper;
import com.mc.recommend.service.IRecommendCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 心理课程推荐Service业务层处理
 *
 * @author caidu
 * @date 2025-11-08
 */
@Service
public class RecommendCourseServiceImpl extends ServiceImpl<RecommendCourseMapper, RecommendCourse> implements IRecommendCourseService {
    @Autowired
    private RecommendCourseMapper recommendCourseMapper;

    @Override
    public RecommendCourse selectRecommendCourseByCourseId(Long courseId) {
        return recommendCourseMapper.selectRecommendCourseByCourseId(courseId);
    }

    @Override
    public List<RecommendCourse> selectRecommendCourseList(RecommendCourse recommendCourse) {
        return recommendCourseMapper.selectRecommendCourseList(recommendCourse);
    }

    @Override
    public int insertRecommendCourse(RecommendCourse recommendCourse) {
        recommendCourse.setCreateBy(SecurityUtils.getUsername());
        recommendCourse.setCreateTime(DateUtils.getNowDate());
        return recommendCourseMapper.insertRecommendCourse(recommendCourse);
    }

    @Override
    public int updateRecommendCourse(RecommendCourse recommendCourse) {
        recommendCourse.setUpdateBy(SecurityUtils.getUsername());
        recommendCourse.setUpdateTime(DateUtils.getNowDate());
        return recommendCourseMapper.updateRecommendCourse(recommendCourse);
    }

    @Override
    public int deleteRecommendCourseByCourseIds(Long[] courseIds) {
        return recommendCourseMapper.deleteRecommendCourseByCourseIds(courseIds);
    }
}

