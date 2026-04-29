package com.mc.knowledge.kg.service;

import java.util.List;

/**
 * 向量化服务接口
 *
 * @author MindCampus
 */
public interface IEmbeddingService {

    /**
     * 单条文本向量化
     *
     * @param text 文本内容
     * @return float 数组（向量）
     */
    float[] embed(String text);

    /**
     * 批量文本向量化
     *
     * @param texts 文本列表
     * @return 向量列表
     */
    List<float[]> embedBatch(List<String> texts);

    /**
     * 获取向量维度
     *
     * @return 向量维度
     */
    int getDimension();
}
