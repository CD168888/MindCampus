package com.mc.community.service.impl;

import com.mc.common.utils.DateUtils;
import com.mc.community.domain.CommunityPost;
import com.mc.community.mapper.CommunityCommentMapper;
import com.mc.community.mapper.CommunityPostMapper;
import com.mc.community.service.ICommunityPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 帖子管理Service业务层处理
 *
 * @author mc
 * @date 2025-01-09
 */
@Service
public class CommunityPostServiceImpl implements ICommunityPostService {
    @Autowired
    private CommunityPostMapper communityPostMapper;

    @Autowired
    private CommunityCommentMapper communityCommentMapper;

    /**
     * 查询帖子管理
     *
     * @param postId 帖子管理主键
     * @return 帖子管理
     */
    @Override
    public CommunityPost selectCommunityPostByPostId(Long postId) {
        return communityPostMapper.selectCommunityPostByPostId(postId);
    }

    /**
     * 查询帖子管理列表
     *
     * @param communityPost 帖子管理
     * @return 帖子管理
     */
    @Override
    public List<CommunityPost> selectCommunityPostList(CommunityPost communityPost) {
        return communityPostMapper.selectCommunityPostList(communityPost);
    }

    /**
     * 新增帖子管理
     *
     * @param communityPost 帖子管理
     * @return 结果
     */
    @Override
    public int insertCommunityPost(CommunityPost communityPost) {
        communityPost.setCreateTime(DateUtils.getNowDate());
        return communityPostMapper.insertCommunityPost(communityPost);
    }

    /**
     * 修改帖子管理
     *
     * @param communityPost 帖子管理
     * @return 结果
     */
    @Override
    public int updateCommunityPost(CommunityPost communityPost) {
        communityPost.setUpdateTime(DateUtils.getNowDate());
        return communityPostMapper.updateCommunityPost(communityPost);
    }

    /**
     * 批量删除帖子管理
     *
     * @param postIds 需要删除的帖子管理主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteCommunityPostByPostIds(Long[] postIds) {
        // 先删除关联的评论
        communityCommentMapper.deleteCommunityCommentByPostIds(postIds);
        // 再删除帖子
        return communityPostMapper.deleteCommunityPostByPostIds(postIds);
    }

    /**
     * 删除帖子管理信息
     *
     * @param postId 帖子管理主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteCommunityPostByPostId(Long postId) {
        // 先删除关联的评论
        communityCommentMapper.deleteCommunityCommentByPostIds(new Long[]{postId});
        // 再删除帖子
        return communityPostMapper.deleteCommunityPostByPostId(postId);
    }
}


