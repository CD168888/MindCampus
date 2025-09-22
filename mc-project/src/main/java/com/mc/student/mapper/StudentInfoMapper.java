package com.mc.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mc.student.domain.Student;

import java.util.List;

/**
 * 学生信息Mapper接口
 *
 * @author caidu
 * @date 2025-09-21
 */
public interface StudentInfoMapper extends BaseMapper<Student> {
    /**
     * 查询学生信息
     *
     * @param studentId 学生信息主键
     * @return 学生信息
     */
    public Student selectStudentInfoByStudentId(Long studentId);

    /**
     * 查询学生信息列表
     *
     * @param student 学生信息
     * @return 学生信息集合
     */
    public List<Student> selectStudentInfoList(Student student);

    /**
     * 新增学生信息
     *
     * @param student 学生信息
     * @return 结果
     */
    public int insertStudentInfo(Student student);

    /**
     * 修改学生信息
     *
     * @param student 学生信息
     * @return 结果
     */
    public int updateStudentInfo(Student student);

    /**
     * 删除学生信息
     *
     * @param studentId 学生信息主键
     * @return 结果
     */
    public int deleteStudentInfoByStudentId(Long studentId);

    /**
     * 批量删除学生信息
     *
     * @param studentIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStudentInfoByStudentIds(Long[] studentIds);
}
