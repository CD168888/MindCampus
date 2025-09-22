package com.mc.student.domain;

import com.mc.common.annotation.Excel;
import com.mc.common.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 学生信息对象 student_info
 *
 * @author caidu
 * @date 2025-09-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "学生信息实体")
public class Student extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 学生ID
     */
    @Schema(description = "学生主键ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long studentId;

    /**
     * 用户id
     */
    @Schema(description = "关联的用户ID", example = "100", required = true)
    @Excel(name = "用户id")
    private Long userId;

    /**
     * 学号
     */
    @Schema(description = "学生学号", example = "2024001", required = true, maxLength = 20)
    @Excel(name = "学号")
    private String studentNo;

    /**
     * 姓名
     */
    @Schema(description = "学生姓名", example = "张三", required = true, maxLength = 50)
    @Excel(name = "姓名")
    private String name;

    /**
     * 性别（0/1）
     */
    @Schema(description = "学生性别", example = "0", allowableValues = {"0", "1"},
            implementation = String.class,
            externalDocs = @io.swagger.v3.oas.annotations.ExternalDocumentation(description = "0:男, 1:女"))
    @Excel(name = "性别", readConverterExp = "0=/1")
    private String gender;

    /**
     * 年级
     */
    @Schema(description = "所在年级", example = "2024级", maxLength = 10)
    @Excel(name = "年级")
    private String grade;

    /**
     * 专业
     */
    @Schema(description = "专业名称", example = "计算机科学与技术", maxLength = 100)
    @Excel(name = "专业")
    private String major;

    /**
     * 班级
     */
    @Schema(description = "班级名称", example = "计科2401班", maxLength = 50)
    @Excel(name = "班级")
    private String className;

    /**
     * 联系电话
     */
    @Schema(description = "学生联系电话", example = "13812345678", pattern = "^1[3-9]\\d{9}$", maxLength = 15)
    @Excel(name = "联系电话")
    private String phone;

    /**
     * 状态（0正常 1异常）
     */
    @Schema(description = "学生状态", example = "0", allowableValues = {"0", "1"},
            implementation = String.class,
            externalDocs = @io.swagger.v3.oas.annotations.ExternalDocumentation(description = "0:正常, 1:异常"))
    @Excel(name = "状态", readConverterExp = "0=正常,1=异常")
    private String status;
}