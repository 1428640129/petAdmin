package com.pet.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.pet.common.annotation.Excel;
import com.pet.common.annotation.Excel.ColumnType;
import com.pet.common.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 预约对象 pet_bath_appointment
 * 
 * @author Pet
 */
public class PetBathAppointment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 预约ID */
    @Excel(name = "预约ID", cellType = ColumnType.NUMERIC)
    private Long appointmentId;

    /** 预约单号 */
    @Excel(name = "预约单号")
    private String appointmentNo;

    /** 用户ID */
    @Excel(name = "用户ID", cellType = ColumnType.NUMERIC)
    private Long userId;

    /** 宠物ID */
    @Excel(name = "宠物ID", cellType = ColumnType.NUMERIC)
    private Long petId;

    /** 宠物名称 */
    @Excel(name = "宠物名称")
    private String petName;

    /** 宠物体重（kg） */
    @Excel(name = "宠物体重", cellType = ColumnType.NUMERIC)
    private BigDecimal petWeight;

    /** 宠物类型（0=短毛,1=长毛） */
    @Excel(name = "宠物类型", readConverterExp = "0=短毛,1=长毛")
    private String petType;

    /** 服务ID */
    @Excel(name = "服务ID", cellType = ColumnType.NUMERIC)
    private Long serviceId;

    /** 服务名称 */
    @Excel(name = "服务名称")
    private String serviceName;

    /** 预约时间 */
    @Excel(name = "预约时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date appointmentTime;

    /** 预计价格 */
    @Excel(name = "预计价格", cellType = ColumnType.NUMERIC)
    private BigDecimal expectedPrice;

    /** 实际价格 */
    @Excel(name = "实际价格", cellType = ColumnType.NUMERIC)
    private BigDecimal actualPrice;

    /** 预约状态 */
    @Excel(name = "预约状态", readConverterExp = "pending=待确认,confirmed=已确认,in_service=服务中,completed=已完成,cancelled=已取消")
    private String status;

    /** 取消原因 */
    @Excel(name = "取消原因")
    private String cancelReason;

    /** 取消时间 */
    @Excel(name = "取消时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date cancelTime;

    public Long getAppointmentId()
    {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId)
    {
        this.appointmentId = appointmentId;
    }

    public String getAppointmentNo()
    {
        return appointmentNo;
    }

    public void setAppointmentNo(String appointmentNo)
    {
        this.appointmentNo = appointmentNo;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getPetId()
    {
        return petId;
    }

    public void setPetId(Long petId)
    {
        this.petId = petId;
    }

    public String getPetName()
    {
        return petName;
    }

    public void setPetName(String petName)
    {
        this.petName = petName;
    }

    public BigDecimal getPetWeight()
    {
        return petWeight;
    }

    public void setPetWeight(BigDecimal petWeight)
    {
        this.petWeight = petWeight;
    }

    public String getPetType()
    {
        return petType;
    }

    public void setPetType(String petType)
    {
        this.petType = petType;
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

    public Date getAppointmentTime()
    {
        return appointmentTime;
    }

    public void setAppointmentTime(Date appointmentTime)
    {
        this.appointmentTime = appointmentTime;
    }

    public BigDecimal getExpectedPrice()
    {
        return expectedPrice;
    }

    public void setExpectedPrice(BigDecimal expectedPrice)
    {
        this.expectedPrice = expectedPrice;
    }

    public BigDecimal getActualPrice()
    {
        return actualPrice;
    }

    public void setActualPrice(BigDecimal actualPrice)
    {
        this.actualPrice = actualPrice;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getCancelReason()
    {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason)
    {
        this.cancelReason = cancelReason;
    }

    public Date getCancelTime()
    {
        return cancelTime;
    }

    public void setCancelTime(Date cancelTime)
    {
        this.cancelTime = cancelTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("appointmentId", getAppointmentId())
            .append("appointmentNo", getAppointmentNo())
            .append("userId", getUserId())
            .append("petId", getPetId())
            .append("petName", getPetName())
            .append("petWeight", getPetWeight())
            .append("petType", getPetType())
            .append("serviceId", getServiceId())
            .append("serviceName", getServiceName())
            .append("appointmentTime", getAppointmentTime())
            .append("expectedPrice", getExpectedPrice())
            .append("actualPrice", getActualPrice())
            .append("status", getStatus())
            .append("cancelReason", getCancelReason())
            .append("cancelTime", getCancelTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

