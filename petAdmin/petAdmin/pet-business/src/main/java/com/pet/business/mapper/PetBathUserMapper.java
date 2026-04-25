package com.pet.business.mapper;

import java.util.List;
import com.pet.system.domain.PetBathUser;

/**
 * 前台用户 数据层
 * 
 * @author Pet
 */
public interface PetBathUserMapper
{
    /**
     * 查询前台用户
     * 
     * @param userId 前台用户主键
     * @return 前台用户
     */
    public PetBathUser selectPetBathUserById(Long userId);

    /**
     * 根据用户名查询前台用户
     * 
     * @param userName 用户名
     * @return 前台用户
     */
    public PetBathUser selectPetBathUserByUserName(String userName);

    /**
     * 根据手机号查询前台用户（手机号唯一）
     * 
     * @param phone 手机号
     * @return 前台用户
     */
    public PetBathUser selectPetBathUserByPhone(String phone);

    /**
     * 查询前台用户列表
     * 
     * @param user 前台用户
     * @return 前台用户集合
     */
    public List<PetBathUser> selectPetBathUserList(PetBathUser user);

    /**
     * 新增前台用户
     * 
     * @param user 前台用户
     * @return 结果
     */
    public int insertPetBathUser(PetBathUser user);

    /**
     * 修改前台用户
     * 
     * @param user 前台用户
     * @return 结果
     */
    public int updatePetBathUser(PetBathUser user);

    /**
     * 删除前台用户
     * 
     * @param userId 前台用户主键
     * @return 结果
     */
    public int deletePetBathUserById(Long userId);

    /**
     * 批量删除前台用户
     * 
     * @param userIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePetBathUserByIds(Long[] userIds);
}











