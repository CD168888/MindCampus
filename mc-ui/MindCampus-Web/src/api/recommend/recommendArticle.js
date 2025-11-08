import request from '@/utils/request'

// 查询心理文章推荐列表
export function listRecommendArticle(query) {
  return request({
    url: '/recommend/recommendArticle/list',
    method: 'get',
    params: query
  })
}

// 查询心理文章推荐详细
export function getRecommendArticle(articleId) {
  return request({
    url: '/recommend/recommendArticle/' + articleId,
    method: 'get'
  })
}

// 新增心理文章推荐
export function addRecommendArticle(data) {
  return request({
    url: '/recommend/recommendArticle',
    method: 'post',
    data: data
  })
}

// 修改心理文章推荐
export function updateRecommendArticle(data) {
  return request({
    url: '/recommend/recommendArticle',
    method: 'put',
    data: data
  })
}

// 删除心理文章推荐
export function delRecommendArticle(articleId) {
  return request({
    url: '/recommend/recommendArticle/' + articleId,
    method: 'delete'
  })
}

