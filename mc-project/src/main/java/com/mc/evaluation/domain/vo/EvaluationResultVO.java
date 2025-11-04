package com.mc.evaluation.domain.vo;

import com.mc.evaluation.domain.EvaluationResult;
import com.mc.evaluation.domain.QuestionnaireAnswer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 测评结果VO（包含答题记录）
 *
 * @author caidu
 * @date 2025-11-04
 */
@Data
@Schema(description = "测评结果VO")
public class EvaluationResultVO {

  @Schema(description = "测评结果")
  private EvaluationResult result;

  @Schema(description = "答题记录列表")
  private List<QuestionnaireAnswer> answers;

  @Schema(description = "总题数")
  private Integer totalQuestions;

  @Schema(description = "正确题数")
  private Integer correctCount;

  @Schema(description = "准确率")
  private String accuracy;
}
