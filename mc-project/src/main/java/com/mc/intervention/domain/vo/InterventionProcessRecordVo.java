package com.mc.intervention.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 干预处理记录表 查询参数
 *
 * @author mc
 */
@Data
@Schema(description = "干预处理记录表查询参数")
public class InterventionProcessRecordVo {

    private static final long serialVersionUID = 1L;

    @Schema(description = "记录ID")
    private Long recordId;

    @Schema(description = "通知ID")
    private Long notificationId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "通知内容")
    private String notificationContent;

    @Schema(description = "处理内容")
    private String processContent;

    @Schema(description = "处理状态（0正常 1异常）")
    private String status;

    @Schema(description = "处理结果")
    private String processResult;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "处理时间")
    private Date processTime;

    @Schema(description = "页码")
    private Integer pageNum;

    @Schema(description = "每页条数")
    private Integer pageSize;
}
