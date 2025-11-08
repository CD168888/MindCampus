package com.mc.questionnaire.domain;

import com.mc.common.annotation.Excel;
import com.mc.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.ibatis.type.Alias;

/**
 * 心理测评答题记录对象 questionnaire_answer
 *
 * @author caidu
 * @date 2025-11-08
 */
@Alias("QuestionnaireAnswerRecord")
public class QuestionnaireAnswer extends BaseEntity {
  private static final long serialVersionUID = 1L;

  /**
   * 答题记录ID
   */
  private Long answerId;

  /**
   * 关联心理测评结果ID
   */
  @Excel(name = "测评结果ID")
  private Long resultId;

  /**
   * 问卷ID
   */
  @Excel(name = "问卷ID")
  private Long questionnaireId;

  /**
   * 问卷标题
   */
  @Excel(name = "问卷标题")
  private String questionnaireTitle;

  /**
   * 题目ID
   */
  @Excel(name = "题目ID")
  private Long questionId;

  /**
   * 题目类型（choice选择题/short_answer简答题）
   */
  @Excel(name = "题目类型", readConverterExp = "choice=选择题,short_answer=简答题")
  private String type;

  /**
   * 题干内容
   */
  @Excel(name = "题干内容")
  private String content;

  /**
   * 选择题选项（A/B/C/D…），简答题为空
   */
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
  private Integer score;

  /**
   * 用户作答内容
   */
  @Excel(name = "用户答案")
  private String userAnswer;

  /**
   * 是否答对（1正确/0错误/2表示简答题 无）
   */
  @Excel(name = "是否答对", readConverterExp = "1=正确,0=错误,2=无")
  private Integer isCorrect;

  /**
   * 用户获得分数
   */
  @Excel(name = "获得分数")
  private Integer obtainScore;

  /**
   * 学生ID
   */
  private Long studentId;

  /**
   * 学生姓名
   */
  @Excel(name = "学生姓名")
  private String studentName;

  public void setAnswerId(Long answerId) {
    this.answerId = answerId;
  }

  public Long getAnswerId() {
    return answerId;
  }

  public void setResultId(Long resultId) {
    this.resultId = resultId;
  }

  public Long getResultId() {
    return resultId;
  }

  public void setQuestionnaireId(Long questionnaireId) {
    this.questionnaireId = questionnaireId;
  }

  public Long getQuestionnaireId() {
    return questionnaireId;
  }

  public void setQuestionnaireTitle(String questionnaireTitle) {
    this.questionnaireTitle = questionnaireTitle;
  }

  public String getQuestionnaireTitle() {
    return questionnaireTitle;
  }

  public void setQuestionId(Long questionId) {
    this.questionId = questionId;
  }

  public Long getQuestionId() {
    return questionId;
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

  public void setScore(Integer score) {
    this.score = score;
  }

  public Integer getScore() {
    return score;
  }

  public void setUserAnswer(String userAnswer) {
    this.userAnswer = userAnswer;
  }

  public String getUserAnswer() {
    return userAnswer;
  }

  public void setIsCorrect(Integer isCorrect) {
    this.isCorrect = isCorrect;
  }

  public Integer getIsCorrect() {
    return isCorrect;
  }

  public void setObtainScore(Integer obtainScore) {
    this.obtainScore = obtainScore;
  }

  public Integer getObtainScore() {
    return obtainScore;
  }

  public void setStudentId(Long studentId) {
    this.studentId = studentId;
  }

  public Long getStudentId() {
    return studentId;
  }

  public void setStudentName(String studentName) {
    this.studentName = studentName;
  }

  public String getStudentName() {
    return studentName;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
        .append("answerId", getAnswerId())
        .append("resultId", getResultId())
        .append("questionnaireId", getQuestionnaireId())
        .append("questionnaireTitle", getQuestionnaireTitle())
        .append("questionId", getQuestionId())
        .append("type", getType())
        .append("content", getContent())
        .append("options", getOptions())
        .append("standardAnswer", getStandardAnswer())
        .append("score", getScore())
        .append("userAnswer", getUserAnswer())
        .append("isCorrect", getIsCorrect())
        .append("obtainScore", getObtainScore())
        .append("studentId", getStudentId())
        .append("studentName", getStudentName())
        .append("createBy", getCreateBy())
        .append("createTime", getCreateTime())
        .append("updateBy", getUpdateBy())
        .append("updateTime", getUpdateTime())
        .append("remark", getRemark())
        .toString();
  }
}
