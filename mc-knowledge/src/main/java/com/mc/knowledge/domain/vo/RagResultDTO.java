package com.mc.knowledge.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RAG 检索结果 DTO
 *
 * @author MindCampus
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "RAG 检索结果")
public class RagResultDTO {

    @Schema(description = "Milvus 向量ID")
    private String milvusId;

    @Schema(description = "关联的 chunk ID")
    private Long chunkId;

    @Schema(description = "关联的文档ID")
    private Long docId;

    @Schema(description = "关联的知识库ID")
    private Long kbId;

    @Schema(description = "文本内容")
    private String content;

    @Schema(description = "来源文件名")
    private String fileName;

    @Schema(description = "相似度得分（0-1）")
    private Double score;

    @Schema(description = "所属知识库名称")
    private String kbName;

    @Schema(description = "文档标题")
    private String title;
}
