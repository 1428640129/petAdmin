package com.pet.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.pet.common.annotation.Excel;
import com.pet.common.annotation.Excel.ColumnType;
import com.pet.common.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 洗浴服务价格梯度对象 pet_bath_service_price
 * 
 * @author Pet
 */
public class PetBathServicePrice extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 价格ID */
    private Long priceId;

    /** 服务ID */
    @Excel(name = "服务ID", cellType = ColumnType.NUMERIC)
    private Long serviceId;

    /** 宠物类型（0=短毛,1=长毛） */
    @Excel(name = "宠物类型", readConverterExp = "0=短毛,1=长毛")
    private String petType;

    /** 最小体重（kg） */
    @Excel(name = "最小体重", cellType = ColumnType.NUMERIC)
    private BigDecimal weightMin;

    /** 最大体重（kg） */
    @Excel(name = "最大体重", cellType = ColumnType.NUMERIC)
    private BigDecimal weightMax;

    /** 价格（元） */
    @Excel(name = "价格", cellType = ColumnType.NUMERIC)
    private BigDecimal price;

    public Long getPriceId()
    {
        return priceId;
    }

    public void setPriceId(Long priceId)
    {
        this.priceId = priceId;
    }

    public Long getServiceId()
    {
        return serviceId;
    }

    public void setServiceId(Long serviceId)
    {
        this.serviceId = serviceId;
    }

    public String getPetType()
    {
        return petType;
    }

    public void setPetType(String petType)
    {
        this.petType = petType;
    }

    public BigDecimal getWeightMin()
    {
        return weightMin;
    }

    public void setWeightMin(BigDecimal weightMin)
    {
        this.weightMin = weightMin;
    }

    public BigDecimal getWeightMax()
    {
        return weightMax;
    }

    public void setWeightMax(BigDecimal weightMax)
    {
        this.weightMax = weightMax;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("priceId", getPriceId())
            .append("serviceId", getServiceId())
            .append("petType", getPetType())
            .append("weightMin", getWeightMin())
            .append("weightMax", getWeightMax())
            .append("price", getPrice())
            .toString();
    }
}

