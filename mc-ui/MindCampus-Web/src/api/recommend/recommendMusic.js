import request from '@/utils/request'

// 查询心理音乐推荐列表
export function listRecommendMusic(query) {
  return request({
    url: '/recommend/recommendMusic/list',
    method: 'get',
    params: query
  })
}

// 查询心理音乐推荐详细
export function getRecommendMusic(musicId) {
  return request({
    url: '/recommend/recommendMusic/' + musicId,
    method: 'get'
  })
}

// 新增心理音乐推荐
export function addRecommendMusic(data) {
  return request({
    url: '/recommend/recommendMusic',
    method: 'post',
    data: data
  })
}

// 修改心理音乐推荐
export function updateRecommendMusic(data) {
  return request({
    url: '/recommend/recommendMusic',
    method: 'put',
    data: data
  })
}

// 删除心理音乐推荐
export function delRecommendMusic(musicId) {
  return request({
    url: '/recommend/recommendMusic/' + musicId,
    method: 'delete'
  })
}

