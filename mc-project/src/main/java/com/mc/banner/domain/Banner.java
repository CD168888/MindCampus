package com.mc.banner.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.common.annotation.Excel;
import com.mc.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * 轮播图对象 banner
 *
 * @author caidu
 * @date 2025-11-08
 */
@Data
@TableName("banner")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Banner extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 轮播图ID */
    @TableId(type = IdType.AUTO)
    private Long bannerId;

    /** 轮播标题 */
    @Excel(name = "轮播标题")
    private String title;

    /** 轮播图链接 */
    @Excel(name = "轮播图链接")
    private String imageUrl;

    /** 跳转链接（点击后访问的页面） */
    @Excel(name = "跳转链接")
    private String linkUrl;

    /** 显示顺序（越小越靠前） */
    @Excel(name = "显示顺序")
    private Integer sortOrder;

    /** 展示开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "展示开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 展示结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "展示结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 状态（0正常 1下架） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=下架")
    private String status;
}

