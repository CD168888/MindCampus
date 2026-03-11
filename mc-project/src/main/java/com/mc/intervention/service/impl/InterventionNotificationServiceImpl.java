package com.mc.intervention.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mc.common.core.domain.entity.SysUser;
import com.mc.counselor.domain.CounselorInfo;
import com.mc.counselor.service.ICounselorDeptService;
import com.mc.counselor.service.ICounselorInfoService;
import com.mc.evaluation.domain.EvaluationResult;
import com.mc.evaluation.domain.QuestionnaireAnswer;
import com.mc.evaluation.mapper.QuestionnaireAnswerMapper;
import com.mc.evaluation.service.IEvaluationResultService;
import com.mc.intervention.domain.InterventionNotification;
import com.mc.intervention.domain.InterventionRiskConfig;
import com.mc.intervention.domain.vo.HighRiskUnnotifiedVo;
import com.mc.intervention.domain.vo.InterventionNotificationVo;
import com.mc.intervention.mapper.InterventionNotificationMapper;
import com.mc.intervention.service.IInterventionNotificationService;
import com.mc.intervention.service.IInterventionRiskConfigService;
import com.mc.student.domain.Student;
import com.mc.student.service.IStudentInfoService;
import com.mc.system.service.ISysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 干预通知表 服务实现
 *
 * @author mc
 */
@Service
public class InterventionNotificationServiceImpl implements IInterventionNotificationService {
    private static final Logger log = LoggerFactory.getLogger(InterventionNotificationServiceImpl.class);

    @Autowired
    private InterventionNotificationMapper notificationMapper;

    @Autowired
    private IEvaluationResultService evaluationResultService;

    @Autowired
    private IStudentInfoService studentInfoService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ICounselorDeptService counselorDeptService;

    @Autowired
    private ICounselorInfoService counselorInfoService;

    @Autowired
    private IInterventionRiskConfigService riskConfigService;

    @Autowired
    private QuestionnaireAnswerMapper questionnaireAnswerMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("mentalHealthChatClient")
    private ChatClient chatClient;

    /**
     * 查询干预通知表列表
     */
    @Override
    public List<InterventionNotification> selectNotificationList(InterventionNotificationVo notification) {
        return notificationMapper.selectNotificationList(notification);
    }

    /**
     * 查询高风险未通知的评测结果
     */
    @Override
    public List<HighRiskUnnotifiedVo> selectHighRiskUnnotifiedResults() {
        return notificationMapper.selectHighRiskUnnotifiedResults();
    }

    /**
     * 查询干预通知表信息
     */
    @Override
    public InterventionNotification selectNotificationById(Long notificationId) {
        return notificationMapper.selectNotificationById(notificationId);
    }

    /**
     * 根据评测结果ID查询通知
     */
    @Override
    public List<InterventionNotification> selectNotificationByResultId(Long resultId) {
        return notificationMapper.selectNotificationByResultId(resultId);
    }

    /**
     * 根据学生ID查询通知
     */
    @Override
    public List<InterventionNotification> selectNotificationByStudentId(Long studentId) {
        return notificationMapper.selectNotificationByStudentId(studentId);
    }

    /**
     * 根据用户ID查询通知
     */
    @Override
    public List<InterventionNotification> selectNotificationByUserId(Long userId) {
        return notificationMapper.selectNotificationByUserId(userId);
    }

    /**
     * 新增干预通知表
     */
    @Override
    public int insertNotification(InterventionNotification notification) {
        notification.setCreateTime(new Date());
        return notificationMapper.insertNotification(notification);
    }

    /**
     * 修改干预通知表
     */
    @Override
    public int updateNotification(InterventionNotification notification) {
        notification.setUpdateTime(new Date());
        return notificationMapper.updateNotification(notification);
    }

    /**
     * 删除干预通知表
     */
    @Override
    public int deleteNotificationById(Long notificationId) {
        return notificationMapper.deleteNotificationById(notificationId);
    }

    /**
     * 批量删除干预通知表
     */
    @Override
    public int deleteNotificationByIds(Long[] notificationIds) {
        return notificationMapper.deleteNotificationByIds(notificationIds);
    }

