package com.mc.evaluation.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 问卷列表VO
 *
 * @author caidu
 * @date 2025-11-04
 */
@Data
@Schema(description = "问卷列表VO")
public class QuestionnaireListVO {

    @Schema(description = "待填问卷列表")
    private List<QuestionnaireItemVO> pendingList;

    @Schema(description = "已完成问卷列表")
    private List<QuestionnaireItemVO> completedList;
}

