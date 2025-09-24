package com.mc.counselor.controller;

import com.mc.common.annotation.Log;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.AjaxResult;
import com.mc.common.core.domain.R;
import com.mc.common.core.page.TableDataInfo;
import com.mc.common.enums.BusinessType;
import com.mc.common.utils.poi.ExcelUtil;
import com.mc.counselor.domain.CounselorInfo;
import com.mc.counselor.service.ICounselorInfoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 辅导员管理Controller
 *
 * @author caidu
 * @date 2025-09-24
 */
@RestController
@RequestMapping("/counselor/counselorinfo")
public class CounselorInfoController extends BaseController {
    @Autowired
    private ICounselorInfoService counselorInfoService;

    /**
     * 查询辅导员管理列表
     */
    @PreAuthorize("@ss.hasPermi('counselor:counselorinfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(CounselorInfo counselorInfo) {
        startPage();
        List<CounselorInfo> list = counselorInfoService.selectCounselorInfoList(counselorInfo);
        return getDataTable(list);
    }

    /**
     * 导出辅导员管理列表
     */
    @PreAuthorize("@ss.hasPermi('counselor:counselorinfo:export')")
    @Log(title = "辅导员管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CounselorInfo counselorInfo) {
        List<CounselorInfo> list = counselorInfoService.selectCounselorInfoList(counselorInfo);
        ExcelUtil<CounselorInfo> util = new ExcelUtil<CounselorInfo>(CounselorInfo.class);
        util.exportExcel(response, list, "辅导员管理数据");
    }

    /**
     * 获取辅导员管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('counselor:counselorinfo:query')")
    @GetMapping(value = "/{counselorId}")
    public AjaxResult getInfo(@PathVariable("counselorId") Long counselorId) {
        return success(counselorInfoService.selectCounselorInfoByCounselorId(counselorId));
    }

    /**
     * 新增辅导员管理
     */
    @PreAuthorize("@ss.hasPermi('counselor:counselorinfo:add')")
    @Log(title = "辅导员管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CounselorInfo counselorInfo) {
        return toAjax(counselorInfoService.insertCounselorInfo(counselorInfo));
    }

    /**
     * 修改辅导员管理
     */
    @PreAuthorize("@ss.hasPermi('counselor:counselorinfo:edit')")
    @Log(title = "辅导员管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CounselorInfo counselorInfo) {
        return toAjax(counselorInfoService.updateCounselorInfo(counselorInfo));
    }

    /**
     * 删除辅导员管理
     */
    @PreAuthorize("@ss.hasPermi('counselor:counselorinfo:remove')")
    @Log(title = "辅导员管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{counselorIds}")
    public AjaxResult remove(@PathVariable Long[] counselorIds) {
        return toAjax(counselorInfoService.deleteCounselorInfoByCounselorIds(counselorIds));
    }

    /**
     * 查询未绑定的用户ID/昵称
     */
    @Operation(summary = "查询未绑定的用户ID/昵称列表")
    @PreAuthorize("@ss.hasPermi('counselor:counselorinfo:listUnbindUsers')")
    @GetMapping("/listUnbindUsers")
    public R<List<Map<String, Object>>> listUnbindUserIds() {
        List<Map<String, Object>> maps = counselorInfoService.listUnbindUserInfos();
        return R.ok(maps);
    }
}
