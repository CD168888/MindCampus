package com.mc.knowledge.kg.service.impl;

import com.mc.knowledge.domain.vo.RagResultDTO;
import com.mc.knowledge.kg.graph.Neo4jClient;
import com.mc.knowledge.kg.service.IEmbeddingService;
import com.mc.knowledge.kg.service.ILightRagService;
import com.mc.knowledge.kg.service.IStudentProfileKGService;
import com.mc.knowledge.kg.service.LightRagResult;
import com.mc.knowledge.kg.vector.MilvusVectorClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * LightRAG 风格检索服务实现
 * 
 * LightRAG 的核心思想：
 * 1. NaiveRAG - 简单的向量检索
 * 2. LocalRAG - 基于向量相似度的局部检索，关注文档片段
 * 3. GlobalRAG - 基于知识图谱的全局检索，关注实体和关系
 * 4. Hybrid - 混合模式，结合 Local + Global
 * 
 * 本实现整合了：
 * - Milvus 向量检索（Local）
 * - Neo4j 知识图谱检索（Global）
 * - 学生画像上下文增强
 */
@Service
@Slf4j
public class LightRagServiceImpl implements ILightRagService {

    @Resource
    private MilvusVectorClient milvusVectorClient;

    @Resource
    private IEmbeddingService embeddingService;

    @Resource
    private Neo4jClient neo4jClient;

    @Resource
    private IStudentProfileKGService studentProfileKGService;

    @Value("${spring.knowledge.rag.top-k:5}")
    private int defaultTopK;

    @Value("${spring.knowledge.rag.min-score:0.5}")
    private double minScore;

    @Value("${spring.knowledge.rag.hybrid-mode:true}")
    private boolean hybridModeEnabled;

    /**
     * 混合检索 - 结合向量检索和知识图谱检索
     * 
     * 流程：
     * 1. 学生画像上下文（Neo4j）
     * 2. 向量检索相关文档片段（Milvus）
     * 3. 知识图谱实体关联检索
     * 4. 整合生成完整上下文
     */
    @Override
    public LightRagResult hybridSearch(String query, Long userId, Long kbId, int topK) {
        long startTime = System.currentTimeMillis();
        LightRagResult result = new LightRagResult();
        result.setMode("hybrid");

        try {
            // 1. 获取学生画像上下文（来自 Neo4j）
            String kgContext = studentProfileKGService.buildKgContext(userId);
            result.setKgContext(kgContext);

            // 2. 向量检索（Local 检索）
            float[] queryVector = embeddingService.embed(query);
            List<RagResultDTO> chunks = milvusVectorClient.search(queryVector, kbId, topK, minScore);
            result.setChunks(chunks);

            // 3. 从检索结果中提取关键词，查询相关实体
            List<LightRagResult.EntityInfo> entities = extractEntitiesFromChunks(chunks, userId);
            result.setEntities(entities);

            // 4. 获取相关关系信息
            List<LightRagResult.RelationInfo> relations = extractRelationsFromEntities(entities, userId);
            result.setRelations(relations);

            // 5. 构建完整上下文
            result.buildFullContext();

            long cost = System.currentTimeMillis() - startTime;
            result.setCostMs(cost);
            log.info("[LightRAG] 混合检索完成 - query: {}, userId: {}, chunks: {}, cost: {}ms", 
                query, userId, chunks.size(), cost);

        } catch (Exception e) {
            log.error("[LightRAG] 混合检索失败 - query: {}, userId: {}", query, userId, e);
        }

        return result;
    }

    /**
     * 局部检索 - 仅基于向量相似度
     */
    @Override
    public LightRagResult localSearch(String query, Long kbId, int topK) {
        long startTime = System.currentTimeMillis();
        LightRagResult result = new LightRagResult();
        result.setMode("local");

        try {
            float[] queryVector = embeddingService.embed(query);
            List<RagResultDTO> chunks = milvusVectorClient.search(queryVector, kbId, topK, minScore);
            result.setChunks(chunks);
            result.buildFullContext();

            long cost = System.currentTimeMillis() - startTime;
            result.setCostMs(cost);
            log.info("[LightRAG] 局部检索完成 - query: {}, kbId: {}, chunks: {}, cost: {}ms", 
                query, kbId, chunks.size(), cost);

        } catch (Exception e) {
            log.error("[LightRAG] 局部检索失败 - query: {}, kbId: {}", query, kbId, e);
        }

        return result;
    }

