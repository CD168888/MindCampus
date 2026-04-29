package com.mc.knowledge.controller;

import com.mc.common.annotation.Log;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.AjaxResult;
import com.mc.common.core.page.TableDataInfo;
import com.mc.common.enums.BusinessType;
import com.mc.knowledge.domain.entity.KnowledgeBase;
import com.mc.knowledge.kg.service.IKnowledgeBaseService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库管理 Controller
 *
 * @author MindCampus
 */
@RestController
@RequestMapping("/knowledge/base")
public class KnowledgeBaseController extends BaseController {

    @Resource
    private IKnowledgeBaseService knowledgeBaseService;

    @PreAuthorize("@ss.hasPermi('knowledge:base:list')")
    @GetMapping("/list")
    public TableDataInfo list(KnowledgeBase knowledgeBase) {
        startPage();
        List<KnowledgeBase> list = knowledgeBaseService.selectKnowledgeBaseList(knowledgeBase);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('knowledge:base:query')")
    @GetMapping("/{kbId}")
    public AjaxResult getInfo(@PathVariable("kbId") Long kbId) {
        return success(knowledgeBaseService.selectKnowledgeBaseById(kbId));
    }

    @PreAuthorize("@ss.hasPermi('knowledge:base:add')")
    @Log(title = "知识库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody KnowledgeBase knowledgeBase) {
        Long kbId = knowledgeBaseService.createKnowledgeBase(knowledgeBase);
        return success(kbId);
    }

    @PreAuthorize("@ss.hasPermi('knowledge:base:edit')")
    @Log(title = "知识库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody KnowledgeBase knowledgeBase) {
        return toAjax(knowledgeBaseService.updateKnowledgeBase(knowledgeBase));
    }

    @PreAuthorize("@ss.hasPermi('knowledge:base:remove')")
    @Log(title = "知识库", businessType = BusinessType.DELETE)
    @DeleteMapping
    public AjaxResult remove(@RequestBody List<Long> kbIds) {
        int count = 0;
        for (Long kbId : kbIds) {
            count += knowledgeBaseService.deleteKnowledgeBase(kbId);
        }
        return toAjax(count);
    }
}
