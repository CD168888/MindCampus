package com.mc.common.utils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 问卷工具类
 * 提供问卷内容构建等公共方法
 *
 * @author mc
 */
public class QuestionnaireUtils {

    private QuestionnaireUtils() {
        // 工具类，禁止实例化
    }

    /**
     * 构建问卷内容字符串
     * 适用于包含 content、type、userAnswer 字段的问卷答案对象
     *
     * @param contents 题目内容列表
     * @param types 题目类型列表（choice/short_answer）
     * @param userAnswers 用户答案列表
     * @return 格式化的问卷内容字符串
     */
    public static String buildQuestionnaireContent(List<String> contents, List<String> types, List<String> userAnswers) {
        if (contents == null || contents.isEmpty()) {
            return "无";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < contents.size(); i++) {
            if (i > 0) {
                sb.append("\n\n");
            }
            sb.append("题目: ").append(contents.get(i));
            sb.append("\n类型: ").append("choice".equals(types.get(i)) ? "选择题" : "简答题");
            sb.append("\n用户回答: ").append(userAnswers.get(i));
        }
        return sb.toString();
    }

    /**
     * 构建问卷内容字符串（使用对象数组）
     * 每个数组元素包含 [content, type, userAnswer]
     *
     * @param answerData 问卷答案数据数组
     * @return 格式化的问卷内容字符串
     */
    public static String buildQuestionnaireContentFromArray(List<String[]> answerData) {
        if (answerData == null || answerData.isEmpty()) {
            return "无";
        }

        return answerData.stream()
                .map(data -> {
                    String question = "题目: " + data[0];
                    String type = "类型: " + ("choice".equals(data[1]) ? "选择题" : "简答题");
                    String userAnswer = "用户回答: " + data[2];
                    return String.format("%s\n%s\n%s", question, type, userAnswer);
                })
                .collect(Collectors.joining("\n\n"));
    }
}
