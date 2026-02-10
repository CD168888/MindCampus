/**
 * 干预处理记录API
 */
import request from '@/utils/request'

// 查询干预处理记录列表
export function listProcessRecord(query) {
  return request({
    url: '/intervention/process/list',
    method: 'get',
    params: query
  })
}

// 查询干预处理记录详细
export function getProcessRecord(recordId) {
  return request({
    url: '/intervention/process/' + recordId,
    method: 'get'
  })
}

// 新增干预处理记录
export function addProcessRecord(data) {
  return request({
    url: '/intervention/process',
    method: 'post',
    data: data
  })
}

// 修改干预处理记录
export function updateProcessRecord(data) {
  return request({
    url: '/intervention/process',
    method: 'put',
    data: data
  })
}

// 删除干预处理记录
export function delProcessRecord(recordId) {
  return request({
    url: '/intervention/process/' + recordId,
    method: 'delete'
  })
}

// 处理干预通知
export function handleNotification(notificationId, userId, processContent, processResult) {
  return request({
    url: '/intervention/process/handle',
    method: 'post',
    params: { notificationId, userId, processContent, processResult }
  })
}
