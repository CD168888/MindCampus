package com.mc.dashboard.controller;

import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.R;
import com.mc.dashboard.domain.vo.DashboardActivityVO;
import com.mc.dashboard.domain.vo.DashboardStatisticsVO;
import com.mc.dashboard.domain.vo.DashboardTodoVO;
import com.mc.dashboard.domain.vo.DashboardTrendVO;
import com.mc.dashboard.service.IDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "首页数据统计")
@RestController
@RequestMapping("/dashboard")
public class DashboardController extends BaseController {

    @Autowired
    private IDashboardService dashboardService;

    @Operation(summary = "获取首页统计数据")
    @GetMapping("/statistics")
    public R<DashboardStatisticsVO> getStatistics() {
        return R.ok(dashboardService.getStatistics());
    }

    @Operation(summary = "获取测评趋势数据")
    @GetMapping("/trend")
    public R<List<DashboardTrendVO>> getTrendData(
            @Parameter(description = "时间周期：week-近7天，month-近30天，year-近一年") 
            @RequestParam(defaultValue = "week") String period) {
        return R.ok(dashboardService.getTrendData(period));
    }

    @Operation(summary = "获取最新动态")
    @GetMapping("/activities")
    public R<List<DashboardActivityVO>> getRecentActivities(
            @Parameter(description = "返回数量限制") 
            @RequestParam(defaultValue = "5") int limit) {
        return R.ok(dashboardService.getRecentActivities(limit));
    }

    @Operation(summary = "获取待办事项")
    @GetMapping("/todos")
    public R<List<DashboardTodoVO>> getTodoList(
            @Parameter(description = "返回数量限制") 
            @RequestParam(defaultValue = "5") int limit) {
        return R.ok(dashboardService.getTodoList(limit));
    }
}
