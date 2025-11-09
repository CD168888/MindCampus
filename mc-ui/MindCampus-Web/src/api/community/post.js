import request from '@/utils/request'

// 查询帖子管理列表
export function listPost(query) {
  return request({
    url: '/community/post/list',
    method: 'get',
    params: query
  })
}

// 查询帖子管理详细
export function getPost(postId) {
  return request({
    url: '/community/post/' + postId,
    method: 'get'
  })
}

// 新增帖子管理
export function addPost(data) {
  return request({
    url: '/community/post',
    method: 'post',
    data: data
  })
}

// 修改帖子管理
export function updatePost(data) {
  return request({
    url: '/community/post',
    method: 'put',
    data: data
  })
}

// 删除帖子管理
export function delPost(postId) {
  return request({
    url: '/community/post/' + postId,
    method: 'delete'
  })
}


