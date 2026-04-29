package com.mc.knowledge.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.common.annotation.Excel;
import com.mc.common.core.domain.BaseEntityPlus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

/**
 * 知识库对象 kg_knowledge_base
 *
 * @author MindCampus
 */
@Data
@TableName("kg_knowledge_base")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "知识库实体")
public class KnowledgeBase extends BaseEntityPlus {
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private Map<String, Object> params;

    @Schema(description = "知识库ID", accessMode = Schema.AccessMode.READ_ONLY)
    @com.baomidou.mybatisplus.annotation.TableId(value = "kb_id", type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Long kbId;

    @Schema(description = "知识库名称", required = true, maxLength = 100)
    @Excel(name = "知识库名称")
    private String kbName;

    @Schema(description = "知识库描述", maxLength = 500)
    @Excel(name = "知识库描述")
    private String kbDesc;

    @Schema(description = "分类", maxLength = 50)
    @Excel(name = "分类")
    private String category;

    @Schema(description = "文档数量")
    @Excel(name = "文档数量")
    private Integer docCount;

    @Schema(description = "状态（0启用 1禁用）", allowableValues = {"0", "1"})
    @Excel(name = "状态", readConverterExp = "0=启用,1=禁用")
    private String status;
}
