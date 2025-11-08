package com.mc.recommend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.common.utils.DateUtils;
import com.mc.common.utils.SecurityUtils;
import com.mc.recommend.domain.RecommendArticle;
import com.mc.recommend.mapper.RecommendArticleMapper;
import com.mc.recommend.service.IRecommendArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 心理文章推荐Service业务层处理
 *
 * @author caidu
 * @date 2025-11-08
 */
@Service
public class RecommendArticleServiceImpl extends ServiceImpl<RecommendArticleMapper, RecommendArticle> implements IRecommendArticleService {
    @Autowired
    private RecommendArticleMapper recommendArticleMapper;

    @Override
    public RecommendArticle selectRecommendArticleByArticleId(Long articleId) {
        return recommendArticleMapper.selectRecommendArticleByArticleId(articleId);
    }

    @Override
    public List<RecommendArticle> selectRecommendArticleList(RecommendArticle recommendArticle) {
        return recommendArticleMapper.selectRecommendArticleList(recommendArticle);
    }

    @Override
    public int insertRecommendArticle(RecommendArticle recommendArticle) {
        recommendArticle.setCreateBy(SecurityUtils.getUsername());
        recommendArticle.setCreateTime(DateUtils.getNowDate());
        return recommendArticleMapper.insertRecommendArticle(recommendArticle);
    }

    @Override
    public int updateRecommendArticle(RecommendArticle recommendArticle) {
        recommendArticle.setUpdateBy(SecurityUtils.getUsername());
        recommendArticle.setUpdateTime(DateUtils.getNowDate());
        return recommendArticleMapper.updateRecommendArticle(recommendArticle);
    }

    @Override
    public int deleteRecommendArticleByArticleIds(Long[] articleIds) {
        return recommendArticleMapper.deleteRecommendArticleByArticleIds(articleIds);
    }
}

