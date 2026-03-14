package com.mc.community.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 社区帖子点赞记录对象 community_post_like
 *
 * @author mc
 * @date 2026-03-14
 */
public class CommunityPostLike implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 帖子ID */
    private Long postId;

    /** 点赞用户ID */
    private Long userId;

    /** 点赞时间 */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "CommunityPostLike{" +
                "id=" + id +
                ", postId=" + postId +
                ", userId=" + userId +
                ", createTime=" + createTime +
                "}";
    }
}
