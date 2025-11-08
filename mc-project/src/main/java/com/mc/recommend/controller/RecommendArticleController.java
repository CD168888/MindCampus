package com.mc.recommend.controller;

import com.mc.common.annotation.Log;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.AjaxResult;
import com.mc.common.core.page.TableDataInfo;
import com.mc.common.enums.BusinessType;
import com.mc.common.utils.poi.ExcelUtil;
import com.mc.recommend.domain.RecommendArticle;
import com.mc.recommend.service.IRecommendArticleService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 心理文章推荐Controller
 *
 * @author caidu
 * @date 2025-11-08
 */
@RestController
@RequestMapping("/recommend/recommendArticle")
public class RecommendArticleController extends BaseController {
    @Autowired
    private IRecommendArticleService recommendArticleService;

    /**
     * 查询心理文章推荐列表
     */
    @PreAuthorize("@ss.hasPermi('recommendArticle:recommendArticle:list')")
    @GetMapping("/list")
    public TableDataInfo<List<RecommendArticle>> list(RecommendArticle recommendArticle) {
        startPage();
        List<RecommendArticle> list = recommendArticleService.selectRecommendArticleList(recommendArticle);
        return getDataTable(list);
    }

    /**
     * 导出心理文章推荐列表
     */
    @PreAuthorize("@ss.hasPermi('recommendArticle:recommendArticle:export')")
    @Log(title = "心理文章推荐", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RecommendArticle recommendArticle) {
        List<RecommendArticle> list = recommendArticleService.selectRecommendArticleList(recommendArticle);
        ExcelUtil<RecommendArticle> util = new ExcelUtil<RecommendArticle>(RecommendArticle.class);
        util.exportExcel(response, list, "心理文章推荐数据");
    }

    /**
     * 获取心理文章推荐详细信息
     */
    @PreAuthorize("@ss.hasPermi('recommendArticle:recommendArticle:query')")
    @GetMapping(value = "/{articleId}")
    public AjaxResult getInfo(@PathVariable("articleId") Long articleId) {
        return success(recommendArticleService.selectRecommendArticleByArticleId(articleId));
    }

    /**
     * 新增心理文章推荐
     */
    @PreAuthorize("@ss.hasPermi('recommendArticle:recommendArticle:add')")
    @Log(title = "心理文章推荐", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RecommendArticle recommendArticle) {
        return toAjax(recommendArticleService.insertRecommendArticle(recommendArticle));
    }

    /**
     * 修改心理文章推荐
     */
    @PreAuthorize("@ss.hasPermi('recommendArticle:recommendArticle:edit')")
    @Log(title = "心理文章推荐", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RecommendArticle recommendArticle) {
        return toAjax(recommendArticleService.updateRecommendArticle(recommendArticle));
    }

    /**
     * 删除心理文章推荐
     */
    @PreAuthorize("@ss.hasPermi('recommendArticle:recommendArticle:remove')")
    @Log(title = "心理文章推荐", businessType = BusinessType.DELETE)
    @DeleteMapping("/{articleIds}")
    public AjaxResult remove(@PathVariable Long[] articleIds) {
        return toAjax(recommendArticleService.deleteRecommendArticleByArticleIds(articleIds));
    }
}

