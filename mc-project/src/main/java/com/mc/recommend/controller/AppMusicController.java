package com.mc.recommend.controller;

import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.R;
import com.mc.common.core.page.TableDataInfo;
import com.mc.common.utils.SecurityUtils;
import com.mc.recommend.domain.RecommendMusic;
import com.mc.recommend.service.IRecommendMusicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * APP端音乐推荐Controller
 *
 * @author caidu
 * @date 2025-11-08
 */
@Tag(name = "APP端音乐推荐")
@RestController
@RequestMapping("/app/music")
@RequiredArgsConstructor
public class AppMusicController extends BaseController {

    private final IRecommendMusicService recommendMusicService;

    /**
     * 获取推荐音乐（首页推荐3个）
     * 模拟推荐算法：目前返回状态为正常的音乐，按创建时间倒序取前3个
     */
    @Operation(summary = "获取推荐音乐")
    @GetMapping("/recommended")
    public R<List<RecommendMusic>> getRecommendedMusic() {
        // 模拟推荐算法：查询状态为正常的音乐，按创建时间倒序，取前3个
        RecommendMusic query = new RecommendMusic();
        query.setStatus("0"); // 0-正常
        List<RecommendMusic> list = recommendMusicService.selectRecommendMusicList(query);
        
        // 取前3个
        if (list.size() > 3) {
            list = list.subList(0, 3);
        }
        
        return R.ok(list);
    }

    /**
     * 查询音乐列表（分页）
     */
    @Operation(summary = "查询音乐列表")
    @GetMapping("/list")
    public TableDataInfo listMusic(RecommendMusic recommendMusic) {
        // 只查询状态为正常的音乐
        recommendMusic.setStatus("0");
        startPage();
        List<RecommendMusic> list = recommendMusicService.selectRecommendMusicList(recommendMusic);
        return getDataTable(list);
    }

    /**
     * 查询音乐详情
     */
    @Operation(summary = "查询音乐详情")
    @GetMapping("/{musicId}")
    public R<RecommendMusic> getMusicDetail(@PathVariable Long musicId) {
        RecommendMusic music = recommendMusicService.selectRecommendMusicByMusicId(musicId);
        return R.ok(music);
    }

    /**
     * 点赞/取消点赞音乐
     */
    @Operation(summary = "点赞/取消点赞音乐")
    @PostMapping("/{musicId}/like")
    public R<Map<String, Object>> likeMusic(@PathVariable Long musicId) {
        Long userId = SecurityUtils.getUserId();
        boolean liked = recommendMusicService.likeMusic(musicId, userId);
        int likeCount = recommendMusicService.getMusicLikeCount(musicId);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        result.put("likeCount", likeCount);
        return R.ok(result);
    }

    /**
     * 获取音乐点赞状态和数量
     */
    @Operation(summary = "获取音乐点赞状态和数量")
    @GetMapping("/{musicId}/like/status")
    public R<Map<String, Object>> getLikeStatus(@PathVariable Long musicId) {
        Long userId = SecurityUtils.getUserId();
        boolean liked = recommendMusicService.checkMusicLiked(musicId, userId);
        int likeCount = recommendMusicService.getMusicLikeCount(musicId);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        result.put("likeCount", likeCount);
        return R.ok(result);
    }

    /**
     * 获取用户点赞的音乐列表
     */
    @Operation(summary = "获取用户点赞的音乐列表")
    @GetMapping("/like/list")
    public R<List<RecommendMusic>> getLikedMusic() {
        Long userId = SecurityUtils.getUserId();
        List<RecommendMusic> list = recommendMusicService.getLikedMusic(userId);
        return R.ok(list);
    }
}

