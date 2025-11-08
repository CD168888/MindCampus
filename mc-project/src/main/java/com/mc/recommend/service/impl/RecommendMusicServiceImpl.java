package com.mc.recommend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.common.utils.DateUtils;
import com.mc.common.utils.SecurityUtils;
import com.mc.recommend.domain.RecommendMusic;
import com.mc.recommend.mapper.RecommendMusicMapper;
import com.mc.recommend.service.IRecommendMusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 心理音乐推荐Service业务层处理
 *
 * @author caidu
 * @date 2025-11-08
 */
@Service
public class RecommendMusicServiceImpl extends ServiceImpl<RecommendMusicMapper, RecommendMusic> implements IRecommendMusicService {
    @Autowired
    private RecommendMusicMapper recommendMusicMapper;

    @Override
    public RecommendMusic selectRecommendMusicByMusicId(Long musicId) {
        return recommendMusicMapper.selectRecommendMusicByMusicId(musicId);
    }

    @Override
    public List<RecommendMusic> selectRecommendMusicList(RecommendMusic recommendMusic) {
        return recommendMusicMapper.selectRecommendMusicList(recommendMusic);
    }

    @Override
    public int insertRecommendMusic(RecommendMusic recommendMusic) {
        recommendMusic.setCreateBy(SecurityUtils.getUsername());
        recommendMusic.setCreateTime(DateUtils.getNowDate());
        return recommendMusicMapper.insertRecommendMusic(recommendMusic);
    }

    @Override
    public int updateRecommendMusic(RecommendMusic recommendMusic) {
        recommendMusic.setUpdateBy(SecurityUtils.getUsername());
        recommendMusic.setUpdateTime(DateUtils.getNowDate());
        return recommendMusicMapper.updateRecommendMusic(recommendMusic);
    }

    @Override
    public int deleteRecommendMusicByMusicIds(Long[] musicIds) {
        return recommendMusicMapper.deleteRecommendMusicByMusicIds(musicIds);
    }
}

