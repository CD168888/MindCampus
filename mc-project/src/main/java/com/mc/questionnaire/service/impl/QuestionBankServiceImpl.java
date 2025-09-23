package com.mc.questionnaire.service.impl;

import com.mc.common.utils.DateUtils;
import com.mc.questionnaire.domain.QuestionBank;
import com.mc.questionnaire.mapper.QuestionBankMapper;
import com.mc.questionnaire.service.IQuestionBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 题库管理Service业务层处理
 *
 * @author caidu
 * @date 2025-09-23
 */
@Service
public class QuestionBankServiceImpl implements IQuestionBankService {
    @Autowired
    private QuestionBankMapper questionBankMapper;

    /**
     * 查询题库管理
     *
     * @param bankId 题库管理主键
     * @return 题库管理
     */
    @Override
    public QuestionBank selectQuestionBankByBankId(Long bankId) {
        return questionBankMapper.selectQuestionBankByBankId(bankId);
    }

    /**
     * 查询题库管理列表
     *
     * @param questionBank 题库管理
     * @return 题库管理
     */
    @Override
    public List<QuestionBank> selectQuestionBankList(QuestionBank questionBank) {
        return questionBankMapper.selectQuestionBankList(questionBank);
    }

    /**
     * 新增题库管理
     *
     * @param questionBank 题库管理
     * @return 结果
     */
    @Override
    public int insertQuestionBank(QuestionBank questionBank) {
        questionBank.setCreateTime(DateUtils.getNowDate());
        return questionBankMapper.insertQuestionBank(questionBank);
    }

    /**
     * 修改题库管理
     *
     * @param questionBank 题库管理
     * @return 结果
     */
    @Override
    public int updateQuestionBank(QuestionBank questionBank) {
        questionBank.setUpdateTime(DateUtils.getNowDate());
        return questionBankMapper.updateQuestionBank(questionBank);
    }

    /**
     * 批量删除题库管理
     *
     * @param bankIds 需要删除的题库管理主键
     * @return 结果
     */
    @Override
    public int deleteQuestionBankByBankIds(Long[] bankIds) {
        return questionBankMapper.deleteQuestionBankByBankIds(bankIds);
    }

    /**
     * 删除题库管理信息
     *
     * @param bankId 题库管理主键
     * @return 结果
     */
    @Override
    public int deleteQuestionBankByBankId(Long bankId) {
        return questionBankMapper.deleteQuestionBankByBankId(bankId);
    }
}
