import request from '@/utils/request';

export function listKnowledgeBase(query) {
  return request({
    url: '/knowledge/base/list',
    method: 'get',
    params: query,
  });
}

export function getKnowledgeBase(kbId) {
  return request({
    url: '/knowledge/base/' + kbId,
    method: 'get',
  });
}

export function addKnowledgeBase(data) {
  return request({
    url: '/knowledge/base',
    method: 'post',
    data: data,
  });
}

export function updateKnowledgeBase(data) {
  return request({
    url: '/knowledge/base',
    method: 'put',
    data: data,
  });
}

export function delKnowledgeBase(kbIds) {
  return request({
    url: '/knowledge/base',
    method: 'delete',
    data: kbIds,
  });
}
