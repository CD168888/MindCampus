import request from '@/utils/request';

/**
 * 获取轮播图列表（首页展示）
 */
export function getBannerList() {
  return request({
    url: '/app/banner/list',
    method: 'get',
  });
}

