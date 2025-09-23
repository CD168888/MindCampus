package com.mc.questionnaire.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.common.utils.DateUtils;
import com.mc.common.utils.SecurityUtils;
import com.mc.questionnaire.domain.Question;
import com.mc.questionnaire.domain.Questionnaire;
import com.mc.questionnaire.domain.dto.QuestionnaireDTO;
import com.mc.questionnaire.mapper.QuestionMapper;
import com.mc.questionnaire.mapper.QuestionnaireMapper;
import com.mc.questionnaire.service.IQuestionnaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionnaireServiceImpl extends ServiceImpl<QuestionnaireMapper, Questionnaire>
        implements IQuestionnaireService {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private final QuestionnaireMapper questionnaireMapper;

    @Override
    public List<Questionnaire> selectQuestionnaireList(Questionnaire questionnaire) {
        return this.lambdaQuery()
                .like(questionnaire.getTitle() != null, Questionnaire::getTitle, questionnaire.getTitle())
                .like(questionnaire.getDescription() != null, Questionnaire::getDescription, questionnaire.getDescription())
                .eq(questionnaire.getStatus() != null, Questionnaire::getStatus, questionnaire.getStatus())
                .eq(questionnaire.getType() != null, Questionnaire::getType, questionnaire.getType())
                // 开始时间 >= 查询条件
                .ge(questionnaire.getStartTime() != null, Questionnaire::getStartTime, questionnaire.getStartTime())
                // 结束时间 <= 查询条件
                .le(questionnaire.getEndTime() != null, Questionnaire::getEndTime, questionnaire.getEndTime())
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveQuestionnaire(QuestionnaireDTO questionnaireDTO) {
        // 先准备问卷对象
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setQuestionnaireId(questionnaireDTO.getQuestionnaireId());
        questionnaire.setTitle(questionnaireDTO.getTitle());
        questionnaire.setDescription(questionnaireDTO.getDescription());
        questionnaire.setStatus(questionnaireDTO.getStatus());
        questionnaire.setType(questionnaireDTO.getType());
        questionnaire.setStartTime(questionnaireDTO.getStartTime());
        questionnaire.setEndTime(questionnaireDTO.getEndTime());

        // 总分 = 题目分值累加
        int totalScore = 0;
        if (questionnaireDTO.getQuestions() != null && !questionnaireDTO.getQuestions().isEmpty()) {
            totalScore = questionnaireDTO.getQuestions().stream()
                    .mapToInt(q -> q.getScore() == null ? 0 : q.getScore())
                    .sum();
        }
        questionnaire.setTotalScore(totalScore);

        if (questionnaireDTO.getQuestionnaireId() == null) {
            // 新增问卷
            questionnaire.setCreateBy(SecurityUtils.getUsername());
            questionnaire.setCreateTime(DateUtils.getNowDate());
            questionnaireMapper.insert(questionnaire);
            questionnaireDTO.setQuestionnaireId(questionnaire.getQuestionnaireId()); // 回写ID
        } else {
            // 更新问卷
            questionnaire.setUpdateBy(SecurityUtils.getUsername());
            questionnaire.setUpdateTime(DateUtils.getNowDate());
            questionnaireMapper.updateById(questionnaire);

            // 删除旧题目
            questionMapper.delete(new LambdaQueryWrapper<Question>()
                    .eq(Question::getQuestionnaireId, questionnaireDTO.getQuestionnaireId()));
        }

        // 保存题目列表
        List<Question> questions = questionnaireDTO.getQuestions();
        if (questions != null && !questions.isEmpty()) {
            int orderNum = 1;
            for (Question q : questions) {
                q.setQuestionnaireId(questionnaireDTO.getQuestionnaireId());
                q.setOrderNum(orderNum++);
                q.setCreateBy(SecurityUtils.getUsername());
                q.setCreateTime(DateUtils.getNowDate());
                q.setUpdateBy(SecurityUtils.getUsername());
                q.setUpdateTime(DateUtils.getNowDate());
                questionMapper.insert(q);
            }
        }
    }

    @Override
    public List<Question> selectQuestionByQuestionnaireId(Long questionnaireId) {
        return questionMapper.selectList(
                new LambdaQueryWrapper<Question>().eq(Question::getQuestionnaireId, questionnaireId)
                        .orderByAsc(Question::getOrderNum));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteQuestionnaire(Long[] questionnaireIds) {
        // 删除题目
        questionMapper.delete(new LambdaQueryWrapper<Question>().in(Question::getQuestionnaireId, questionnaireIds));
        // 删除问卷
        this.removeByIds(List.of(questionnaireIds));
    }
}