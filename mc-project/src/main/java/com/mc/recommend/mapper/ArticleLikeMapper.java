package com.mc.recommend.mapper;

import com.mc.recommend.domain.ArticleLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文章点赞 Mapper接口
 *
 * @author caidu
 * @date 2025-11-08
 */
@Mapper
public interface ArticleLikeMapper {

    /**
     * 检查用户是否已点赞
     */
    int checkLike(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 新增点赞
     */
    int insertLike(ArticleLike articleLike);

    /**
     * 取消点赞
     */
    int deleteLike(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 获取文章点赞数
     */
    int selectLikeCount(@Param("articleId") Long articleId);
}
