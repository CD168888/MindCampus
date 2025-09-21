package com.mc.student.service.impl;

import java.util.List;

import com.mc.common.core.domain.entity.SysUser;
import com.mc.common.utils.DateUtils;
import com.mc.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mc.student.mapper.StudentInfoMapper;
import com.mc.student.domain.StudentInfo;
import com.mc.student.service.IStudentInfoService;

/**
 * 学生信息Service业务层处理
 *
 * @author caidu
 * @date 2025-09-21
 */
@Service
public class StudentInfoServiceImpl implements IStudentInfoService {
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
    public StudentInfo selectStudentInfoByStudentId(Long studentId) {
        return studentInfoMapper.selectStudentInfoByStudentId(studentId);
    }

    /**
     * 查询学生信息列表
     *
     * @param studentInfo 学生信息
     * @return 学生信息
     */
    @Override
    public List<StudentInfo> selectStudentInfoList(StudentInfo studentInfo) {
        return studentInfoMapper.selectStudentInfoList(studentInfo);
    }

    /**
     * 新增学生信息
     *
     * @param studentInfo 学生信息
     * @return 结果
     */
    @Override
    public int insertStudentInfo(StudentInfo studentInfo) {
        studentInfo.setCreateTime(DateUtils.getNowDate());
        return studentInfoMapper.insertStudentInfo(studentInfo);
    }

    /**
     * 修改学生信息
     *
     * @param studentInfo 学生信息
     * @return 结果
     */
    @Override
    public int updateStudentInfo(StudentInfo studentInfo) {
        studentInfo.setUpdateTime(DateUtils.getNowDate());
        return studentInfoMapper.updateStudentInfo(studentInfo);
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
     * 查询未绑定的用户ID列表
     *
     * @return 列表
     */
    @Override
    public List<String> listUnbindUserIds() {
        // 查询所有用户列表
        List<SysUser> sysUsers = sysUserMapper.selectUserList(new SysUser());
        // 提取所有用户的ID，并过滤掉空ID
        List<String> allUserIds = sysUsers.stream()
                .map(user -> String.valueOf(user.getUserId()))
                .filter(id -> !id.isEmpty())
                .toList();

        // 查询所有已绑定的学生信息列表
        List<StudentInfo> studentInfos = studentInfoMapper.selectStudentInfoList(new StudentInfo());
        // 提取已绑定学生的用户ID，并过滤掉空ID
        List<String> boundUserIds = studentInfos.stream()
                .map(student -> String.valueOf(student.getUserId()))
                .filter(id -> !id.isEmpty())
                .toList();

        // 返回未绑定的用户ID列表（即在所有用户ID中但不在已绑定用户ID中的ID）
        return allUserIds.stream()
                .filter(id -> !boundUserIds.contains(id))
                .toList();
    }
}
