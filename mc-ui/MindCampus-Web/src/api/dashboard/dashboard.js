import request from '@/utils/request'

export function getStatistics() {
  return request({
    url: '/dashboard/statistics',
    method: 'get'
  })
}

export function getTrendData(period = 'week') {
  return request({
    url: '/dashboard/trend',
    method: 'get',
    params: { period }
  })
}

export function getRecentActivities(limit = 5) {
  return request({
    url: '/dashboard/activities',
    method: 'get',
    params: { limit }
  })
}

export function getTodoList(limit = 5) {
  return request({
    url: '/dashboard/todos',
    method: 'get',
    params: { limit }
  })
}
