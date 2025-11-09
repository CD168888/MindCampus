package com.mc.community.domain;

import com.mc.common.annotation.Excel;
import com.mc.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * 社区评论对象 community_comment
 *
 * @author mc
 * @date 2025-01-09
 */
public class CommunityComment extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 评论ID */
    private Long commentId;

    /** 帖子ID */
    @Excel(name = "帖子ID")
    private Long postId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 用户名 */
    @Excel(name = "用户名")
    private String userName;

    /** 用户头像 */
    private String userAvatar;

    /** 评论内容 */
    @Excel(name = "评论内容")
    private String content;

    /** 父评论ID（用于实现评论回复） */
    private Long parentId;

    /** 回复的用户ID */
    private Long replyToUserId;

    /** 回复的用户名 */
    private String replyToUserName;

    /** 点赞数 */
    @Excel(name = "点赞数")
    private Long likeCount;

    /** 是否匿名（0否 1是） */
    @Excel(name = "是否匿名", readConverterExp = "0=否,1=是")
    private String isAnonymous;

    /** 状态（0正常 1屏蔽） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=屏蔽")
    private String status;

    /** 评论人ID（兼容旧字段） */
    private Long studentId;

    /** 学生姓名（非数据库字段，用于显示） */
    private String studentName;

    /** 是否已点赞（非数据库字段） */
    private Boolean isLiked;

    /** 子评论列表（用于树状结构展示） */
    private List<CommunityComment> replies;

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getCommentId() {
        return commentId;
    }

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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public Long getReplyToUserId() {
        return replyToUserId;
    }

    public void setReplyToUserId(Long replyToUserId) {
        this.replyToUserId = replyToUserId;
    }

    public String getReplyToUserName() {
        return replyToUserName;
    }

    public void setReplyToUserName(String replyToUserName) {
        this.replyToUserName = replyToUserName;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public Boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }

    public List<CommunityComment> getReplies() {
        return replies;
    }

    public void setReplies(List<CommunityComment> replies) {
        this.replies = replies;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("commentId", getCommentId())
                .append("postId", getPostId())
                .append("userId", getUserId())
                .append("userName", getUserName())
                .append("userAvatar", getUserAvatar())
                .append("content", getContent())
                .append("parentId", getParentId())
                .append("replyToUserId", getReplyToUserId())
                .append("replyToUserName", getReplyToUserName())
                .append("likeCount", getLikeCount())
                .append("studentId", getStudentId())
                .append("isAnonymous", getIsAnonymous())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .append("studentName", getStudentName())
                .append("isLiked", getIsLiked())
                .toString();
    }
}


