package com.mc.questionnaire.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * AI 生成心理测评题目响应 VO（预览用，返回给前端）
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "AI 生成题目预览结果")
public class AiGenerateQuestionVO {

    @Schema(description = "题目内容（题干）")
    private String content;

    @Schema(description = "题目类型：choice-选择题，short_answer-简答题")
    private String type;

    @Schema(description = "选择题选项 JSON 字符串，与 question_bank.options 一致；简答题为空")
    private String options;

    @Schema(description = "选项预览列表")
    private List<OptionItem> optionList;

    @Data
    @Schema(description = "选项项")
    public static class OptionItem {
        private String label;   // A / B / C / D
        private String content; // 选项内容
    }
}
