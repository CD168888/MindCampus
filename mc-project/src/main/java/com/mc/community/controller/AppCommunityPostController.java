package com.mc.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.R;
import com.mc.common.core.domain.entity.SysUser;
import com.mc.common.core.page.TableDataInfo;
import com.mc.common.exception.ServiceException;
import com.mc.common.utils.SecurityUtils;
import com.mc.community.domain.CommunityPost;
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
    private final StudentInfoMapper studentInfoMapper;
    private final SysUserMapper sysUserMapper;

    /**
     * 查询帖子列表（分页）
     */
    @Operation(summary = "查询帖子列表")
    @GetMapping("/list")
    public TableDataInfo list(CommunityPost communityPost) {
        // 只查询状态为正常的帖子
        communityPost.setStatus("0");
        startPage();
        
        List<CommunityPost> list = communityPostService.selectCommunityPostList(communityPost);
        
        // 处理匿名显示逻辑（APP端需要隐藏匿名用户的真实信息）
        for (CommunityPost post : list) {
            communityPostService.processAnonymousDisplay(post);
        }
        
        return getDataTable(list);
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
        
        // 处理匿名显示逻辑（APP端需要隐藏匿名用户的真实信息）
        communityPostService.processAnonymousDisplay(post);
        
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
            
            // 设置发布用户信息（始终保存完整信息到数据库）
            communityPost.setUserId(userId);
            communityPost.setUserName(sysUser.getNickName()); // 使用昵称
            
            // 设置用户头像，如果为空则使用默认头像
            String avatar = sysUser.getAvatar();
            if (avatar == null || avatar.trim().isEmpty()) {
                avatar = "/profile/avatar/default.png"; // 默认头像
            }
            communityPost.setUserAvatar(avatar);
            communityPost.setStudentId(student.getStudentId()); // 设置学生ID
            
            // 设置帖子默认值
            communityPost.setStatus("0"); // 0-正常
            communityPost.setLikeCount(0L);
            communityPost.setCommentCount(0L);
            communityPost.setViewCount(0L);
            
            // 如果未设置is_anonymous，默认为非匿名
            if (communityPost.getIsAnonymous() == null) {
                communityPost.setIsAnonymous("0");
            }
            
            int result = communityPostService.insertCommunityPost(communityPost);
            return result > 0 ? R.ok() : R.fail("发布失败");
        } catch (ServiceException e) {
            return R.fail(e.getMessage());
        } catch (Exception e) {
            return R.fail("发布失败：" + e.getMessage());
        }
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



