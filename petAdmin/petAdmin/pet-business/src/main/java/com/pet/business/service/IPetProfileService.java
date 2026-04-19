package com.pet.business.service;

import java.util.List;
import com.pet.system.domain.PetProfile;

/**
 * 宠物档案Service接口
 * 
 * @author Pet
 */
public interface IPetProfileService
{
    /**
     * 查询宠物档案
     * 
     * @param petId 宠物ID
     * @return 宠物档案
     */
    public PetProfile selectPetProfileById(Long petId);

    /**
     * 查询宠物档案列表
     * 
     * @param petProfile 宠物档案
     * @return 宠物档案集合
     */
    public List<PetProfile> selectPetProfileList(PetProfile petProfile);

    /**
     * 根据用户ID查询宠物档案列表
     * 
     * @param userId 用户ID
     * @return 宠物档案集合
     */
    public List<PetProfile> selectPetProfileListByUserId(Long userId);

    /**
     * 查询用户的默认宠物
     * 
     * @param userId 用户ID
     * @return 宠物档案
     */
    public PetProfile selectDefaultPetByUserId(Long userId);

    /**
     * 新增宠物档案
     * 
     * @param petProfile 宠物档案
     * @return 结果
     */
    public int insertPetProfile(PetProfile petProfile);

    /**
     * 修改宠物档案
     * 
     * @param petProfile 宠物档案
     * @return 结果
     */
    public int updatePetProfile(PetProfile petProfile);

    /**
     * 批量删除宠物档案
     * 
     * @param petIds 需要删除的宠物ID
     * @return 结果
     */
    public int deletePetProfileByIds(Long[] petIds);

    /**
     * 删除宠物档案信息
     * 
     * @param petId 宠物ID
     * @return 结果
     */
    public int deletePetProfileById(Long petId);
}





