package com.mc.recommend.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.common.annotation.Excel;
import com.mc.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 心理文章推荐对象 recommend_article
 *
 * @author caidu
 * @date 2025-11-08
 */
@Data
@TableName("recommend_article")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RecommendArticle extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 文章ID */
    @TableId(type = IdType.AUTO)
    private Long articleId;

    /** 文章标题 */
    @Excel(name = "文章标题")
    private String title;

    /** 文章内容（Markdown格式） */
    @Excel(name = "文章内容")
    private String content;

    /** 文章摘要 */
    @Excel(name = "文章摘要")
    private String summary;

    /** 作者 */
    @Excel(name = "作者")
    private String author;

    /** 阅读量 */
    @Excel(name = "阅读量")
    private Integer readCount;

    /** 文章分类 */
    @Excel(name = "文章分类")
    private String category;

    /** 状态（0正常 1下架） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=下架")
    private String status;

    /** 搜索关键词（用于模糊搜索 title 或 author，非数据库字段） */
    @TableField(exist = false)
    private String keyword;
}
