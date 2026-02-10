/**
 * 风险等级配置API
 */
import request from '@/utils/request'

// 查询风险等级配置列表
export function listRiskConfig(query) {
  return request({
    url: '/intervention/risk/list',
    method: 'get',
    params: query
  })
}

// 查询风险等级配置详细
export function getRiskConfig(configId) {
  return request({
    url: '/intervention/risk/' + configId,
    method: 'get'
  })
}

// 新增风险等级配置
export function addRiskConfig(data) {
  return request({
    url: '/intervention/risk',
    method: 'post',
    data: data
  })
}

// 修改风险等级配置
export function updateRiskConfig(data) {
  return request({
    url: '/intervention/risk',
    method: 'put',
    data: data
  })
}

// 删除风险等级配置
export function delRiskConfig(configId) {
  return request({
    url: '/intervention/risk/' + configId,
    method: 'delete'
  })
}

// 根据分数查询风险等级配置
export function getConfigByScore(score) {
  return request({
    url: '/intervention/risk/getByScore',
    method: 'get',
    params: { score }
  })
}

// 根据风险等级查询配置
export function getConfigByRiskLevel(riskLevel) {
  return request({
    url: '/intervention/risk/getByLevel',
    method: 'get',
    params: { riskLevel }
  })
}
