package com.mc.recommend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mc.recommend.domain.RecommendArticle;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 心理文章推荐Mapper接口
 *
 * @author caidu
 * @date 2025-11-08
 */
@Mapper
public interface RecommendArticleMapper extends BaseMapper<RecommendArticle> {
    /**
     * 查询心理文章推荐列表
     *
     * @param recommendArticle 心理文章推荐
     * @return 心理文章推荐集合
     */
    List<RecommendArticle> selectRecommendArticleList(RecommendArticle recommendArticle);

    /**
     * 查询心理文章推荐
     *
     * @param articleId 心理文章推荐主键
     * @return 心理文章推荐
     */
    RecommendArticle selectRecommendArticleByArticleId(Long articleId);

    /**
     * 新增心理文章推荐
     *
     * @param recommendArticle 心理文章推荐
     * @return 结果
     */
    int insertRecommendArticle(RecommendArticle recommendArticle);

    /**
     * 修改心理文章推荐
     *
     * @param recommendArticle 心理文章推荐
     * @return 结果
     */
    int updateRecommendArticle(RecommendArticle recommendArticle);

    /**
     * 删除心理文章推荐
     *
     * @param articleId 心理文章推荐主键
     * @return 结果
     */
    int deleteRecommendArticleByArticleId(Long articleId);

    /**
     * 批量删除心理文章推荐
     *
     * @param articleIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteRecommendArticleByArticleIds(Long[] articleIds);
}

