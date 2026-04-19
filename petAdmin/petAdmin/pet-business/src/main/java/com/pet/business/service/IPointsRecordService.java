package com.pet.business.service;

import java.util.List;
import com.pet.system.domain.PointsRecord;

/**
 * 积分记录Service接口
 * 
 * @author Pet
 */
public interface IPointsRecordService
{
    /**
     * 查询积分记录
     * 
     * @param recordId 记录ID
     * @return 积分记录
     */
    public PointsRecord selectPointsRecordById(Long recordId);

    /**
     * 查询积分记录列表
     * 
     * @param pointsRecord 积分记录
     * @return 积分记录集合
     */
    public List<PointsRecord> selectPointsRecordList(PointsRecord pointsRecord);

    /**
     * 根据用户ID查询积分记录列表
     * 
     * @param userId 用户ID
     * @return 积分记录集合
     */
    public List<PointsRecord> selectPointsRecordListByUserId(Long userId);

    /**
     * 新增积分记录
     * 
     * @param pointsRecord 积分记录
     * @return 结果
     */
    public int insertPointsRecord(PointsRecord pointsRecord);

    /**
     * 批量删除积分记录
     * 
     * @param recordIds 需要删除的记录ID
     * @return 结果
     */
    public int deletePointsRecordByIds(Long[] recordIds);

    /**
     * 删除积分记录
     * 
     * @param recordId 记录ID
     * @return 结果
     */
    public int deletePointsRecordById(Long recordId);
}





