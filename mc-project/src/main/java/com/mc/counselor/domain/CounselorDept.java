package com.mc.counselor.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.common.core.domain.BaseEntityPlus;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 辅导员部门关联对象 counselor_dept
 *
 * @author caidu
 * @date 2025-12-22
 */
@TableName("counselor_dept")
public class CounselorDept extends BaseEntityPlus {
    private static final long serialVersionUID = 1L;

    /** 关联ID */
    private Long deptCounselorId;

    /** 辅导员ID */
    private Long counselorId;

    /** 部门ID */
    private Long deptId;

    /** 状态（0正常 1停用） */
    private String status;

    public void setDeptCounselorId(Long deptCounselorId) {
        this.deptCounselorId = deptCounselorId;
    }

    public Long getDeptCounselorId() {
        return deptCounselorId;
    }

    public void setCounselorId(Long counselorId) {
        this.counselorId = counselorId;
    }

    public Long getCounselorId() {
        return counselorId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getDeptId() {
        return deptId;
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
                .append("deptCounselorId", getDeptCounselorId())
                .append("counselorId", getCounselorId())
                .append("deptId", getDeptId())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}