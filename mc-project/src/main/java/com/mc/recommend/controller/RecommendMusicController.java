package com.mc.recommend.controller;

import com.mc.common.annotation.Log;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.AjaxResult;
import com.mc.common.core.page.TableDataInfo;
import com.mc.common.enums.BusinessType;
import com.mc.common.utils.poi.ExcelUtil;
import com.mc.recommend.domain.RecommendMusic;
import com.mc.recommend.service.IRecommendMusicService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 心理音乐推荐Controller
 *
 * @author caidu
 * @date 2025-11-08
 */
@RestController
@RequestMapping("/recommend/recommendMusic")
public class RecommendMusicController extends BaseController {
    @Autowired
    private IRecommendMusicService recommendMusicService;

    /**
     * 查询心理音乐推荐列表
     */
    @PreAuthorize("@ss.hasPermi('recommendMusic:recommendMusic:list')")
    @GetMapping("/list")
    public TableDataInfo<List<RecommendMusic>> list(RecommendMusic recommendMusic) {
        startPage();
        List<RecommendMusic> list = recommendMusicService.selectRecommendMusicList(recommendMusic);
        return getDataTable(list);
    }

    /**
     * 导出心理音乐推荐列表
     */
    @PreAuthorize("@ss.hasPermi('recommendMusic:recommendMusic:export')")
    @Log(title = "心理音乐推荐", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RecommendMusic recommendMusic) {
        List<RecommendMusic> list = recommendMusicService.selectRecommendMusicList(recommendMusic);
        ExcelUtil<RecommendMusic> util = new ExcelUtil<RecommendMusic>(RecommendMusic.class);
        util.exportExcel(response, list, "心理音乐推荐数据");
    }

    /**
     * 获取心理音乐推荐详细信息
     */
    @PreAuthorize("@ss.hasPermi('recommendMusic:recommendMusic:query')")
    @GetMapping(value = "/{musicId}")
    public AjaxResult getInfo(@PathVariable("musicId") Long musicId) {
        return success(recommendMusicService.selectRecommendMusicByMusicId(musicId));
    }

    /**
     * 新增心理音乐推荐
     */
    @PreAuthorize("@ss.hasPermi('recommendMusic:recommendMusic:add')")
    @Log(title = "心理音乐推荐", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RecommendMusic recommendMusic) {
        return toAjax(recommendMusicService.insertRecommendMusic(recommendMusic));
    }

    /**
     * 修改心理音乐推荐
     */
    @PreAuthorize("@ss.hasPermi('recommendMusic:recommendMusic:edit')")
    @Log(title = "心理音乐推荐", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RecommendMusic recommendMusic) {
        return toAjax(recommendMusicService.updateRecommendMusic(recommendMusic));
    }

    /**
     * 删除心理音乐推荐
     */
    @PreAuthorize("@ss.hasPermi('recommendMusic:recommendMusic:remove')")
    @Log(title = "心理音乐推荐", businessType = BusinessType.DELETE)
    @DeleteMapping("/{musicIds}")
    public AjaxResult remove(@PathVariable Long[] musicIds) {
        return toAjax(recommendMusicService.deleteRecommendMusicByMusicIds(musicIds));
    }
}

