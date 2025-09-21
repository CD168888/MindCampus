package com.mc.student.service;

import java.util.List;
import com.mc.student.domain.StudentInfo;

/**
 * 学生信息Service接口
 * 
 * @author caidu
 * @date 2025-09-21
 */
public interface IStudentInfoService 
{
    /**
     * 查询学生信息
     * 
     * @param studentId 学生信息主键
     * @return 学生信息
     */
    public StudentInfo selectStudentInfoByStudentId(Long studentId);

    /**
     * 查询学生信息列表
     * 
     * @param studentInfo 学生信息
     * @return 学生信息集合
     */
    public List<StudentInfo> selectStudentInfoList(StudentInfo studentInfo);

    /**
     * 新增学生信息
     * 
     * @param studentInfo 学生信息
     * @return 结果
     */
    public int insertStudentInfo(StudentInfo studentInfo);

    /**
     * 修改学生信息
     * 
     * @param studentInfo 学生信息
     * @return 结果
     */
    public int updateStudentInfo(StudentInfo studentInfo);

    /**
     * 批量删除学生信息
     * 
     * @param studentIds 需要删除的学生信息主键集合
     * @return 结果
     */
    public int deleteStudentInfoByStudentIds(Long[] studentIds);

    /**
     * 删除学生信息信息
     * 
     * @param studentId 学生信息主键
     * @return 结果
     */
    public int deleteStudentInfoByStudentId(Long studentId);

    /**
     * 查询未绑定的用户ID列表
     *
     * @return 用户ID列表
     */
    List<String> listUnbindUserIds();
}
