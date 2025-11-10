package com.mc.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mc.common.exception.ServiceException;
import com.mc.common.utils.DateUtils;
import com.mc.community.domain.CommunityPost;
import com.mc.community.mapper.CommunityCommentMapper;
import com.mc.community.mapper.CommunityPostMapper;
import com.mc.community.service.ICommunityPostService;
import com.mc.student.domain.Student;
import com.mc.student.mapper.StudentInfoMapper;
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

    @Autowired
    private StudentInfoMapper studentInfoMapper;

    /**
     * 通过userId获取studentId
     *
     * @param userId 用户ID
     * @return 学生ID
     */
    private Long getStudentIdByUserId(Long userId) {
        if (userId == null) {
            throw new ServiceException("用户ID不能为空");
        }

        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getUserId, userId);
        Student student = studentInfoMapper.selectOne(wrapper);

        if (student == null) {
            throw new ServiceException("学生信息不存在，请先完善学生档案");
        }

        return student.getStudentId();
    }

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
     * 处理匿名显示逻辑（供APP端使用）
     * 如果is_anonymous='1'(匿名)，则隐藏真实用户信息
     *
     * @param post 帖子对象
     */
    public void processAnonymousDisplay(CommunityPost post) {
        if (post != null && "1".equals(post.getIsAnonymous())) {
            // 匿名帖子：使用默认头像和匿名用户名
            post.setUserName("匿名用户");
            post.setUserAvatar("/profile/avatar/default.png"); // 默认头像路径
            // 注意：userId和studentId保留在数据库中，但可以在VO中过滤掉
        }
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
        communityCommentMapper.deleteCommunityCommentByPostIds(new Long[] { postId });
        // 再删除帖子
        return communityPostMapper.deleteCommunityPostByPostId(postId);
    }

    /**
     * 增加帖子浏览量
     *
     * @param postId 帖子ID
     * @return 结果
     */
    @Override
    public int incrementViewCount(Long postId) {
        return communityPostMapper.incrementViewCount(postId);
    }

    /**
     * 增加帖子评论数
     *
     * @param postId 帖子ID
     * @return 结果
     */
    @Override
    public int incrementCommentCount(Long postId) {
        return communityPostMapper.incrementCommentCount(postId);
    }

    /**
     * 点赞帖子
     * 注意：这里简化实现，直接增加点赞数
     * 实际项目中应该维护一个点赞记录表，避免重复点赞
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public boolean likePost(Long postId, Long userId) {
        // TODO: 实际项目中应该先检查用户是否已点赞，并记录到点赞表
        CommunityPost post = communityPostMapper.selectCommunityPostByPostId(postId);
        if (post != null) {
            post.setLikeCount(post.getLikeCount() + 1);
            return communityPostMapper.updateCommunityPost(post) > 0;
        }
        return false;
    }

    /**
     * 取消点赞帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public boolean unlikePost(Long postId, Long userId) {
        // TODO: 实际项目中应该从点赞表中删除记录
        CommunityPost post = communityPostMapper.selectCommunityPostByPostId(postId);
        if (post != null && post.getLikeCount() > 0) {
            post.setLikeCount(post.getLikeCount() - 1);
            return communityPostMapper.updateCommunityPost(post) > 0;
        }
        return false;
    }
}
