package com.mc.knowledge.kg.service;

/**
 * LightRAG 风格的混合检索服务接口
 * 结合向量检索 + 知识图谱检索，提供更全面的 RAG 能力
 */
public interface ILightRagService {

    /**
     * 混合检索 - 结合向量检索和图谱检索
     * @param query 用户查询
     * @param userId 用户ID（用于获取学生画像上下文）
     * @param kbId 知识库ID
     * @param topK 返回结果数量
     * @return 检索结果，包含向量检索片段和图谱实体信息
     */
    LightRagResult hybridSearch(String query, Long userId, Long kbId, int topK);

    /**
     * 局部检索 - 仅基于向量相似度检索
     * @param query 用户查询
     * @param kbId 知识库ID
     * @param topK 返回结果数量
     * @return 向量检索片段
     */
    LightRagResult localSearch(String query, Long kbId, int topK);

    /**
     * 全局检索 - 基于知识图谱的实体关系检索
     * @param query 用户查询
     * @param userId 用户ID
     * @return 图谱相关的上下文信息
     */
    LightRagResult globalSearch(String query, Long userId);

    /**
     * 零样本检索 - 不依赖用户画像的通用检索
     * @param query 用户查询
     * @param kbId 知识库ID
     * @param topK 返回结果数量
     * @return 检索结果
     */
    LightRagResult zeroShotSearch(String query, Long kbId, int topK);
}
