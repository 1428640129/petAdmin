package com.pet.business.service;

import java.util.List;
import com.pet.system.domain.PetBathOrder;

/**
 * 订单Service接口
 * 
 * @author Pet
 */
public interface IPetBathOrderService
{
    /**
     * 查询订单
     * 
     * @param orderId 订单主键
     * @return 订单
     */
    public PetBathOrder selectBathOrderByOrderId(Long orderId);

    /**
     * 查询订单列表
     * 
     * @param order 订单
     * @return 订单集合
     */
    public List<PetBathOrder> selectBathOrderList(PetBathOrder order);

    /**
     * 新增订单
     * 
     * @param order 订单
     * @return 结果
     */
    public int insertBathOrder(PetBathOrder order);

    /**
     * 修改订单
     * 
     * @param order 订单
     * @return 结果
     */
    public int updateBathOrder(PetBathOrder order);

    /**
     * 批量删除订单
     * 
     * @param orderIds 需要删除的订单主键集合
     * @return 结果
     */
    public int deleteBathOrderByOrderIds(Long[] orderIds);

    /**
     * 删除订单信息
     * 
     * @param orderId 订单主键
     * @return 结果
     */
    public int deleteBathOrderByOrderId(Long orderId);

    /**
     * 支付订单
     * 
     * @param orderId 订单ID
     * @param payAmount 支付金额
     * @return 结果
     */
    public int payOrder(Long orderId, java.math.BigDecimal payAmount);

    /**
     * 根据预约ID查询订单
     * 
     * @param appointmentId 预约ID
     * @return 订单
     */
    public PetBathOrder selectBathOrderByAppointmentId(Long appointmentId);
}

