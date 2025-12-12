package com.mc.ai.config;

import com.mc.student.domain.Student;
import com.mc.student.service.IStudentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * AI工具配置类
 * 定义适合用户端使用的工具方法
 */
@Configuration
public class ToolConfig {

    @Autowired
    private IStudentInfoService studentInfoService;

    /**
     * 查询所有学生信息
     * @return 学生信息列表
     */
    @Bean
    @Description("查询所有学生信息，返回学生列表")
    public Supplier<List<Student>> getAllStudents() {
        return () -> studentInfoService.selectStudentInfoList(new Student());
    }

    /**
     * 根据学生ID查询学生信息
     * @return 学生信息
     */
    @Bean
    @Description("根据学生ID查询学生信息，参数为学生ID，返回学生对象")
    public Function<Long, Student> getStudentById() {
        return studentId -> studentInfoService.selectStudentInfoByStudentId(studentId);
    }

    /**
     * 根据学号查询学生信息
     * @return 学生信息
     */
    @Bean
    @Description("根据学号查询学生信息，参数为学号字符串，返回学生对象")
    public Function<String, Student> getStudentByNo() {
        return studentNo -> {
            Student student = new Student();
            student.setStudentNo(studentNo);
            List<Student> students = studentInfoService.selectStudentInfoList(student);
            return students.isEmpty() ? null : students.get(0);
        };
    }

    /**
     * 根据条件查询学生信息
     * @return 学生信息列表
     */
    @Bean
    @Description("根据条件查询学生信息，参数为包含查询条件的学生对象，返回学生列表")
    public Function<Student, List<Student>> queryStudents() {
        return student -> studentInfoService.selectStudentInfoList(student);
    }

    /**
     * 查询未绑定用户的学生信息
     * @return 未绑定用户的学生信息列表
     */
    @Bean
    @Description("查询未绑定用户的学生信息，返回未绑定用户的学生列表")
    public Supplier<List<Map<String, Object>>> getUnbindStudents() {
        return studentInfoService::listUnbindUserInfos;
    }
}


