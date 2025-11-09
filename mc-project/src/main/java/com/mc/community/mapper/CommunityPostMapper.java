package com.mc.community.mapper;

import com.mc.community.domain.CommunityPost;

import java.util.List;

/**
 * 帖子管理Mapper接口
 *
 * @author mc
 * @date 2025-01-09
 */
public interface CommunityPostMapper {
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
     * 删除帖子管理
     *
     * @param postId 帖子管理主键
     * @return 结果
     */
    public int deleteCommunityPostByPostId(Long postId);

    /**
     * 批量删除帖子管理
     *
     * @param postIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCommunityPostByPostIds(Long[] postIds);
}


