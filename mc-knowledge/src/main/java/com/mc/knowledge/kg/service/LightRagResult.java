package com.mc.knowledge.kg.service;

import com.mc.knowledge.domain.vo.RagResultDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * LightRAG 检索结果封装
 * 整合向量检索片段 + 图谱上下文 + 实体信息
 */
@Data
public class LightRagResult {

    /**
     * 向量检索片段
     */
    private List<RagResultDTO> chunks;

    /**
     * 知识图谱上下文（用于增强生成）
     */
    private String kgContext;

    /**
     * 关联的实体列表
     */
    private List<EntityInfo> entities;

    /**
     * 关联的关系列表
     */
    private List<RelationInfo> relations;

    /**
     * 最终用于增强生成的完整上下文
     */
    private String fullContext;

    /**
     * 检索模式：local, global, hybrid, zero-shot
     */
    private String mode;

    /**
     * 检索耗时（毫秒）
     */
    private long costMs;

    public LightRagResult() {
        this.chunks = new ArrayList<>();
        this.entities = new ArrayList<>();
        this.relations = new ArrayList<>();
    }

    /**
     * 实体信息
     */
    @Data
    public static class EntityInfo {
        private String id;
        private String name;
        private String type;
        private String description;
        private double score;

        public EntityInfo() {}

        public EntityInfo(String id, String name, String type) {
            this.id = id;
            this.name = name;
            this.type = type;
        }
    }

    /**
     * 关系信息
     */
    @Data
    public static class RelationInfo {
        private String sourceId;
        private String targetId;
        private String sourceName;
        private String targetName;
        private String relationType;
        private String description;

        public RelationInfo() {}
    }

    /**
     * 添加实体
     */
    public void addEntity(EntityInfo entity) {
        this.entities.add(entity);
    }

    /**
     * 添加关系
     */
    public void addRelation(RelationInfo relation) {
        this.relations.add(relation);
    }

    /**
     * 构建完整上下文
     */
    public String buildFullContext() {
        StringBuilder sb = new StringBuilder();

        // 1. 知识图谱上下文
        if (kgContext != null && !kgContext.isEmpty()) {
            sb.append(kgContext).append("\n\n");
        }

        // 2. 关联实体信息
        if (!entities.isEmpty()) {
            sb.append("【相关实体】\n");
            for (EntityInfo entity : entities) {
                sb.append("- ").append(entity.getName())
                  .append("（").append(entity.getType()).append("）");
                if (entity.getDescription() != null) {
                    sb.append("：").append(entity.getDescription());
                }
                sb.append("\n");
            }
            sb.append("\n");
        }

        // 3. 向量检索片段
        if (chunks != null && !chunks.isEmpty()) {
            sb.append("【相关文档片段】\n");
            int idx = 1;
            for (RagResultDTO chunk : chunks) {
                sb.append(idx++).append(". ");
                if (chunk.getTitle() != null) {
                    sb.append("[").append(chunk.getTitle()).append("] ");
                }
                sb.append(chunk.getContent());
                if (idx <= chunks.size()) {
                    sb.append("\n\n");
                }
            }
        }

        this.fullContext = sb.toString();
        return this.fullContext;
    }
}
