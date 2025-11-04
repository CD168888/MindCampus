package com.mc.evaluation.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 提交答题DTO
 *
 * @author caidu
 * @date 2025-11-04
 */
@Data
@Schema(description = "提交答题DTO")
public class SubmitAnswerDTO {

  @Schema(description = "问卷ID")
  private Long questionnaireId;

  @Schema(description = "答题列表")
  private List<AnswerItem> answers;

  @Data
  @Schema(description = "答题项")
  public static class AnswerItem {
    @Schema(description = "题目ID")
    private Long questionId;

    @Schema(description = "用户答案")
    private String userAnswer;
  }
}
