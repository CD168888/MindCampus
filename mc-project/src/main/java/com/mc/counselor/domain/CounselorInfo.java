package com.mc.counselor.domain;

import com.mc.common.annotation.Excel;
import com.mc.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 辅导员管理对象 counselor_info
 *
 * @author caidu
 * @date 2025-09-24
 */
public class CounselorInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 辅导员ID
     */
    private Long counselorId;

    /**
     * 用户id
     */
    @Excel(name = "用户id")
    private Long userId;

    /**
     * 姓名
     */
    @Excel(name = "姓名")
    private String name;

    /**
     * 联系电话
     */
    @Excel(name = "联系电话")
    private String phone;

    /**
     * 邮箱
     */
    @Excel(name = "邮箱")
    private String email;

    /**
     * 办公室地址
     */
    @Excel(name = "办公室地址")
    private String office;

    /**
     * 状态（0正常 1停用）
     */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public void setCounselorId(Long counselorId) {
        this.counselorId = counselorId;
    }

    public Long getCounselorId() {
        return counselorId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getOffice() {
        return office;
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
                .append("counselorId", getCounselorId())
                .append("userId", getUserId())
                .append("name", getName())
                .append("phone", getPhone())
                .append("email", getEmail())
                .append("office", getOffice())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
