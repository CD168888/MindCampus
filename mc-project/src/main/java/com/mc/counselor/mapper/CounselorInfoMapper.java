package com.mc.counselor.mapper;

import com.mc.counselor.domain.CounselorInfo;

import java.util.List;

/**
 * 辅导员管理Mapper接口
 *
 * @author caidu
 * @date 2025-09-24
 */
public interface CounselorInfoMapper {
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
     * 删除辅导员管理
     *
     * @param counselorId 辅导员管理主键
     * @return 结果
     */
    public int deleteCounselorInfoByCounselorId(Long counselorId);

    /**
     * 批量删除辅导员管理
     *
     * @param counselorIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCounselorInfoByCounselorIds(Long[] counselorIds);
}
