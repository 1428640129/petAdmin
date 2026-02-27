package com.pet.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.pet.common.annotation.Excel;
import com.pet.common.annotation.Excel.ColumnType;
import com.pet.common.core.domain.BaseEntity;

/**
 * 轮播图对象 pet_bath_carousel
 * 
 * @author Pet
 */
public class PetBathCarousel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 轮播图ID */
    @Excel(name = "轮播图ID", cellType = ColumnType.NUMERIC)
    private Long carouselId;

    /** 轮播图标题 */
    @Excel(name = "轮播图标题")
    private String title;

    /** 图片URL */
    @Excel(name = "图片URL")
    private String imageUrl;

    /** 跳转链接（可选） */
    @Excel(name = "跳转链接")
    private String linkUrl;

    /** 排序（数字越小越靠前） */
    @Excel(name = "排序", cellType = ColumnType.NUMERIC)
    private Integer sortOrder;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public Long getCarouselId()
    {
        return carouselId;
    }

    public void setCarouselId(Long carouselId)
    {
        this.carouselId = carouselId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public String getLinkUrl()
    {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl)
    {
        this.linkUrl = linkUrl;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
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
            .append("carouselId", getCarouselId())
            .append("title", getTitle())
            .append("imageUrl", getImageUrl())
            .append("linkUrl", getLinkUrl())
            .append("sortOrder", getSortOrder())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}










