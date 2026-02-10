package com.mc.intervention.controller;

import com.mc.common.annotation.Log;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.R;
import com.mc.common.core.page.TableDataInfo;
import com.mc.common.enums.BusinessType;
import com.mc.common.utils.poi.ExcelUtil;
import com.mc.intervention.domain.InterventionNotification;
import com.mc.intervention.domain.vo.InterventionNotificationVo;
import com.mc.intervention.service.IInterventionNotificationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 干预通知表 控制层
 *
 * @author mc
 */
@RestController
@RequestMapping("/intervention/notification")
public class InterventionNotificationController extends BaseController {
    @Autowired
    private IInterventionNotificationService notificationService;

    /**
     * 查询干预通知表列表
     */
    @PreAuthorize("@ss.hasPermi('intervention:notification:list')")
    @GetMapping("/list")
    public TableDataInfo list(InterventionNotificationVo notification) {
        startPage();
        List<InterventionNotification> list = notificationService.selectNotificationList(notification);
        return getDataTable(list);
    }

    /**
     * 导出干预通知表列表
     */
    @PreAuthorize("@ss.hasPermi('intervention:notification:export')")
    @Log(title = "干预通知表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, InterventionNotificationVo notification) {
        List<InterventionNotification> list = notificationService.selectNotificationList(notification);
        ExcelUtil<InterventionNotification> util = new ExcelUtil<>(InterventionNotification.class);
        util.exportExcel(response, list, "干预通知表数据");
    }

    /**
     * 获取干预通知表详细信息
     */
    @PreAuthorize("@ss.hasPermi('intervention:notification:query')")
    @GetMapping(value = "/{notificationId}")
    public R<InterventionNotification> getInfo(@PathVariable("notificationId") Long notificationId) {
        return R.ok(notificationService.selectNotificationById(notificationId));
    }

    /**
     * 新增干预通知表
     */
    @PreAuthorize("@ss.hasPermi('intervention:notification:add')")
    @Log(title = "干预通知表", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Integer> add(@RequestBody InterventionNotification notification) {
        return R.ok(notificationService.insertNotification(notification));
    }

    /**
     * 修改干预通知表
     */
    @PreAuthorize("@ss.hasPermi('intervention:notification:edit')")
    @Log(title = "干预通知表", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Integer> edit(@RequestBody InterventionNotification notification) {
        return R.ok(notificationService.updateNotification(notification));
    }

    /**
     * 删除干预通知表
     */
    @PreAuthorize("@ss.hasPermi('intervention:notification:remove')")
    @Log(title = "干预通知表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{notificationIds}")
    public R<Integer> remove(@PathVariable Long[] notificationIds) {
        return R.ok(notificationService.deleteNotificationByIds(notificationIds));
    }

    /**
     * 更新通知阅读状态
     */
    @PreAuthorize("@ss.hasPermi('intervention:notification:edit')")
    @Log(title = "干预通知表", businessType = BusinessType.UPDATE)
    @PutMapping("/readStatus")
    public R<Integer> updateReadStatus(@RequestParam Long notificationId, @RequestParam String readStatus) {
        return R.ok(notificationService.updateNotificationReadStatus(notificationId, readStatus));
    }

    /**
     * 更新通知处理状态
     */
    @PreAuthorize("@ss.hasPermi('intervention:notification:edit')")
    @Log(title = "干预通知表", businessType = BusinessType.UPDATE)
    @PutMapping("/processStatus")
    public R<Integer> updateProcessStatus(@RequestParam Long notificationId, @RequestParam String processStatus) {
        return R.ok(notificationService.updateNotificationProcessStatus(notificationId, processStatus));
    }

    /**
     * 生成干预通知
     */
    @PreAuthorize("@ss.hasPermi('intervention:notification:add')")
    @Log(title = "干预通知表", businessType = BusinessType.INSERT)
    @PostMapping("/generate")
    public R<Integer> generateNotification(@RequestParam Long resultId, @RequestParam Long studentId) {
        return R.ok(notificationService.generateNotification(resultId, studentId));
    }

    /**
     * 批量生成干预通知
     */
    @PreAuthorize("@ss.hasPermi('intervention:notification:add')")
    @Log(title = "干预通知表", businessType = BusinessType.INSERT)
    @PostMapping("/batchGenerate")
    public R<Integer> batchGenerateNotification(@RequestBody List<Long> resultIds) {
        return R.ok(notificationService.batchGenerateNotification(resultIds));
    }
}
