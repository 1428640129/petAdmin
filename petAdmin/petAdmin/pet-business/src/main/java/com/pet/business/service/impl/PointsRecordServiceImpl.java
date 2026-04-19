package com.pet.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pet.business.mapper.PointsRecordMapper;
import com.pet.business.service.IPointsRecordService;
import com.pet.system.domain.PointsRecord;

/**
 * 积分记录Service业务层处理
 * 
 * @author Pet
 */
@Service
public class PointsRecordServiceImpl implements IPointsRecordService
{
    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    /**
     * 查询积分记录
     * 
     * @param recordId 记录ID
     * @return 积分记录
     */
    @Override
    public PointsRecord selectPointsRecordById(Long recordId)
    {
        return pointsRecordMapper.selectPointsRecordById(recordId);
    }

    /**
     * 查询积分记录列表
     * 
     * @param pointsRecord 积分记录
     * @return 积分记录
     */
    @Override
    public List<PointsRecord> selectPointsRecordList(PointsRecord pointsRecord)
    {
        return pointsRecordMapper.selectPointsRecordList(pointsRecord);
    }

    /**
     * 根据用户ID查询积分记录列表
     * 
     * @param userId 用户ID
     * @return 积分记录集合
     */
    @Override
    public List<PointsRecord> selectPointsRecordListByUserId(Long userId)
    {
        return pointsRecordMapper.selectPointsRecordListByUserId(userId);
    }

    /**
     * 新增积分记录
     * 
     * @param pointsRecord 积分记录
     * @return 结果
     */
    @Override
    public int insertPointsRecord(PointsRecord pointsRecord)
    {
        return pointsRecordMapper.insertPointsRecord(pointsRecord);
    }

    /**
     * 批量删除积分记录
     * 
     * @param recordIds 需要删除的记录ID
     * @return 结果
     */
    @Override
    public int deletePointsRecordByIds(Long[] recordIds)
    {
        return pointsRecordMapper.deletePointsRecordByIds(recordIds);
    }

    /**
     * 删除积分记录
     * 
     * @param recordId 记录ID
     * @return 结果
     */
    @Override
    public int deletePointsRecordById(Long recordId)
    {
        return pointsRecordMapper.deletePointsRecordById(recordId);
    }
}





