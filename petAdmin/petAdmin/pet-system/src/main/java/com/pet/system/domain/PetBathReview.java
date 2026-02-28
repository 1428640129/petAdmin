package com.pet.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.pet.common.annotation.Excel;
import com.pet.common.annotation.Excel.ColumnType;
import com.pet.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 评价评论对象 pet_bath_review
 * 
 * @author Pet
 */
public class PetBathReview extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 评价ID */
    @Excel(name = "评价ID", cellType = ColumnType.NUMERIC)
    private Long reviewId;

    /** 订单ID */
    @Excel(name = "订单ID", cellType = ColumnType.NUMERIC)
    private Long orderId;

    /** 预约ID */
    @Excel(name = "预约ID", cellType = ColumnType.NUMERIC)
    private Long appointmentId;

    /** 用户ID */
    @Excel(name = "用户ID", cellType = ColumnType.NUMERIC)
    private Long userId;

    /** 服务ID */
    @Excel(name = "服务ID", cellType = ColumnType.NUMERIC)
    private Long serviceId;

    /** 服务名称（关联查询字段，不存储到数据库） */
    private String serviceName;

    /** 评分（1-5星） */
    @Excel(name = "评分", cellType = ColumnType.NUMERIC)
    private Integer rating;

    /** 评论内容 */
    @Excel(name = "评论内容")
    private String content;

    /** 评价图片 */
    @Excel(name = "评价图片")
    private String images;

    /** 商家回复内容 */
    @Excel(name = "商家回复")
    private String replyContent;

    /** 回复时间 */
    @Excel(name = "回复时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date replyTime;

    /** 状态 */
    @Excel(name = "状态", readConverterExp = "0=正常,1=隐藏")
    private String status;

    public Long getReviewId()
    {
        return reviewId;
    }

    public void setReviewId(Long reviewId)
    {
        this.reviewId = reviewId;
    }

    public Long getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    public Long getAppointmentId()
    {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId)
    {
        this.appointmentId = appointmentId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getServiceId()
    {
        return serviceId;
    }

    public void setServiceId(Long serviceId)
    {
        this.serviceId = serviceId;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }

    public Integer getRating()
    {
        return rating;
    }

    public void setRating(Integer rating)
    {
        this.rating = rating;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getImages()
    {
        return images;
    }

    public void setImages(String images)
    {
        this.images = images;
    }

    public String getReplyContent()
    {
        return replyContent;
    }

    public void setReplyContent(String replyContent)
    {
        this.replyContent = replyContent;
    }

    public Date getReplyTime()
    {
        return replyTime;
    }

    public void setReplyTime(Date replyTime)
    {
        this.replyTime = replyTime;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("reviewId", getReviewId())
            .append("orderId", getOrderId())
            .append("appointmentId", getAppointmentId())
            .append("userId", getUserId())
            .append("serviceId", getServiceId())
            .append("rating", getRating())
            .append("content", getContent())
            .append("images", getImages())
            .append("replyContent", getReplyContent())
            .append("replyTime", getReplyTime())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .toString();
    }
}

