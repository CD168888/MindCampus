package com.mc.intervention.mapper;

import com.mc.intervention.domain.InterventionProcessRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 干预处理记录表 数据层
 *
 * @author mc
 */
@Mapper
public interface InterventionProcessRecordMapper {
    /**
     * 查询干预处理记录表数据
     *
     * @param record 干预处理记录表
     * @return 干预处理记录表数据
     */
    public List<InterventionProcessRecord> selectRecordList(InterventionProcessRecord record);

    /**
     * 查询干预处理记录表信息
     *
     * @param recordId 干预处理记录表ID
     * @return 干预处理记录表信息
     */
    public InterventionProcessRecord selectRecordById(Long recordId);

    /**
     * 根据通知ID查询处理记录
     *
     * @param notificationId 通知ID
     * @return 干预处理记录表信息
     */
    public List<InterventionProcessRecord> selectRecordByNotificationId(Long notificationId);

    /**
     * 根据用户ID查询处理记录
     *
     * @param userId 用户ID
     * @return 干预处理记录表信息
     */
    public List<InterventionProcessRecord> selectRecordByUserId(Long userId);

    /**
     * 新增干预处理记录表
     *
     * @param record 干预处理记录表
     * @return 结果
     */
    public int insertRecord(InterventionProcessRecord record);

    /**
     * 修改干预处理记录表
     *
     * @param record 干预处理记录表
     * @return 结果
     */
    public int updateRecord(InterventionProcessRecord record);

    /**
     * 删除干预处理记录表
     *
     * @param recordId 干预处理记录表ID
     * @return 结果
     */
    public int deleteRecordById(Long recordId);

    /**
     * 批量删除干预处理记录表
     *
     * @param recordIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteRecordByIds(Long[] recordIds);
}
