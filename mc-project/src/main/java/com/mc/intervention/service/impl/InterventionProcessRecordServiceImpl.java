package com.mc.intervention.service.impl;


import com.mc.intervention.domain.InterventionProcessRecord;
import com.mc.intervention.mapper.InterventionProcessRecordMapper;
import com.mc.intervention.service.IInterventionNotificationService;
import com.mc.intervention.service.IInterventionProcessRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 干预处理记录表 服务实现
 *
 * @author mc
 */
@Service
public class InterventionProcessRecordServiceImpl implements IInterventionProcessRecordService {
    @Autowired
    private InterventionProcessRecordMapper recordMapper;

    @Autowired
    private IInterventionNotificationService notificationService;

    /**
     * 查询干预处理记录表列表
     */
    @Override
    public List<InterventionProcessRecord> selectRecordList(InterventionProcessRecord record) {
        return recordMapper.selectRecordList(record);
    }

    /**
     * 查询干预处理记录表信息
     */
    @Override
    public InterventionProcessRecord selectRecordById(Long recordId) {
        return recordMapper.selectRecordById(recordId);
    }

    /**
     * 根据通知ID查询处理记录
     */
    @Override
    public List<InterventionProcessRecord> selectRecordByNotificationId(Long notificationId) {
        return recordMapper.selectRecordByNotificationId(notificationId);
    }

    /**
     * 根据用户ID查询处理记录
     */
    @Override
    public List<InterventionProcessRecord> selectRecordByUserId(Long userId) {
        return recordMapper.selectRecordByUserId(userId);
    }

    /**
     * 新增干预处理记录表
     */
    @Override
    public int insertRecord(InterventionProcessRecord record) {
        record.setCreateTime(new Date());
        return recordMapper.insertRecord(record);
    }

    /**
     * 修改干预处理记录表
     */
    @Override
    public int updateRecord(InterventionProcessRecord record) {
        record.setUpdateTime(new Date());
        return recordMapper.updateRecord(record);
    }

    /**
     * 删除干预处理记录表
     */
    @Override
    public int deleteRecordById(Long recordId) {
        return recordMapper.deleteRecordById(recordId);
    }

    /**
     * 批量删除干预处理记录表
     */
    @Override
    public int deleteRecordByIds(Long[] recordIds) {
        return recordMapper.deleteRecordByIds(recordIds);
    }

    /**
     * 处理干预通知
     */
    @Override
    public int processNotification(Long notificationId, Long userId, String processContent, String processResult) {
        // 创建处理记录
        InterventionProcessRecord record = new InterventionProcessRecord();
        record.setNotificationId(notificationId);
        record.setUserId(userId);
        record.setProcessContent(processContent);
        record.setProcessResult(processResult);
        record.setProcessTime(new Date());
        record.setStatus("0");
        record.setCreateTime(new Date());

        // 插入处理记录
        int result = recordMapper.insertRecord(record);

        // 更新通知处理状态
        if (result > 0) {
            notificationService.updateNotificationProcessStatus(notificationId, "1");
        }

        return result;
    }
}
