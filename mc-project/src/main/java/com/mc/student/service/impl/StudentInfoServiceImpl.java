package com.mc.student.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.common.core.domain.entity.SysUser;
import com.mc.common.utils.DateUtils;
import com.mc.student.domain.Student;
import com.mc.student.mapper.StudentInfoMapper;
import com.mc.student.service.IStudentInfoService;
import com.mc.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 学生信息Service业务层处理
 *
 * @author caidu
 * @date 2025-09-21
 */
@Service
public class StudentInfoServiceImpl extends ServiceImpl<StudentInfoMapper, Student> implements IStudentInfoService {
    @Autowired
    private StudentInfoMapper studentInfoMapper;
    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 查询学生信息
     *
     * @param studentId 学生信息主键
     * @return 学生信息
     */
    @Override
    public Student selectStudentInfoByStudentId(Long studentId) {
        return studentInfoMapper.selectStudentInfoByStudentId(studentId);
    }

    /**
     * 查询学生信息列表
     *
     * @param student 学生信息
     * @return 学生信息
     */
    @Override
    public List<Student> selectStudentInfoList(Student student) {
        return studentInfoMapper.selectStudentInfoList(student);
    }

    /**
     * 新增学生信息
     *
     * @param student 学生信息
     * @return 结果
     */
    @Override
    public int insertStudentInfo(Student student) {
        student.setCreateTime(DateUtils.getNowDate());
        return studentInfoMapper.insertStudentInfo(student);
    }

    /**
     * 修改学生信息
     *
     * @param student 学生信息
     * @return 结果
     */
    @Override
    public int updateStudentInfo(Student student) {
        student.setUpdateTime(DateUtils.getNowDate());
        return studentInfoMapper.updateStudentInfo(student);
    }

    /**
     * 批量删除学生信息
     *
     * @param studentIds 需要删除的学生信息主键
     * @return 结果
     */
    @Override
    public int deleteStudentInfoByStudentIds(Long[] studentIds) {
        return studentInfoMapper.deleteStudentInfoByStudentIds(studentIds);
    }

    /**
     * 删除学生信息信息
     *
     * @param studentId 学生信息主键
     * @return 结果
     */
    @Override
    public int deleteStudentInfoByStudentId(Long studentId) {
        return studentInfoMapper.deleteStudentInfoByStudentId(studentId);
    }

    /**
     * 查询未绑定的用户ID/昵称列表
     *
     * @return 列表
     */
    @Override
    public List<Map<String, Object>> listUnbindUserInfos() {
        List<SysUser> sysUsers = sysUserMapper.selectUserList(new SysUser());

        Map<Long, SysUser> userMap = sysUsers.stream()
                .filter(user -> "01".equals(user.getUserType()) && user.getUserId() != null && user.getUserId() != 1)
                .collect(Collectors.toMap(SysUser::getUserId, user -> user));

        Set<Long> boundUserIds = studentInfoMapper.selectStudentInfoList(new Student()).stream()
                .map(Student::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return userMap.keySet().stream()
                .filter(id -> !boundUserIds.contains(id))
                .map(id -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("userId", id);
                    map.put("nickName", userMap.get(id).getNickName());
                    return map;
                })
                .toList();
    }
}
