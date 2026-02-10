package com.mc.intervention.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.common.annotation.Excel;
import com.mc.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 干预处理记录表
 *
 * @author mc
 * @date 2026-02-03
 */
public class InterventionProcessRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    private Long recordId;

    /**
     * 通知ID
     */
    @Excel(name = "通知ID")
    private Long notificationId;

    /**
     * 处理人用户ID
     */
    @Excel(name = "处理人用户ID")
    private Long userId;

    /**
     * 处理内容
     */
    @Excel(name = "处理内容")
    private String processContent;

    /**
     * 处理时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "处理时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date processTime;

    /**
     * 处理结果
     */
    @Excel(name = "处理结果")
    private String processResult;

    /**
     * 状态（0正常 1异常）
     */
    @Excel(name = "状态", readConverterExp = "0=正常,1=异常")
    private String status;

    // 关联信息
    /**
     * 处理人姓名
     */
    private String userName;

    /**
     * 通知内容
     */
    private String notificationContent;

    /**
     * 学生姓名
     */
    private String studentName;

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setProcessContent(String processContent) {
        this.processContent = processContent;
    }

    public String getProcessContent() {
        return processContent;
    }

    public void setProcessTime(Date processTime) {
        this.processTime = processTime;
    }

    public Date getProcessTime() {
        return processTime;
    }

    public void setProcessResult(String processResult) {
        this.processResult = processResult;
    }

    public String getProcessResult() {
        return processResult;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentName() {
        return studentName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("recordId", getRecordId())
                .append("notificationId", getNotificationId())
                .append("userId", getUserId())
                .append("processContent", getProcessContent())
                .append("processTime", getProcessTime())
                .append("processResult", getProcessResult())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .append("userName", getUserName())
                .append("notificationContent", getNotificationContent())
                .append("studentName", getStudentName())
                .toString();
    }
}
