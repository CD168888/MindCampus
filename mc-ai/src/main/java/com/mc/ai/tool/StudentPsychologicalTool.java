package com.mc.ai.tool;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 学生心理评估工具类，用于AI工具调用
 * 提供心理评估、心理健康状态查询等功能
 *
 * @author caidu
 * @date 2025-09-22
 */
@Component
public class StudentPsychologicalTool {

    /**
     * 根据学生ID查询心理健康状态
     *
     * @param studentId 学生ID
     * @return 心理健康状态信息
     */
    @Tool(name = "getStudentMentalHealthStatus", description = "根据学生ID查询心理健康状态")
    public Map<String, Object> getStudentMentalHealthStatus(@Parameter(description = "学生ID") Long studentId) {
        // 模拟心理健康状态数据
        Map<String, Object> status = new HashMap<>();
        status.put("studentId", studentId);
        status.put("mentalHealthLevel", "良好");
        status.put("lastAssessmentDate", "2025-09-15");
        status.put("assessmentScore", 85);
        status.put("riskLevel", "低风险");
        status.put("suggestion", "保持当前积极心态，继续参与集体活动");
        return status;
    }

    /**
     * 根据学生ID获取心理评估报告
     *
     * @param studentId 学生ID
     * @return 心理评估报告
     */
    @Tool(name = "getPsychologicalAssessmentReport", description = "根据学生ID获取最近的心理评估报告")
    public String getPsychologicalAssessmentReport(@Parameter(description = "学生ID") Long studentId) {
        // 模拟心理评估报告
        return "学生心理评估报告\n" +
                "学生ID: " + studentId + "\n" +
                "评估日期: 2025-09-15\n" +
                "评估类型: 抑郁自评量表(SDS)\n" +
                "评估分数: 35\n" +
                "结果解释: 正常范围(无抑郁症状)\n" +
                "评估建议: 保持当前的生活方式，继续关注心理健康。建议定期进行心理评估，保持良好的心理状态。\n" +
                "备注: 学生在评估过程中表现积极，没有明显的情绪问题。";
    }

    /**
     * 预约心理辅导
     *
     * @param studentId 学生ID
     * @param counselorId 辅导员ID
     * @param date 预约日期
     * @param time 预约时间
     * @return 预约结果
     */
    @Tool(name = "bookPsychologicalCounseling", description = "为学生预约心理辅导")
    public Map<String, Object> bookPsychologicalCounseling(
            @Parameter(description = "学生ID") Long studentId,
            @Parameter(description = "辅导员ID") Long counselorId,
            @Parameter(description = "预约日期，格式：YYYY-MM-DD") String date,
            @Parameter(description = "预约时间，格式：HH:mm") String time) {
        // 模拟预约结果
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "心理辅导预约成功");
        result.put("appointmentId", System.currentTimeMillis());
        result.put("studentId", studentId);
        result.put("counselorId", counselorId);
        result.put("appointmentDate", date);
        result.put("appointmentTime", time);
        result.put("location", "心理咨询室302");
        return result;
    }

    /**
     * 查询学生心理辅导历史记录
     *
     * @param studentId 学生ID
     * @param limit 记录数量限制
     * @return 辅导历史记录
     */
    @Tool(name = "getCounselingHistory", description = "查询学生心理辅导历史记录")
    public Map<String, Object> getCounselingHistory(
            @Parameter(description = "学生ID") Long studentId,
            @Parameter(description = "返回记录数量限制") Integer limit) {
        // 模拟辅导历史记录
        Map<String, Object> history = new HashMap<>();
        history.put("studentId", studentId);
        history.put("totalRecords", 3);
        history.put("limit", limit);
        
        Map<String, Object> record1 = new HashMap<>();
        record1.put("counselingId", 1001);
        record1.put("date", "2025-08-20");
        record1.put("counselorId", 201);
        record1.put("counselorName", "张老师");
        record1.put("duration", 45);
        record1.put("topic", "学业压力管理");
        
        Map<String, Object> record2 = new HashMap<>();
        record2.put("counselingId", 1002);
        record2.put("date", "2025-09-05");
        record2.put("counselorId", 201);
        record2.put("counselorName", "张老师");
        record2.put("duration", 50);
        record2.put("topic", "人际关系问题");
        
        history.put("records", limit == 1 ? new Object[]{record1} : new Object[]{record1, record2});
        
        return history;
    }
}
