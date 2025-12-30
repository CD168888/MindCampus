package com.mc.counselor.controller;

import com.mc.common.annotation.Log;
import com.mc.common.core.controller.BaseController;
import com.mc.common.core.domain.AjaxResult;
import com.mc.common.enums.BusinessType;
import com.mc.counselor.domain.CounselorInfo;
import com.mc.counselor.service.ICounselorDeptService;
import com.mc.counselor.service.ICounselorInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 辅导员管理Controller
 *
 * @author caidu
 * @date 2025-09-24
 */
@RestController
@RequestMapping("/counselor/counselordept")
public class CounselorDeptController extends BaseController {
    @Autowired
    private ICounselorDeptService counselorDeptService;

    @Autowired
    private ICounselorInfoService counselorInfoService;

    /**
     * 绑定辅导员和部门
     *
     * @param counselorId 辅导员ID
     * @param deptIds 部门ID列表
     * @return 绑定结果
     */
    @PostMapping("/bind")
    @Log(title = "辅导员管理", businessType = BusinessType.INSERT)
    public AjaxResult bindCounselorDept(Long counselorId, Long[] deptIds) {
        return toAjax(counselorDeptService.bindCounselorDept(counselorId, deptIds));
    }

    /**
     * 查询所有辅导员信息
     */
    @GetMapping("/list")
    public AjaxResult list() {
        return success(counselorInfoService.selectCounselorInfoList(new CounselorInfo()));
    }
}
