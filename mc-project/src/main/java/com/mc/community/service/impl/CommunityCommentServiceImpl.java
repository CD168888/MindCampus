package com.mc.community.service.impl;


import com.mc.common.utils.DateUtils;
import com.mc.community.domain.CommunityComment;
import com.mc.community.mapper.CommunityCommentMapper;
import com.mc.community.service.ICommunityCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论管理Service业务层处理
 *
 * @author mc
 * @date 2025-01-09
 */
@Service
public class CommunityCommentServiceImpl implements ICommunityCommentService {
    @Autowired
    private CommunityCommentMapper communityCommentMapper;

    /**
     * 查询评论管理
     *
     * @param commentId 评论管理主键
     * @return 评论管理
     */
    @Override
    public CommunityComment selectCommunityCommentByCommentId(Long commentId) {
        return communityCommentMapper.selectCommunityCommentByCommentId(commentId);
    }

    /**
     * 根据帖子ID查询评论树
     *
     * @param postId 帖子ID
     * @return 评论树结构
     */
    @Override
    public List<CommunityComment> selectCommentTreeByPostId(Long postId) {
        List<CommunityComment> allComments = communityCommentMapper.selectCommunityCommentListByPostId(postId);
        // 由于当前数据表没有parent_id字段，这里返回平铺列表
        // 如果以后扩展了parent_id字段，可以构建树形结构
        return allComments;
    }

    /**
     * 查询评论管理列表
     *
     * @param communityComment 评论管理
     * @return 评论管理
     */
    @Override
    public List<CommunityComment> selectCommunityCommentList(CommunityComment communityComment) {
        return communityCommentMapper.selectCommunityCommentList(communityComment);
    }

    /**
     * 新增评论管理
     *
     * @param communityComment 评论管理
     * @return 结果
     */
    @Override
    public int insertCommunityComment(CommunityComment communityComment) {
        communityComment.setCreateTime(DateUtils.getNowDate());
        return communityCommentMapper.insertCommunityComment(communityComment);
    }

    /**
     * 修改评论管理
     *
     * @param communityComment 评论管理
     * @return 结果
     */
    @Override
    public int updateCommunityComment(CommunityComment communityComment) {
        communityComment.setUpdateTime(DateUtils.getNowDate());
        return communityCommentMapper.updateCommunityComment(communityComment);
    }

    /**
     * 批量删除评论管理
     *
     * @param commentIds 需要删除的评论管理主键
     * @return 结果
     */
    @Override
    public int deleteCommunityCommentByCommentIds(Long[] commentIds) {
        return communityCommentMapper.deleteCommunityCommentByCommentIds(commentIds);
    }

    /**
     * 删除评论管理信息
     *
     * @param commentId 评论管理主键
     * @return 结果
     */
    @Override
    public int deleteCommunityCommentByCommentId(Long commentId) {
        return communityCommentMapper.deleteCommunityCommentByCommentId(commentId);
    }

    /**
     * 点赞评论
     * 注意：这里简化实现，直接增加点赞数
     * 实际项目中应该维护一个点赞记录表，避免重复点赞
     *
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public boolean likeComment(Long commentId, Long userId) {
        // TODO: 实际项目中应该先检查用户是否已点赞，并记录到点赞表
        CommunityComment comment = communityCommentMapper.selectCommunityCommentByCommentId(commentId);
        if (comment != null) {
            comment.setLikeCount(comment.getLikeCount() + 1);
            return communityCommentMapper.updateCommunityComment(comment) > 0;
        }
        return false;
    }

    /**
     * 取消点赞评论
     *
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public boolean unlikeComment(Long commentId, Long userId) {
        // TODO: 实际项目中应该从点赞表中删除记录
        CommunityComment comment = communityCommentMapper.selectCommunityCommentByCommentId(commentId);
        if (comment != null && comment.getLikeCount() > 0) {
            comment.setLikeCount(comment.getLikeCount() - 1);
            return communityCommentMapper.updateCommunityComment(comment) > 0;
        }
        return false;
    }
}


