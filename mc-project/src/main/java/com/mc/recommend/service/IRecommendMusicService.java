package com.mc.recommend.service;

import com.mc.recommend.domain.RecommendMusic;

import java.util.List;

/**
 * 心理音乐推荐Service接口
 *
 * @author caidu
 * @date 2025-11-08
 */
public interface IRecommendMusicService {
    /**
     * 查询心理音乐推荐
     *
     * @param musicId 心理音乐推荐主键
     * @return 心理音乐推荐
     */
    RecommendMusic selectRecommendMusicByMusicId(Long musicId);

    /**
     * 查询心理音乐推荐列表
     *
     * @param recommendMusic 心理音乐推荐
     * @return 心理音乐推荐集合
     */
    List<RecommendMusic> selectRecommendMusicList(RecommendMusic recommendMusic);

    /**
     * 新增心理音乐推荐
     *
     * @param recommendMusic 心理音乐推荐
     * @return 结果
     */
    int insertRecommendMusic(RecommendMusic recommendMusic);

    /**
     * 修改心理音乐推荐
     *
     * @param recommendMusic 心理音乐推荐
     * @return 结果
     */
    int updateRecommendMusic(RecommendMusic recommendMusic);

    /**
     * 批量删除心理音乐推荐
     *
     * @param musicIds 需要删除的心理音乐推荐主键集合
     * @return 结果
     */
    int deleteRecommendMusicByMusicIds(Long[] musicIds);
}

