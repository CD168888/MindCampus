package com.mc.evaluation.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.common.core.domain.BaseEntityPlus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 心理测评答题记录实体
 *
 * @author caidu
 * @date 2025-11-04
 */
@Data
@TableName("questionnaire_answer")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "心理测评答题记录")
public class QuestionnaireAnswer extends BaseEntityPlus {
  private static final long serialVersionUID = 1L;

  @Schema(description = "答题记录ID")
  @TableId(type = IdType.AUTO)
  private Long answerId;

  @Schema(description = "关联心理测评结果ID")
  private Long resultId;

  @Schema(description = "问卷ID")
  private Long questionnaireId;

  @Schema(description = "题目ID")
  private Long questionId;

  // 冗余字段（用于历史追溯和快速统计）
  @Schema(description = "题目类型（choice选择题/short_answer简答题）")
  private String type;

  @Schema(description = "题干内容")
  private String content;

  @Schema(description = "选择题选项（A/B/C/D…），简答题为空")
  private String options;

  // 用户作答部分
  @Schema(description = "用户作答内容")
  private String userAnswer;
}
