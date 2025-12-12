package com.mc.ai.domain;

import java.util.List;

/**
 * 问卷评估请求类
 * 用于封装问卷答案和评估请求参数
 *
 * @author caidu
 */
public class QuestionnaireEvaluationRequest {
    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 问卷ID
     */
    private Long questionnaireId;

    /**
     * 问卷答案列表
     */
    private List<QuestionAnswer> answers;

    // getter 和 setter 方法
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(Long questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public List<QuestionAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuestionAnswer> answers) {
        this.answers = answers;
    }

    /**
     * 问卷答案类
     */
    public static class QuestionAnswer {
        /**
         * 题目ID
         */
        private Long questionId;

        /**
         * 题目类型（choice选择题/short_answer简答题）
         */
        private String type;

        /**
         * 题干内容
         */
        private String content;

        /**
         * 选择题选项（JSON格式），简答题为空
         */
        private String options;

        /**
         * 用户作答内容
         */
        private String userAnswer;

        /**
         * 题目分值
         */
        private Integer score;

        // getter 和 setter 方法
        public Long getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Long questionId) {
            this.questionId = questionId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getOptions() {
            return options;
        }

        public void setOptions(String options) {
            this.options = options;
        }

        public String getUserAnswer() {
            return userAnswer;
        }

        public void setUserAnswer(String userAnswer) {
            this.userAnswer = userAnswer;
        }

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }
    }
}