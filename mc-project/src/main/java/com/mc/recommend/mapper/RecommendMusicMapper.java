package com.mc.recommend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mc.recommend.domain.RecommendMusic;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 心理音乐推荐Mapper接口
 *
 * @author caidu
 * @date 2025-11-08
 */
@Mapper
public interface RecommendMusicMapper extends BaseMapper<RecommendMusic> {
    /**
     * 查询心理音乐推荐列表
     *
     * @param recommendMusic 心理音乐推荐
     * @return 心理音乐推荐集合
     */
    List<RecommendMusic> selectRecommendMusicList(RecommendMusic recommendMusic);

    /**
     * 查询心理音乐推荐
     *
     * @param musicId 心理音乐推荐主键
     * @return 心理音乐推荐
     */
    RecommendMusic selectRecommendMusicByMusicId(Long musicId);

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
     * 删除心理音乐推荐
     *
     * @param musicId 心理音乐推荐主键
     * @return 结果
     */
    int deleteRecommendMusicByMusicId(Long musicId);

    /**
     * 批量删除心理音乐推荐
     *
     * @param musicIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteRecommendMusicByMusicIds(Long[] musicIds);
}

