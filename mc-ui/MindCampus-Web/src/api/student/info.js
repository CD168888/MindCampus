import request from '@/utils/request';

// 查询学生信息列表
export function listInfo(query) {
  return request({
    url: '/student/info/list',
    method: 'get',
    params: query,
  });
}

// 查询学生信息详细
export function getInfo(studentId) {
  return request({
    url: '/student/info/' + studentId,
    method: 'get',
  });
}

// 新增学生信息
export function addInfo(data) {
  return request({
    url: '/student/info',
    method: 'post',
    data: data,
  });
}

// 修改学生信息
export function updateInfo(data) {
  return request({
    url: '/student/info',
    method: 'put',
    data: data,
  });
}

// 删除学生信息
export function delInfo(studentId) {
  return request({
    url: '/student/info/' + studentId,
    method: 'delete',
  });
}

// 查询未绑定的用户ID/昵称列表
export function listUnbindUsers() {
  return request({
    url: '/student/info/listUnbindUsers',
    method: 'get',
  });
}
