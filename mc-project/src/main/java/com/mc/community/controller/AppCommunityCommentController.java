package com.mc.community.controller;

import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.R;
import com.mc.common.core.domain.entity.SysUser;
import com.mc.common.exception.ServiceException;
import com.mc.common.utils.SecurityUtils;
import com.mc.community.domain.CommunityComment;
import com.mc.community.service.ICommunityCommentService;
import com.mc.community.service.ICommunityPostService;
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

            // 验证用户存在（userId 通过 user_id 关联查询 sys_user 表获取用户名和头像）
            SysUser sysUser = sysUserMapper.selectUserById(userId);
            if (sysUser == null) {
                return R.fail("用户信息不存在");
            }

            // 只保存 userId，其他信息通过关联查询获取
            comment.setUserId(userId);

            // 处理回复：设置被回复人的用户名
            if (comment.getReplyToUserId() != null) {
                SysUser replyToUser = sysUserMapper.selectUserById(comment.getReplyToUserId());
                if (replyToUser != null) {
                    comment.setReplyToUserName(replyToUser.getNickName());
                }
            }

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

