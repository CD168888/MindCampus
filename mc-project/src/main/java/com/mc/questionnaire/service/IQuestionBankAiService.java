package com.mc.questionnaire.service;

import com.mc.questionnaire.domain.dto.AiGenerateQuestionDTO;
import com.mc.questionnaire.domain.dto.AiGenerateQuestionnaireDTO;
import com.mc.questionnaire.domain.vo.AiGenerateQuestionVO;
import com.mc.questionnaire.domain.vo.AiGenerateQuestionnaireVO;

/**
 * 题库 AI 题目生成服务接口
 */
public interface IQuestionBankAiService {

    /**
     * AI 生成心理测评题目（仅预览，不入库）
     *
     * @param dto 题目类型和描述
     * @return 生成的题目预览 VO
     */
    AiGenerateQuestionVO generateQuestion(AiGenerateQuestionDTO dto);

    /**
     * AI 生成整套心理测评问卷（仅预览，不入库）
     *
     * @param dto 问卷主题、选择题数量、简答题数量
     * @return 生成的整套问卷预览 VO
     */
    AiGenerateQuestionnaireVO generateQuestionnaire(AiGenerateQuestionnaireDTO dto);
}
