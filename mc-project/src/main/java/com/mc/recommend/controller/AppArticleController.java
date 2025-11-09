package com.mc.recommend.controller;

import com.mc.common.annotation.Anonymous;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.AjaxResult;
import com.mc.common.core.page.TableDataInfo;
import com.mc.recommend.domain.RecommendArticle;
import com.mc.recommend.service.IRecommendArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 心理文章推荐App接口
 *
 * @author caidu
 * @date 2025-11-09
 */
@RestController
@RequestMapping("/app/article")
public class AppArticleController extends BaseController {
  @Autowired
  private IRecommendArticleService recommendArticleService;

  /**
   * 查询心理文章推荐列表
   */
  @Anonymous
  @GetMapping("/list")
  public TableDataInfo list(RecommendArticle recommendArticle) {
    startPage();
    // 只查询正常状态的文章
    recommendArticle.setStatus("0");
    List<RecommendArticle> list = recommendArticleService.selectRecommendArticleList(recommendArticle);
    return getDataTable(list);
  }

  /**
   * 获取心理文章推荐详细信息
   */
  @Anonymous
  @GetMapping("/{articleId}")
  public AjaxResult getInfo(@PathVariable("articleId") Long articleId) {
    // 增加阅读量
    recommendArticleService.incrementReadCount(articleId);
    // 查询文章详情
    RecommendArticle article = recommendArticleService.selectRecommendArticleByArticleId(articleId);
    return success(article);
  }

  /**
   * 获取推荐文章（首页推荐3个）
   */
  @Anonymous
  @GetMapping("/recommended")
  public AjaxResult getRecommended() {
    RecommendArticle query = new RecommendArticle();
    query.setStatus("0");
    List<RecommendArticle> list = recommendArticleService.selectRecommendArticleList(query);
    // 取前3个
    if (list.size() > 3) {
      list = list.subList(0, 3);
    }
    return success(list);
  }
}
