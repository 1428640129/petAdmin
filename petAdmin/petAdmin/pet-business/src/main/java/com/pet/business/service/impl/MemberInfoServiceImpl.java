package com.pet.business.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.pet.business.mapper.MemberInfoMapper;
import com.pet.business.mapper.PointsRecordMapper;
import com.pet.business.service.IMemberInfoService;
import com.pet.common.utils.SecurityUtils;
import com.pet.system.domain.MemberInfo;
import com.pet.system.domain.PointsRecord;

/**
 * 会员信息Service业务层处理
 * 
 * @author Pet
 */
@Service
public class MemberInfoServiceImpl implements IMemberInfoService
{
    @Autowired
    private MemberInfoMapper memberInfoMapper;

    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    /**
     * 查询会员信息
     * 
     * @param memberId 会员ID
     * @return 会员信息
     */
    @Override
    public MemberInfo selectMemberInfoById(Long memberId)
    {
        return memberInfoMapper.selectMemberInfoById(memberId);
    }

    /**
     * 根据用户ID查询会员信息
     * 
     * @param userId 用户ID
     * @return 会员信息
     */
    @Override
    public MemberInfo selectMemberInfoByUserId(Long userId)
    {
        return memberInfoMapper.selectMemberInfoByUserId(userId);
    }

    /**
     * 查询会员信息列表
     * 
     * @param memberInfo 会员信息
     * @return 会员信息
     */
    @Override
    public List<MemberInfo> selectMemberInfoList(MemberInfo memberInfo)
    {
        return memberInfoMapper.selectMemberInfoList(memberInfo);
    }

    /**
     * 新增会员信息
     * 
     * @param memberInfo 会员信息
     * @return 结果
     */
    @Override
    public int insertMemberInfo(MemberInfo memberInfo)
    {
        if (memberInfo.getMemberLevel() == null || memberInfo.getMemberLevel().isEmpty())
        {
            memberInfo.setMemberLevel("普通");
        }
        if (memberInfo.getPoints() == null)
        {
            memberInfo.setPoints(0);
        }
        if (memberInfo.getTotalConsumption() == null)
        {
            memberInfo.setTotalConsumption(BigDecimal.ZERO);
        }
        if (memberInfo.getStatus() == null || memberInfo.getStatus().isEmpty())
        {
            memberInfo.setStatus("0");
        }
        if (memberInfo.getMemberSince() == null)
        {
            memberInfo.setMemberSince(new Date());
        }
        memberInfo.setCreateBy(SecurityUtils.getUsername());
        return memberInfoMapper.insertMemberInfo(memberInfo);
    }

    /**
     * 修改会员信息
     * 
     * @param memberInfo 会员信息
     * @return 结果
     */
    @Override
    public int updateMemberInfo(MemberInfo memberInfo)
    {
        memberInfo.setUpdateBy(SecurityUtils.getUsername());
        return memberInfoMapper.updateMemberInfo(memberInfo);
    }

    /**
     * 批量删除会员信息
     * 
     * @param memberIds 需要删除的会员ID
     * @return 结果
     */
    @Override
    public int deleteMemberInfoByIds(Long[] memberIds)
    {
        return memberInfoMapper.deleteMemberInfoByIds(memberIds);
    }

    /**
     * 删除会员信息
     * 
     * @param memberId 会员ID
     * @return 结果
     */
    @Override
    public int deleteMemberInfoById(Long memberId)
    {
        return memberInfoMapper.deleteMemberInfoById(memberId);
    }

