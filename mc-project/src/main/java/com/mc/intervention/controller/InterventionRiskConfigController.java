package com.mc.intervention.controller;

import com.mc.common.annotation.Log;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.R;
import com.mc.common.core.page.TableDataInfo;
import com.mc.common.enums.BusinessType;
import com.mc.common.utils.poi.ExcelUtil;
import com.mc.intervention.domain.InterventionRiskConfig;
import com.mc.intervention.service.IInterventionRiskConfigService;
import jakarta.servlet.http.HttpServletResponse;
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
     * 查询风险等级配置表列表
     */
    @PreAuthorize("@ss.hasPermi('intervention:risk:list')")
    @GetMapping("/list")
    public TableDataInfo list(InterventionRiskConfig config) {
        startPage();
        List<InterventionRiskConfig> list = configService.selectConfigList(config);
        return getDataTable(list);
    }

    /**
     * 导出风险等级配置表列表
     */
    @PreAuthorize("@ss.hasPermi('intervention:risk:export')")
    @Log(title = "风险等级配置表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, InterventionRiskConfig config) {
        List<InterventionRiskConfig> list = configService.selectConfigList(config);
        ExcelUtil<InterventionRiskConfig> util = new ExcelUtil<InterventionRiskConfig>(InterventionRiskConfig.class);
        util.exportExcel(response, list, "风险等级配置表数据");
    }

    /**
     * 获取风险等级配置表详细信息
     */
    @PreAuthorize("@ss.hasPermi('intervention:risk:query')")
    @GetMapping(value = "/{configId}")
    public R<InterventionRiskConfig> getInfo(@PathVariable("configId") Long configId) {
        return R.ok(configService.selectConfigById(configId));
    }

    /**
     * 新增风险等级配置表
     */
    @PreAuthorize("@ss.hasPermi('intervention:risk:add')")
    @Log(title = "风险等级配置表", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Integer> add(@RequestBody InterventionRiskConfig config) {
        return R.ok(configService.insertConfig(config));
    }

    /**
     * 修改风险等级配置表
     */
    @PreAuthorize("@ss.hasPermi('intervention:risk:edit')")
    @Log(title = "风险等级配置表", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Integer> edit(@RequestBody InterventionRiskConfig config) {
        return R.ok(configService.updateConfig(config));
    }

    /**
     * 删除风险等级配置表
     */
    @PreAuthorize("@ss.hasPermi('intervention:risk:remove')")
    @Log(title = "风险等级配置表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public R<Integer> remove(@PathVariable Long[] configIds) {
        return R.ok(configService.deleteConfigByIds(configIds));
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
        return R.ok(configService.selectConfigByRiskLevel(riskLevel));
    }
}
