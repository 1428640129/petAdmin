package com.pet.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.pet.common.annotation.Excel;
import com.pet.common.annotation.Excel.ColumnType;
import com.pet.common.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单对象 pet_bath_order
 * 
 * @author Pet
 */
public class PetBathOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订单ID */
    @Excel(name = "订单ID", cellType = ColumnType.NUMERIC)
    private Long orderId;

    /** 订单号 */
    @Excel(name = "订单号")
    private String orderNo;

    /** 预约ID */
    @Excel(name = "预约ID", cellType = ColumnType.NUMERIC)
    private Long appointmentId;

    /** 用户ID */
    @Excel(name = "用户ID", cellType = ColumnType.NUMERIC)
    private Long userId;

    /** 服务ID */
    @Excel(name = "服务ID", cellType = ColumnType.NUMERIC)
    private Long serviceId;

    /** 服务名称 */
    @Excel(name = "服务名称")
    private String serviceName;

    /** 订单总金额 */
    @Excel(name = "订单总金额", cellType = ColumnType.NUMERIC)
    private BigDecimal totalAmount;

    /** 已支付金额 */
    @Excel(name = "已支付金额", cellType = ColumnType.NUMERIC)
    private BigDecimal paidAmount;

    /** 退款金额 */
    @Excel(name = "退款金额", cellType = ColumnType.NUMERIC)
    private BigDecimal refundAmount;

    /** 订单状态 */
    @Excel(name = "订单状态", readConverterExp = "unpaid=待支付,paid=已支付,in_service=服务中,completed=已完成,refunded=已退款,cancelled=已取消")
    private String status;

    /** 支付时间 */
    @Excel(name = "支付时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    /** 完成时间 */
    @Excel(name = "完成时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date completeTime;

    /** 取消时间 */
    @Excel(name = "取消时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date cancelTime;

    /** 取消原因 */
    @Excel(name = "取消原因")
    private String cancelReason;

    public Long getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    public String getOrderNo()
    {
        return orderNo;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
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

    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPaidAmount()
    {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount)
    {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getRefundAmount()
    {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount)
    {
        this.refundAmount = refundAmount;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getPayTime()
    {
        return payTime;
    }

    public void setPayTime(Date payTime)
    {
        this.payTime = payTime;
    }

    public Date getCompleteTime()
    {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime)
    {
        this.completeTime = completeTime;
    }

    public Date getCancelTime()
    {
        return cancelTime;
    }

    public void setCancelTime(Date cancelTime)
    {
        this.cancelTime = cancelTime;
    }

    public String getCancelReason()
    {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason)
    {
        this.cancelReason = cancelReason;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("orderId", getOrderId())
            .append("orderNo", getOrderNo())
            .append("appointmentId", getAppointmentId())
            .append("userId", getUserId())
            .append("serviceId", getServiceId())
            .append("serviceName", getServiceName())
            .append("totalAmount", getTotalAmount())
            .append("paidAmount", getPaidAmount())
            .append("refundAmount", getRefundAmount())
            .append("status", getStatus())
            .append("payTime", getPayTime())
            .append("completeTime", getCompleteTime())
            .append("cancelTime", getCancelTime())
            .append("cancelReason", getCancelReason())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

