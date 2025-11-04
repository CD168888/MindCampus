package com.mc.evaluation.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 测评统计VO
 *
 * @author caidu
 * @date 2025-11-04
 */
@Data
@Schema(description = "测评统计VO")
public class AssessmentStatisticsVO {

    @Schema(description = "待填问卷数量")
    private Integer pending;

    @Schema(description = "已完成数量")
    private Integer completed;

    @Schema(description = "总计")
    private Integer total;
}