    /**
     * 更新通知阅读状态
     */
    @Override
    public int updateNotificationReadStatus(Long notificationId, String readStatus) {
        return notificationMapper.updateNotificationReadStatus(notificationId, readStatus);
    }

    /**
     * 更新通知处理状态
     */
    @Override
    public int updateNotificationProcessStatus(Long notificationId, String processStatus) {
        return notificationMapper.updateNotificationProcessStatus(notificationId, processStatus);
    }

    /**
     * 生成干预通知（异步）
     *
     * @param resultId   评测结果ID
     * @param studentId  学生ID
     */
    @Async("threadPoolTaskExecutor")
    @Override
    public void generateNotification(Long resultId, Long studentId) {
        try {
            // 1. 根据评测结果查询风险等级
            EvaluationResult evaluationResult = evaluationResultService.selectEvaluationResultByResultId(resultId);
            if (evaluationResult == null) {
                log.warn("评测结果不存在 - resultId: {}", resultId);
                return;
            }

            // 2. 获取风险等级配置
            String riskLevel = evaluationResult.getRiskLevel();
            InterventionRiskConfig riskConfig = riskConfigService.selectConfigByRiskLevel(riskLevel);
            if (riskConfig == null) {
                log.warn("风险等级配置不存在 - riskLevel: {}", riskLevel);
                return;
            }

            // 3. 检查配置是否启用
            if (!"0".equals(riskConfig.getStatus())) {
                log.info("风险等级配置已停用 - riskLevel: {}", riskLevel);
                return;
            }

            // 4. 获取通知模板
            String template = riskConfig.getNotificationTemplate();
            if (template == null || template.trim().isEmpty()) {
                template = "学生 {studentName} 的心理健康评测结果为 {riskLevel} 风险，建议及时关注并给予支持。";
            }

            // 5. 根据学生ID查询学生信息
            Student student = studentInfoService.selectStudentInfoByStudentId(studentId);
            if (student == null) {
                log.warn("学生信息不存在 - studentId: {}", studentId);
                return;
            }

            Long userId = student.getUserId();
            if (userId == null) {
                log.warn("学生用户ID不存在 - studentId: {}", studentId);
                return;
            }

            // 6. 根据用户ID查询用户信息，获取部门ID
            SysUser user = sysUserService.selectUserById(userId);
            if (user == null) {
                log.warn("用户信息不存在 - userId: {}", userId);
                return;
            }

            Long deptId = user.getDeptId();
            if (deptId == null) {
                log.warn("用户部门ID不存在 - userId: {}", userId);
                return;
            }

            // 7. 根据部门ID查询辅导员ID
            Long counselorId = counselorDeptService.selectCounselorIdByDeptId(deptId);
            if (counselorId == null) {
                log.warn("辅导员信息不存在 - deptId: {}", deptId);
                return;
            }

            // 8. 查询辅导员信息，获取用户ID
            CounselorInfo counselorInfo = counselorInfoService.selectCounselorInfoByCounselorId(counselorId);
            if (counselorInfo == null) {
                log.warn("辅导员详细信息不存在 - counselorId: {}", counselorId);
                return;
            }

            Long counselorUserId = counselorInfo.getUserId();
            if (counselorUserId == null) {
                log.warn("辅导员用户ID不存在 - counselorId: {}", counselorId);
                return;
            }

            // 9. 获取问卷答题信息
            List<QuestionnaireAnswer> answers = questionnaireAnswerMapper.selectByResultId(resultId);
            String questionnaireContent = buildQuestionnaireContent(answers);

            // 10. 获取AI分析结果
            String aiAnalysis = evaluationResult.getAiAnalysis();

            // 11. 调用AI生成通知内容
            String notificationContent = generateNotificationContent(
                    student,
                    evaluationResult,
                    questionnaireContent,
                    aiAnalysis,
                    template
            );

            // 12. 创建通知记录
            InterventionNotification notification = new InterventionNotification();
            notification.setResultId(resultId);
            notification.setStudentId(studentId);
            notification.setUserId(counselorUserId);
            notification.setDeptId(deptId);
            notification.setNotificationType("风险干预");
            notification.setNotificationContent(notificationContent);
            notification.setSendTime(new Date());
            notification.setReadStatus("0");
            notification.setProcessStatus("0");
            notification.setCreateTime(new Date());

            notificationMapper.insertNotification(notification);
            log.info("干预通知生成成功 - resultId: {}, studentId: {}, counselorUserId: {}",
                    resultId, studentId, counselorUserId);

        } catch (Exception e) {
            log.error("生成干预通知失败 - resultId: {}, studentId: {}", resultId, studentId, e);
        }
    }

