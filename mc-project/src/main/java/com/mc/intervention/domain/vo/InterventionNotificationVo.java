package com.mc.intervention.domain.vo;

import com.mc.intervention.domain.InterventionNotification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 干预通知表 查询参数
 *
 * @author mc
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "干预通知表查询参数")
public class InterventionNotificationVo extends InterventionNotification {
    private static final long serialVersionUID = 1L;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String beginTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endTime;

    /**
     * 分页参数
     */
    @Schema(description = "页码")
    private Integer pageNum;

    @Schema(description = "每页条数")
    private Integer pageSize;
}
