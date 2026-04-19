package com.pet.business.mapper;

import java.util.List;
import com.pet.system.domain.PetBathNotification;

/**
 * 通知记录 数据层
 * 
 * @author Pet
 */
public interface PetBathNotificationMapper
{
    /**
     * 查询通知记录信息
     * 
     * @param notificationId 通知ID
     * @return 通知记录信息
     */
    public PetBathNotification selectBathNotificationById(Long notificationId);

    /**
     * 查询通知记录列表
     * 
     * @param notification 通知记录信息
     * @return 通知记录集合
     */
    public List<PetBathNotification> selectBathNotificationList(PetBathNotification notification);

    /**
     * 新增通知记录
     * 
     * @param notification 通知记录信息
     * @return 结果
     */
    public int insertBathNotification(PetBathNotification notification);

    /**
     * 修改通知记录
     * 
     * @param notification 通知记录信息
     * @return 结果
     */
    public int updateBathNotification(PetBathNotification notification);

    /**
     * 删除通知记录
     * 
     * @param notificationId 通知ID
     * @return 结果
     */
    public int deleteBathNotificationById(Long notificationId);

    /**
     * 批量删除通知记录
     * 
     * @param notificationIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteBathNotificationByIds(Long[] notificationIds);
}
















