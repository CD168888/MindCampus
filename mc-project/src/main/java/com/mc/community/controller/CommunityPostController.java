package com.mc.community.controller;

import com.mc.common.annotation.Log;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.AjaxResult;
import com.mc.common.core.domain.entity.SysUser;
import com.mc.common.core.page.TableDataInfo;
import com.mc.common.enums.BusinessType;
import com.mc.common.utils.poi.ExcelUtil;
import com.mc.community.domain.CommunityPost;
import com.mc.community.service.ICommunityCommentService;
import com.mc.community.service.ICommunityPostService;
import com.mc.student.domain.Student;
import com.mc.student.mapper.StudentInfoMapper;
import com.mc.system.mapper.SysUserMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * 帖子管理Controller
 *
 * @author mc
 * @date 2025-01-09
 */
@RestController
@RequestMapping("/community/post")
public class CommunityPostController extends BaseController {
    @Autowired
    private ICommunityPostService communityPostService;

    @Autowired
    private ICommunityCommentService communityCommentService;

    @Autowired
    private StudentInfoMapper studentInfoMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 查询帖子管理列表
     */
    @PreAuthorize("@ss.hasPermi('community:community:list')")
    @GetMapping("/list")
    public TableDataInfo list(CommunityPost communityPost) {
        startPage();
        List<CommunityPost> list = communityPostService.selectCommunityPostList(communityPost);
        return getDataTable(list);
    }

    /**
     * 导出帖子管理列表
     */
    @PreAuthorize("@ss.hasPermi('community:community:export')")
    @Log(title = "帖子管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CommunityPost communityPost) {
        List<CommunityPost> list = communityPostService.selectCommunityPostList(communityPost);
        ExcelUtil<CommunityPost> util = new ExcelUtil<CommunityPost>(CommunityPost.class);
        util.exportExcel(response, list, "帖子管理数据");
    }

    /**
     * 获取帖子管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('community:community:query')")
    @GetMapping(value = "/{postId}")
    public AjaxResult getInfo(@PathVariable("postId") Long postId) {
        AjaxResult ajax = AjaxResult.success();
        ajax.put("post", communityPostService.selectCommunityPostByPostId(postId));
        // 获取该帖子的所有评论
        ajax.put("comments", communityCommentService.selectCommentTreeByPostId(postId));
        return ajax;
    }

    /**
     * 新增帖子管理
     */
    @PreAuthorize("@ss.hasPermi('community:community:add')")
    @Log(title = "帖子管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CommunityPost communityPost) {
        // 补充用户信息
        fillUserInfo(communityPost);
        return toAjax(communityPostService.insertCommunityPost(communityPost));
    }

    /**
     * 修改帖子管理
     */
    @PreAuthorize("@ss.hasPermi('community:community:edit')")
    @Log(title = "帖子管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CommunityPost communityPost) {
        // 补充用户信息
        fillUserInfo(communityPost);
        return toAjax(communityPostService.updateCommunityPost(communityPost));
    }

    /**
     * 根据studentId填充用户信息
     * 
     * @param communityPost 帖子对象
     */
    private void fillUserInfo(CommunityPost communityPost) {
        if (communityPost.getStudentId() != null) {
            // 查询学生信息获取userId
            Student student = studentInfoMapper.selectStudentInfoByStudentId(communityPost.getStudentId());
            if (student != null && student.getUserId() != null) {
                // 查询用户完整信息
                SysUser sysUser = sysUserMapper.selectUserById(student.getUserId());
                if (sysUser != null) {
                    communityPost.setUserId(sysUser.getUserId());
                    communityPost.setUserName(sysUser.getNickName());
                    
                    // 设置用户头像，如果为空则使用默认头像
                    String avatar = sysUser.getAvatar();
                    if (avatar == null || avatar.trim().isEmpty()) {
                        avatar = "/profile/avatar/default.png";
                    }
                    communityPost.setUserAvatar(avatar);
                }
            }
        }
    }

    /**
     * 删除帖子管理
     */
    @PreAuthorize("@ss.hasPermi('community:community:remove')")
    @Log(title = "帖子管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public AjaxResult remove(@PathVariable Long[] postIds) {
        return toAjax(communityPostService.deleteCommunityPostByPostIds(postIds));
    }
}


