package com.pet.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.pet.common.annotation.Excel;
import com.pet.common.annotation.Excel.ColumnType;
import com.pet.common.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付记录对象 pet_bath_payment
 * 
 * @author Pet
 */
public class PetBathPayment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 支付ID */
    @Excel(name = "支付ID", cellType = ColumnType.NUMERIC)
    private Long paymentId;

    /** 支付单号 */
    @Excel(name = "支付单号")
    private String paymentNo;

    /** 订单ID */
    @Excel(name = "订单ID", cellType = ColumnType.NUMERIC)
    private Long orderId;

    /** 订单号 */
    @Excel(name = "订单号")
    private String orderNo;

    /** 用户ID */
    @Excel(name = "用户ID", cellType = ColumnType.NUMERIC)
    private Long userId;

    /** 支付方式 */
    @Excel(name = "支付方式", readConverterExp = "alipay=支付宝,wechat=微信,balance=余额")
    private String paymentType;

    /** 支付金额 */
    @Excel(name = "支付金额", cellType = ColumnType.NUMERIC)
    private BigDecimal paymentAmount;

    /** 支付状态 */
    @Excel(name = "支付状态", readConverterExp = "pending=待支付,paid=已支付,failed=支付失败,refunded=已退款")
    private String status;

    /** 第三方交易号 */
    @Excel(name = "第三方交易号")
    private String transactionId;

    /** 支付时间 */
    @Excel(name = "支付时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    /** 退款时间 */
    @Excel(name = "退款时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date refundTime;

    /** 退款原因 */
    @Excel(name = "退款原因")
    private String refundReason;

    public Long getPaymentId()
    {
        return paymentId;
    }

    public void setPaymentId(Long paymentId)
    {
        this.paymentId = paymentId;
    }

    public String getPaymentNo()
    {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo)
    {
        this.paymentNo = paymentNo;
    }

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

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getPaymentType()
    {
        return paymentType;
    }

    public void setPaymentType(String paymentType)
    {
        this.paymentType = paymentType;
    }

    public BigDecimal getPaymentAmount()
    {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount)
    {
        this.paymentAmount = paymentAmount;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getTransactionId()
    {
        return transactionId;
    }

    public void setTransactionId(String transactionId)
    {
        this.transactionId = transactionId;
    }

    public Date getPayTime()
    {
        return payTime;
    }

    public void setPayTime(Date payTime)
    {
        this.payTime = payTime;
    }

    public Date getRefundTime()
    {
        return refundTime;
    }

    public void setRefundTime(Date refundTime)
    {
        this.refundTime = refundTime;
    }

    public String getRefundReason()
    {
        return refundReason;
    }

    public void setRefundReason(String refundReason)
    {
        this.refundReason = refundReason;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("paymentId", getPaymentId())
            .append("paymentNo", getPaymentNo())
            .append("orderId", getOrderId())
            .append("orderNo", getOrderNo())
            .append("userId", getUserId())
            .append("paymentType", getPaymentType())
            .append("paymentAmount", getPaymentAmount())
            .append("status", getStatus())
            .append("transactionId", getTransactionId())
            .append("payTime", getPayTime())
            .append("refundTime", getRefundTime())
            .append("refundReason", getRefundReason())
            .append("createTime", getCreateTime())
            .toString();
    }
}

