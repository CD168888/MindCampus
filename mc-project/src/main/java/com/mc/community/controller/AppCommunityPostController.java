package com.mc.community.controller;

import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.R;
import com.mc.common.core.page.TableDataInfo;
import com.mc.common.utils.SecurityUtils;
import com.mc.community.domain.CommunityPost;
import com.mc.community.service.ICommunityPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * APP端社区帖子Controller
 *
 * @author mc
 * @date 2025-11-09
 */
@Tag(name = "APP端社区帖子")
@RestController
@RequestMapping("/app/community/post")
@RequiredArgsConstructor
public class AppCommunityPostController extends BaseController {

    private final ICommunityPostService communityPostService;

    /**
     * 查询帖子列表（分页）
     */
    @Operation(summary = "查询帖子列表")
    @GetMapping("/list")
    public TableDataInfo list(CommunityPost communityPost) {
        // 只查询状态为正常的帖子
        communityPost.setStatus("0");
        startPage();
        return getDataTable(communityPostService.selectCommunityPostList(communityPost));
    }

    /**
     * 查询帖子详情
     */
    @Operation(summary = "查询帖子详情")
    @GetMapping("/{postId}")
    public R<CommunityPost> getDetail(@PathVariable Long postId) {
        CommunityPost post = communityPostService.selectCommunityPostByPostId(postId);
        if (post == null) {
            return R.fail("帖子不存在");
        }
        
        // 增加浏览量
        communityPostService.incrementViewCount(postId);
        
        return R.ok(post);
    }

    /**
     * 发布帖子
     */
    @Operation(summary = "发布帖子")
    @PostMapping
    public R<Void> publish(@RequestBody CommunityPost communityPost) {
        // 设置发布用户信息
        Long userId = SecurityUtils.getUserId();
        String userName = SecurityUtils.getUsername();
        
        communityPost.setUserId(userId);
        communityPost.setUserName(userName);
        communityPost.setStatus("0"); // 0-正常
        communityPost.setLikeCount(0L);
        communityPost.setCommentCount(0L);
        communityPost.setViewCount(0L);
        
        int result = communityPostService.insertCommunityPost(communityPost);
        return result > 0 ? R.ok() : R.fail("发布失败");
    }

    /**
     * 点赞帖子
     */
    @Operation(summary = "点赞帖子")
    @PostMapping("/{postId}/like")
    public R<Void> like(@PathVariable Long postId) {
        Long userId = SecurityUtils.getUserId();
        boolean result = communityPostService.likePost(postId, userId);
        return result ? R.ok() : R.fail("点赞失败");
    }

    /**
     * 取消点赞帖子
     */
    @Operation(summary = "取消点赞帖子")
    @PostMapping("/{postId}/unlike")
    public R<Void> unlike(@PathVariable Long postId) {
        Long userId = SecurityUtils.getUserId();
        boolean result = communityPostService.unlikePost(postId, userId);
        return result ? R.ok() : R.fail("取消点赞失败");
    }
}



