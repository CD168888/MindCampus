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
 * 知识库文档对象 kg_document
 *
 * @author MindCampus
 */
@Data
@TableName("kg_document")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "知识库文档实体")
public class KbDocument extends BaseEntityPlus {
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private Map<String, Object> params;

    @Schema(description = "文档ID", accessMode = Schema.AccessMode.READ_ONLY)
    @com.baomidou.mybatisplus.annotation.TableId(value = "doc_id", type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Long docId;

    @Schema(description = "所属知识库ID", required = true)
    @Excel(name = "所属知识库ID")
    private Long kbId;

    @Schema(description = "文件名", required = true, maxLength = 255)
    @Excel(name = "文件名")
    private String fileName;

    @Schema(description = "文件存储路径（OSS）", maxLength = 500)
    @Excel(name = "文件存储路径")
    private String fileUrl;

    @Schema(description = "文件类型（txt/md/pdf/docx）", maxLength = 20)
    @Excel(name = "文件类型")
    private String fileType;

    @Schema(description = "文件大小（字节）")
    @Excel(name = "文件大小")
    private Long fileSize;

    @Schema(description = "切分段数")
    @Excel(name = "切分段数")
    private Integer chunkCount;

    @Schema(description = "向量化状态（0待处理 1处理中 2已完成 3失败）", allowableValues = {"0", "1", "2", "3"})
    @Excel(name = "向量化状态", readConverterExp = "0=待处理,1=处理中,2=已完成,3=失败")
    private String embeddingStatus;

    @Schema(description = "向量化完成时间")
    @Excel(name = "向量化完成时间")
    private java.time.LocalDateTime embeddingTime;

    @Schema(description = "状态（0正常 1禁用）", allowableValues = {"0", "1"})
    @Excel(name = "状态", readConverterExp = "0=正常,1=禁用")
    private String status;
}
