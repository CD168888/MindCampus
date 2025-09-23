package com.mc.questionnaire.service;

import com.mc.questionnaire.domain.Question;
import com.mc.questionnaire.domain.Questionnaire;
import com.mc.questionnaire.domain.dto.QuestionnaireDTO;

import java.util.List;

/**
 * 问卷 Service
 */
public interface IQuestionnaireService {

    /**
     * 查询问卷列表
     */
    List<Questionnaire> selectQuestionnaireList(Questionnaire questionnaire);

    /**
     * 保存问卷及题目
     */
    void saveQuestionnaire(QuestionnaireDTO questionnaireDTO);

    /**
     * 根据问卷ID查询题目
     */
    List<Question> selectQuestionByQuestionnaireId(Long questionnaireId);

    /**
     * 删除问卷及题目
     */
    void deleteQuestionnaire(Long[] questionnaireIds);
}