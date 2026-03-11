/**
 * 风险等级配置API
 */
import request from '@/utils/request'

// 获取所有风险等级配置（低、中、高）
export function getRiskConfig() {
  return request({
    url: '/intervention/risk/config',
    method: 'get'
  })
}

// 修改风险等级配置
export function updateRiskConfig(data) {
  return request({
    url: '/intervention/risk/config',
    method: 'put',
    data: data
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
