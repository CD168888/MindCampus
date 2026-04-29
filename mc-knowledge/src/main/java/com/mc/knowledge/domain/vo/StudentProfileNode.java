package com.mc.knowledge.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 学生画像节点上下文（从 Neo4j 图谱中检索得到）
 *
 * @author MindCampus
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "学生画像节点上下文")
public class StudentProfileNode {

    @Schema(description = "学生用户ID")
    private Long userId;

    @Schema(description = "学生姓名")
    private String name;

    @Schema(description = "学号")
    private String studentNo;

    @Schema(description = "年级")
    private String grade;

    @Schema(description = "专业")
    private String major;

    @Schema(description = "班级")
    private String className;

    @Schema(description = "性格描述")
    private String personality;

    @Schema(description = "沟通风格")
    private String communicationStyle;

    @Schema(description = "兴趣爱好列表")
    private List<String> interests;

    @Schema(description = "关注事项列表")
    private List<String> concerns;

    @Schema(description = "最近风险等级")
    @JsonProperty("latestRiskLevel")
    private String riskLevel;

    @Schema(description = "最近评估分数")
    private Integer latestScore;

    @Schema(description = "最近评估日期")
    private String latestAssessmentDate;

    @Schema(description = "最近情绪状态")
    private String latestEmotion;

    @Schema(description = "情绪强度（1-10）")
    private Integer emotionIntensity;

    @Schema(description = "历史评估记录")
    private List<AssessmentRecord> assessmentHistory;

    @Schema(description = "AI 对话交互摘要")
    private InteractionSummary interactionSummary;

    @Schema(description = "未处理的干预通知数")
    private Long unprocessedNotifications;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssessmentRecord {
        private Long resultId;
        private String questionnaireTitle;
        private Integer totalScore;
        private String riskLevel;
        private String assessmentDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InteractionSummary {
        private Integer totalSessions;
        private Integer totalMessages;
        private Double avgScore;
        private String lastInteractionDate;
    }
}
