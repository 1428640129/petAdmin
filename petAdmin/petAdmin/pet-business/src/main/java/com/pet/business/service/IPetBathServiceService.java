package com.pet.business.service;

import java.util.List;
import com.pet.system.domain.PetBathService;
import java.math.BigDecimal;

/**
 * 洗浴服务Service接口
 * 
 * @author Pet
 */
public interface IPetBathServiceService
{
    /**
     * 查询洗浴服务
     * 
     * @param serviceId 洗浴服务主键
     * @return 洗浴服务
     */
    public PetBathService selectBathServiceByServiceId(Long serviceId);

    /**
     * 查询洗浴服务列表
     * 
     * @param bathService 洗浴服务
     * @return 洗浴服务集合
     */
    public List<PetBathService> selectBathServiceList(PetBathService bathService);

    /**
     * 查询启用的洗浴服务列表（用于前端展示）
     * 
     * @return 洗浴服务集合
     */
    public List<PetBathService> selectEnabledBathServiceList();

    /**
     * 根据体重计算服务价格
     * 
     * @param serviceId 服务ID
     * @param weight 宠物体重
     * @return 价格
     */
    public BigDecimal calculatePrice(Long serviceId, BigDecimal weight);

    /**
     * 根据体重和宠物类型计算服务价格
     * 
     * @param serviceId 服务ID
     * @param petType 宠物类型（0=短毛,1=长毛）
     * @param weight 宠物体重
     * @return 价格
     */
    public BigDecimal calculatePrice(Long serviceId, String petType, BigDecimal weight);

    /**
     * 新增洗浴服务
     * 
     * @param bathService 洗浴服务
     * @return 结果
     */
    public int insertBathService(PetBathService bathService);

    /**
     * 修改洗浴服务
     * 
     * @param bathService 洗浴服务
     * @return 结果
     */
    public int updateBathService(PetBathService bathService);

    /**
     * 批量删除洗浴服务
     * 
     * @param serviceIds 需要删除的洗浴服务主键集合
     * @return 结果
     */
    public int deleteBathServiceByServiceIds(Long[] serviceIds);

    /**
     * 删除洗浴服务信息
     * 
     * @param serviceId 洗浴服务主键
     * @return 结果
     */
    public int deleteBathServiceByServiceId(Long serviceId);
}

