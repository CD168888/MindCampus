package com.mc.intervention.mapper;

import com.mc.intervention.domain.InterventionNotification;
import com.mc.intervention.domain.vo.InterventionNotificationVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 干预通知表 数据层
 *
 * @author mc
 */
@Mapper
public interface InterventionNotificationMapper {
    /**
     * 查询干预通知表数据
     *
     * @param notification 干预通知表
     * @return 干预通知表数据
     */
    public List<InterventionNotification> selectNotificationList(InterventionNotificationVo notification);

    /**
     * 查询干预通知表信息
     *
     * @param notificationId 干预通知表ID
     * @return 干预通知表信息
     */
    public InterventionNotification selectNotificationById(Long notificationId);

    /**
     * 根据评测结果ID查询通知
     *
     * @param resultId 评测结果ID
     * @return 干预通知表信息
     */
    public List<InterventionNotification> selectNotificationByResultId(Long resultId);

    /**
     * 根据学生ID查询通知
     *
     * @param studentId 学生ID
     * @return 干预通知表信息
     */
    public List<InterventionNotification> selectNotificationByStudentId(Long studentId);

    /**
     * 根据用户ID查询通知
     *
     * @param userId 用户ID
     * @return 干预通知表信息
     */
    public List<InterventionNotification> selectNotificationByUserId(Long userId);

    /**
     * 新增干预通知表
     *
     * @param notification 干预通知表
     * @return 结果
     */
    public int insertNotification(InterventionNotification notification);

    /**
     * 修改干预通知表
     *
     * @param notification 干预通知表
     * @return 结果
     */
    public int updateNotification(InterventionNotification notification);

    /**
     * 删除干预通知表
     *
     * @param notificationId 干预通知表ID
     * @return 结果
     */
    public int deleteNotificationById(Long notificationId);

    /**
     * 批量删除干预通知表
     *
     * @param notificationIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteNotificationByIds(Long[] notificationIds);

    /**
     * 更新通知阅读状态
     *
     * @param notificationId 通知ID
     * @param readStatus     阅读状态
     * @return 结果
     */
    public int updateNotificationReadStatus(@Param("notificationId") Long notificationId, @Param("readStatus") String readStatus);

    /**
     * 更新通知处理状态
     *
     * @param notificationId 通知ID
     * @param processStatus  处理状态
     * @return 结果
     */
    public int updateNotificationProcessStatus(@Param("notificationId") Long notificationId, @Param("processStatus") String processStatus);
}
