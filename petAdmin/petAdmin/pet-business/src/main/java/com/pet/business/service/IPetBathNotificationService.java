package com.pet.business.service;

import java.util.List;
import com.pet.system.domain.PetBathNotification;

/**
 * 通知记录Service接口
 * 
 * @author Pet
 */
public interface IPetBathNotificationService
{
    /**
     * 查询通知记录
     * 
     * @param notificationId 通知主键
     * @return 通知记录
     */
    public PetBathNotification selectBathNotificationByNotificationId(Long notificationId);

    /**
     * 查询通知记录列表
     * 
     * @param notification 通知记录
     * @return 通知记录集合
     */
    public List<PetBathNotification> selectBathNotificationList(PetBathNotification notification);

    /**
     * 新增通知记录
     * 
     * @param notification 通知记录
     * @return 结果
     */
    public int insertBathNotification(PetBathNotification notification);

    /**
     * 修改通知记录
     * 
     * @param notification 通知记录
     * @return 结果
     */
    public int updateBathNotification(PetBathNotification notification);

    /**
     * 批量删除通知记录
     * 
     * @param notificationIds 需要删除的通知主键集合
     * @return 结果
     */
    public int deleteBathNotificationByNotificationIds(Long[] notificationIds);

    /**
     * 删除通知记录信息
     * 
     * @param notificationId 通知主键
     * @return 结果
     */
    public int deleteBathNotificationByNotificationId(Long notificationId);

    /**
     * 发送通知
     * 
     * @param userId 用户ID
     * @param notificationType 通知类型
     * @param title 标题
     * @param content 内容
     * @param appointmentId 预约ID（可选）
     * @param orderId 订单ID（可选）
     * @return 结果
     */
    public int sendNotification(Long userId, String notificationType, String title, String content, Long appointmentId, Long orderId);

    /**
     * 解析用于发送业务短信的手机号：优先从预约备注中提取，其次使用前台用户表手机号或手机号账号
     *
     * @param userId 前台用户ID（pet_bath_user）
     * @param appointmentId 预约ID
     * @return 11位大陆手机号，无法解析时返回 null
     */
    public String resolveSmsPhone(Long userId, Long appointmentId);
}