    /**
     * 增加积分
     * 
     * @param userId 用户ID
     * @param points 积分数量
     * @param pointsType 积分类型
     * @param orderId 订单ID（可选）
     * @param remark 备注
     * @return 结果
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int addPoints(Long userId, Integer points, String pointsType, Long orderId, String remark)
    {
        // 更新会员积分
        MemberInfo memberInfo = memberInfoMapper.selectMemberInfoByUserId(userId);
        if (memberInfo == null)
        {
            // 如果会员信息不存在，先初始化
            initMemberInfo(userId);
            memberInfo = memberInfoMapper.selectMemberInfoByUserId(userId);
        }
        
        memberInfo.setPoints(memberInfo.getPoints() + points);
        memberInfo.setUpdateBy(SecurityUtils.getUsername());
        memberInfoMapper.updateMemberInfo(memberInfo);
        
        // 记录积分变动
        PointsRecord pointsRecord = new PointsRecord();
        pointsRecord.setUserId(userId);
        pointsRecord.setPoints(points);
        pointsRecord.setPointsType(pointsType);
        pointsRecord.setOrderId(orderId);
        pointsRecord.setRemark(remark);
        pointsRecordMapper.insertPointsRecord(pointsRecord);
        
        return 1;
    }

    /**
     * 消费积分
     * 
     * @param userId 用户ID
     * @param points 积分数量
     * @param orderId 订单ID（可选）
     * @param remark 备注
     * @return 结果
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int consumePoints(Long userId, Integer points, Long orderId, String remark)
    {
        MemberInfo memberInfo = memberInfoMapper.selectMemberInfoByUserId(userId);
        if (memberInfo == null || memberInfo.getPoints() < points)
        {
            throw new RuntimeException("积分不足");
        }
        
        memberInfo.setPoints(memberInfo.getPoints() - points);
        memberInfo.setUpdateBy(SecurityUtils.getUsername());
        memberInfoMapper.updateMemberInfo(memberInfo);
        
        // 记录积分变动
        PointsRecord pointsRecord = new PointsRecord();
        pointsRecord.setUserId(userId);
        pointsRecord.setPoints(-points);
        pointsRecord.setPointsType("兑换消费");
        pointsRecord.setOrderId(orderId);
        pointsRecord.setRemark(remark);
        pointsRecordMapper.insertPointsRecord(pointsRecord);
        
        return 1;
    }

    /**
     * 增加累计消费并更新会员等级
     * 
     * @param userId 用户ID
     * @param amount 消费金额
     * @return 结果
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int addConsumption(Long userId, BigDecimal amount)
    {
        MemberInfo memberInfo = memberInfoMapper.selectMemberInfoByUserId(userId);
        if (memberInfo == null)
        {
            // 如果会员信息不存在，先初始化
            initMemberInfo(userId);
            memberInfo = memberInfoMapper.selectMemberInfoByUserId(userId);
        }
        
        // 增加累计消费
        BigDecimal newTotalConsumption = memberInfo.getTotalConsumption().add(amount);
        memberInfo.setTotalConsumption(newTotalConsumption);
        
        // 计算并更新会员等级
        String newLevel = calculateMemberLevel(newTotalConsumption);
        if (!newLevel.equals(memberInfo.getMemberLevel()))
        {
            memberInfo.setMemberLevel(newLevel);
        }
        
        memberInfo.setUpdateBy(SecurityUtils.getUsername());
        return memberInfoMapper.updateMemberInfo(memberInfo);
    }

    /**
     * 初始化会员信息（用户注册时调用）
     * 
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public int initMemberInfo(Long userId)
    {
        MemberInfo memberInfo = new MemberInfo();
        memberInfo.setUserId(userId);
        memberInfo.setMemberLevel("普通");
        memberInfo.setPoints(0);
        memberInfo.setTotalConsumption(BigDecimal.ZERO);
        memberInfo.setMemberSince(new Date());
        memberInfo.setStatus("0");
        memberInfo.setCreateBy("system");
        return insertMemberInfo(memberInfo);
    }

    /**
     * 根据累计消费计算会员等级
     * 
     * @param totalConsumption 累计消费
     * @return 会员等级
     */
    @Override
    public String calculateMemberLevel(BigDecimal totalConsumption)
    {
        if (totalConsumption == null)
        {
            return "普通";
        }
        
        double amount = totalConsumption.doubleValue();
        if (amount >= 5000)
        {
            return "钻石";
        }
        else if (amount >= 2000)
        {
            return "金卡";
        }
        else if (amount >= 500)
        {
            return "银卡";
        }
        else
        {
            return "普通";
        }
    }
}





