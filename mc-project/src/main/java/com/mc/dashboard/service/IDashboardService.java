package com.mc.dashboard.service;

import com.mc.dashboard.domain.vo.DashboardActivityVO;
import com.mc.dashboard.domain.vo.DashboardStatisticsVO;
import com.mc.dashboard.domain.vo.DashboardTodoVO;
import com.mc.dashboard.domain.vo.DashboardTrendVO;

import java.util.List;

public interface IDashboardService {

    DashboardStatisticsVO getStatistics();

    List<DashboardTrendVO> getTrendData(String period);

    List<DashboardActivityVO> getRecentActivities(int limit);

    List<DashboardTodoVO> getTodoList(int limit);
}
