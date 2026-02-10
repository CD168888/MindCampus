package com.mc.intervention.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.common.annotation.Excel;
import com.mc.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 干预通知表
 *
 * @author mc
 * @date 2026-02-03
 */
public class InterventionNotification extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 通知ID
     */
    private Long notificationId;

    /**
     * 评测结果ID
     */
    @Excel(name = "评测结果ID")
    private Long resultId;

    /**
     * 学生ID
     */
    @Excel(name = "学生ID")
    private Long studentId;

    /**
     * 用户ID（负责人）
     */
    @Excel(name = "用户ID（负责人）")
    private Long userId;

    /**
     * 部门ID
     */
    @Excel(name = "部门ID")
    private Long deptId;

    /**
     * 通知类型
     */
    @Excel(name = "通知类型")
    private String notificationType;

    /**
     * 通知内容
     */
    @Excel(name = "通知内容")
    private String notificationContent;

    /**
     * 发送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "发送时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;

    /**
     * 阅读状态（0未读 1已读）
     */
    @Excel(name = "阅读状态", readConverterExp = "0=未读,1=已读")
    private String readStatus;

    /**
     * 处理状态（0待处理 1已处理 2处理中）
     */
    @Excel(name = "处理状态", readConverterExp = "0=待处理,1=已处理,2=处理中")
    private String processStatus;

    // 关联信息
    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 负责人姓名
     */
    private String userName;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 评测结果
     */
    private String riskLevel;

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public Long getResultId() {
        return resultId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }

    public String getReadStatus() {
        return readStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("notificationId", getNotificationId())
                .append("resultId", getResultId())
                .append("studentId", getStudentId())
                .append("userId", getUserId())
                .append("deptId", getDeptId())
                .append("notificationType", getNotificationType())
                .append("notificationContent", getNotificationContent())
                .append("sendTime", getSendTime())
                .append("readStatus", getReadStatus())
                .append("processStatus", getProcessStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .append("studentName", getStudentName())
                .append("userName", getUserName())
                .append("deptName", getDeptName())
                .append("riskLevel", getRiskLevel())
                .toString();
    }
}
