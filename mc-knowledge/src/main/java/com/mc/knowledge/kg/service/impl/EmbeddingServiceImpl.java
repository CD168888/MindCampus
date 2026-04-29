package com.mc.knowledge.kg.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mc.knowledge.kg.service.IEmbeddingService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * DashScope text-embedding-v3 向量化服务实现
 * <p>
 * 通过 RestTemplate 直连 DashScope Embedding API，不依赖 EmbeddingModel。
 *
 * @author MindCampus
 */
@Service
@Slf4j
public class EmbeddingServiceImpl implements IEmbeddingService {

    private static final String EMBEDDING_API_URL =
        "https://dashscope.aliyuncs.com/compatible-mode/v1/embeddings";
    private static final String MODEL_NAME = "text-embedding-v3";

    @Resource
    private RestTemplate restTemplate;

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    @Value("${spring.knowledge.embedding.batch-size:25}")
    private int batchSize;

    @Value("${spring.knowledge.embedding.dimension:1024}")
    private int dimension;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public float[] embed(String text) {
        try {
            List<float[]> results = embedBatch(Collections.singletonList(text));
            return results.isEmpty() ? new float[dimension] : results.get(0);
        } catch (Exception e) {
            log.error("[Embedding] 单条向量化失败: {}", e.getMessage());
            return new float[dimension];
        }
    }

    @Override
    public List<float[]> embedBatch(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            return new ArrayList<>();
        }

        List<float[]> results = new ArrayList<>();
        for (int i = 0; i < texts.size(); i += batchSize) {
            int end = Math.min(i + batchSize, texts.size());
            List<String> batch = texts.subList(i, end);

            try {
                List<float[]> vectors = callEmbeddingApi(batch);
                if (vectors.isEmpty()) {
                    for (int j = 0; j < batch.size(); j++) {
                        results.add(new float[dimension]);
                    }
                } else {
                    results.addAll(vectors);
                }
            } catch (Exception e) {
                log.error("[Embedding] 批量向量化失败 - 第 {} 批: {}", i / batchSize + 1, e.getMessage());
                for (int j = 0; j < batch.size(); j++) {
                    results.add(new float[dimension]);
                }
            }
        }
        return results;
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    /**
     * 调用 DashScope Embedding API，返回多个文本的向量列表
     */
    private List<float[]> callEmbeddingApi(List<String> texts) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", MODEL_NAME);
        body.put("input", texts);
        body.put("dimensions", dimension);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(
            EMBEDDING_API_URL,
            HttpMethod.POST,
            entity,
            String.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return parseEmbeddingResponse(response.getBody());
        } else {
            throw new RuntimeException("Embedding API 返回错误: " + response.getStatusCode());
        }
    }

    private List<float[]> parseEmbeddingResponse(String jsonResponse) throws Exception {
        List<float[]> vectors = new ArrayList<>();
        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode data = root.get("data");
        if (data == null || !data.isArray()) {
            return vectors;
        }

        for (JsonNode item : data) {
            JsonNode embeddingNode = item.get("embedding");
            if (embeddingNode == null || !embeddingNode.isArray()) {
                vectors.add(new float[dimension]);
                continue;
            }
            float[] vec = new float[embeddingNode.size()];
            for (int i = 0; i < embeddingNode.size(); i++) {
                vec[i] = (float) embeddingNode.get(i).asDouble();
            }
            vectors.add(vec);
        }
        return vectors;
    }
}
