package com.mc.recommend.mapper;

import com.mc.recommend.domain.RecommendArticle;

import java.util.List;

/**
 * 心理文章推荐Mapper接口
 *
 * @author caidu
 * @date 2025-11-08
 */
public interface RecommendArticleMapper {
    /**
     * 查询心理文章推荐列表
     *
     * @param recommendArticle 心理文章推荐
     * @return 心理文章推荐集合
     */
    public List<RecommendArticle> selectRecommendArticleList(RecommendArticle recommendArticle);

    /**
     * 查询心理文章推荐
     *
     * @param articleId 心理文章推荐主键
     * @return 心理文章推荐
     */
    public RecommendArticle selectRecommendArticleByArticleId(Long articleId);

    /**
     * 新增心理文章推荐
     *
     * @param recommendArticle 心理文章推荐
     * @return 结果
     */
    public int insertRecommendArticle(RecommendArticle recommendArticle);

    /**
     * 修改心理文章推荐
     *
     * @param recommendArticle 心理文章推荐
     * @return 结果
     */
    public int updateRecommendArticle(RecommendArticle recommendArticle);

    /**
     * 删除心理文章推荐
     *
     * @param articleId 心理文章推荐主键
     * @return 结果
     */
    public int deleteRecommendArticleByArticleId(Long articleId);

    /**
     * 批量删除心理文章推荐
     *
     * @param articleIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRecommendArticleByArticleIds(Long[] articleIds);

    /**
     * 增加文章阅读量
     *
     * @param articleId 文章ID
     * @return 结果
     */
    public int incrementReadCount(Long articleId);
}
