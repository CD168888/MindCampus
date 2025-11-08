import request from '@/utils/request'

// 查询心理测评答题记录列表
export function listQuestionnaireAnswer(query) {
  return request({
    url: '/questionnaireAnswer/questionnaireAnswer/list',
    method: 'get',
    params: query
  })
}

// 查询心理测评答题记录详细
export function getQuestionnaireAnswer(answerId) {
  return request({
    url: '/questionnaireAnswer/questionnaireAnswer/' + answerId,
    method: 'get'
  })
}

// 新增心理测评答题记录
export function addQuestionnaireAnswer(data) {
  return request({
    url: '/questionnaireAnswer/questionnaireAnswer',
    method: 'post',
    data: data
  })
}

// 修改心理测评答题记录
export function updateQuestionnaireAnswer(data) {
  return request({
    url: '/questionnaireAnswer/questionnaireAnswer',
    method: 'put',
    data: data
  })
}

// 删除心理测评答题记录
export function delQuestionnaireAnswer(answerId) {
  return request({
    url: '/questionnaireAnswer/questionnaireAnswer/' + answerId,
    method: 'delete'
  })
}

