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

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 用户名 */
    @Excel(name = "用户名")
    private String userName;

    /** 用户头像 */
    private String userAvatar;

    /** 帖子标题 */
    @Excel(name = "帖子标题")
    private String title;

    /** 帖子内容 */
    @Excel(name = "帖子内容")
    private String content;

    /** 图片列表（逗号分隔） */
    private String images;

    /** 点赞数 */
    @Excel(name = "点赞数")
    private Long likeCount;

    /** 评论数 */
    @Excel(name = "评论数")
    private Long commentCount;

    /** 浏览数 */
    @Excel(name = "浏览数")
    private Long viewCount;

    /** 是否匿名（0否 1是） */
    @Excel(name = "是否匿名", readConverterExp = "0=否,1=是")
    private String isAnonymous;

    /** 状态（0正常 1屏蔽） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=屏蔽")
    private String status;

    /** 学生ID（兼容旧字段） */
    private Long studentId;

    /** 学生姓名（非数据库字段，用于显示和搜索） */
    private String studentName;

    /** 是否已点赞（非数据库字段，用于前端显示） */
    private Boolean isLiked;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("postId", getPostId())
                .append("userId", getUserId())
                .append("userName", getUserName())
                .append("userAvatar", getUserAvatar())
                .append("title", getTitle())
                .append("content", getContent())
                .append("images", getImages())
                .append("likeCount", getLikeCount())
                .append("commentCount", getCommentCount())
                .append("viewCount", getViewCount())
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


