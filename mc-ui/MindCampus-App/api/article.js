import request from '@/utils/request';

/**
 * 获取推荐文章（首页推荐3个）
 */
export function getRecommendedArticles() {
  return request({
    url: '/app/article/recommended',
    method: 'get',
  });
}

/**
 * 查询文章列表（分页）
 * @param {Object} params 查询参数 { pageNum, pageSize, keyword, category, status }
 * keyword: 搜索关键词，会模糊搜索 title 或 author（OR 逻辑）
 */
export function listArticles(params) {
  return request({
    url: '/app/article/list',
    method: 'get',
    params: params,
  });
}

/**
 * 查询文章详情
 * @param {Number} articleId 文章ID
 */
export function getArticleDetail(articleId) {
  return request({
    url: `/app/article/${articleId}`,
    method: 'get',
  });
}

