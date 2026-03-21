package com.mc.questionnaire.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * AI 生成整套问卷响应 VO
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "AI 生成整套问卷预览结果")
public class AiGenerateQuestionnaireVO {

    @Schema(description = "生成的题目列表（按顺序：选择题在前，简答题在后）")
    private List<AiGenerateQuestionVO> questions;
}
