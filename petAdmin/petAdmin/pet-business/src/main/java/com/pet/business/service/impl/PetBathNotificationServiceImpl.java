package com.pet.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pet.common.utils.DateUtils;
import com.pet.business.mapper.PetBathNotificationMapper;
import com.pet.system.domain.PetBathNotification;
import com.pet.business.service.IPetBathNotificationService;

/**
 * 通知记录Service业务层处理
 * 
 * @author Pet
 */
@Service
public class PetBathNotificationServiceImpl implements IPetBathNotificationService
{
    @Autowired
    private PetBathNotificationMapper bathNotificationMapper;

    /**
     * 查询通知记录
     * 
     * @param notificationId 通知主键
     * @return 通知记录
     */
    @Override
    public PetBathNotification selectBathNotificationByNotificationId(Long notificationId)
    {
        return bathNotificationMapper.selectBathNotificationById(notificationId);
    }

    /**
     * 查询通知记录列表
     * 
     * @param notification 通知记录
     * @return 通知记录
     */
    @Override
    public List<PetBathNotification> selectBathNotificationList(PetBathNotification notification)
    {
        return bathNotificationMapper.selectBathNotificationList(notification);
    }

    /**
     * 新增通知记录
     * 
     * @param notification 通知记录
     * @return 结果
     */
    @Override
    public int insertBathNotification(PetBathNotification notification)
    {
        if (notification.getIsRead() == null || notification.getIsRead().isEmpty())
        {
            notification.setIsRead("0");
        }
        return bathNotificationMapper.insertBathNotification(notification);
    }

    /**
     * 修改通知记录
     * 
     * @param notification 通知记录
     * @return 结果
     */
    @Override
    public int updateBathNotification(PetBathNotification notification)
    {
        return bathNotificationMapper.updateBathNotification(notification);
    }

    /**
     * 批量删除通知记录
     * 
     * @param notificationIds 需要删除的通知主键
     * @return 结果
     */
    @Override
    public int deleteBathNotificationByNotificationIds(Long[] notificationIds)
    {
        return bathNotificationMapper.deleteBathNotificationByIds(notificationIds);
    }

    /**
     * 删除通知记录信息
     * 
     * @param notificationId 通知主键
     * @return 结果
     */
    @Override
    public int deleteBathNotificationByNotificationId(Long notificationId)
    {
        return bathNotificationMapper.deleteBathNotificationById(notificationId);
    }

    /**
     * 发送通知
     */
    @Override
    public int sendNotification(Long userId, String notificationType, String title, String content, Long appointmentId, Long orderId)
    {
        PetBathNotification notification = new PetBathNotification();
        notification.setUserId(userId);
        notification.setNotificationType(notificationType);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setAppointmentId(appointmentId);
        notification.setOrderId(orderId);
        notification.setIsRead("0");
        return insertBathNotification(notification);
    }
}










