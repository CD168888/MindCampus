package com.mc.intervention.controller;

import com.mc.common.annotation.Log;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.R;
import com.mc.common.core.page.TableDataInfo;
import com.mc.common.enums.BusinessType;
import com.mc.common.utils.poi.ExcelUtil;
import com.mc.intervention.domain.InterventionProcessRecord;
import com.mc.intervention.service.IInterventionProcessRecordService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 干预处理记录表 控制层
 *
 * @author mc
 */
@RestController
@RequestMapping("/intervention/process")
public class InterventionProcessRecordController extends BaseController {
    @Autowired
    private IInterventionProcessRecordService recordService;

    /**
     * 查询干预处理记录表列表
     */
    @PreAuthorize("@ss.hasPermi('intervention:process:list')")
    @GetMapping("/list")
    public TableDataInfo list(InterventionProcessRecord record) {
        startPage();
        List<InterventionProcessRecord> list = recordService.selectRecordList(record);
        return getDataTable(list);
    }

    /**
     * 导出干预处理记录表列表
     */
    @PreAuthorize("@ss.hasPermi('intervention:process:export')")
    @Log(title = "干预处理记录表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, InterventionProcessRecord record) {
        List<InterventionProcessRecord> list = recordService.selectRecordList(record);
        ExcelUtil<InterventionProcessRecord> util = new ExcelUtil<InterventionProcessRecord>(InterventionProcessRecord.class);
        util.exportExcel(response, list, "干预处理记录表数据");
    }

    /**
     * 获取干预处理记录表详细信息
     */
    @PreAuthorize("@ss.hasPermi('intervention:process:query')")
    @GetMapping(value = "/{recordId}")
    public R<InterventionProcessRecord> getInfo(@PathVariable("recordId") Long recordId) {
        return R.ok(recordService.selectRecordById(recordId));
    }

    /**
     * 新增干预处理记录表
     */
    @PreAuthorize("@ss.hasPermi('intervention:process:add')")
    @Log(title = "干预处理记录表", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Integer> add(@RequestBody InterventionProcessRecord record) {
        return R.ok(recordService.insertRecord(record));
    }

    /**
     * 修改干预处理记录表
     */
    @PreAuthorize("@ss.hasPermi('intervention:process:edit')")
    @Log(title = "干预处理记录表", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Integer> edit(@RequestBody InterventionProcessRecord record) {
        return R.ok(recordService.updateRecord(record));
    }

    /**
     * 删除干预处理记录表
     */
    @PreAuthorize("@ss.hasPermi('intervention:process:remove')")
    @Log(title = "干预处理记录表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public R<Integer> remove(@PathVariable Long[] recordIds) {
        return R.ok(recordService.deleteRecordByIds(recordIds));
    }

    /**
     * 处理干预通知
     */
    @PreAuthorize("@ss.hasPermi('intervention:process:add')")
    @Log(title = "干预处理记录表", businessType = BusinessType.INSERT)
    @PostMapping("/handle")
    public R<Integer> handleNotification(@RequestParam Long notificationId,
                                         @RequestParam Long userId,
                                         @RequestParam String processContent,
                                         @RequestParam String processResult) {
        return R.ok(recordService.processNotification(notificationId, userId, processContent, processResult));
    }
}
