import request from '@/utils/request'

// 绑定辅导员和部门
export function bindCounselorDept(data) {
  return request({
    url: '/counselor/counselordept/bind',
    method: 'post',
    data: data
  })
}

// 查询所有辅导员信息
export function getAllCounselors() {
  return request({
    url: '/counselor/counselordept/list',
    method: 'get'
  })
}
