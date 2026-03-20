package com.mc.dashboard.mapper;

import com.mc.dashboard.domain.vo.DashboardActivityVO;
import com.mc.dashboard.domain.vo.DashboardStatisticsVO;
import com.mc.dashboard.domain.vo.DashboardTodoVO;
import com.mc.dashboard.domain.vo.DashboardTrendVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DashboardMapper {

    Long selectTotalStudents();

    Long selectCompletedEvaluations();

    Long selectAttentionStudents();

    Long selectHighRiskCount();

    Long selectLowRiskCount();

    Long selectMediumRiskCount();

    Long selectWeekNewEvaluations(@Param("startDate") String startDate, @Param("endDate") String endDate);

    Long selectLastWeekEvaluations(@Param("startDate") String startDate, @Param("endDate") String endDate);

    List<DashboardTrendVO> selectTrendData(@Param("startDate") String startDate, @Param("endDate") String endDate);

    List<DashboardActivityVO> selectRecentActivities(@Param("limit") int limit);

    List<DashboardTodoVO> selectTodoList(@Param("limit") int limit);
}
