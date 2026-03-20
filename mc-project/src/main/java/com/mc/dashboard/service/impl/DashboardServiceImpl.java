package com.mc.dashboard.service.impl;

import com.mc.dashboard.domain.vo.DashboardActivityVO;
import com.mc.dashboard.domain.vo.DashboardStatisticsVO;
import com.mc.dashboard.domain.vo.DashboardTodoVO;
import com.mc.dashboard.domain.vo.DashboardTrendVO;
import com.mc.dashboard.mapper.DashboardMapper;
import com.mc.dashboard.service.IDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardServiceImpl implements IDashboardService {

    @Autowired
    private DashboardMapper dashboardMapper;

    @Override
    public DashboardStatisticsVO getStatistics() {
        DashboardStatisticsVO vo = new DashboardStatisticsVO();
        
        vo.setTotalStudents(dashboardMapper.selectTotalStudents());
        vo.setCompletedEvaluations(dashboardMapper.selectCompletedEvaluations());
        vo.setAttentionStudents(dashboardMapper.selectAttentionStudents());
        vo.setHighRiskCount(dashboardMapper.selectHighRiskCount());
        vo.setLowRiskCount(dashboardMapper.selectLowRiskCount());
        vo.setMediumRiskCount(dashboardMapper.selectMediumRiskCount());
        
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate weekEnd = weekStart.plusDays(6);
        LocalDate lastWeekStart = weekStart.minusWeeks(1);
        LocalDate lastWeekEnd = weekStart.minusDays(1);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        vo.setWeekNewEvaluations(dashboardMapper.selectWeekNewEvaluations(
            weekStart.format(formatter) + " 00:00:00",
            weekEnd.format(formatter) + " 23:59:59"
        ));
        vo.setLastWeekEvaluations(dashboardMapper.selectLastWeekEvaluations(
            lastWeekStart.format(formatter) + " 00:00:00",
            lastWeekEnd.format(formatter) + " 23:59:59"
        ));
        
        return vo;
    }

    @Override
    public List<DashboardTrendVO> getTrendData(String period) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;
        
        switch (period) {
            case "month":
                startDate = endDate.minusDays(29);
                break;
            case "year":
                startDate = endDate.minusMonths(11).withDayOfMonth(1);
                break;
            default:
                startDate = endDate.minusDays(6);
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<DashboardTrendVO> trendData = dashboardMapper.selectTrendData(
            startDate.format(formatter) + " 00:00:00",
            endDate.format(formatter) + " 23:59:59"
        );
        
        if ("year".equals(period)) {
            return aggregateByMonth(trendData);
        }
        
        return fillMissingDates(trendData, startDate, endDate, period);
    }

    @Override
    public List<DashboardActivityVO> getRecentActivities(int limit) {
        List<DashboardActivityVO> activities = dashboardMapper.selectRecentActivities(limit);
        
        for (DashboardActivityVO activity : activities) {
            String riskLevel = activity.getRiskLevel();
            if ("高".equals(riskLevel)) {
                activity.setActivityType("danger");
                activity.setTitle(activity.getStudentName() + "完成了" + activity.getQuestionnaireTitle());
                activity.setDescription("测评结果为高风险，需要关注");
            } else if ("中".equals(riskLevel)) {
                activity.setActivityType("warning");
                activity.setTitle(activity.getStudentName() + "完成了" + activity.getQuestionnaireTitle());
                activity.setDescription("测评结果为中度风险，建议关注");
            } else {
                activity.setActivityType("success");
                activity.setTitle(activity.getStudentName() + "完成了" + activity.getQuestionnaireTitle());
                activity.setDescription("测评结果正常");
            }
        }
        
        return activities;
    }

    @Override
    public List<DashboardTodoVO> getTodoList(int limit) {
        List<DashboardTodoVO> todoList = dashboardMapper.selectTodoList(limit);
        
        for (DashboardTodoVO todo : todoList) {
            String riskLevel = todo.getRiskLevel();
            if ("高".equals(riskLevel)) {
                todo.setPriority("high");
                todo.setContent("跟进" + todo.getStudentName() + "的高风险测评结果");
            } else if ("中".equals(riskLevel)) {
                todo.setPriority("medium");
                todo.setContent("关注" + todo.getStudentName() + "的中度风险测评结果");
            } else {
                todo.setPriority("low");
                todo.setContent("查看" + todo.getStudentName() + "的测评结果");
            }
        }
        
        return todoList;
    }

    private List<DashboardTrendVO> fillMissingDates(List<DashboardTrendVO> trendData, LocalDate startDate, LocalDate endDate, String period) {
        List<DashboardTrendVO> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        if ("week".equals(period)) {
            String[] weekDays = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
            for (int i = 0; i < 7; i++) {
                LocalDate date = startDate.plusDays(i);
                DashboardTrendVO vo = findTrendByDate(trendData, date.format(formatter));
                if (vo == null) {
                    vo = new DashboardTrendVO();
                    vo.setDate(weekDays[i]);
                    vo.setEvaluationCount(0L);
                    vo.setCompletedCount(0L);
                    vo.setHighRiskCount(0L);
                } else {
                    vo.setDate(weekDays[i]);
                }
                result.add(vo);
            }
        } else {
            LocalDate current = startDate;
            while (!current.isAfter(endDate)) {
                DashboardTrendVO vo = findTrendByDate(trendData, current.format(formatter));
                if (vo == null) {
                    vo = new DashboardTrendVO();
                    vo.setDate(current.format(formatter));
                    vo.setEvaluationCount(0L);
                    vo.setCompletedCount(0L);
                    vo.setHighRiskCount(0L);
                }
                result.add(vo);
                current = current.plusDays(1);
            }
        }
        
        return result;
    }

    private List<DashboardTrendVO> aggregateByMonth(List<DashboardTrendVO> trendData) {
        return trendData;
    }

    private DashboardTrendVO findTrendByDate(List<DashboardTrendVO> trendData, String date) {
        for (DashboardTrendVO vo : trendData) {
            if (date.equals(vo.getDate())) {
                return vo;
            }
        }
        return null;
    }
}
