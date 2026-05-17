package com.mc.common.service;

import java.util.List;

/**
 * 知识图谱查询服务接口
 * 用于 mc-ai 模块调用 mc-knowledge 模块的功能，避免循环依赖
 */
public interface KnowledgeQueryService {

    /**
     * 构建知识图谱上下文
     *
     * @param userId 用户ID
     * @return 知识图谱上下文文本
     */
    String buildKgContext(Long userId);

    /**
     * RAG 向量检索
     *
     * @param query 查询文本
     * @param userId 用户ID（可选，用于过滤）
     * @param topK 返回结果数量
     * @return 检索结果列表
     */
    List<RagResult> ragRetrieve(String query, Long userId, int topK);

    /**
     * RAG 检索结果
     */
    class RagResult {
        private String content;
        private double score;
        private String kbName;

        public RagResult() {}

        public RagResult(String content, double score, String kbName) {
            this.content = content;
            this.score = score;
            this.kbName = kbName;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public String getKbName() {
            return kbName;
        }

        public void setKbName(String kbName) {
            this.kbName = kbName;
        }
    }
}
