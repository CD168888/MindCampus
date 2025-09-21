package com.mc.student.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.mc.common.annotation.Excel;
import com.mc.common.core.domain.BaseEntity;

/**
 * 学生信息对象 student_info
 *
 * @author caidu
 * @date 2025-09-21
 */
public class StudentInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 用户id
     */
    @Excel(name = "用户id")
    private Long userId;

    /**
     * 学号
     */
    @Excel(name = "学号")
    private String studentNo;

    /**
     * 姓名
     */
    @Excel(name = "姓名")
    private String name;

    /**
     * 性别（0/1）
     */
    @Excel(name = "性别", readConverterExp = "0=/1")
    private String gender;

    /**
     * 年级
     */
    @Excel(name = "年级")
    private String grade;

    /**
     * 专业
     */
    @Excel(name = "专业")
    private String major;

    /**
     * 班级
     */
    @Excel(name = "班级")
    private String className;

    /**
     * 联系电话
     */
    @Excel(name = "联系电话")
    private String phone;

    /**
     * 状态（0正常 1异常）
     */
    @Excel(name = "状态", readConverterExp = "0=正常,1=异常")
    private String status;

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

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getGrade() {
        return grade;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMajor() {
        return major;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("studentId", getStudentId())
                .append("userId", getUserId())
                .append("studentNo", getStudentNo())
                .append("name", getName())
                .append("gender", getGender())
                .append("grade", getGrade())
                .append("major", getMajor())
                .append("className", getClassName())
                .append("phone", getPhone())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
