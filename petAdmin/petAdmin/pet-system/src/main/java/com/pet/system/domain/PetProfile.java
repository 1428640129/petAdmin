package com.pet.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.pet.common.annotation.Excel;
import com.pet.common.annotation.Excel.ColumnType;
import com.pet.common.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 宠物档案对象 pet_profile
 * 
 * @author Pet
 */
public class PetProfile extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 宠物ID */
    @Excel(name = "宠物ID", cellType = ColumnType.NUMERIC)
    private Long petId;

    /** 用户ID */
    @Excel(name = "用户ID", cellType = ColumnType.NUMERIC)
    private Long userId;

    /** 宠物名称 */
    @Excel(name = "宠物名称")
    private String petName;

    /** 宠物品种 */
    @Excel(name = "宠物品种")
    private String petBreed;

    /** 年龄（月） */
    @Excel(name = "年龄", cellType = ColumnType.NUMERIC)
    private Integer petAge;

    /** 性别（0公 1母 2未知） */
    @Excel(name = "性别", readConverterExp = "0=公,1=母,2=未知")
    private String petSex;

    /** 体重（kg） */
    @Excel(name = "体重", cellType = ColumnType.NUMERIC)
    private BigDecimal petWeight;

    /** 毛发类型（0短毛 1长毛） */
    @Excel(name = "毛发类型", readConverterExp = "0=短毛,1=长毛")
    private String hairType;

    /** 宠物照片 */
    private String petPhoto;

    /** 健康状况 */
    @Excel(name = "健康状况")
    private String healthStatus;

    /** 特殊需求 */
    @Excel(name = "特殊需求")
    private String specialNeeds;

    /** 过敏史 */
    @Excel(name = "过敏史")
    private String allergyHistory;

    /** 是否默认宠物（0否 1是） */
    @Excel(name = "是否默认", readConverterExp = "0=否,1=是")
    private String isDefault;

    public Long getPetId()
    {
        return petId;
    }

    public void setPetId(Long petId)
    {
        this.petId = petId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getPetName()
    {
        return petName;
    }

    public void setPetName(String petName)
    {
        this.petName = petName;
    }

    public String getPetBreed()
    {
        return petBreed;
    }

    public void setPetBreed(String petBreed)
    {
        this.petBreed = petBreed;
    }

    public Integer getPetAge()
    {
        return petAge;
    }

    public void setPetAge(Integer petAge)
    {
        this.petAge = petAge;
    }

    public String getPetSex()
    {
        return petSex;
    }

    public void setPetSex(String petSex)
    {
        this.petSex = petSex;
    }

    public BigDecimal getPetWeight()
    {
        return petWeight;
    }

    public void setPetWeight(BigDecimal petWeight)
    {
        this.petWeight = petWeight;
    }

    public String getHairType()
    {
        return hairType;
    }

    public void setHairType(String hairType)
    {
        this.hairType = hairType;
    }

    public String getPetPhoto()
    {
        return petPhoto;
    }

    public void setPetPhoto(String petPhoto)
    {
        this.petPhoto = petPhoto;
    }

    public String getHealthStatus()
    {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus)
    {
        this.healthStatus = healthStatus;
    }

    public String getSpecialNeeds()
    {
        return specialNeeds;
    }

    public void setSpecialNeeds(String specialNeeds)
    {
        this.specialNeeds = specialNeeds;
    }

    public String getAllergyHistory()
    {
        return allergyHistory;
    }

    public void setAllergyHistory(String allergyHistory)
    {
        this.allergyHistory = allergyHistory;
    }

    public String getIsDefault()
    {
        return isDefault;
    }

    public void setIsDefault(String isDefault)
    {
        this.isDefault = isDefault;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("petId", getPetId())
            .append("userId", getUserId())
            .append("petName", getPetName())
            .append("petBreed", getPetBreed())
            .append("petAge", getPetAge())
            .append("petSex", getPetSex())
            .append("petWeight", getPetWeight())
            .append("hairType", getHairType())
            .append("petPhoto", getPetPhoto())
            .append("healthStatus", getHealthStatus())
            .append("specialNeeds", getSpecialNeeds())
            .append("allergyHistory", getAllergyHistory())
            .append("isDefault", getIsDefault())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}





