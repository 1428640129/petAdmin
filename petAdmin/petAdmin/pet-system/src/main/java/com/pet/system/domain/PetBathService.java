package com.pet.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.pet.common.annotation.Excel;
import com.pet.common.annotation.Excel.ColumnType;
import com.pet.common.core.domain.BaseEntity;

import java.util.List;

/**
 * 洗浴服务对象 pet_bath_service
 * 
 * @author Pet
 */
public class PetBathService extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 服务ID */
    @Excel(name = "服务ID", cellType = ColumnType.NUMERIC)
    private Long serviceId;

    /** 服务名称 */
    @Excel(name = "服务名称")
    private String serviceName;

    /** 服务描述 */
    @Excel(name = "服务描述")
    private String serviceDesc;

    /** 服务类型（0=基础洗浴,1=深度护理,2=豪华套餐） */
    @Excel(name = "服务类型", readConverterExp = "0=基础洗浴,1=深度护理,2=豪华套餐")
    private String serviceType;

    /** 服务图片（JSON格式存储多张图片URL） */
    private String serviceImages;

    /** 服务时长（分钟） */
    @Excel(name = "服务时长", cellType = ColumnType.NUMERIC)
    private Integer duration;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 排序 */
    @Excel(name = "排序", cellType = ColumnType.NUMERIC)
    private Integer sortOrder;

    /** 价格梯度列表 */
    private List<PetBathServicePrice> prices;

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

    public String getServiceDesc()
    {
        return serviceDesc;
    }

    public void setServiceDesc(String serviceDesc)
    {
        this.serviceDesc = serviceDesc;
    }

    public String getServiceType()
    {
        return serviceType;
    }

    public void setServiceType(String serviceType)
    {
        this.serviceType = serviceType;
    }

    public String getServiceImages()
    {
        return serviceImages;
    }

    public void setServiceImages(String serviceImages)
    {
        this.serviceImages = serviceImages;
    }

    public Integer getDuration()
    {
        return duration;
    }

    public void setDuration(Integer duration)
    {
        this.duration = duration;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public List<PetBathServicePrice> getPrices()
    {
        return prices;
    }

    public void setPrices(List<PetBathServicePrice> prices)
    {
        this.prices = prices;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("serviceId", getServiceId())
            .append("serviceName", getServiceName())
            .append("serviceDesc", getServiceDesc())
            .append("serviceType", getServiceType())
            .append("serviceImages", getServiceImages())
            .append("duration", getDuration())
            .append("status", getStatus())
            .append("sortOrder", getSortOrder())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

