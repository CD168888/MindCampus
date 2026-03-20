package com.mc.questionnaire.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AI 生成心理测评题目请求 DTO
 */
@Data
@Schema(description = "AI 生成心理测评题目请求")
public class AiGenerateQuestionDTO {

    @Schema(description = "题目类型：choice-选择题，short_answer-简答题")
    @NotBlank(message = "题目类型不能为空")
    private String type;

    @Schema(description = "管理员描述的题目主题/要求，如：\"关于学业压力的自我认知\"、\"一道评估抑郁情绪的选择题\"")
    @NotBlank(message = "题目描述不能为空")
    private String description;
}
