package com.mc.ai.tool;

import com.mc.student.domain.Student;
import com.mc.student.service.IStudentInfoService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 学生信息工具类，用于AI工具调用
 * 提供学生信息查询、管理等功能
 *
 * @author caidu
 * @date 2025-09-22
 */
@Component
public class StudentInfoTool {

    @Autowired
    private IStudentInfoService studentInfoService;

    /**
     * 根据学生ID查询学生信息
     *
     * @param studentId 学生ID
     * @return 学生信息
     */
    @Tool(name = "getStudentById", description = "根据学生ID查询学生详细信息")
    public Student getStudentById(@Parameter(description = "学生ID") Long studentId) {
        return studentInfoService.selectStudentInfoByStudentId(studentId);
    }

    /**
     * 查询所有学生信息
     *
     * @return 学生信息列表
     */
    @Tool(name = "getAllStudents", description = "查询所有学生信息列表")
    public List<Student> getAllStudents() {
        return studentInfoService.selectStudentInfoList(new Student());
    }

    /**
     * 根据学号查询学生信息
     *
     * @param studentNo 学号
     * @return 学生信息
     */
    @Tool(name = "getStudentByNo", description = "根据学号查询学生信息")
    public Student getStudentByNo(@Parameter(description = "学号") String studentNo) {
        Student student = new Student();
        student.setStudentNo(studentNo);
        List<Student> studentList = studentInfoService.selectStudentInfoList(student);
        return studentList.isEmpty() ? null : studentList.get(0);
    }

    /**
     * 查询未绑定用户的学生列表
     *
     * @return 未绑定用户的学生列表
     */
    @Tool(name = "getUnbindUsers", description = "查询未绑定学生信息的系统用户列表")
    public List<Map<String, Object>> getUnbindUsers() {
        return studentInfoService.listUnbindUserInfos();
    }
}
