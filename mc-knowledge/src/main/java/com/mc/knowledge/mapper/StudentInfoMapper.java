package com.mc.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mc.knowledge.domain.entity.StudentInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 学生信息Mapper接口
 */
@Mapper
@Component("knowledgeStudentInfoMapper")
public interface StudentInfoMapper extends BaseMapper<StudentInfo> {

    /**
     * 查询所有学生（用于初始化Neo4j节点）
     */
    @Select("SELECT user_id as userId, student_id as studentId, student_no as studentNo, name, grade, major, class_name as className FROM student_info")
    List<StudentInfo> selectAllStudents();
}
