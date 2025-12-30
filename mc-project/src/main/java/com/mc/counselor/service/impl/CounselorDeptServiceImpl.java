package com.mc.counselor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.counselor.domain.CounselorDept;
import com.mc.counselor.mapper.CounselorDeptMapper;
import com.mc.counselor.service.ICounselorDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.stream;

/**
 * 辅导员部门关联Service业务层处理
 *
 * @author caidu
 * @date 2025-12-22
 */
@Service
public class CounselorDeptServiceImpl extends ServiceImpl<CounselorDeptMapper, CounselorDept> implements ICounselorDeptService {
    @Autowired
    private CounselorDeptMapper counselorDeptMapper;

    /**
     * 查询辅导员部门关联
     *
     * @param deptCounselorId 辅导员部门关联主键
     * @return 辅导员部门关联
     */
    @Override
    public CounselorDept selectCounselorDeptByDeptCounselorId(Long deptCounselorId) {
        return counselorDeptMapper.selectById(deptCounselorId);
    }

    /**
     * 查询辅导员部门关联列表
     *
     * @param counselorDept 辅导员部门关联
     * @return 辅导员部门关联集合
     */
    @Override
    public List<CounselorDept> selectCounselorDeptList(CounselorDept counselorDept) {
        return counselorDeptMapper.selectList(null);
    }

    /**
     * 新增辅导员部门关联
     *
     * @param counselorDept 辅导员部门关联
     * @return 结果
     */
    @Override
    public int insertCounselorDept(CounselorDept counselorDept) {
        return counselorDeptMapper.insert(counselorDept);
    }

    /**
     * 修改辅导员部门关联
     *
     * @param counselorDept 辅导员部门关联
     * @return 结果
     */
    @Override
    public int updateCounselorDept(CounselorDept counselorDept) {
        return counselorDeptMapper.updateById(counselorDept);
    }

    /**
     * 批量删除辅导员部门关联
     *
     * @param deptCounselorIds 需要删除的辅导员部门关联主键集合
     * @return 结果
     */
    @Override
    public int deleteCounselorDeptByDeptCounselorIds(Long[] deptCounselorIds) {
        int result = 0;
        for (Long deptCounselorId : deptCounselorIds) {
            result += counselorDeptMapper.deleteById(deptCounselorId);
        }
        return result;
    }

    /**
     * 删除辅导员部门关联信息
     *
     * @param deptCounselorId 辅导员部门关联主键
     * @return 结果
     */
    @Override
    public int deleteCounselorDeptByDeptCounselorId(Long deptCounselorId) {
        return counselorDeptMapper.deleteById(deptCounselorId);
    }

    /**
     * 根据部门ID查询辅导员ID
     *
     * @param deptId 部门ID
     * @return 辅导员ID
     */
    @Override
    public Long selectCounselorIdByDeptId(Long deptId) {
        LambdaQueryWrapper<CounselorDept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CounselorDept::getDeptId, deptId);
        CounselorDept counselorDept = counselorDeptMapper.selectOne(queryWrapper);
        return counselorDept != null ? counselorDept.getCounselorId() : null;
    }

    /**
     * 根据辅导员ID查询部门列表
     *
     * @param counselorId 辅导员ID
     * @return 部门ID列表
     */
    @Override

    public Long[] selectDeptIdsByCounselorId(Long counselorId) {
        LambdaQueryWrapper<CounselorDept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CounselorDept::getCounselorId, counselorId);
        return counselorDeptMapper.selectList(queryWrapper)
                .stream()
                .map(CounselorDept::getDeptId)
                .toArray(Long[]::new);
    }

    /**
     * 绑定辅导员部门关系
     *
     * @param counselorId 辅导员ID
     * @param deptIds     部门ID列表
     * @return 绑定结果
     */
    @Override
    public int bindCounselorDept(Long counselorId, Long[] deptIds) {
        return stream(deptIds)
                .mapToInt(deptId -> {
                    CounselorDept counselorDept = new CounselorDept();
                    counselorDept.setCounselorId(counselorId);
                    counselorDept.setDeptId(deptId);
                    return counselorDeptMapper.insert(counselorDept);
                })
                .sum();
    }
}