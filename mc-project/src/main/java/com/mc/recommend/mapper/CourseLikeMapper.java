package com.mc.recommend.mapper;

import com.mc.recommend.domain.CourseLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 课程点赞 Mapper接口
 *
 * @author caidu
 * @date 2025-11-08
 */
@Mapper
public interface CourseLikeMapper {

    /**
     * 检查用户是否已点赞
     */
    int checkLike(@Param("courseId") Long courseId, @Param("userId") Long userId);

    /**
     * 新增点赞
     */
    int insertLike(CourseLike courseLike);

    /**
     * 取消点赞
     */
    int deleteLike(@Param("courseId") Long courseId, @Param("userId") Long userId);

    /**
     * 获取课程点赞数
     */
    int selectLikeCount(@Param("courseId") Long courseId);
}
