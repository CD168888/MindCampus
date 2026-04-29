package com.mc.knowledge.kg.vector;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mc.knowledge.domain.entity.KbDocumentChunk;
import com.mc.knowledge.domain.vo.RagResultDTO;
import com.mc.knowledge.kg.service.IEmbeddingService;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.collection.request.HasCollectionReq;
import io.milvus.v2.service.vector.request.DeleteReq;
import io.milvus.v2.service.vector.request.InsertReq;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.response.DeleteResp;
import io.milvus.v2.service.vector.response.InsertResp;
import io.milvus.v2.service.vector.response.SearchResp;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MilvusVectorClient {

    @Resource
    private MilvusClientV2 milvusClient;

    @Resource
    private IEmbeddingService embeddingService;

    private static final String COLLECTION_NAME = "knowledge_base_chunks";
    private static final String VECTOR_FIELD = "embedding";

    private final Gson gson = new Gson();

    @PostConstruct
    public void initCollection() {
        try {
            Boolean exists = milvusClient.hasCollection(
                HasCollectionReq.builder()
                    .collectionName(COLLECTION_NAME)
                    .build());
            if (!exists) {
                milvusClient.createCollection(
                    CreateCollectionReq.builder()
                        .collectionName(COLLECTION_NAME)
                        .dimension(embeddingService.getDimension())
                        .description("知识库文档向量集合")
                        .build());
                log.info("[Milvus] Collection '{}' 创建成功", COLLECTION_NAME);
            } else {
                log.info("[Milvus] Collection '{}' 已存在", COLLECTION_NAME);
            }
        } catch (Exception e) {
            log.warn("[Milvus] 初始化 Collection 失败: {}", e.getMessage());
        }
    }

    public List<Long> insertVectors(List<KbDocumentChunk> chunks, List<float[]> vectors, String kbName) {
        if (chunks.size() != vectors.size()) {
            throw new IllegalArgumentException("chunks 和 vectors 数量不一致");
        }

        List<JsonObject> rows = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            JsonObject row = new JsonObject();
            row.addProperty("chunk_id", String.valueOf(chunks.get(i).getChunkId()));
            row.addProperty("doc_id", chunks.get(i).getDocId());
            row.addProperty("kb_id", chunks.get(i).getKbId());
            row.addProperty("content", chunks.get(i).getContent());
            row.addProperty("file_name",
                chunks.get(i).getFileName() != null ? chunks.get(i).getFileName() : "");
            row.addProperty("kb_name", kbName != null ? kbName : "");
            row.add(VECTOR_FIELD, gson.toJsonTree(toList(vectors.get(i))));
            rows.add(row);
        }

        try {
            InsertResp resp = milvusClient.insert(
                InsertReq.builder()
                    .collectionName(COLLECTION_NAME)
                    .data(rows)
                    .build());

            if (resp.getInsertCnt() > 0) {
                log.info("[Milvus] 插入 {} 条向量成功", resp.getInsertCnt());
            }
            return chunks.stream().map(c -> c.getChunkId()).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("[Milvus] 插入向量失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<RagResultDTO> search(float[] queryVector, Long kbId, int topK, double minScore) {
        try {
            SearchResp resp = milvusClient.search(
                SearchReq.builder()
                    .collectionName(COLLECTION_NAME)
                    .data(List.of(new FloatVec(queryVector)))
                    .outputFields(List.of("chunk_id", "content", "doc_id", "kb_id", "file_name", "kb_name"))
                    .topK(topK)
                    .build());

            List<RagResultDTO> results = new ArrayList<>();

            List<List<SearchResp.SearchResult>> allResults = resp.getSearchResults();
            if (allResults != null) {
                for (List<SearchResp.SearchResult> group : allResults) {
                    for (SearchResp.SearchResult sr : group) {
                        Float scoreVal = sr.getScore();
                        double score = scoreVal != null ? scoreVal : 0.0;
                        if (score < minScore) continue;

                        Map<String, Object> entity = sr.getEntity();
                        if (entity == null) continue;

                        Long resultKbId = parseLong(entity.get("kb_id"));
                        if (kbId != null && !kbId.equals(resultKbId)) continue;

                        String chunkIdStr = String.valueOf(entity.get("chunk_id"));
                        String content = String.valueOf(entity.get("content"));
                        String fileName = String.valueOf(entity.get("file_name"));
                        String kbNameResult = String.valueOf(entity.get("kb_name"));
                        Long docIdVal = parseLong(entity.get("doc_id"));

                        results.add(RagResultDTO.builder()
                            .milvusId(chunkIdStr)
                            .chunkId(parseLong(chunkIdStr))
                            .docId(docIdVal)
                            .kbId(resultKbId)
                            .content(content)
                            .fileName(fileName)
                            .kbName(kbNameResult)
                            .score(score)
                            .build());
                    }
                }
            }
            log.debug("[Milvus] 检索到 {} 条结果", results.size());
            return results;
        } catch (Exception e) {
            log.error("[Milvus] 向量检索异常: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public void deleteByChunkIds(List<String> chunkIds) {
        try {
            List<Object> objectChunkIds = new ArrayList<>(chunkIds);
            DeleteReq deleteReq = DeleteReq.builder()
                    .collectionName(COLLECTION_NAME)
                    .ids(objectChunkIds)
                    .build();

            DeleteResp resp = milvusClient.delete(deleteReq);
            if (resp.getDeleteCnt() > 0) {
                log.info("[Milvus] 成功删除 {} 条向量数据", resp.getDeleteCnt());
            }
        } catch (Exception e) {
            log.error("[Milvus] 删除向量失败: {}", e.getMessage());
        }
    }

    private List<Float> toList(float[] arr) {
        List<Float> list = new ArrayList<>(arr.length);
        for (float v : arr) list.add(v);
        return list;
    }

    private Long parseLong(Object val) {
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).longValue();
        try {
            return Long.parseLong(String.valueOf(val));
        } catch (Exception e) {
            return null;
        }
    }
}
