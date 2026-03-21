package com.mc.questionnaire.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * AI 生成整套心理测评问卷请求 DTO
 */
@Data
@Schema(description = "AI 生成整套问卷请求")
public class AiGenerateQuestionnaireDTO {

    @Schema(description = "问卷主题描述，如\"大学生学业压力与适应能力评估\"")
    @NotBlank(message = "问卷描述不能为空")
    private String description;

    @Schema(description = "选择题数量，建议 3-10 道")
    @NotNull(message = "选择题数量不能为空")
    @Min(value = 1, message = "选择题数量最少为1道")
    @Max(value = 20, message = "选择题数量最多为20道")
    private Integer choiceCount;

    @Schema(description = "简答题数量，建议 1-5 道，放置于问卷末尾")
    @NotNull(message = "简答题数量不能为空")
    @Min(value = 0, message = "简答题数量最少为0道")
    @Max(value = 10, message = "简答题数量最多为10道")
    private Integer shortAnswerCount;
}
