package com.pet.business.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pet.business.mapper.PetBathNotificationMapper;
import com.pet.business.mapper.PetBathAppointmentMapper;
import com.pet.business.service.IPetBathNotificationService;
import com.pet.business.service.IPetBathUserService;
import com.pet.common.constants.NotificationTypeConstants;
import com.pet.common.utils.StringUtils;
import com.pet.framework.sms.SpugSmsClient;
import com.pet.system.domain.PetBathNotification;
import com.pet.system.domain.PetBathUser;
import com.pet.system.domain.PetBathAppointment;

/**
 * 通知记录Service业务层处理
 * 
 * @author Pet
 */
@Service
public class PetBathNotificationServiceImpl implements IPetBathNotificationService
{
    private static final Logger log = LoggerFactory.getLogger(PetBathNotificationServiceImpl.class);

    @Autowired
    private PetBathNotificationMapper bathNotificationMapper;

    @Autowired
    private IPetBathUserService bathUserService;

    @Autowired
    private SpugSmsClient spugSmsClient;

    @Autowired
    private PetBathAppointmentMapper bathAppointmentMapper;

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
        // 如果用户没有指定通知类型，默认只显示预约确认和服务完成
        if (notification.getNotificationType() == null || notification.getNotificationType().isEmpty())
        {
            // 查询所有通知，然后过滤出预约确认和服务完成
            List<PetBathNotification> allList = bathNotificationMapper.selectBathNotificationList(notification);
            return allList.stream()
                .filter(n -> "1".equals(n.getNotificationType()) || "3".equals(n.getNotificationType()))
                .collect(java.util.stream.Collectors.toList());
        }
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
     * 先保存通知记录，再发送短信。短信发送失败不影响通知记录的保存
     */
    @Override
    public int sendNotification(Long userId, String notificationType, String title, String content, Long appointmentId, Long orderId)
    {
        // 参数校验
        if (userId == null)
        {
            log.warn("发送通知失败：userId 不能为空");
            return 0;
        }
        if (StringUtils.isEmpty(notificationType))
        {
            log.warn("发送通知失败：notificationType 不能为空, userId={}", userId);
            return 0;
        }
        if (StringUtils.isEmpty(title))
        {
            log.warn("发送通知失败：title 不能为空, userId={}, notificationType={}", userId, notificationType);
            return 0;
        }

        try
        {
            // 1. 先落库记录（使用独立事务，确保即使后续短信发送失败也能保存）
            PetBathNotification notification = new PetBathNotification();
            notification.setUserId(userId);
            notification.setNotificationType(notificationType);
            notification.setTitle(title);
            notification.setContent(content);
            notification.setAppointmentId(appointmentId);
            notification.setOrderId(orderId);
            notification.setIsRead("0");
            
            int rows = insertBathNotification(notification);
            
            if (rows > 0)
            {
                log.info("通知记录保存成功：notificationId={}, userId={}, notificationType={}, title={}", 
                    notification.getNotificationId(), userId, notificationType, title);
            }
            else
            {
                log.error("通知记录保存失败：userId={}, notificationType={}, title={}", userId, notificationType, title);
                return 0;
            }

            // 2. 只对预约确认和服务完成发送短信
            // 短信发送失败不影响通知记录的保存，使用 try-catch 捕获异常
            // 始终使用预约时填写的联系电话发送短信
            try
            {
                // 只处理预约确认和服务完成两种类型
                if (!NotificationTypeConstants.APPOINTMENT_CONFIRMED.equals(notificationType) 
                    && !NotificationTypeConstants.SERVICE_COMPLETED.equals(notificationType))
                {
                    log.debug("通知类型不是预约确认或服务完成，跳过短信发送：notificationType={}", notificationType);
                    return rows;
                }
                
                String phone = null;
                
                // 从预约备注中提取联系电话
                if (appointmentId != null)
                {
                    try
                    {
                        PetBathAppointment appointment = bathAppointmentMapper.selectBathAppointmentById(appointmentId);
                        if (appointment != null && StringUtils.isNotEmpty(appointment.getRemark()))
                        {
                            String remark = appointment.getRemark();
                            // 匹配格式：联系电话：手机号 或 联系电话：手机号
                            java.util.regex.Pattern phonePattern = java.util.regex.Pattern.compile("联系电话[：:]\\s*(1[3-9]\\d{9})");
                            java.util.regex.Matcher phoneMatcher = phonePattern.matcher(remark);
                            if (phoneMatcher.find())
                            {
                                phone = phoneMatcher.group(1);
                                log.debug("从预约备注中提取联系电话：appointmentId={}, phone={}", 
                                    appointmentId, phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        log.warn("查询预约信息失败，无法提取联系电话：appointmentId={}", appointmentId, e);
                    }
                }
                
                if (StringUtils.isNotEmpty(phone))
                {
                    boolean smsResult = false;
                    // 预约确认通知使用专门的推送链接（只需要 to 参数）
                    if (NotificationTypeConstants.APPOINTMENT_CONFIRMED.equals(notificationType))
                    {
                        log.info("开始发送预约确认短信：phone={}, appointmentId={}", 
                            phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"), appointmentId);
                        smsResult = spugSmsClient.sendAppointmentSms(phone);
                        log.info("预约确认短信发送结果：success={}, phone={}", smsResult, 
                            phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
                    }
                    // 服务完成通知使用专门的推送链接（只需要 to 参数）
                    else if (NotificationTypeConstants.SERVICE_COMPLETED.equals(notificationType))
                    {
                        smsResult = spugSmsClient.sendServiceCompletedSms(phone);
                    }
                    
                    if (smsResult)
                    {
                        log.info("短信发送成功：userId={}, phone={}, notificationType={}", 
                            userId, phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"), notificationType);
                    }
                    else
                    {
                        log.warn("短信发送失败：userId={}, phone={}, notificationType={}", 
                            userId, phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"), notificationType);
                    }
                }
                else
                {
                    log.debug("预约备注中未找到联系电话，跳过短信发送：userId={}, appointmentId={}", userId, appointmentId);
                }
            }
            catch (Exception e)
            {
                // 短信发送异常不影响通知记录的保存
                log.error("短信发送异常，但通知记录已保存：userId={}, notificationType={}, title={}", 
                    userId, notificationType, title, e);
            }

            return rows;
        }
        catch (Exception e)
        {
            log.error("发送通知异常：userId={}, notificationType={}, title={}", userId, notificationType, title, e);
            throw e; // 重新抛出异常，触发事务回滚
        }
    }
}










