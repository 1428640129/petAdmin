package com.pet.business.service;

import java.math.BigDecimal;
import java.util.List;
import com.pet.system.domain.MemberInfo;

/**
 * 会员信息Service接口
 * 
 * @author Pet
 */
public interface IMemberInfoService
{
    /**
     * 查询会员信息
     * 
     * @param memberId 会员ID
     * @return 会员信息
     */
    public MemberInfo selectMemberInfoById(Long memberId);

    /**
     * 根据用户ID查询会员信息
     * 
     * @param userId 用户ID
     * @return 会员信息
     */
    public MemberInfo selectMemberInfoByUserId(Long userId);

    /**
     * 查询会员信息列表
     * 
     * @param memberInfo 会员信息
     * @return 会员信息集合
     */
    public List<MemberInfo> selectMemberInfoList(MemberInfo memberInfo);

    /**
     * 新增会员信息
     * 
     * @param memberInfo 会员信息
     * @return 结果
     */
    public int insertMemberInfo(MemberInfo memberInfo);

    /**
     * 修改会员信息
     * 
     * @param memberInfo 会员信息
     * @return 结果
     */
    public int updateMemberInfo(MemberInfo memberInfo);

    /**
     * 批量删除会员信息
     * 
     * @param memberIds 需要删除的会员ID
     * @return 结果
     */
    public int deleteMemberInfoByIds(Long[] memberIds);

    /**
     * 删除会员信息
     * 
     * @param memberId 会员ID
     * @return 结果
     */
    public int deleteMemberInfoById(Long memberId);

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
    public int addPoints(Long userId, Integer points, String pointsType, Long orderId, String remark);

    /**
     * 消费积分
     * 
     * @param userId 用户ID
     * @param points 积分数量
     * @param orderId 订单ID（可选）
     * @param remark 备注
     * @return 结果
     */
    public int consumePoints(Long userId, Integer points, Long orderId, String remark);

    /**
     * 增加累计消费并更新会员等级
     * 
     * @param userId 用户ID
     * @param amount 消费金额
     * @return 结果
     */
    public int addConsumption(Long userId, BigDecimal amount);

    /**
     * 初始化会员信息（用户注册时调用）
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public int initMemberInfo(Long userId);

    /**
     * 根据累计消费计算会员等级
     * 
     * @param totalConsumption 累计消费
     * @return 会员等级
     */
    public String calculateMemberLevel(BigDecimal totalConsumption);
}





