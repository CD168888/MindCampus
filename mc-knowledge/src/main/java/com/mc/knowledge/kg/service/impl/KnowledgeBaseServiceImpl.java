package com.mc.knowledge.kg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.common.utils.SecurityUtils;
import com.mc.common.utils.StringUtils;
import com.mc.knowledge.domain.entity.KbDocument;
import com.mc.knowledge.domain.entity.KbDocumentChunk;
import com.mc.knowledge.domain.entity.KnowledgeBase;
import com.mc.knowledge.kg.service.IEmbeddingService;
import com.mc.knowledge.kg.service.IKnowledgeBaseService;
import com.mc.knowledge.kg.vector.MilvusVectorClient;
import com.mc.knowledge.kg.vector.TextSplitter;
import com.mc.knowledge.mapper.KbDocumentChunkMapper;
import com.mc.knowledge.mapper.KbDocumentMapper;
import com.mc.knowledge.mapper.KnowledgeBaseMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 知识库管理服务实现
 *
 * @author MindCampus
 */
@Service
@Slf4j
public class KnowledgeBaseServiceImpl extends ServiceImpl<KnowledgeBaseMapper, KnowledgeBase>
    implements IKnowledgeBaseService {

    @Resource
    private KbDocumentMapper documentMapper;

    @Resource
    private KbDocumentChunkMapper chunkMapper;

    @Resource
    private TextSplitter textSplitter;

    @Resource
    private IEmbeddingService embeddingService;

    @Resource
    private MilvusVectorClient milvusVectorClient;

    @Value("${spring.knowledge.chunk.size:500}")
    private int chunkSize;

    @Value("${spring.knowledge.chunk.overlap:50}")
    private int chunkOverlap;

    @Value("${spring.knowledge.chunk.batch-size:20}")
    private int embedBatchSize;

    @Override
    public Long createKnowledgeBase(KnowledgeBase knowledgeBase) {
        knowledgeBase.setStatus("0");
        knowledgeBase.setDocCount(0);
        knowledgeBase.setCreateBy(SecurityUtils.getUsername());
        baseMapper.insert(knowledgeBase);
        log.info("[KB] 创建知识库 - kbId: {}, name: {}", knowledgeBase.getKbId(), knowledgeBase.getKbName());
        return knowledgeBase.getKbId();
    }

    @Override
    public int updateKnowledgeBase(KnowledgeBase knowledgeBase) {
        knowledgeBase.setUpdateBy(SecurityUtils.getUsername());
        return baseMapper.updateById(knowledgeBase);
    }

    @Override
    public int deleteKnowledgeBase(Long kbId) {
        LambdaQueryWrapper<KbDocument> docWrapper = new LambdaQueryWrapper<>();
        docWrapper.eq(KbDocument::getKbId, kbId);
        List<KbDocument> docs = documentMapper.selectList(docWrapper);

        for (KbDocument doc : docs) {
            deleteDocument(doc.getDocId());
        }
        return baseMapper.deleteById(kbId);
    }

    @Override
    public List<KnowledgeBase> selectKnowledgeBaseList(KnowledgeBase knowledgeBase) {
        LambdaQueryWrapper<KnowledgeBase> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(knowledgeBase.getKbName())) {
            wrapper.like(KnowledgeBase::getKbName, knowledgeBase.getKbName());
        }
        if (StringUtils.isNotBlank(knowledgeBase.getCategory())) {
            wrapper.eq(KnowledgeBase::getCategory, knowledgeBase.getCategory());
        }
        if (StringUtils.isNotBlank(knowledgeBase.getStatus())) {
            wrapper.eq(KnowledgeBase::getStatus, knowledgeBase.getStatus());
        }
        wrapper.orderByDesc(KnowledgeBase::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public KnowledgeBase selectKnowledgeBaseById(Long kbId) {
        return baseMapper.selectById(kbId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long uploadDocument(Long kbId, MultipartFile file, String kbName) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileType = getFileType(originalFilename);

            KbDocument document = new KbDocument();
            document.setKbId(kbId);
            document.setFileName(originalFilename);
            document.setFileType(fileType);
            document.setFileSize(file.getSize());
            document.setStatus("0");
            document.setEmbeddingStatus("0");
            document.setCreateBy(SecurityUtils.getUsername());

            documentMapper.insert(document);

            final long docId = document.getDocId();
            final Long finalKbId = kbId;
            final String finalKbName = kbName;
            final String createBy = document.getCreateBy();

            new Thread(() -> {
                try {
                    int totalChunks = processDocumentInStream(file, fileType, docId, finalKbId, finalKbName, createBy);
                    KbDocument doc = documentMapper.selectById(docId);
                    if (doc != null) {
                        doc.setChunkCount(totalChunks);
                        doc.setEmbeddingStatus("2");
                        doc.setEmbeddingTime(LocalDateTime.now());
                        documentMapper.updateById(doc);
                    }
                } catch (Exception e) {
                    log.error("[KB] 文档向量化失败 - docId: {}", docId, e);
                    KbDocument doc = documentMapper.selectById(docId);
                    if (doc != null) {
                        doc.setEmbeddingStatus("3");
                        documentMapper.updateById(doc);
                    }
                }

                LambdaQueryWrapper<KbDocument> countWrapper = new LambdaQueryWrapper<>();
                countWrapper.eq(KbDocument::getKbId, finalKbId);
                long count = documentMapper.selectCount(countWrapper);
                KnowledgeBase kb = baseMapper.selectById(finalKbId);
                if (kb != null) {
                    kb.setDocCount((int) count);
                    baseMapper.updateById(kb);
                }
            }).start();

            log.info("[KB] 上传文档并开始向量化 - docId: {}, fileName: {}", docId, originalFilename);
            return docId;
        } catch (Exception e) {
            log.error("[KB] 上传文档失败 - kbId: {}", kbId, e);
            throw new RuntimeException("文档上传失败: " + e.getMessage(), e);
        }
    }

    /**
     * 流式处理文档：边解析边切分边向量化，内存占用极低
     */
    private int processDocumentInStream(MultipartFile file, String fileType, Long docId, Long kbId, String kbName, String createBy) throws Exception {
        int totalChunks = 0;
        StringBuilder currentChunk = new StringBuilder(chunkSize + 100);
        List<String> batchTexts = new ArrayList<>(embedBatchSize);
        List<KbDocumentChunk> batchChunks = new ArrayList<>(embedBatchSize);

        List<String> paragraphs = parseFileParagraphs(file, fileType);
        textSplitter.gc();

        for (int i = 0; i < paragraphs.size(); i++) {
            String para = paragraphs.get(i);
            if (para.isEmpty()) continue;

            if (currentChunk.length() + para.length() + 1 > chunkSize) {
                if (currentChunk.length() > 10) {
                    String chunkText = currentChunk.toString().trim();
                    totalChunks += insertChunkAndEmbed(docId, kbId, kbName, chunkText, totalChunks,
                        batchTexts, batchChunks, file.getOriginalFilename(), createBy);
                    currentChunk.setLength(0);
                }

                if (para.length() > chunkSize) {
                    List<String> subChunks = textSplitter.split(para, chunkSize, chunkOverlap);
                    for (String sub : subChunks) {
                        totalChunks += insertChunkAndEmbed(docId, kbId, kbName, sub, totalChunks,
                            batchTexts, batchChunks, file.getOriginalFilename(), createBy);
                    }
                    continue;
                }
            }

            if (currentChunk.length() > 0) {
                currentChunk.append('\n');
            }
            currentChunk.append(para);
        }

        if (currentChunk.length() > 10) {
            totalChunks += insertChunkAndEmbed(docId, kbId, kbName, currentChunk.toString().trim(), totalChunks,
                batchTexts, batchChunks, file.getOriginalFilename(), createBy);
        }

        if (!batchTexts.isEmpty()) {
            flushBatch(batchTexts, batchChunks, kbName);
        }

        log.info("[KB] 流式处理完成 - docId: {}, 总片段: {}", docId, totalChunks);
        return totalChunks;
    }

    private int insertChunkAndEmbed(Long docId, Long kbId, String kbName, String chunkText,
            int currentTotal, List<String> batchTexts, List<KbDocumentChunk> batchChunks, String fileName, String createBy) {
        KbDocumentChunk chunk = new KbDocumentChunk();
        chunk.setDocId(docId);
        chunk.setKbId(kbId);
        chunk.setChunkIndex(currentTotal);
        chunk.setContent(chunkText);
        chunk.setChunkHash(md5Hash(chunkText));
        chunk.setVectorStatus("0");
        chunk.setFileName(fileName);
        chunk.setCreateBy(createBy);
        chunkMapper.insert(chunk);

        batchChunks.add(chunk);
        batchTexts.add(chunkText);

        if (batchTexts.size() >= embedBatchSize) {
            flushBatch(new ArrayList<>(batchTexts), new ArrayList<>(batchChunks), kbName);
            batchTexts.clear();
            batchChunks.clear();
            textSplitter.gc();
        }
        return 1;
    }

    private void flushBatch(List<String> texts, List<KbDocumentChunk> chunks, String kbName) {
        if (texts.isEmpty()) return;
        try {
            List<float[]> vectors = embeddingService.embedBatch(texts);
            milvusVectorClient.insertVectors(chunks, vectors, kbName);
            for (int i = 0; i < chunks.size() && i < vectors.size(); i++) {
                chunks.get(i).setVectorStatus("1");
                chunkMapper.updateById(chunks.get(i));
            }
        } catch (Exception e) {
            log.error("[KB] 批次向量化失败", e);
        }
    }

    private List<String> parseFileParagraphs(MultipartFile file, String fileType) throws Exception {
        try (InputStream is = file.getInputStream()) {
            byte[] bytes = is.readAllBytes();
            if ("docx".equalsIgnoreCase(fileType) || "doc".equalsIgnoreCase(fileType)) {
                return textSplitter.parseWordParagraphs(bytes);
            } else if ("pdf".equalsIgnoreCase(fileType)) {
                String text = textSplitter.parsePdf(bytes);
                List<String> paras = new ArrayList<>();
                for (String line : text.split("\n")) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty()) paras.add(trimmed);
                }
                return paras;
            } else {
                String content = new String(bytes, StandardCharsets.UTF_8);
                content = textSplitter.parseByFileType(content, fileType);
                List<String> paras = new ArrayList<>();
                for (String line : content.split("\n")) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty()) paras.add(trimmed);
                }
                return paras;
            }
        }
    }

    private String getFileType(String filename) {
        if (filename == null) return "txt";
        int lastDot = filename.lastIndexOf('.');
        if (lastDot < 0) return "txt";
        return filename.substring(lastDot + 1).toLowerCase().trim();
    }

    private String md5Hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return String.valueOf(text.hashCode());
        }
    }

    @Override
    public List<KbDocument> selectDocumentList(KbDocument document) {
        LambdaQueryWrapper<KbDocument> wrapper = new LambdaQueryWrapper<>();
        if (document.getKbId() != null) {
            wrapper.eq(KbDocument::getKbId, document.getKbId());
        }
        if (StringUtils.isNotBlank(document.getFileName())) {
            wrapper.like(KbDocument::getFileName, document.getFileName());
        }
        if (StringUtils.isNotBlank(document.getEmbeddingStatus())) {
            wrapper.eq(KbDocument::getEmbeddingStatus, document.getEmbeddingStatus());
        }
        wrapper.orderByDesc(KbDocument::getCreateTime);
        return documentMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDocument(Long docId) {
        KbDocument document = documentMapper.selectById(docId);
        if (document == null) return 0;

        LambdaQueryWrapper<KbDocumentChunk> chunkWrapper = new LambdaQueryWrapper<>();
        chunkWrapper.eq(KbDocumentChunk::getDocId, docId);
        List<KbDocumentChunk> chunks = chunkMapper.selectList(chunkWrapper);

        List<String> milvusIds = chunks.stream()
            .map(c -> String.valueOf(c.getChunkId()))
            .collect(Collectors.toList());
        if (!milvusIds.isEmpty()) {
            milvusVectorClient.deleteByChunkIds(milvusIds);
        }

        chunkMapper.delete(chunkWrapper);
        return documentMapper.deleteById(docId);
    }

    @Override
    public List<KbDocumentChunk> selectChunksByDocId(Long docId) {
        LambdaQueryWrapper<KbDocumentChunk> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KbDocumentChunk::getDocId, docId)
               .orderByAsc(KbDocumentChunk::getChunkIndex);
        return chunkMapper.selectList(wrapper);
    }

    @Override
    public void reEmbedDocument(Long docId) {
        KbDocument document = documentMapper.selectById(docId);
        if (document == null) return;

        LambdaQueryWrapper<KbDocumentChunk> chunkWrapper = new LambdaQueryWrapper<>();
        chunkWrapper.eq(KbDocumentChunk::getDocId, docId);
        List<KbDocumentChunk> oldChunks = chunkMapper.selectList(chunkWrapper);

        List<String> oldIds = oldChunks.stream()
            .map(c -> String.valueOf(c.getChunkId()))
            .collect(Collectors.toList());
        if (!oldIds.isEmpty()) {
            milvusVectorClient.deleteByChunkIds(oldIds);
        }
        chunkMapper.delete(chunkWrapper);

        document.setEmbeddingStatus("0");
        documentMapper.updateById(document);

        if (oldChunks.isEmpty()) {
            document.setEmbeddingStatus("2");
            document.setChunkCount(0);
            documentMapper.updateById(document);
            return;
        }

        final Long kbId = document.getKbId();
        final String kbName = document.getFileName();

        new Thread(() -> {
            int total = 0;
            List<String> batchTexts = new ArrayList<>(embedBatchSize);
            List<KbDocumentChunk> batchChunks = new ArrayList<>(embedBatchSize);

            for (int i = 0; i < oldChunks.size(); i++) {
                KbDocumentChunk src = oldChunks.get(i);
                KbDocumentChunk chunk = new KbDocumentChunk();
                chunk.setDocId(docId);
                chunk.setKbId(kbId);
                chunk.setChunkIndex(i);
                chunk.setContent(src.getContent());
                chunk.setChunkHash(md5Hash(src.getContent()));
                chunk.setVectorStatus("0");
                chunk.setFileName(src.getFileName());
                chunk.setCreateBy(src.getCreateBy());
                chunkMapper.insert(chunk);

                batchChunks.add(chunk);
                batchTexts.add(src.getContent());
                total++;

                if (batchTexts.size() >= embedBatchSize) {
                    flushBatch(new ArrayList<>(batchTexts), new ArrayList<>(batchChunks), kbName);
                    batchTexts.clear();
                    batchChunks.clear();
                    textSplitter.gc();
                }
            }

            if (!batchTexts.isEmpty()) {
                flushBatch(batchTexts, batchChunks, kbName);
            }

            KbDocument doc = documentMapper.selectById(docId);
            if (doc != null) {
                doc.setChunkCount(total);
                doc.setEmbeddingStatus("2");
                doc.setEmbeddingTime(LocalDateTime.now());
                documentMapper.updateById(doc);
            }
        }).start();
    }

    @Override
    public int getEmbeddingProgress(Long docId) {
        KbDocument document = documentMapper.selectById(docId);
        if (document == null) return 0;

        String status = document.getEmbeddingStatus();
        if ("2".equals(status)) return 100;
        if ("3".equals(status)) return -1;

        LambdaQueryWrapper<KbDocumentChunk> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KbDocumentChunk::getDocId, docId)
               .eq(KbDocumentChunk::getVectorStatus, "1");
        long embedded = chunkMapper.selectCount(wrapper);

        if (document.getChunkCount() == null || document.getChunkCount() == 0) {
            return 0;
        }
        return (int) (embedded * 100 / document.getChunkCount());
    }
}
