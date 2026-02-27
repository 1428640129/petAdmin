package com.pet.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pet.business.mapper.PetBathUserMapper;
import com.pet.business.service.IPetBathUserService;
import com.pet.common.utils.SecurityUtils;
import com.pet.system.domain.PetBathUser;

/**
 * 前台用户Service业务层处理
 * 
 * @author Pet
 */
@Service
public class PetBathUserServiceImpl implements IPetBathUserService
{
    @Autowired
    private PetBathUserMapper userMapper;

    /**
     * 查询前台用户
     * 
     * @param userId 前台用户主键
     * @return 前台用户
     */
    @Override
    public PetBathUser selectPetBathUserById(Long userId)
    {
        return userMapper.selectPetBathUserById(userId);
    }

    /**
     * 根据用户名查询前台用户
     * 
     * @param userName 用户名
     * @return 前台用户
     */
    @Override
    public PetBathUser selectPetBathUserByUserName(String userName)
    {
        return userMapper.selectPetBathUserByUserName(userName);
    }

    /**
     * 查询前台用户列表
     * 
     * @param user 前台用户
     * @return 前台用户
     */
    @Override
    public List<PetBathUser> selectPetBathUserList(PetBathUser user)
    {
        return userMapper.selectPetBathUserList(user);
    }

    /**
     * 新增前台用户
     * 
     * @param user 前台用户
     * @return 结果
     */
    @Override
    public int insertPetBathUser(PetBathUser user)
    {
        if (user.getPassword() != null && !user.getPassword().isEmpty())
        {
            user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        }
        if (user.getUserType() == null || user.getUserType().isEmpty())
        {
            user.setUserType("0"); // 默认顾客
        }
        if (user.getStatus() == null || user.getStatus().isEmpty())
        {
            user.setStatus("0"); // 默认正常
        }
        user.setCreateBy("app");
        return userMapper.insertPetBathUser(user);
    }

    /**
     * 修改前台用户
     * 
     * @param user 前台用户
     * @return 结果
     */
    @Override
    public int updatePetBathUser(PetBathUser user)
    {
        if (user.getPassword() != null && !user.getPassword().isEmpty())
        {
            user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        }
        else
        {
            // 不更新密码
            user.setPassword(null);
        }
        return userMapper.updatePetBathUser(user);
    }

    /**
     * 批量删除前台用户
     * 
     * @param userIds 需要删除的前台用户主键
     * @return 结果
     */
    @Override
    public int deletePetBathUserByIds(Long[] userIds)
    {
        return userMapper.deletePetBathUserByIds(userIds);
    }

    /**
     * 删除前台用户信息
     * 
     * @param userId 前台用户主键
     * @return 结果
     */
    @Override
    public int deletePetBathUserById(Long userId)
    {
        return userMapper.deletePetBathUserById(userId);
    }
}

