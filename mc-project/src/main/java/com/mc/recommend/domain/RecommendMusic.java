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
 * 心理音乐推荐对象 recommend_music
 *
 * @author caidu
 * @date 2025-11-08
 */
@Data
@TableName("recommend_music")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RecommendMusic extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 音乐ID */
    @TableId(type = IdType.AUTO)
    private Long musicId;

    /** 音乐标题 */
    @Excel(name = "音乐标题")
    private String title;

    /** 音频文件链接 */
    @Excel(name = "音频文件链接")
    private String mp3Url;

    /** 封面图链接 */
    @Excel(name = "封面图链接")
    private String coverUrl;

    /** 演唱者/作者 */
    @Excel(name = "演唱者/作者")
    private String artist;

    /** 音乐风格 */
    @Excel(name = "音乐风格")
    private String genre;

    /** 时长（秒） */
    @Excel(name = "时长（秒）")
    private Integer duration;

    /** 音乐简介 */
    @Excel(name = "音乐简介")
    private String description;

    /** 状态（0正常 1下架） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=下架")
    private String status;
}

