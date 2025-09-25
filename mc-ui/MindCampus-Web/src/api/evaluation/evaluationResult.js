import request from '@/utils/request'

// 查询心理测评结果列表
export function listEvaluationResult(query) {
  return request({
    url: '/evaluation/evaluationResult/list',
    method: 'get',
    params: query
  })
}

// 查询心理测评结果详细
export function getEvaluationResult(resultId) {
  return request({
    url: '/evaluation/evaluationResult/' + resultId,
    method: 'get'
  })
}

// 新增心理测评结果
export function addEvaluationResult(data) {
  return request({
    url: '/evaluation/evaluationResult',
    method: 'post',
    data: data
  })
}

// 修改心理测评结果
export function updateEvaluationResult(data) {
  return request({
    url: '/evaluation/evaluationResult',
    method: 'put',
    data: data
  })
}

// 删除心理测评结果
export function delEvaluationResult(resultId) {
  return request({
    url: '/evaluation/evaluationResult/' + resultId,
    method: 'delete'
  })
}
