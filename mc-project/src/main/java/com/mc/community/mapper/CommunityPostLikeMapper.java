package com.mc.community.mapper;

import com.mc.community.domain.CommunityPostLike;
import org.apache.ibatis.annotations.Param;

/**
 * 帖子点赞记录Mapper接口
 *
 * @author mc
 * @date 2026-03-14
 */
public interface CommunityPostLikeMapper {

    /**
     * 检查用户是否已点赞
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 点赞记录
     */
    CommunityPostLike selectLikeByPostAndUser(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 新增点赞记录
     *
     * @param communityPostLike 点赞记录
     * @return 结果
     */
    int insertLike(CommunityPostLike communityPostLike);

    /**
     * 删除点赞记录
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    int deleteLike(@Param("postId") Long postId, @Param("userId") Long userId);
}
