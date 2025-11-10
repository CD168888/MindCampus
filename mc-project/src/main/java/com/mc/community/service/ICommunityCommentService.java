package com.mc.community.service;

import com.mc.community.domain.CommunityComment;

import java.util.List;

/**
 * 评论管理Service接口
 *
 * @author mc
 * @date 2025-01-09
 */
public interface ICommunityCommentService {
    /**
     * 查询评论管理
     *
     * @param commentId 评论管理主键
     * @return 评论管理
     */
    public CommunityComment selectCommunityCommentByCommentId(Long commentId);

    /**
     * 根据帖子ID查询评论树
     *
     * @param postId 帖子ID
     * @return 评论树结构
     */
    public List<CommunityComment> selectCommentTreeByPostId(Long postId);

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
     * 批量删除评论管理
     *
     * @param commentIds 需要删除的评论管理主键集合
     * @return 结果
     */
    public int deleteCommunityCommentByCommentIds(Long[] commentIds);

    /**
     * 删除评论管理信息
     *
     * @param commentId 评论管理主键
     * @return 结果
     */
    public int deleteCommunityCommentByCommentId(Long commentId);

    /**
     * 点赞评论
     *
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 结果
     */
    public boolean likeComment(Long commentId, Long userId);

    /**
     * 取消点赞评论
     *
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 结果
     */
    public boolean unlikeComment(Long commentId, Long userId);

    /**
     * 处理匿名显示逻辑（供APP端使用）
     * 如果is_anonymous='1'(匿名)，则隐藏真实用户信息
     *
     * @param comment 评论对象
     */
    public void processAnonymousDisplay(CommunityComment comment);
}


