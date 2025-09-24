import request from '@/utils/request';

// 查询问卷列表
export function listQuestionnaire(query) {
  return request({
    url: '/questionnaire/info/list',
    method: 'get',
    params: query,
  });
}

// 查询问卷题目列表
export function getQuestions(questionnaireId) {
  return request({
    url: `/questionnaire/info/questions/${questionnaireId}`,
    method: 'get',
  });
}

// 新增/修改问卷（统一接口）
export function saveQuestionnaire(data) {
  return request({
    url: '/questionnaire/info',
    method: 'post',
    data: data,
  });
}

// 删除问卷
export function delQuestionnaire(questionnaireId) {
  return request({
    url: '/questionnaire/info/' + questionnaireId,
    method: 'delete',
  });
}
