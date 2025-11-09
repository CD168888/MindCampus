package com.mc.community.service;

import com.mc.community.domain.CommunityPost;

import java.util.List;

/**
 * 帖子管理Service接口
 *
 * @author mc
 * @date 2025-01-09
 */
public interface ICommunityPostService {
    /**
     * 查询帖子管理
     *
     * @param postId 帖子管理主键
     * @return 帖子管理
     */
    public CommunityPost selectCommunityPostByPostId(Long postId);

    /**
     * 查询帖子管理列表
     *
     * @param communityPost 帖子管理
     * @return 帖子管理集合
     */
    public List<CommunityPost> selectCommunityPostList(CommunityPost communityPost);

    /**
     * 新增帖子管理
     *
     * @param communityPost 帖子管理
     * @return 结果
     */
    public int insertCommunityPost(CommunityPost communityPost);

    /**
     * 修改帖子管理
     *
     * @param communityPost 帖子管理
     * @return 结果
     */
    public int updateCommunityPost(CommunityPost communityPost);

    /**
     * 批量删除帖子管理
     *
     * @param postIds 需要删除的帖子管理主键集合
     * @return 结果
     */
    public int deleteCommunityPostByPostIds(Long[] postIds);

    /**
     * 删除帖子管理信息
     *
     * @param postId 帖子管理主键
     * @return 结果
     */
    public int deleteCommunityPostByPostId(Long postId);

    /**
     * 增加帖子浏览量
     *
     * @param postId 帖子ID
     * @return 结果
     */
    public int incrementViewCount(Long postId);

    /**
     * 增加帖子评论数
     *
     * @param postId 帖子ID
     * @return 结果
     */
    public int incrementCommentCount(Long postId);

    /**
     * 点赞帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    public boolean likePost(Long postId, Long userId);

    /**
     * 取消点赞帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    public boolean unlikePost(Long postId, Long userId);
}


