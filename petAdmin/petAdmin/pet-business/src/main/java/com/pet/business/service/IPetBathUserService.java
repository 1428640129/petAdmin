package com.pet.business.service;

import java.util.List;
import com.pet.system.domain.PetBathUser;

/**
 * 前台用户Service接口
 * 
 * @author Pet
 */
public interface IPetBathUserService
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
     * 批量删除前台用户
     * 
     * @param userIds 需要删除的前台用户主键集合
     * @return 结果
     */
    public int deletePetBathUserByIds(Long[] userIds);

    /**
     * 删除前台用户信息
     * 
     * @param userId 前台用户主键
     * @return 结果
     */
    public int deletePetBathUserById(Long userId);
}











