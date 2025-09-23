package com.mc.questionnaire.domain;

import com.mc.common.annotation.Excel;
import com.mc.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 题库管理对象 question_bank
 *
 * @author caidu
 * @date 2025-09-23
 */
public class QuestionBank extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 题库题目ID
     */
    private Long bankId;

    /**
     * 题目类型（choice选择题/short_answer简答题）
     */
    @Excel(name = "题目类型", dictType = "question_type")
    private String type;

    /**
     * 题干内容
     */
    @Excel(name = "题干内容")
    private String content;

    /**
     * 选择题选项（A/B/C/D…），简答题为空
     */
    @Excel(name = "选择题选项")
    private String options;

    /**
     * 标准答案（仅选择题有效）
     */
    @Excel(name = "标准答案")
    private String standardAnswer;

    /**
     * 分值
     */
    @Excel(name = "分值")
    private Long score;

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public Long getBankId() {
        return bankId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getOptions() {
        return options;
    }

    public void setStandardAnswer(String standardAnswer) {
        this.standardAnswer = standardAnswer;
    }

    public String getStandardAnswer() {
        return standardAnswer;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public Long getScore() {
        return score;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("bankId", getBankId())
                .append("type", getType())
                .append("content", getContent())
                .append("options", getOptions())
                .append("standardAnswer", getStandardAnswer())
                .append("score", getScore())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
