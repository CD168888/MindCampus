package com.mc.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.R;
import com.mc.common.core.domain.entity.SysUser;
import com.mc.common.exception.ServiceException;
import com.mc.common.utils.SecurityUtils;
import com.mc.community.domain.CommunityComment;
import com.mc.community.service.ICommunityCommentService;
import com.mc.community.service.ICommunityPostService;
import com.mc.student.domain.Student;
import com.mc.student.mapper.StudentInfoMapper;
import com.mc.system.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * APP端社区评论Controller
 *
 * @author mc
 * @date 2025-11-09
 */
@Tag(name = "APP端社区评论")
@RestController
@RequestMapping("/app/community")
@RequiredArgsConstructor
public class AppCommunityCommentController extends BaseController {

    private final ICommunityCommentService communityCommentService;
    private final ICommunityPostService communityPostService;
    private final StudentInfoMapper studentInfoMapper;
    private final SysUserMapper sysUserMapper;

    /**
     * 查询帖子的评论列表（树形结构）
     */
    @Operation(summary = "查询帖子的评论列表")
    @GetMapping("/post/{postId}/comments")
    public R<List<CommunityComment>> listComments(@PathVariable Long postId) {
        List<CommunityComment> comments = communityCommentService.selectCommentTreeByPostId(postId);
        
        // 处理匿名显示逻辑（APP端需要隐藏匿名用户的真实信息）
        for (CommunityComment comment : comments) {
            communityCommentService.processAnonymousDisplay(comment);
        }
        
        return R.ok(comments);
    }

    /**
     * 发表评论
     */
    @Operation(summary = "发表评论")
    @PostMapping("/comment")
    public R<Void> addComment(@RequestBody CommunityComment comment) {
        try {
            // 获取当前登录用户ID
        Long userId = SecurityUtils.getUserId();
            
            // 查询用户完整信息（包括头像）
            SysUser sysUser = sysUserMapper.selectUserById(userId);
            if (sysUser == null) {
                return R.fail("用户信息不存在");
            }
            
            // 查询学生ID
            LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Student::getUserId, userId);
            Student student = studentInfoMapper.selectOne(wrapper);
            if (student == null) {
                return R.fail("学生信息不存在，请先完善学生档案");
            }
            
            // 设置评论用户信息（始终保存完整信息到数据库）
        comment.setUserId(userId);
            comment.setUserName(sysUser.getNickName()); // 使用昵称
            
            // 设置用户头像，如果为空则使用默认头像
            String avatar = sysUser.getAvatar();
            if (avatar == null || avatar.trim().isEmpty()) {
                avatar = "/profile/avatar/default.png"; // 默认头像
            }
            comment.setUserAvatar(avatar);
            comment.setStudentId(student.getStudentId()); // 设置学生ID
            
            // 设置评论默认值
        comment.setStatus("0"); // 0-正常
        comment.setLikeCount(0L);
            
            // 如果未设置is_anonymous，默认为非匿名
            if (comment.getIsAnonymous() == null) {
                comment.setIsAnonymous("0");
            }
        
        int result = communityCommentService.insertCommunityComment(comment);
        
        if (result > 0) {
            // 增加帖子的评论数
            communityPostService.incrementCommentCount(comment.getPostId());
            return R.ok();
        }
        
        return R.fail("评论失败");
        } catch (ServiceException e) {
            return R.fail(e.getMessage());
        } catch (Exception e) {
            return R.fail("评论失败：" + e.getMessage());
        }
    }

    /**
     * 点赞评论
     */
    @Operation(summary = "点赞评论")
    @PostMapping("/comment/{commentId}/like")
    public R<Void> likeComment(@PathVariable Long commentId) {
        Long userId = SecurityUtils.getUserId();
        boolean result = communityCommentService.likeComment(commentId, userId);
        return result ? R.ok() : R.fail("点赞失败");
    }

    /**
     * 取消点赞评论
     */
    @Operation(summary = "取消点赞评论")
    @PostMapping("/comment/{commentId}/unlike")
    public R<Void> unlikeComment(@PathVariable Long commentId) {
        Long userId = SecurityUtils.getUserId();
        boolean result = communityCommentService.unlikeComment(commentId, userId);
        return result ? R.ok() : R.fail("取消点赞失败");
    }
}

