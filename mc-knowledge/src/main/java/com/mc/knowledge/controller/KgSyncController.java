package com.mc.knowledge.controller;

import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.AjaxResult;
import com.mc.knowledge.kg.service.impl.KgSyncService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 知识图谱同步管理 Controller
 */
@RestController
@RequestMapping("/knowledge/sync")
public class KgSyncController extends BaseController {

    @Resource
    private KgSyncService kgSyncService;

    /**
     * 手动触发全量同步
     */
    @PreAuthorize("@ss.hasPermi('knowledge:sync:full')")
    @PostMapping("/full")
    public AjaxResult fullSync() {
        Map<String, Object> result = kgSyncService.manualFullSync();
        if (Boolean.TRUE.equals(result.get("success"))) {
            return success(result);
        } else {
            return error("同步失败: " + result.get("error"));
        }
    }

    /**
     * 同步指定学生
     */
    @PreAuthorize("@ss.hasPermi('knowledge:sync:student')")
    @PostMapping("/student/{studentId}")
    public AjaxResult syncStudent(@PathVariable Long studentId) {
        try {
            kgSyncService.syncStudent(studentId);
            return success();
        } catch (Exception e) {
            return error("同步失败: " + e.getMessage());
        }
    }
}
