import request from '@/utils/request';

export function listDocument(query) {
  return request({
    url: '/knowledge/document/list',
    method: 'get',
    params: query,
  });
}

export function getDocument(docId) {
  return request({
    url: '/knowledge/document/' + docId,
    method: 'get',
  });
}

export function uploadDocument(formData) {
  return request({
    url: '/knowledge/document/upload',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data: formData,
  });
}

export function delDocument(docId) {
  return request({
    url: '/knowledge/document/' + docId,
    method: 'delete',
  });
}

export function reEmbedDocument(docId) {
  return request({
    url: '/knowledge/document/reembed/' + docId,
    method: 'post',
  });
}

export function getDocumentProgress(docId) {
  return request({
    url: '/knowledge/document/progress/' + docId,
    method: 'get',
  });
}
