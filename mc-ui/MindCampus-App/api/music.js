import request from '@/utils/request';

/**
 * 获取推荐音乐（首页推荐3个）
 */
export function getRecommendedMusic() {
  return request({
    url: '/app/music/recommended',
    method: 'get',
  });
}

/**
 * 查询音乐列表（分页）
 * @param {Object} params 查询参数 { pageNum, pageSize, keyword, genre, status }
 * keyword: 搜索关键词，会模糊搜索 title 或 artist（OR 逻辑）
 */
export function listMusic(params) {
  return request({
    url: '/app/music/list',
    method: 'get',
    params: params,
  });
}

/**
 * 查询音乐详情
 * @param {Number} musicId 音乐ID
 */
export function getMusicDetail(musicId) {
  return request({
    url: `/app/music/${musicId}`,
    method: 'get',
  });
}

/**
 * 点赞/取消点赞音乐
 * @param {Number} musicId 音乐ID
 */
export function likeMusic(musicId) {
  return request({
    url: `/app/music/${musicId}/like`,
    method: 'post',
  });
}

/**
 * 获取音乐点赞状态和数量
 * @param {Number} musicId 音乐ID
 */
export function getLikeStatus(musicId) {
  return request({
    url: `/app/music/${musicId}/like/status`,
    method: 'get',
  });
}
