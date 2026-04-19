package com.pet.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.pet.common.annotation.Excel;
import com.pet.common.annotation.Excel.ColumnType;
import com.pet.common.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员信息对象 member_info
 * 
 * @author Pet
 */
public class MemberInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 会员ID */
    @Excel(name = "会员ID", cellType = ColumnType.NUMERIC)
    private Long memberId;

    /** 用户ID */
    @Excel(name = "用户ID", cellType = ColumnType.NUMERIC)
    private Long userId;

    /** 会员等级（普通、银卡、金卡、钻石） */
    @Excel(name = "会员等级")
    private String memberLevel;

    /** 积分 */
    @Excel(name = "积分", cellType = ColumnType.NUMERIC)
    private Integer points;

    /** 累计消费（元） */
    @Excel(name = "累计消费", cellType = ColumnType.NUMERIC)
    private BigDecimal totalConsumption;

    /** 成为会员时间 */
    @Excel(name = "成为会员时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date memberSince;

    /** 会员到期时间 */
    @Excel(name = "会员到期时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    /** 状态（0正常 1过期） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=过期")
    private String status;

    public Long getMemberId()
    {
        return memberId;
    }

    public void setMemberId(Long memberId)
    {
        this.memberId = memberId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getMemberLevel()
    {
        return memberLevel;
    }

    public void setMemberLevel(String memberLevel)
    {
        this.memberLevel = memberLevel;
    }

    public Integer getPoints()
    {
        return points;
    }

    public void setPoints(Integer points)
    {
        this.points = points;
    }

    public BigDecimal getTotalConsumption()
    {
        return totalConsumption;
    }

    public void setTotalConsumption(BigDecimal totalConsumption)
    {
        this.totalConsumption = totalConsumption;
    }

    public Date getMemberSince()
    {
        return memberSince;
    }

    public void setMemberSince(Date memberSince)
    {
        this.memberSince = memberSince;
    }

    public Date getExpireTime()
    {
        return expireTime;
    }

    public void setExpireTime(Date expireTime)
    {
        this.expireTime = expireTime;
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
            .append("memberId", getMemberId())
            .append("userId", getUserId())
            .append("memberLevel", getMemberLevel())
            .append("points", getPoints())
            .append("totalConsumption", getTotalConsumption())
            .append("memberSince", getMemberSince())
            .append("expireTime", getExpireTime())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}





