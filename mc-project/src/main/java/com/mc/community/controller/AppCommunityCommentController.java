package com.mc.community.controller;

import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.R;
import com.mc.common.utils.SecurityUtils;
import com.mc.community.domain.CommunityComment;
import com.mc.community.service.ICommunityCommentService;
import com.mc.community.service.ICommunityPostService;
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

    /**
     * 查询帖子的评论列表（树形结构）
     */
    @Operation(summary = "查询帖子的评论列表")
    @GetMapping("/post/{postId}/comments")
    public R<List<CommunityComment>> listComments(@PathVariable Long postId) {
        List<CommunityComment> comments = communityCommentService.selectCommentTreeByPostId(postId);
        return R.ok(comments);
    }

    /**
     * 发表评论
     */
    @Operation(summary = "发表评论")
    @PostMapping("/comment")
    public R<Void> addComment(@RequestBody CommunityComment comment) {
        // 设置评论用户信息
        Long userId = SecurityUtils.getUserId();
        String userName = SecurityUtils.getUsername();
        
        comment.setUserId(userId);
        comment.setUserName(userName);
        comment.setStatus("0"); // 0-正常
        comment.setLikeCount(0L);
        
        int result = communityCommentService.insertCommunityComment(comment);
        
        if (result > 0) {
            // 增加帖子的评论数
            communityPostService.incrementCommentCount(comment.getPostId());
            return R.ok();
        }
        
        return R.fail("评论失败");
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

