import request from '@/utils/request';

// 查询辅导员管理列表
export function listCounselorinfo(query) {
  return request({
    url: '/counselor/counselorinfo/list',
    method: 'get',
    params: query,
  });
}

// 查询辅导员管理详细
export function getCounselorinfo(counselorId) {
  return request({
    url: '/counselor/counselorinfo/' + counselorId,
    method: 'get',
  });
}

// 新增辅导员管理
export function addCounselorinfo(data) {
  return request({
    url: '/counselor/counselorinfo',
    method: 'post',
    data: data,
  });
}

// 修改辅导员管理
export function updateCounselorinfo(data) {
  return request({
    url: '/counselor/counselorinfo',
    method: 'put',
    data: data,
  });
}

// 删除辅导员管理
export function delCounselorinfo(counselorId) {
  return request({
    url: '/counselor/counselorinfo/' + counselorId,
    method: 'delete',
  });
}

// 查询未绑定的用户ID/昵称列表
export function listUnbindUsers() {
  return request({
    url: '/counselor/counselorinfo/listUnbindUsers',
    method: 'get',
  });
}
