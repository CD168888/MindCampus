package com.mc.recommend.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 音乐点赞对象 music_like
 *
 * @author caidu
 * @date 2025-11-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("music_like")
public class MusicLike extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 音乐ID */
    private Long musicId;

    /** 用户ID */
    private Long userId;

    /** 点赞时间 */
    private Date createTime;
}
