package com.pet.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.pet.common.annotation.Excel;
import com.pet.common.annotation.Excel.ColumnType;
import com.pet.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 通知记录对象 pet_bath_notification
 * 
 * @author Pet
 */
public class PetBathNotification extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 通知ID */
    @Excel(name = "通知ID", cellType = ColumnType.NUMERIC)
    private Long notificationId;

    /** 用户ID */
    @Excel(name = "用户ID", cellType = ColumnType.NUMERIC)
    private Long userId;

    /** 预约ID */
    @Excel(name = "预约ID", cellType = ColumnType.NUMERIC)
    private Long appointmentId;

    /** 订单ID */
    @Excel(name = "订单ID", cellType = ColumnType.NUMERIC)
    private Long orderId;

    /** 通知类型 */
    @Excel(name = "通知类型")
    private String notificationType;

    /** 通知标题 */
    @Excel(name = "通知标题")
    private String title;

    /** 通知内容 */
    @Excel(name = "通知内容")
    private String content;

    /** 是否已读 */
    @Excel(name = "是否已读", readConverterExp = "0=未读,1=已读")
    private String isRead;

    /** 阅读时间 */
    @Excel(name = "阅读时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;

    public Long getNotificationId()
    {
        return notificationId;
    }

    public void setNotificationId(Long notificationId)
    {
        this.notificationId = notificationId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getAppointmentId()
    {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId)
    {
        this.appointmentId = appointmentId;
    }

    public Long getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    public String getNotificationType()
    {
        return notificationType;
    }

    public void setNotificationType(String notificationType)
    {
        this.notificationType = notificationType;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getIsRead()
    {
        return isRead;
    }

    public void setIsRead(String isRead)
    {
        this.isRead = isRead;
    }

    public Date getReadTime()
    {
        return readTime;
    }

    public void setReadTime(Date readTime)
    {
        this.readTime = readTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("notificationId", getNotificationId())
            .append("userId", getUserId())
            .append("appointmentId", getAppointmentId())
            .append("orderId", getOrderId())
            .append("notificationType", getNotificationType())
            .append("title", getTitle())
            .append("content", getContent())
            .append("isRead", getIsRead())
            .append("readTime", getReadTime())
            .append("createTime", getCreateTime())
            .toString();
    }
}

