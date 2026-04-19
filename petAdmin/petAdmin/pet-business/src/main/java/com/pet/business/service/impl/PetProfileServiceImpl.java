package com.pet.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pet.business.mapper.PetProfileMapper;
import com.pet.business.service.IPetProfileService;
import com.pet.common.utils.SecurityUtils;
import com.pet.system.domain.PetProfile;

/**
 * 宠物档案Service业务层处理
 * 
 * @author Pet
 */
@Service
public class PetProfileServiceImpl implements IPetProfileService
{
    @Autowired
    private PetProfileMapper petProfileMapper;

    /**
     * 查询宠物档案
     * 
     * @param petId 宠物ID
     * @return 宠物档案
     */
    @Override
    public PetProfile selectPetProfileById(Long petId)
    {
        return petProfileMapper.selectPetProfileById(petId);
    }

    /**
     * 查询宠物档案列表
     * 
     * @param petProfile 宠物档案
     * @return 宠物档案
     */
    @Override
    public List<PetProfile> selectPetProfileList(PetProfile petProfile)
    {
        return petProfileMapper.selectPetProfileList(petProfile);
    }

    /**
     * 根据用户ID查询宠物档案列表
     * 
     * @param userId 用户ID
     * @return 宠物档案集合
     */
    @Override
    public List<PetProfile> selectPetProfileListByUserId(Long userId)
    {
        return petProfileMapper.selectPetProfileListByUserId(userId);
    }

    /**
     * 查询用户的默认宠物
     * 
     * @param userId 用户ID
     * @return 宠物档案
     */
    @Override
    public PetProfile selectDefaultPetByUserId(Long userId)
    {
        return petProfileMapper.selectDefaultPetByUserId(userId);
    }

    /**
     * 新增宠物档案
     * 
     * @param petProfile 宠物档案
     * @return 结果
     */
    @Override
    @Transactional
    public int insertPetProfile(PetProfile petProfile)
    {
        // 如果设置为默认宠物，先取消其他宠物的默认状态
        if ("1".equals(petProfile.getIsDefault()) && petProfile.getUserId() != null)
        {
            petProfileMapper.cancelDefaultPetByUserId(petProfile.getUserId());
        }
        
        // 如果用户没有宠物，自动设置为默认
        List<PetProfile> existingPets = petProfileMapper.selectPetProfileListByUserId(petProfile.getUserId());
        if (existingPets == null || existingPets.isEmpty())
        {
            petProfile.setIsDefault("1");
        }
        else if (petProfile.getIsDefault() == null || petProfile.getIsDefault().isEmpty())
        {
            petProfile.setIsDefault("0");
        }
        
        petProfile.setCreateBy(SecurityUtils.getUsername());
        return petProfileMapper.insertPetProfile(petProfile);
    }

    /**
     * 修改宠物档案
     * 
     * @param petProfile 宠物档案
     * @return 结果
     */
    @Override
    @Transactional
    public int updatePetProfile(PetProfile petProfile)
    {
        // 如果设置为默认宠物，先取消其他宠物的默认状态
        if ("1".equals(petProfile.getIsDefault()) && petProfile.getUserId() != null)
        {
            petProfileMapper.cancelDefaultPetByUserId(petProfile.getUserId());
        }
        
        petProfile.setUpdateBy(SecurityUtils.getUsername());
        return petProfileMapper.updatePetProfile(petProfile);
    }

    /**
     * 批量删除宠物档案
     * 
     * @param petIds 需要删除的宠物ID
     * @return 结果
     */
    @Override
    public int deletePetProfileByIds(Long[] petIds)
    {
        return petProfileMapper.deletePetProfileByIds(petIds);
    }

    /**
     * 删除宠物档案信息
     * 
     * @param petId 宠物ID
     * @return 结果
     */
    @Override
    public int deletePetProfileById(Long petId)
    {
        return petProfileMapper.deletePetProfileById(petId);
    }
}





