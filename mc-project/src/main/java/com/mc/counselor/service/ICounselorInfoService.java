package com.mc.counselor.service;

import com.mc.counselor.domain.CounselorInfo;

import java.util.List;
import java.util.Map;

/**
 * 辅导员管理Service接口
 *
 * @author caidu
 * @date 2025-09-24
 */
public interface ICounselorInfoService {
    /**
     * 查询辅导员管理
     *
     * @param counselorId 辅导员管理主键
     * @return 辅导员管理
     */
    public CounselorInfo selectCounselorInfoByCounselorId(Long counselorId);

    /**
     * 查询辅导员管理列表
     *
     * @param counselorInfo 辅导员管理
     * @return 辅导员管理集合
     */
    public List<CounselorInfo> selectCounselorInfoList(CounselorInfo counselorInfo);

    /**
     * 新增辅导员管理
     *
     * @param counselorInfo 辅导员管理
     * @return 结果
     */
    public int insertCounselorInfo(CounselorInfo counselorInfo);

    /**
     * 修改辅导员管理
     *
     * @param counselorInfo 辅导员管理
     * @return 结果
     */
    public int updateCounselorInfo(CounselorInfo counselorInfo);

    /**
     * 批量删除辅导员管理
     *
     * @param counselorIds 需要删除的辅导员管理主键集合
     * @return 结果
     */
    public int deleteCounselorInfoByCounselorIds(Long[] counselorIds);

    /**
     * 删除辅导员管理信息
     *
     * @param counselorId 辅导员管理主键
     * @return 结果
     */
    public int deleteCounselorInfoByCounselorId(Long counselorId);

    /**
     * 查询未绑定的辅导员信息
     *
     * @return 辅导员信息
     */
    List<Map<String, Object>> listUnbindUserInfos();
}
