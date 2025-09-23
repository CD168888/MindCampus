package com.mc.questionnaire.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.common.core.domain.BaseEntityPlus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 题目实体类
 */
@Data
@TableName("question")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "题目实体")
public class Question extends BaseEntityPlus {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "题目ID")
    private Long questionId;

    @Schema(description = "问卷ID")
    private Long questionnaireId;

    @Schema(description = "题目类型 choice/short_answer")
    private String type;

    @Schema(description = "题干内容")
    private String content;

    @Schema(description = "选项（JSON 格式）")
    private String options;

    @Schema(description = "标准答案")
    private String standardAnswer;

    @Schema(description = "分值")
    private Integer score;

    @Schema(description = "题目顺序")
    private Integer orderNum;
}