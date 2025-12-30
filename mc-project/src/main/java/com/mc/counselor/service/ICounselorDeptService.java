package com.mc.counselor.service;

import com.mc.counselor.domain.CounselorDept;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 辅导员部门关联Service接口
 *
 * @author caidu
 * @date 2025-12-22
 */
public interface ICounselorDeptService extends IService<CounselorDept> {
    /**
     * 查询辅导员部门关联
     *
     * @param deptCounselorId 辅导员部门关联主键
     * @return 辅导员部门关联
     */
    public CounselorDept selectCounselorDeptByDeptCounselorId(Long deptCounselorId);

    /**
     * 查询辅导员部门关联列表
     *
     * @param counselorDept 辅导员部门关联
     * @return 辅导员部门关联集合
     */
    public List<CounselorDept> selectCounselorDeptList(CounselorDept counselorDept);

    /**
     * 新增辅导员部门关联
     *
     * @param counselorDept 辅导员部门关联
     * @return 结果
     */
    public int insertCounselorDept(CounselorDept counselorDept);

    /**
     * 修改辅导员部门关联
     *
     * @param counselorDept 辅导员部门关联
     * @return 结果
     */
    public int updateCounselorDept(CounselorDept counselorDept);

    /**
     * 批量删除辅导员部门关联
     *
     * @param deptCounselorIds 需要删除的辅导员部门关联主键集合
     * @return 结果
     */
    public int deleteCounselorDeptByDeptCounselorIds(Long[] deptCounselorIds);

    /**
     * 删除辅导员部门关联信息
     *
     * @param deptCounselorId 辅导员部门关联主键
     * @return 结果
     */
    public int deleteCounselorDeptByDeptCounselorId(Long deptCounselorId);

    /**
     * 根据部门ID查询辅导员ID
     *
     * @param deptId 部门ID
     * @return 辅导员ID
     */
    Long selectCounselorIdByDeptId(Long deptId);

    /**
     * 根据辅导员ID查询部门列表
     *
     * @param counselorId 辅导员ID
     * @return 部门ID列表
     */
    Long[] selectDeptIdsByCounselorId(Long counselorId);

    /**
     * 绑定辅导员部门关系
     *
     * @param counselorId 辅导员ID
     * @param deptIds 部门ID列表
     * @return 结果
     */
    int bindCounselorDept(Long counselorId, Long[] deptIds);
}