    /**
     * 全局检索 - 基于知识图谱
     * 
     * 通过分析查询中的实体，从 Neo4j 中检索相关的图谱信息
     */
    @Override
    public LightRagResult globalSearch(String query, Long userId) {
        long startTime = System.currentTimeMillis();
        LightRagResult result = new LightRagResult();
        result.setMode("global");

        try {
            // 1. 获取学生完整画像
            String kgContext = studentProfileKGService.buildKgContext(userId);
            result.setKgContext(kgContext);

            // 2. 提取查询中的关键实体
            List<String> keywords = extractKeywords(query);
            
            // 3. 根据关键词检索相关实体
            List<LightRagResult.EntityInfo> entities = searchEntitiesInGraph(keywords, userId);
            result.setEntities(entities);

            // 4. 获取关联关系
            List<LightRagResult.RelationInfo> relations = extractRelationsFromEntities(entities, userId);
            result.setRelations(relations);

            // 5. 构建上下文
            result.buildFullContext();

            long cost = System.currentTimeMillis() - startTime;
            result.setCostMs(cost);
            log.info("[LightRAG] 全局检索完成 - query: {}, userId: {}, entities: {}, cost: {}ms", 
                query, userId, entities.size(), cost);

        } catch (Exception e) {
            log.error("[LightRAG] 全局检索失败 - query: {}, userId: {}", query, userId, e);
        }

        return result;
    }

    /**
     * 零样本检索 - 不依赖用户画像的通用检索
     */
    @Override
    public LightRagResult zeroShotSearch(String query, Long kbId, int topK) {
        long startTime = System.currentTimeMillis();
        LightRagResult result = new LightRagResult();
        result.setMode("zero-shot");

        try {
            // 直接进行向量检索，不依赖用户上下文
            float[] queryVector = embeddingService.embed(query);
            List<RagResultDTO> chunks = milvusVectorClient.search(queryVector, kbId, topK, minScore);
            result.setChunks(chunks);
            result.buildFullContext();

            long cost = System.currentTimeMillis() - startTime;
            result.setCostMs(cost);
            log.info("[LightRAG] 零样本检索完成 - query: {}, kbId: {}, chunks: {}, cost: {}ms", 
                query, kbId, chunks.size(), cost);

        } catch (Exception e) {
            log.error("[LightRAG] 零样本检索失败 - query: {}, kbId: {}", query, kbId, e);
        }

        return result;
    }

    /**
     * 从检索片段中提取实体信息
     */
    private List<LightRagResult.EntityInfo> extractEntitiesFromChunks(List<RagResultDTO> chunks, Long userId) {
        // 简单实现：根据检索片段的内容，关联学生画像中的相关信息
        // 实际项目中可以使用 NER 模型提取实体
        return List.of();
    }

    /**
     * 从实体列表中提取关系信息
     */
    private List<LightRagResult.RelationInfo> extractRelationsFromEntities(
            List<LightRagResult.EntityInfo> entities, Long userId) {
        // 简化实现：返回空列表
        // 实际项目中可以从 Neo4j 中查询实体间的关系
        return List.of();
    }

    /**
     * 在图谱中搜索相关实体
     */
    private List<LightRagResult.EntityInfo> searchEntitiesInGraph(List<String> keywords, Long userId) {
        // 简化实现：返回空列表
        // 实际项目中可以从 Neo4j 中根据关键词搜索实体
        return List.of();
    }

    /**
     * 从查询中提取关键词
     */
    private List<String> extractKeywords(String query) {
        // 简化实现：直接返回原查询
        // 实际项目中可以使用分词、实体识别等 NLP 技术
        return List.of(query);
    }
}
