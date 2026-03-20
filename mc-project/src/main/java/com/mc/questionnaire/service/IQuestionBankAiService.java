package com.mc.questionnaire.service;

import com.mc.questionnaire.domain.dto.AiGenerateQuestionDTO;
import com.mc.questionnaire.domain.vo.AiGenerateQuestionVO;

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
}
