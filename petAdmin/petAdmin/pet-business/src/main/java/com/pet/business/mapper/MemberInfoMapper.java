package com.pet.business.mapper;

import java.util.List;
import com.pet.system.domain.MemberInfo;

/**
 * 会员信息 数据层
 * 
 * @author Pet
 */
public interface MemberInfoMapper
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
     * 删除会员信息
     * 
     * @param memberId 会员ID
     * @return 结果
     */
    public int deleteMemberInfoById(Long memberId);

    /**
     * 批量删除会员信息
     * 
     * @param memberIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteMemberInfoByIds(Long[] memberIds);
}





