package com.mc.community.controller;

import com.mc.common.annotation.Log;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.AjaxResult;
import com.mc.common.core.page.TableDataInfo;
import com.mc.common.enums.BusinessType;
import com.mc.common.utils.poi.ExcelUtil;
import com.mc.community.domain.CommunityComment;
import com.mc.community.service.ICommunityCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论管理Controller
 *
 * @author mc
 * @date 2025-01-09
 */
@Tag(name = "评论管理")
@RestController
@RequestMapping("/community/comment")
@RequiredArgsConstructor
public class CommunityCommentController extends BaseController {

    private final ICommunityCommentService communityCommentService;

    /**
     * 查询评论管理列表
     */
    @PreAuthorize("@ss.hasPermi('community:comment:list')")
    @Operation(summary = "查询评论列表")
    @GetMapping("/list")
    public TableDataInfo list(CommunityComment communityComment) {
        startPage();
        List<CommunityComment> list = communityCommentService.selectCommunityCommentList(communityComment);
        return getDataTable(list);
    }

    /**
     * 导出评论管理列表
     */
    @PreAuthorize("@ss.hasPermi('community:comment:export')")
    @Log(title = "评论管理", businessType = BusinessType.EXPORT)
    @Operation(summary = "导出评论列表")
    @PostMapping("/export")
    public void export(HttpServletResponse response, CommunityComment communityComment) {
        List<CommunityComment> list = communityCommentService.selectCommunityCommentList(communityComment);
        ExcelUtil<CommunityComment> util = new ExcelUtil<CommunityComment>(CommunityComment.class);
        util.exportExcel(response, list, "评论管理数据");
    }

    /**
     * 获取评论管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('community:comment:query')")
    @Operation(summary = "获取评论详情")
    @GetMapping(value = "/{commentId}")
    public AjaxResult getInfo(@PathVariable("commentId") Long commentId) {
        return AjaxResult.success(communityCommentService.selectCommunityCommentByCommentId(commentId));
    }

    /**
     * 新增评论管理
     */
    @PreAuthorize("@ss.hasPermi('community:comment:add')")
    @Log(title = "评论管理", businessType = BusinessType.INSERT)
    @Operation(summary = "新增评论")
    @PostMapping
    public AjaxResult add(@RequestBody CommunityComment communityComment) {
        return toAjax(communityCommentService.insertCommunityComment(communityComment));
    }

    /**
     * 修改评论管理
     */
    @PreAuthorize("@ss.hasPermi('community:comment:edit')")
    @Log(title = "评论管理", businessType = BusinessType.UPDATE)
    @Operation(summary = "修改评论")
    @PutMapping
    public AjaxResult edit(@RequestBody CommunityComment communityComment) {
        return toAjax(communityCommentService.updateCommunityComment(communityComment));
    }

    /**
     * 删除评论管理
     */
    @PreAuthorize("@ss.hasPermi('community:comment:remove')")
    @Log(title = "评论管理", businessType = BusinessType.DELETE)
    @Operation(summary = "删除评论")
    @DeleteMapping("/{commentIds}")
    public AjaxResult remove(@PathVariable Long[] commentIds) {
        return toAjax(communityCommentService.deleteCommunityCommentByCommentIds(commentIds));
    }

    /**
     * 修改评论状态（屏蔽/取消屏蔽）
     */
    @PreAuthorize("@ss.hasPermi('community:comment:edit')")
    @Log(title = "评论状态管理", businessType = BusinessType.UPDATE)
    @Operation(summary = "修改评论状态")
    @PutMapping("/status")
    public AjaxResult changeStatus(@RequestBody CommunityComment communityComment) {
        if (communityComment.getCommentId() == null || communityComment.getStatus() == null) {
            return AjaxResult.error("参数错误");
        }
        CommunityComment comment = new CommunityComment();
        comment.setCommentId(communityComment.getCommentId());
        comment.setStatus(communityComment.getStatus());
        return toAjax(communityCommentService.updateCommunityComment(comment));
    }
}
