package com.mc.questionnaire.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.common.constant.UserConstants;
import com.mc.common.core.domain.entity.SysUser;
import com.mc.common.utils.DateUtils;
import com.mc.common.utils.SecurityUtils;
import com.mc.evaluation.domain.EvaluationResult;
import com.mc.evaluation.mapper.EvaluationResultMapper;
import com.mc.questionnaire.domain.Question;
import com.mc.questionnaire.domain.Questionnaire;
import com.mc.questionnaire.domain.dto.QuestionnaireDTO;
import com.mc.questionnaire.mapper.QuestionMapper;
import com.mc.questionnaire.mapper.QuestionnaireMapper;
import com.mc.questionnaire.service.IQuestionnaireService;
import com.mc.student.domain.Student;
import com.mc.student.mapper.StudentInfoMapper;
import com.mc.system.mapper.SysUserMapper;
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
    @Autowired
    private final EvaluationResultMapper evaluationResultMapper;
    @Autowired
    private final StudentInfoMapper studentInfoMapper;
    @Autowired
    private final SysUserMapper sysUserMapper;

    @Override
    public List<Questionnaire> selectQuestionnaireList(Questionnaire questionnaire) {
        return this.lambdaQuery()
                .like(questionnaire.getTitle() != null, Questionnaire::getTitle, questionnaire.getTitle())
                .like(questionnaire.getDescription() != null, Questionnaire::getDescription,
                        questionnaire.getDescription())
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

    @Override
    public boolean sendQuestionnaire(Long questionnaireId, Long studentId) {
        // 1. 根据学生ID查询学生信息，获取用户ID
        Student student = studentInfoMapper.selectById(studentId);
        if (student == null || student.getUserId() == null) {
            return false; // 学生信息不存在或未关联用户
        }

        // 2. 根据用户ID查询用户信息，检查用户状态
        SysUser user = sysUserMapper.selectUserById(student.getUserId());
        if (user == null) {
            return false; // 用户不存在
        }

        // 3. 检查用户是否被禁用（状态为1表示禁用）
        if (UserConstants.USER_DISABLE.equals(user.getStatus())) {
            return false; // 用户已禁用，不发送问卷
        }

        // 4. 检查是否已存在该学生的该问卷测评记录
        EvaluationResult query = new EvaluationResult();
        query.setStudentId(studentId);
        query.setQuestionnaireId(questionnaireId);
        List<EvaluationResult> existingResults = evaluationResultMapper.selectEvaluationResultList(query);

        // 如果已存在记录，则不重复插入
        if (existingResults != null && !existingResults.isEmpty()) {
            return false; // 返回 false 表示跳过
        }

        // 5. 插入新的测评记录
        evaluationResultMapper.insertEvaluationResult(new EvaluationResult() {
            {
                setQuestionnaireId(questionnaireId);
                setStudentId(studentId);
                setCompletionStatus("0"); // 0-未完成
                setReadStatus("0"); // 0-未读
                setAiStatus("0"); // 0-未分析
            }
        });

        return true; // 返回 true 表示发送成功
    }
}