package com.mc.knowledge.controller;

import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.AjaxResult;
import com.mc.knowledge.domain.vo.StudentProfileNode;
import com.mc.knowledge.kg.graph.Neo4jClient;
import com.mc.knowledge.kg.service.IStudentProfileKGService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生画像知识图谱管理 Controller
 *
 * @author MindCampus
 */
@RestController
@RequestMapping("/knowledge/persona")
public class StudentProfileKGController extends BaseController {

    @Resource
    private IStudentProfileKGService studentProfileKGService;

    @Resource
    private Neo4jClient neo4jClient;

    @PreAuthorize("@ss.hasPermi('knowledge:persona:query')")
    @GetMapping("/profile/{userId}")
    public AjaxResult getProfile(@PathVariable Long userId) {
        StudentProfileNode profile = studentProfileKGService.queryStudentProfile(userId);
        if (profile == null) {
            return error("未找到该用户的图谱数据");
        }
        return success(profile);
    }

    @PreAuthorize("@ss.hasPermi('knowledge:persona:context')")
    @GetMapping("/context/{userId}")
    public AjaxResult getContext(@PathVariable Long userId) {
        String kgContext = studentProfileKGService.buildKgContext(userId);
        Map<String, String> result = new HashMap<>();
        result.put("kgContext", kgContext);
        return success(result);
    }

    @PreAuthorize("@ss.hasPermi('knowledge:persona:emotion')")
    @GetMapping("/emotion/{userId}")
    public AjaxResult getEmotionHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        String emotionHistory = studentProfileKGService.queryEmotionHistory(userId, limit);
        Map<String, String> result = new HashMap<>();
        result.put("emotionHistory", emotionHistory);
        return success(result);
    }

    @PreAuthorize("@ss.hasPermi('knowledge:persona:risk')")
    @GetMapping("/risk/{userId}")
    public AjaxResult getRiskTrend(@PathVariable Long userId) {
        String riskTrend = studentProfileKGService.queryRiskTrend(userId);
        Map<String, String> result = new HashMap<>();
        result.put("riskTrend", riskTrend);
        return success(result);
    }

    @PreAuthorize("@ss.hasPermi('knowledge:persona:update')")
    @PostMapping("/profile/{userId}")
    public AjaxResult updateProfile(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> profileData) {
        try {
            studentProfileKGService.updateProfile(
                userId,
                (String) profileData.get("personality"),
                (String) profileData.get("communicationStyle"),
                (List<String>) profileData.get("interests"),
                (List<String>) profileData.get("concerns")
            );
            return success();
        } catch (Exception e) {
            return error("更新失败: " + e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('knowledge:persona:sync')")
    @PostMapping("/sync/{userId}")
    public AjaxResult syncProfile(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> syncData) {
        try {
            String syncType = (String) syncData.getOrDefault("syncType", "incremental");
            switch (syncType) {
                case "assessment":
                    studentProfileKGService.syncAssessment(
                        userId,
                        ((Number) syncData.get("resultId")).longValue(),
                        (String) syncData.get("questionnaireTitle"),
                        syncData.get("totalScore") != null ? ((Number) syncData.get("totalScore")).intValue() : null,
                        (String) syncData.get("riskLevel")
                    );
                    break;
                case "emotion":
                    studentProfileKGService.syncEmotionState(
                        userId,
                        (String) syncData.get("emotion"),
                        syncData.get("intensity") != null ? ((Number) syncData.get("intensity")).intValue() : null
                    );
                    break;
                case "interaction":
                    studentProfileKGService.syncInteraction(
                        userId,
                        ((Number) syncData.get("sessionId")).longValue(),
                        syncData.get("messageCount") != null ? ((Number) syncData.get("messageCount")).intValue() : null,
                        syncData.get("avgScore") != null ? ((Number) syncData.get("avgScore")).doubleValue() : null
                    );
                    break;
                default:
                    return error("未知的同步类型: " + syncType);
            }
            return success();
        } catch (Exception e) {
            return error("同步失败: " + e.getMessage());
        }
    }

    /**
     * 获取图谱可视化数据
     */
    @PreAuthorize("@ss.hasPermi('knowledge:persona:query')")
    @GetMapping("/graph/{userId}")
    public AjaxResult getGraphData(@PathVariable Long userId) {
        try {
            var graphData = neo4jClient.exportGraphData(userId);
            return success(graphData);
        } catch (Exception e) {
            return error("获取图谱数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取全局图谱数据（所有学生的图谱概览）
     */
    @PreAuthorize("@ss.hasPermi('knowledge:persona:query')")
    @GetMapping("/graph")
    public AjaxResult getFullGraphData(@RequestParam(defaultValue = "50") int limit) {
        try {
            var graphData = neo4jClient.exportFullGraphData(limit);
            return success(graphData);
        } catch (Exception e) {
            return error("获取图谱数据失败: " + e.getMessage());
        }
    }
}
