package com.pet.business.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.pet.system.domain.PetBathServicePrice;

/**
 * 洗浴服务价格梯度 数据层
 * 
 * @author Pet
 */
public interface PetBathServicePriceMapper
{
    /**
     * 查询洗浴服务价格梯度列表
     * 
     * @param serviceId 服务ID
     * @return 价格梯度集合
     */
    public List<PetBathServicePrice> selectBathServicePriceList(Long serviceId);

    /**
     * 根据体重和宠物类型计算价格
     * 
     * @param serviceId 服务ID
     * @param petType 宠物类型（0=短毛,1=长毛）
     * @param weight 宠物体重
     * @return 价格信息
     */
    public PetBathServicePrice selectPriceByWeight(@Param("serviceId") Long serviceId, @Param("petType") String petType, @Param("weight") java.math.BigDecimal weight);

    /**
     * 新增洗浴服务价格梯度
     * 
     * @param price 价格梯度信息
     * @return 结果
     */
    public int insertBathServicePrice(PetBathServicePrice price);

    /**
     * 批量新增价格梯度
     * 
     * @param prices 价格梯度列表
     * @return 结果
     */
    public int insertBathServicePriceBatch(List<PetBathServicePrice> prices);

    /**
     * 修改洗浴服务价格梯度
     * 
     * @param price 价格梯度信息
     * @return 结果
     */
    public int updateBathServicePrice(PetBathServicePrice price);

    /**
     * 删除洗浴服务价格梯度
     * 
     * @param priceId 价格ID
     * @return 结果
     */
    public int deleteBathServicePriceById(Long priceId);

    /**
     * 根据服务ID删除价格梯度
     * 
     * @param serviceId 服务ID
     * @return 结果
     */
    public int deleteBathServicePriceByServiceId(Long serviceId);

    /**
     * 批量删除价格梯度
     * 
     * @param priceIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteBathServicePriceByIds(Long[] priceIds);
}

