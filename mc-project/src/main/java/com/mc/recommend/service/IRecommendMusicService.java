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

    /**
     * 点赞/取消点赞音乐
     *
     * @param musicId 音乐ID
     * @param userId 用户ID
     * @return 结果(true=点赞,false=取消点赞)
     */
    boolean likeMusic(Long musicId, Long userId);

    /**
     * 检查用户是否已点赞
     *
     * @param musicId 音乐ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    boolean checkMusicLiked(Long musicId, Long userId);

    /**
     * 获取音乐点赞数
     *
     * @param musicId 音乐ID
     * @return 点赞数
     */
    int getMusicLikeCount(Long musicId);

    /**
     * 获取用户点赞的音乐列表
     *
     * @param userId 用户ID
     * @return 点赞的音乐列表
     */
    List<RecommendMusic> getLikedMusic(Long userId);
}

