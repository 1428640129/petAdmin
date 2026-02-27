package com.pet.business.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pet.common.constants.PetTypeConstants;
import com.pet.common.constants.PetTypeConstants;
import com.pet.common.utils.SecurityUtils;
import com.pet.common.utils.StringUtils;
import com.pet.business.mapper.PetBathServiceMapper;
import com.pet.business.mapper.PetBathServicePriceMapper;
import com.pet.system.domain.PetBathService;
import com.pet.system.domain.PetBathServicePrice;
import com.pet.business.service.IPetBathServiceService;

/**
 * 洗浴服务Service业务层处理
 * 
 * @author Pet
 */
@Service
public class PetBathServiceServiceImpl implements IPetBathServiceService
{
    @Autowired
    private PetBathServiceMapper bathServiceMapper;

    @Autowired
    private PetBathServicePriceMapper bathServicePriceMapper;

    /**
     * 查询洗浴服务
     * 
     * @param serviceId 洗浴服务主键
     * @return 洗浴服务
     */
    @Override
    public PetBathService selectBathServiceByServiceId(Long serviceId)
    {
        PetBathService service = bathServiceMapper.selectBathServiceById(serviceId);
        if (service != null)
        {
            // 加载价格梯度
            List<PetBathServicePrice> prices = bathServicePriceMapper.selectBathServicePriceList(serviceId);
            service.setPrices(prices);
        }
        return service;
    }

    /**
     * 查询洗浴服务列表
     * 
     * @param bathService 洗浴服务
     * @return 洗浴服务
     */
    @Override
    public List<PetBathService> selectBathServiceList(PetBathService bathService)
    {
        return bathServiceMapper.selectBathServiceList(bathService);
    }

    /**
     * 查询启用的洗浴服务列表（用于前端展示）
     * 
     * @return 洗浴服务集合
     */
    @Override
    public List<PetBathService> selectEnabledBathServiceList()
    {
        // 查询启用的服务基础信息
        List<PetBathService> list = bathServiceMapper.selectEnabledBathServiceList();
        // 为前端展示加载价格梯度（用于计算最低价等）
        if (list != null && !list.isEmpty())
        {
            for (PetBathService service : list)
            {
                if (service != null && service.getServiceId() != null)
                {
                    List<PetBathServicePrice> prices = bathServicePriceMapper.selectBathServicePriceList(service.getServiceId());
                    service.setPrices(prices);
                }
            }
        }
        return list;
    }

    /**
     * 根据体重计算服务价格（默认短毛）
     * 
     * @param serviceId 服务ID
     * @param weight 宠物体重
     * @return 价格
     */
    @Override
    public BigDecimal calculatePrice(Long serviceId, BigDecimal weight)
    {
        return calculatePrice(serviceId, PetTypeConstants.SHORT_HAIR, weight);
    }

    /**
     * 根据体重和宠物类型计算服务价格
     * 
     * @param serviceId 服务ID
     * @param petType 宠物类型（0=短毛,1=长毛）
     * @param weight 宠物体重
     * @return 价格
     */
    @Override
    public BigDecimal calculatePrice(Long serviceId, String petType, BigDecimal weight)
    {
        // 如果petType为空，默认使用短毛
        if (petType == null || petType.isEmpty())
        {
            petType = PetTypeConstants.SHORT_HAIR;
        }
        PetBathServicePrice price = bathServicePriceMapper.selectPriceByWeight(serviceId, petType, weight);
        if (price != null)
        {
            return price.getPrice();
        }
        return BigDecimal.ZERO;
    }

    /**
     * 新增洗浴服务
     * 
     * @param bathService 洗浴服务
     * @return 结果
     */
    @Transactional
    @Override
    public int insertBathService(PetBathService bathService)
    {
        int rows = bathServiceMapper.insertBathService(bathService);
        // 插入价格梯度
        if (bathService.getPrices() != null && !bathService.getPrices().isEmpty())
        {
            for (PetBathServicePrice price : bathService.getPrices())
            {
                price.setServiceId(bathService.getServiceId());
                // 确保petType有值，如果没有则默认为短毛
                if (price.getPetType() == null || price.getPetType().isEmpty())
                {
                    price.setPetType(PetTypeConstants.SHORT_HAIR);
                }
                price.setCreateBy(SecurityUtils.getUsername());
                bathServicePriceMapper.insertBathServicePrice(price);
            }
        }
        return rows;
    }

    /**
     * 修改洗浴服务
     * 
     * @param bathService 洗浴服务
     * @return 结果
     */
    @Transactional
    @Override
    public int updateBathService(PetBathService bathService)
    {
        int rows = bathServiceMapper.updateBathService(bathService);
        // 更新价格梯度：
        // - 如果前端未传递价格信息（prices == null 或 空列表），则不动原有价格数据，避免只改基础信息时把价格清空
        // - 如果前端传递了非空价格列表，则认为是完整覆盖：先删后插
        if (bathService.getPrices() != null && !bathService.getPrices().isEmpty())
        {
            bathServicePriceMapper.deleteBathServicePriceByServiceId(bathService.getServiceId());
            for (PetBathServicePrice price : bathService.getPrices())
            {
                price.setServiceId(bathService.getServiceId());
                // 确保petType有值，如果没有则默认为短毛
                if (price.getPetType() == null || price.getPetType().isEmpty())
                {
                    price.setPetType(PetTypeConstants.SHORT_HAIR);
                }
                price.setCreateBy(SecurityUtils.getUsername());
                bathServicePriceMapper.insertBathServicePrice(price);
            }
        }
        return rows;
    }

    /**
     * 批量删除洗浴服务
     * 
     * @param serviceIds 需要删除的洗浴服务主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteBathServiceByServiceIds(Long[] serviceIds)
    {
        // 先删除价格梯度
        for (Long serviceId : serviceIds)
        {
            bathServicePriceMapper.deleteBathServicePriceByServiceId(serviceId);
        }
        return bathServiceMapper.deleteBathServiceByIds(serviceIds);
    }

    /**
     * 删除洗浴服务信息
     * 
     * @param serviceId 洗浴服务主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteBathServiceByServiceId(Long serviceId)
    {
        // 先删除价格梯度
        bathServicePriceMapper.deleteBathServicePriceByServiceId(serviceId);
        return bathServiceMapper.deleteBathServiceById(serviceId);
    }
}

