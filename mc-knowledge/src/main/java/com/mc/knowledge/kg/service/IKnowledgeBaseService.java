package com.mc.knowledge.kg.service;

import com.mc.knowledge.domain.entity.KbDocument;
import com.mc.knowledge.domain.entity.KbDocumentChunk;
import com.mc.knowledge.domain.entity.KnowledgeBase;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 知识库管理服务接口
 *
 * @author MindCampus
 */
public interface IKnowledgeBaseService {

    /**
     * 创建知识库
     */
    Long createKnowledgeBase(KnowledgeBase knowledgeBase);

    /**
     * 更新知识库
     */
    int updateKnowledgeBase(KnowledgeBase knowledgeBase);

    /**
     * 删除知识库
     */
    int deleteKnowledgeBase(Long kbId);

    /**
     * 查询知识库列表
     */
    List<KnowledgeBase> selectKnowledgeBaseList(KnowledgeBase knowledgeBase);

    /**
     * 根据ID查询知识库
     */
    KnowledgeBase selectKnowledgeBaseById(Long kbId);

    /**
     * 上传文档并触发向量化
     *
     * @param kbId   知识库ID
     * @param file   上传的文件
     * @param kbName 知识库名称（用于 OSS 路径）
     * @return 文档ID
     */
    Long uploadDocument(Long kbId, MultipartFile file, String kbName);

    /**
     * 查询知识库文档列表
     */
    List<KbDocument> selectDocumentList(KbDocument document);

    /**
     * 删除文档
     */
    int deleteDocument(Long docId);

    /**
     * 查询文档的切分块
     */
    List<KbDocumentChunk> selectChunksByDocId(Long docId);

    /**
     * 手动触发文档向量化
     */
    void reEmbedDocument(Long docId);

    /**
     * 获取向量化进度
     *
     * @param docId 文档ID
     * @return 进度百分比（0-100）
     */
    int getEmbeddingProgress(Long docId);
}