    /**
     * 构建问卷内容字符串
     */
    private String buildQuestionnaireContent(List<QuestionnaireAnswer> answers) {
        if (answers == null || answers.isEmpty()) {
            return "无";
        }
        return answers.stream()
                .map(answer -> {
                    String question = "题目: " + answer.getContent();
                    String type = "类型: " + ("choice".equals(answer.getType()) ? "选择题" : "简答题");
                    String userAnswer = "用户回答: " + answer.getUserAnswer();
                    return String.format("%s\n%s\n%s", question, type, userAnswer);
                })
                .collect(Collectors.joining("\n\n"));
    }

    /**
     * 调用AI生成通知内容
     */
    private String generateNotificationContent(Student student,
                                                EvaluationResult evaluationResult,
                                                String questionnaireContent,
                                                String aiAnalysis,
                                                String template) {
        try {
            String prompt = buildNotificationPrompt(student, evaluationResult, questionnaireContent, aiAnalysis, template);

            String result = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            // 如果AI返回为空或异常，使用默认内容
            if (result == null || result.trim().isEmpty()) {
                return buildDefaultContent(student, evaluationResult, template);
            }

            return result.trim();
        } catch (Exception e) {
            log.warn("AI生成通知内容失败，使用默认内容", e);
            return buildDefaultContent(student, evaluationResult, template);
        }
    }

    /**
     * 构建通知提示词
     */
    private String buildNotificationPrompt(Student student,
                                            EvaluationResult evaluationResult,
                                            String questionnaireContent,
                                            String aiAnalysis,
                                            String template) {
        return String.format("""
                你是一位高校心理健康干预专家，负责为辅导员生成学生心理干预通知。
                
                请根据以下信息，生成一份专业、温暖、可操作的通知内容。

                ## 学生基本信息
                - 姓名: %s
                - 性别: %s
                - 年级: %s
                - 专业: %s

                ## 评测信息
                - 问卷标题: %s
                - 风险等级: %s

                ## AI分析结果
                %s

                ## 问卷答题详情
                %s

                ## 通知模板
                %s

                要求：
                1. 基于模板生成通知内容，但要根据学生的实际情况进行个性化调整
                2. 内容要专业、温暖、简洁
                3. 需要包含具体的问题描述和可行的干预建议
                4. 适当引用AI分析中的关键发现
                5. 结尾给出具体的行动建议
                6. 控制在300字以内
                7. 直接输出通知内容，不要有额外的说明
                """,
                student.getName(),
                student.getGender() != null ? ("0".equals(student.getGender()) ? "男" : "女") : "未知",
                student.getGrade() != null ? student.getGrade() : "未知",
                student.getMajor() != null ? student.getMajor() : "未知",
                evaluationResult.getQuestionnaireTitle(),
                evaluationResult.getRiskLevel(),
                aiAnalysis != null ? aiAnalysis : "无AI分析结果",
                questionnaireContent,
                template
        );
    }

    /**
     * 构建默认通知内容
     */
    private String buildDefaultContent(Student student, EvaluationResult evaluationResult, String template) {
        return template
                .replace("{studentName}", student.getName())
                .replace("{riskLevel}", evaluationResult.getRiskLevel())
                .replace("{questionnaireTitle}", evaluationResult.getQuestionnaireTitle());
    }

    /**
     * 批量生成干预通知
     */
    @Override
    public int batchGenerateNotification(List<Long> resultIds) {
        int count = 0;
        for (Long resultId : resultIds) {
            try {
                EvaluationResult evaluationResult = evaluationResultService.selectEvaluationResultByResultId(resultId);
                if (evaluationResult != null) {
                    generateNotification(resultId, evaluationResult.getStudentId());
                    count++;
                }
            } catch (Exception e) {
                log.error("批量生成干预通知失败 - resultId: {}", resultId, e);
            }
        }
        return count;
    }
}
