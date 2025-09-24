import request from '@/utils/request'

// 查询题库管理列表
export function listQuestionnairebank(query) {
  return request({
    url: '/questionnaire/questionnairebank/list',
    method: 'get',
    params: query
  })
}

// 查询题库管理详细
export function getQuestionnairebank(bankId) {
  return request({
    url: '/questionnaire/questionnairebank/' + bankId,
    method: 'get'
  })
}

// 新增题库管理
export function addQuestionnairebank(data) {
  return request({
    url: '/questionnaire/questionnairebank',
    method: 'post',
    data: data
  })
}

// 修改题库管理
export function updateQuestionnairebank(data) {
  return request({
    url: '/questionnaire/questionnairebank',
    method: 'put',
    data: data
  })
}

// 删除题库管理
export function delQuestionnairebank(bankId) {
  return request({
    url: '/questionnaire/questionnairebank/' + bankId,
    method: 'delete'
  })
}
