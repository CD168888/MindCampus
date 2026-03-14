package com.mc.recommend.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 文章点赞对象 article_like
 *
 * @author caidu
 * @date 2025-11-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("article_like")
public class ArticleLike extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 文章ID */
    private Long articleId;

    /** 用户ID */
    private Long userId;

    /** 点赞时间 */
    private Date createTime;
}
