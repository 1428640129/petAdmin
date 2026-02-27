package com.pet.business.mapper;

import java.util.List;
import com.pet.system.domain.PetBathOrder;

/**
 * 订单 数据层
 * 
 * @author Pet
 */
public interface PetBathOrderMapper
{
    /**
     * 查询订单信息
     * 
     * @param orderId 订单ID
     * @return 订单信息
     */
    public PetBathOrder selectBathOrderById(Long orderId);

    /**
     * 根据预约ID查询订单信息
     * 
     * @param appointmentId 预约ID
     * @return 订单信息
     */
    public PetBathOrder selectBathOrderByAppointmentId(Long appointmentId);

    /**
     * 查询订单列表
     * 
     * @param order 订单信息
     * @return 订单集合
     */
    public List<PetBathOrder> selectBathOrderList(PetBathOrder order);

    /**
     * 新增订单
     * 
     * @param order 订单信息
     * @return 结果
     */
    public int insertBathOrder(PetBathOrder order);

    /**
     * 修改订单
     * 
     * @param order 订单信息
     * @return 结果
     */
    public int updateBathOrder(PetBathOrder order);

    /**
     * 删除订单
     * 
     * @param orderId 订单ID
     * @return 结果
     */
    public int deleteBathOrderById(Long orderId);

    /**
     * 批量删除订单
     * 
     * @param orderIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteBathOrderByIds(Long[] orderIds);
}

