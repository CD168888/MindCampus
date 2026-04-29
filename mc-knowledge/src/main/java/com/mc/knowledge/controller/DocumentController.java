package com.mc.knowledge.controller;

import com.mc.common.annotation.Log;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.AjaxResult;
import com.mc.common.core.page.TableDataInfo;
import com.mc.common.enums.BusinessType;
import com.mc.knowledge.domain.entity.KbDocument;
import com.mc.knowledge.domain.entity.KbDocumentChunk;
import com.mc.knowledge.kg.service.IKnowledgeBaseService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 知识库文档管理 Controller
 *
 * @author MindCampus
 */
@RestController
@RequestMapping("/knowledge/document")
public class DocumentController extends BaseController {

    @Resource
    private IKnowledgeBaseService knowledgeBaseService;

    @PreAuthorize("@ss.hasPermi('knowledge:document:list')")
    @GetMapping("/list")
    public TableDataInfo list(KbDocument document) {
        startPage();
        List<KbDocument> list = knowledgeBaseService.selectDocumentList(document);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('knowledge:document:query')")
    @GetMapping("/{docId}")
    public AjaxResult getInfo(@PathVariable("docId") Long docId) {
        List<KbDocumentChunk> chunks = knowledgeBaseService.selectChunksByDocId(docId);
        Map<String, Object> result = new HashMap<>();
        result.put("chunks", chunks);
        result.put("progress", knowledgeBaseService.getEmbeddingProgress(docId));
        return success(result);
    }

    @PreAuthorize("@ss.hasPermi('knowledge:document:upload')")
    @Log(title = "知识库文档", businessType = BusinessType.INSERT)
    @PostMapping("/upload")
    public AjaxResult upload(
            @RequestParam("kbId") Long kbId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "kbName", required = false) String kbName) {
        if (file.isEmpty()) {
            return error("请选择要上传的文件");
        }
        try {
            Long docId = knowledgeBaseService.uploadDocument(kbId, file, kbName);
            return success(docId);
        } catch (Exception e) {
            return error("文件上传失败: " + e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('knowledge:document:remove')")
    @Log(title = "知识库文档", businessType = BusinessType.DELETE)
    @DeleteMapping("/{docId}")
    public AjaxResult remove(@PathVariable Long docId) {
        return toAjax(knowledgeBaseService.deleteDocument(docId));
    }

    @PreAuthorize("@ss.hasPermi('knowledge:document:reembed')")
    @Log(title = "知识库文档", businessType = BusinessType.UPDATE)
    @PostMapping("/reembed/{docId}")
    public AjaxResult reEmbed(@PathVariable Long docId) {
        try {
            knowledgeBaseService.reEmbedDocument(docId);
            return success("重新向量化任务已启动");
        } catch (Exception e) {
            return error("操作失败: " + e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('knowledge:document:progress')")
    @GetMapping("/progress/{docId}")
    public AjaxResult progress(@PathVariable Long docId) {
        int progress = knowledgeBaseService.getEmbeddingProgress(docId);
        return success(progress);
    }
}
