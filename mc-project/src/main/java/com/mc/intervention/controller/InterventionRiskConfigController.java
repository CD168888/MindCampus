package com.mc.intervention.controller;

import com.mc.common.annotation.Log;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.R;
import com.mc.common.enums.BusinessType;
import com.mc.intervention.domain.InterventionRiskConfig;
import com.mc.intervention.service.IInterventionRiskConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 风险等级配置表 控制层
 *
 * @author mc
 */
@RestController
@RequestMapping("/intervention/risk")
public class InterventionRiskConfigController extends BaseController {
    @Autowired
    private IInterventionRiskConfigService configService;

    /**
     * 获取所有风险等级配置（低、中、高）
     */
    @PreAuthorize("@ss.hasPermi('intervention:risk:query')")
    @GetMapping("/config")
    public R<List<InterventionRiskConfig>> getConfig() {
        return R.ok(configService.getOrCreateAllConfig());
    }

    /**
     * 修改风险等级配置
     */
    @PreAuthorize("@ss.hasPermi('intervention:risk:edit')")
    @Log(title = "风险等级配置", businessType = BusinessType.UPDATE)
    @PutMapping("/config")
    public R<Integer> edit(@RequestBody InterventionRiskConfig config) {
        return R.ok(configService.updateConfig(config));
    }

    /**
     * 根据分数查询风险等级配置
     */
    @PreAuthorize("@ss.hasPermi('intervention:risk:query')")
    @GetMapping("/getByScore")
    public R<InterventionRiskConfig> getConfigByScore(@RequestParam Integer score) {
        return R.ok(configService.selectConfigByScore(score));
    }

    /**
     * 根据风险等级查询配置
     */
    @PreAuthorize("@ss.hasPermi('intervention:risk:query')")
    @GetMapping("/getByLevel")
    public R<InterventionRiskConfig> getConfigByRiskLevel(@RequestParam String riskLevel) {
        return R.ok(configService.getOrCreateConfigByLevel(riskLevel));
    }
}
