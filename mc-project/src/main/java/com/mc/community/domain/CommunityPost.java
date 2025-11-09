package com.mc.community.domain;

import com.mc.common.annotation.Excel;
import com.mc.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 社区帖子对象 community_post
 *
 * @author mc
 * @date 2025-01-09
 */
public class CommunityPost extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 帖子ID */
    private Long postId;

    /** 学生ID */
    @Excel(name = "学生ID")
    private Long studentId;

    /** 帖子内容 */
    @Excel(name = "帖子内容")
    private String content;

    /** 是否匿名（0否 1是） */
    @Excel(name = "是否匿名", readConverterExp = "0=否,1=是")
    private String isAnonymous;

    /** 状态（0正常 1屏蔽） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=屏蔽")
    private String status;

    /** 学生姓名（非数据库字段，用于显示和搜索） */
    private String studentName;

    /** 评论数量（非数据库字段，用于显示） */
    private Integer commentCount;

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setIsAnonymous(String isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public String getIsAnonymous() {
        return isAnonymous;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("postId", getPostId())
                .append("studentId", getStudentId())
                .append("content", getContent())
                .append("isAnonymous", getIsAnonymous())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .append("studentName", getStudentName())
                .append("commentCount", getCommentCount())
                .toString();
    }
}


