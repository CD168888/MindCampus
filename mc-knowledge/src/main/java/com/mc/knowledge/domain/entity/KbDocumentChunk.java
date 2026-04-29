package com.mc.knowledge.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.common.core.domain.BaseEntityPlus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

/**
 * 文档切分块对象 kg_document_chunk
 *
 * @author MindCampus
 */
@Data
@TableName("kg_document_chunk")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "文档切分块实体")
public class KbDocumentChunk extends BaseEntityPlus {
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private Map<String, Object> params;

    @Schema(description = "切分块ID", accessMode = Schema.AccessMode.READ_ONLY)
    @com.baomidou.mybatisplus.annotation.TableId(value = "chunk_id", type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Long chunkId;

    @Schema(description = "所属文档ID", required = true)
    private Long docId;

    @Schema(description = "所属知识库ID", required = true)
    private Long kbId;

    @Schema(description = "在文档中的顺序")
    private Integer chunkIndex;

    @Schema(description = "文本内容", required = true)
    private String content;

    @Schema(description = "内容MD5哈希（去重用）", maxLength = 64)
    private String chunkHash;

    @Schema(description = "Milvus 中的向量ID", maxLength = 100)
    private String milvusId;

    @Schema(description = "来源文件名")
    private String fileName;

    @Schema(description = "向量状态（0待入库 1已入库 2失败）", allowableValues = {"0", "1", "2"})
    private String vectorStatus;
}
