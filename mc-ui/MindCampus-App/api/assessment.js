import request from '@/utils/request'

/**
 * 获取测评统计数据
 */
export function getStatistics() {
  return request({
    url: '/app/assessment/statistics',
    method: 'get'
  })
}

/**
 * 查询问卷列表（待填+已完成）
 */
export function listQuestionnaires() {
  return request({
    url: '/app/assessment/questionnaires',
    method: 'get'
  })
}

/**
 * 查询问卷详情（包含题目）
 * @param {Number} questionnaireId 问卷ID
 */
export function getQuestionnaireDetail(questionnaireId) {
  return request({
    url: `/app/assessment/questionnaire/${questionnaireId}`,
    method: 'get'
  })
}

/**
 * 提交答题
 * @param {Object} data 答题数据 { questionnaireId, answers: [{ questionId, userAnswer }] }
 */
export function submitAnswer(data) {
  return request({
    url: '/app/assessment/submit',
    method: 'post',
    data: data
  })
}

/**
 * 查询测评结果详情
 * @param {Number} resultId 结果ID
 */
export function getResultDetail(resultId) {
  return request({
    url: `/app/assessment/result/${resultId}`,
    method: 'get'
  })
}

/**
 * 查询我的测评历史记录
 */
export function listMyResults() {
  return request({
    url: '/app/assessment/my-results',
    method: 'get'
  })
}

/**
 * 检查问卷是否已完成
 * @param {Number} questionnaireId 问卷ID
 */
export function checkCompleted(questionnaireId) {
  return request({
    url: `/app/assessment/check/${questionnaireId}`,
    method: 'get'
  })
}

