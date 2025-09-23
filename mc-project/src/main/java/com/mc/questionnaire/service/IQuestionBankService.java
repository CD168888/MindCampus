package com.mc.questionnaire.service;

import com.mc.questionnaire.domain.QuestionBank;

import java.util.List;

/**
 * 题库管理Service接口
 *
 * @author caidu
 * @date 2025-09-23
 */
public interface IQuestionBankService {
    /**
     * 查询题库管理
     *
     * @param bankId 题库管理主键
     * @return 题库管理
     */
    public QuestionBank selectQuestionBankByBankId(Long bankId);

    /**
     * 查询题库管理列表
     *
     * @param questionBank 题库管理
     * @return 题库管理集合
     */
    public List<QuestionBank> selectQuestionBankList(QuestionBank questionBank);

    /**
     * 新增题库管理
     *
     * @param questionBank 题库管理
     * @return 结果
     */
    public int insertQuestionBank(QuestionBank questionBank);

    /**
     * 修改题库管理
     *
     * @param questionBank 题库管理
     * @return 结果
     */
    public int updateQuestionBank(QuestionBank questionBank);

    /**
     * 批量删除题库管理
     *
     * @param bankIds 需要删除的题库管理主键集合
     * @return 结果
     */
    public int deleteQuestionBankByBankIds(Long[] bankIds);

    /**
     * 删除题库管理信息
     *
     * @param bankId 题库管理主键
     * @return 结果
     */
    public int deleteQuestionBankByBankId(Long bankId);
}
