package com.mc.recommend.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.common.annotation.Excel;
import com.mc.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 心理课程推荐对象 recommend_course
 *
 * @author caidu
 * @date 2025-11-08
 */
@Data
@TableName("recommend_course")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RecommendCourse extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 课程ID */
    @TableId(type = IdType.AUTO)
    private Long courseId;

    /** 课程标题 */
    @Excel(name = "课程标题")
    private String title;

    /** 视频文件链接 */
    @Excel(name = "视频文件链接")
    private String mp4Url;

    /** 封面图链接 */
    @Excel(name = "封面图链接")
    private String coverUrl;

    /** 讲师 */
    @Excel(name = "讲师")
    private String lecturer;

    /** 视频时长（秒） */
    @Excel(name = "视频时长（秒）")
    private Integer duration;

    /** 章节数 */
    @Excel(name = "章节数")
    private Integer chapters;

    /** 课程难度（初级/中级/高级） */
    @Excel(name = "课程难度", readConverterExp = "初级=初级,中级=中级,高级=高级")
    private String level;

    /** 课程简介 */
    @Excel(name = "课程简介")
    private String description;

    /** 状态（0正常 1下架） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=下架")
    private String status;
}

