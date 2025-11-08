import request from '@/utils/request'

// 查询心理课程推荐列表
export function listRecommendCourse(query) {
  return request({
    url: '/recommend/recommendCourse/list',
    method: 'get',
    params: query
  })
}

// 查询心理课程推荐详细
export function getRecommendCourse(courseId) {
  return request({
    url: '/recommend/recommendCourse/' + courseId,
    method: 'get'
  })
}

// 新增心理课程推荐
export function addRecommendCourse(data) {
  return request({
    url: '/recommend/recommendCourse',
    method: 'post',
    data: data
  })
}

// 修改心理课程推荐
export function updateRecommendCourse(data) {
  return request({
    url: '/recommend/recommendCourse',
    method: 'put',
    data: data
  })
}

// 删除心理课程推荐
export function delRecommendCourse(courseId) {
  return request({
    url: '/recommend/recommendCourse/' + courseId,
    method: 'delete'
  })
}

