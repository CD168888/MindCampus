import request from '@/utils/request'

export function listUserDeptRelation(query) {
  return request({
    url: '/intervention/relation/list',
    method: 'get',
    params: query
  })
}

export function getUserDeptRelation(relationId) {
  return request({
    url: '/intervention/relation/' + relationId,
    method: 'get'
  })
}

export function addUserDeptRelation(data) {
  return request({
    url: '/intervention/relation',
    method: 'post',
    data: data
  })
}

export function updateUserDeptRelation(data) {
  return request({
    url: '/intervention/relation',
    method: 'put',
    data: data
  })
}

export function delUserDeptRelation(relationIds) {
  return request({
    url: '/intervention/relation/' + relationIds,
    method: 'delete'
  })
}