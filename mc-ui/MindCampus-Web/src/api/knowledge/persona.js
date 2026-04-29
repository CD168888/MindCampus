import request from '@/utils/request';

export function getStudentProfile(userId) {
  return request({
    url: '/knowledge/persona/profile/' + userId,
    method: 'get',
  });
}

export function getStudentContext(userId) {
  return request({
    url: '/knowledge/persona/context/' + userId,
    method: 'get',
  });
}

export function getEmotionHistory(userId, limit) {
  return request({
    url: '/knowledge/persona/emotion/' + userId,
    method: 'get',
    params: { limit },
  });
}

export function getRiskTrend(userId) {
  return request({
    url: '/knowledge/persona/risk/' + userId,
    method: 'get',
  });
}

export function updateStudentProfile(userId, data) {
  return request({
    url: '/knowledge/persona/profile/' + userId,
    method: 'post',
    data: data,
  });
}

export function syncStudentProfile(userId, data) {
  return request({
    url: '/knowledge/persona/sync/' + userId,
    method: 'post',
    data: data,
  });
}

/**
 * 获取学生图谱可视化数据
 */
export function getGraphData(userId) {
  return request({
    url: '/knowledge/persona/graph/' + userId,
    method: 'get',
  });
}

/**
 * 获取全局图谱数据
 */
export function getFullGraphData(limit) {
  return request({
    url: '/knowledge/persona/graph',
    method: 'get',
    params: { limit },
  });
}

/**
 * 触发全量同步
 */
export function triggerFullSync() {
  return request({
    url: '/knowledge/sync/full',
    method: 'post',
  });
}

/**
 * 同步指定学生
 */
export function syncStudent(studentId) {
  return request({
    url: '/knowledge/sync/student/' + studentId,
    method: 'post',
  });
}
