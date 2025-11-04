package com.mc.ai.domain;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Map;

@JsonPropertyOrder({
        "student_id", "questionnaire_id", "total_score",
        "risk_level", "radar_data", "analysis_text", "ai_status"
})
public record EvaluationAIResult(
        String student_id,
        String questionnaire_id,
        Integer total_score,
        String risk_level,
        Map<String, Integer> radar_data,
        String analysis_text,
        String ai_status
) {
}
