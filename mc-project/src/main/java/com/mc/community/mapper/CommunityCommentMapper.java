package com.mc.community.mapper;

import com.mc.community.domain.CommunityComment;

import java.util.List;

/**
 * 评论管理Mapper接口
 *
 * @author mc
 * @date 2025-01-09
 */
public interface CommunityCommentMapper {
    /**
     * 查询评论管理
     *
     * @param commentId 评论管理主键
     * @return 评论管理
     */
    public CommunityComment selectCommunityCommentByCommentId(Long commentId);

    /**
     * 根据帖子ID查询评论列表
     *
     * @param postId 帖子ID
     * @return 评论集合
     */
    public List<CommunityComment> selectCommunityCommentListByPostId(Long postId);

    /**
     * 查询评论管理列表
     *
     * @param communityComment 评论管理
     * @return 评论管理集合
     */
    public List<CommunityComment> selectCommunityCommentList(CommunityComment communityComment);

    /**
     * 新增评论管理
     *
     * @param communityComment 评论管理
     * @return 结果
     */
    public int insertCommunityComment(CommunityComment communityComment);

    /**
     * 修改评论管理
     *
     * @param communityComment 评论管理
     * @return 结果
     */
    public int updateCommunityComment(CommunityComment communityComment);

    /**
     * 删除评论管理
     *
     * @param commentId 评论管理主键
     * @return 结果
     */
    public int deleteCommunityCommentByCommentId(Long commentId);

    /**
     * 批量删除评论管理
     *
     * @param commentIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCommunityCommentByCommentIds(Long[] commentIds);

    /**
     * 根据帖子ID删除评论
     *
     * @param postIds 帖子ID数组
     * @return 结果
     */
    public int deleteCommunityCommentByPostIds(Long[] postIds);
}


