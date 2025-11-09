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
