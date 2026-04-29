package com.mc.knowledge.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 学生信息实体
 */
@Data
@TableName("student_info")
public class StudentInfo {

    @TableId(type = IdType.AUTO)
    private Long studentId;

    private Long userId;

    private String studentNo;

    private String name;

    private String gender;

    private String grade;

    private String major;

    private String className;

    private String phone;

    private String status;
}
