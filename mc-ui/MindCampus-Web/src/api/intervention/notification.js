/**
 * 干预通知API
 */
import request from '@/utils/request'

// 查询干预通知列表
export function listNotification(query) {
  return request({
    url: '/intervention/notification/list',
    method: 'get',
    params: query
  })
}

// 查询干预通知详细
export function getNotification(notificationId) {
  return request({
    url: '/intervention/notification/' + notificationId,
    method: 'get'
  })
}

// 查看记录（更新阅读状态并返回处理记录）
export function viewRecord(notificationId) {
  return request({
    url: '/intervention/notification/viewRecord/' + notificationId,
    method: 'get'
  })
}

// 新增干预通知
export function addNotification(data) {
  return request({
    url: '/intervention/notification',
    method: 'post',
    data: data
  })
}

// 修改干预通知
export function updateNotification(data) {
  return request({
    url: '/intervention/notification',
    method: 'put',
    data: data
  })
}

// 删除干预通知
export function delNotification(notificationId) {
  return request({
    url: '/intervention/notification/' + notificationId,
    method: 'delete'
  })
}

// 更新干预通知阅读状态
export function updateReadStatus(notificationId, readStatus) {
  return request({
    url: '/intervention/notification/readStatus',
    method: 'put',
    params: { notificationId, readStatus }
  })
}

// 更新干预通知处理状态
export function updateProcessStatus(notificationId, processStatus) {
  return request({
    url: '/intervention/notification/processStatus',
    method: 'put',
    params: { notificationId, processStatus }
  })
}

// 批量生成干预通知
export function generateNotification(resultIds) {
  return request({
    url: '/intervention/notification/batchGenerate',
    method: 'post',
    data: resultIds
  })
}

// 查询高风险未通知的评测结果
export function listHighRiskUnnotified() {
  return request({
    url: '/intervention/notification/highRiskUnnotified',
    method: 'get'
  })
}
