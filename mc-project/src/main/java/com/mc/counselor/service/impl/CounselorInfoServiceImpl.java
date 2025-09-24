package com.mc.counselor.service.impl;

import com.mc.common.core.domain.entity.SysUser;
import com.mc.common.utils.DateUtils;
import com.mc.counselor.domain.CounselorInfo;
import com.mc.counselor.mapper.CounselorInfoMapper;
import com.mc.counselor.service.ICounselorInfoService;
import com.mc.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 辅导员管理Service业务层处理
 *
 * @author caidu
 * @date 2025-09-24
 */
@Service
public class CounselorInfoServiceImpl implements ICounselorInfoService {
    @Autowired
    private CounselorInfoMapper counselorInfoMapper;
    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 查询辅导员管理
     *
     * @param counselorId 辅导员管理主键
     * @return 辅导员管理
     */
    @Override
    public CounselorInfo selectCounselorInfoByCounselorId(Long counselorId) {
        return counselorInfoMapper.selectCounselorInfoByCounselorId(counselorId);
    }

    /**
     * 查询辅导员管理列表
     *
     * @param counselorInfo 辅导员管理
     * @return 辅导员管理
     */
    @Override
    public List<CounselorInfo> selectCounselorInfoList(CounselorInfo counselorInfo) {
        return counselorInfoMapper.selectCounselorInfoList(counselorInfo);
    }

    /**
     * 新增辅导员管理
     *
     * @param counselorInfo 辅导员管理
     * @return 结果
     */
    @Override
    public int insertCounselorInfo(CounselorInfo counselorInfo) {
        counselorInfo.setCreateTime(DateUtils.getNowDate());
        return counselorInfoMapper.insertCounselorInfo(counselorInfo);
    }

    /**
     * 修改辅导员管理
     *
     * @param counselorInfo 辅导员管理
     * @return 结果
     */
    @Override
    public int updateCounselorInfo(CounselorInfo counselorInfo) {
        counselorInfo.setUpdateTime(DateUtils.getNowDate());
        return counselorInfoMapper.updateCounselorInfo(counselorInfo);
    }

    /**
     * 批量删除辅导员管理
     *
     * @param counselorIds 需要删除的辅导员管理主键
     * @return 结果
     */
    @Override
    public int deleteCounselorInfoByCounselorIds(Long[] counselorIds) {
        return counselorInfoMapper.deleteCounselorInfoByCounselorIds(counselorIds);
    }

    /**
     * 删除辅导员管理信息
     *
     * @param counselorId 辅导员管理主键
     * @return 结果
     */
    @Override
    public int deleteCounselorInfoByCounselorId(Long counselorId) {
        return counselorInfoMapper.deleteCounselorInfoByCounselorId(counselorId);
    }

    /**
     * 查询未绑定的用户ID/昵称列表
     *
     * @return 列表
     */
    @Override
    public List<Map<String, Object>> listUnbindUserInfos() {
        List<SysUser> sysUsers = sysUserMapper.selectUserList(new SysUser());

        Map<Long, SysUser> userMap = sysUsers.stream()
                .filter(user -> "01".equals(user.getUserType()) && user.getUserId() != null && user.getUserId() != 1)
                .collect(Collectors.toMap(SysUser::getUserId, user -> user));

        Set<Long> boundUserIds = counselorInfoMapper.selectCounselorInfoList(new CounselorInfo()).stream()
                .map(CounselorInfo::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return userMap.keySet().stream()
                .filter(id -> !boundUserIds.contains(id))
                .map(id -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("userId", id);
                    map.put("nickName", userMap.get(id).getNickName());
                    return map;
                })
                .toList();
    }
}
