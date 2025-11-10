import request from '@/utils/request';

/**
 * 获取帖子列表（分页）
 * @param {Object} params 查询参数 { pageNum, pageSize, keyword, status }
 */
export function listPosts(params) {
  return request({
    url: '/app/community/post/list',
    method: 'get',
    params: params,
  });
}

/**
 * 获取帖子详情
 * @param {Number} postId 帖子ID
 */
export function getPostDetail(postId) {
  return request({
    url: `/app/community/post/${postId}`,
    method: 'get',
  });
}

/**
 * 发布帖子
 * @param {Object} data 帖子数据 { title, content, images }
 */
export function createPost(data) {
  return request({
    url: '/app/community/post',
    method: 'post',
    data: data,
  });
}

/**
 * 点赞帖子
 * @param {Number} postId 帖子ID
 */
export function likePost(postId) {
  return request({
    url: `/app/community/post/${postId}/like`,
    method: 'post',
  });
}

/**
 * 取消点赞帖子
 * @param {Number} postId 帖子ID
 */
export function unlikePost(postId) {
  return request({
    url: `/app/community/post/${postId}/unlike`,
    method: 'post',
  });
}

/**
 * 获取帖子评论列表
 * @param {Number} postId 帖子ID
 * @param {Object} params 查询参数 { pageNum, pageSize }
 */
export function listComments(postId, params) {
  return request({
    url: `/app/community/post/${postId}/comments`,
    method: 'get',
    params: params,
  });
}

/**
 * 发表评论
 * @param {Object} data 评论数据 { postId, content, parentId }
 */
export function createComment(data) {
  return request({
    url: '/app/community/comment',
    method: 'post',
    data: data,
  });
}

/**
 * 点赞评论
 * @param {Number} commentId 评论ID
 */
export function likeComment(commentId) {
  return request({
    url: `/app/community/comment/${commentId}/like`,
    method: 'post',
  });
}

/**
 * 取消点赞评论
 * @param {Number} commentId 评论ID
 */
export function unlikeComment(commentId) {
  return request({
    url: `/app/community/comment/${commentId}/unlike`,
    method: 'post',
  });
}



