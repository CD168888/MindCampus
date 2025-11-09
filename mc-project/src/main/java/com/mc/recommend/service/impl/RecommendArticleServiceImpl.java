package com.mc.recommend.service.impl;

import com.mc.common.utils.DateUtils;
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
public class RecommendArticleServiceImpl implements IRecommendArticleService {
    @Autowired
    private RecommendArticleMapper recommendArticleMapper;

    /**
     * 查询心理文章推荐列表
     *
     * @param recommendArticle 心理文章推荐
     * @return 心理文章推荐
     */
    @Override
    public List<RecommendArticle> selectRecommendArticleList(RecommendArticle recommendArticle) {
        return recommendArticleMapper.selectRecommendArticleList(recommendArticle);
    }

    /**
     * 查询心理文章推荐
     *
     * @param articleId 心理文章推荐主键
     * @return 心理文章推荐
     */
    @Override
    public RecommendArticle selectRecommendArticleByArticleId(Long articleId) {
        return recommendArticleMapper.selectRecommendArticleByArticleId(articleId);
    }

    /**
     * 新增心理文章推荐
     *
     * @param recommendArticle 心理文章推荐
     * @return 结果
     */
    @Override
    public int insertRecommendArticle(RecommendArticle recommendArticle) {
        recommendArticle.setCreateTime(DateUtils.getNowDate());
        return recommendArticleMapper.insertRecommendArticle(recommendArticle);
    }

    /**
     * 修改心理文章推荐
     *
     * @param recommendArticle 心理文章推荐
     * @return 结果
     */
    @Override
    public int updateRecommendArticle(RecommendArticle recommendArticle) {
        recommendArticle.setUpdateTime(DateUtils.getNowDate());
        return recommendArticleMapper.updateRecommendArticle(recommendArticle);
    }

    /**
     * 批量删除心理文章推荐
     *
     * @param articleIds 需要删除的心理文章推荐主键
     * @return 结果
     */
    @Override
    public int deleteRecommendArticleByArticleIds(Long[] articleIds) {
        return recommendArticleMapper.deleteRecommendArticleByArticleIds(articleIds);
    }

    /**
     * 删除心理文章推荐信息
     *
     * @param articleId 心理文章推荐主键
     * @return 结果
     */
    @Override
    public int deleteRecommendArticleByArticleId(Long articleId) {
        return recommendArticleMapper.deleteRecommendArticleByArticleId(articleId);
    }

    /**
     * 增加文章阅读量
     *
     * @param articleId 文章ID
     * @return 结果
     */
    @Override
    public int incrementReadCount(Long articleId) {
        return recommendArticleMapper.incrementReadCount(articleId);
    }
}
