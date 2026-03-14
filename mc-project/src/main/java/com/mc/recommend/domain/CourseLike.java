package com.mc.recommend.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 课程点赞对象 course_like
 *
 * @author caidu
 * @date 2025-11-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_like")
public class CourseLike extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 课程ID */
    private Long courseId;

    /** 用户ID */
    private Long userId;

    /** 点赞时间 */
    private Date createTime;
}
