import request from '@/utils/request';

/**
 * 获取推荐课程（首页推荐3个）
 */
export function getRecommendedCourses() {
  return request({
    url: '/app/course/recommended',
    method: 'get',
  });
}

/**
 * 查询课程列表（分页）
 * @param {Object} params 查询参数 { pageNum, pageSize, keyword, level, status }
 * keyword: 搜索关键词，会模糊搜索 title 或 lecturer（OR 逻辑）
 */
export function listCourses(params) {
  return request({
    url: '/app/course/list',
    method: 'get',
    params: params,
  });
}

/**
 * 查询课程详情
 * @param {Number} courseId 课程ID
 */
export function getCourseDetail(courseId) {
  return request({
    url: `/app/course/${courseId}`,
    method: 'get',
  });
}

/**
 * 点赞/取消点赞课程
 * @param {Number} courseId 课程ID
 */
export function likeCourse(courseId) {
  return request({
    url: `/app/course/${courseId}/like`,
    method: 'post',
  });
}

/**
 * 获取课程点赞状态和数量
 * @param {Number} courseId 课程ID
 */
export function getCourseLikeStatus(courseId) {
  return request({
    url: `/app/course/${courseId}/like/status`,
    method: 'get',
  });
}

/**
 * 获取用户点赞的课程列表
 */
export function getLikedCourses() {
  return request({
    url: '/app/course/like/list',
    method: 'get',
  });
}

