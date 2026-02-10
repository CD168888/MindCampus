package com.mc.intervention.service.impl;

import com.mc.common.core.domain.entity.SysUser;
import com.mc.counselor.domain.CounselorInfo;
import com.mc.counselor.service.ICounselorDeptService;
import com.mc.counselor.service.ICounselorInfoService;
import com.mc.evaluation.domain.EvaluationResult;
import com.mc.evaluation.service.IEvaluationResultService;
import com.mc.intervention.domain.InterventionNotification;
import com.mc.intervention.domain.vo.InterventionNotificationVo;
import com.mc.intervention.mapper.InterventionNotificationMapper;
import com.mc.intervention.service.IInterventionNotificationService;
import com.mc.student.domain.Student;
import com.mc.student.service.IStudentInfoService;
import com.mc.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 干预通知表 服务实现
 *
 * @author mc
 */
@Service
public class InterventionNotificationServiceImpl implements IInterventionNotificationService {
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

    /**
     * 查询干预通知表列表
     */
    @Override
    public List<InterventionNotification> selectNotificationList(InterventionNotificationVo notification) {
        return notificationMapper.selectNotificationList(notification);
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
     * 生成干预通知
     */
    @Override
    public int generateNotification(Long resultId, Long studentId) {
        try {
            // 1. 根据评测结果查询风险等级
            EvaluationResult evaluationResult = evaluationResultService.selectEvaluationResultByResultId(resultId);
            if (evaluationResult == null) {
                return 0;
            }

            // 2. 判断是否为高风险
            String riskLevel = evaluationResult.getRiskLevel();
            if (!"高".equals(riskLevel)) {
                return 0;
            }

            // 3. 根据学生ID查询学生信息，获取用户ID
            Student student = studentInfoService.selectStudentInfoByStudentId(studentId);
            if (student == null) {
                return 0;
            }

            Long userId = student.getUserId();
            if (userId == null) {
                return 0;
            }

            // 4. 根据用户ID查询用户信息，获取部门ID
            SysUser user = sysUserService.selectUserById(userId);
            if (user == null) {
                return 0;
            }

            Long deptId = user.getDeptId();
            if (deptId == null) {
                return 0;
            }

            // 5. 根据部门ID查询辅导员ID
            Long counselorId = counselorDeptService.selectCounselorIdByDeptId(deptId);
            if (counselorId == null) {
                return 0;
            }

            // 6. 查询辅导员信息，获取用户ID
            CounselorInfo counselorInfo = counselorInfoService.selectCounselorInfoByCounselorId(counselorId);
            if (counselorInfo == null) {
                return 0;
            }

            Long counselorUserId = counselorInfo.getUserId();
            if (counselorUserId == null) {
                return 0;
            }

            // 7. 生成通知
            InterventionNotification notification = new InterventionNotification();
            notification.setResultId(resultId);
            notification.setStudentId(studentId);
            notification.setUserId(counselorUserId);
            notification.setDeptId(deptId);
            notification.setNotificationType("风险干预");

            // 构建通知内容
            String studentName = student.getName();
            String content = String.format("学生 %s 的心理健康评测结果为高风险，建议及时进行干预。", studentName);
            notification.setNotificationContent(content);

            notification.setSendTime(new Date());
            notification.setReadStatus("0");
            notification.setProcessStatus("0");
            notification.setCreateTime(new Date());

            int result = notificationMapper.insertNotification(notification);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 批量生成干预通知
     */
    @Override
    public int batchGenerateNotification(List<Long> resultIds) {
        int count = 0;
        for (Long resultId : resultIds) {
            try {
                // 根据评测结果查询学生ID
                com.mc.evaluation.domain.EvaluationResult evaluationResult = evaluationResultService.selectEvaluationResultByResultId(resultId);
                if (evaluationResult != null) {
                    count += generateNotification(resultId, evaluationResult.getStudentId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return count;
    }
}
