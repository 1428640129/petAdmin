package com.pet.business.mapper;

import java.util.List;
import com.pet.system.domain.PetBathService;

/**
 * 洗浴服务 数据层
 * 
 * @author Pet
 */
public interface PetBathServiceMapper
{
    /**
     * 查询洗浴服务信息
     * 
     * @param serviceId 洗浴服务ID
     * @return 洗浴服务信息
     */
    public PetBathService selectBathServiceById(Long serviceId);

    /**
     * 查询洗浴服务列表
     * 
     * @param bathService 洗浴服务信息
     * @return 洗浴服务集合
     */
    public List<PetBathService> selectBathServiceList(PetBathService bathService);

    /**
     * 新增洗浴服务
     * 
     * @param bathService 洗浴服务信息
     * @return 结果
     */
    public int insertBathService(PetBathService bathService);

    /**
     * 修改洗浴服务
     * 
     * @param bathService 洗浴服务信息
     * @return 结果
     */
    public int updateBathService(PetBathService bathService);

    /**
     * 删除洗浴服务
     * 
     * @param serviceId 洗浴服务ID
     * @return 结果
     */
    public int deleteBathServiceById(Long serviceId);

    /**
     * 批量删除洗浴服务
     * 
     * @param serviceIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteBathServiceByIds(Long[] serviceIds);

    /**
     * 查询启用的洗浴服务列表（用于前端展示）
     * 
     * @return 洗浴服务集合
     */
    public List<PetBathService> selectEnabledBathServiceList();
}

