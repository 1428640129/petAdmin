package com.pet.business.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pet.common.utils.DateUtils;
import com.pet.common.utils.SecurityUtils;
import com.pet.business.mapper.PetBathOrderMapper;
import com.pet.system.domain.PetBathOrder;
import com.pet.business.service.IPetBathOrderService;

/**
 * 订单Service业务层处理
 * 
 * @author Pet
 */
@Service
public class PetBathOrderServiceImpl implements IPetBathOrderService
{
    @Autowired
    private PetBathOrderMapper bathOrderMapper;

    /**
     * 查询订单
     * 
     * @param orderId 订单主键
     * @return 订单
     */
    @Override
    public PetBathOrder selectBathOrderByOrderId(Long orderId)
    {
        return bathOrderMapper.selectBathOrderById(orderId);
    }

    /**
     * 查询订单列表
     * 
     * @param order 订单
     * @return 订单
     */
    @Override
    public List<PetBathOrder> selectBathOrderList(PetBathOrder order)
    {
        return bathOrderMapper.selectBathOrderList(order);
    }

    /**
     * 新增订单
     * 
     * @param order 订单
     * @return 结果
     */
    @Transactional
    @Override
    public int insertBathOrder(PetBathOrder order)
    {
        // 生成订单号
        if (order.getOrderNo() == null || order.getOrderNo().isEmpty())
        {
            order.setOrderNo("ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        order.setStatus("unpaid");
        order.setPaidAmount(java.math.BigDecimal.ZERO);
        order.setRefundAmount(java.math.BigDecimal.ZERO);
        order.setCreateBy(SecurityUtils.getUsername());
        return bathOrderMapper.insertBathOrder(order);
    }

    /**
     * 修改订单
     * 
     * @param order 订单
     * @return 结果
     */
    @Override
    public int updateBathOrder(PetBathOrder order)
    {
        order.setUpdateBy(SecurityUtils.getUsername());
        return bathOrderMapper.updateBathOrder(order);
    }

    /**
     * 批量删除订单
     * 
     * @param orderIds 需要删除的订单主键
     * @return 结果
     */
    @Override
    public int deleteBathOrderByOrderIds(Long[] orderIds)
    {
        return bathOrderMapper.deleteBathOrderByIds(orderIds);
    }

    /**
     * 删除订单信息
     * 
     * @param orderId 订单主键
     * @return 结果
     */
    @Override
    public int deleteBathOrderByOrderId(Long orderId)
    {
        return bathOrderMapper.deleteBathOrderById(orderId);
    }

    /**
     * 支付订单
     * 
     * @param orderId 订单ID
     * @param payAmount 支付金额
     * @return 结果
     */
    @Transactional
    @Override
    public int payOrder(Long orderId, BigDecimal payAmount)
    {
        PetBathOrder order = bathOrderMapper.selectBathOrderById(orderId);
        if (order == null)
        {
            throw new RuntimeException("订单不存在");
        }
        if (!"0".equals(order.getStatus())) // 0=待支付
        {
            throw new RuntimeException("订单状态不正确，无法支付");
        }

        PetBathOrder updateOrder = new PetBathOrder();
        updateOrder.setOrderId(orderId);
        updateOrder.setStatus("1"); // 1=已支付
        updateOrder.setPaidAmount(payAmount);
        updateOrder.setPayTime(DateUtils.getNowDate());
        // 尝试获取用户名，如果获取失败（如小程序调用），使用默认值
        try {
            updateOrder.setUpdateBy(SecurityUtils.getUsername());
        } catch (Exception e) {
            updateOrder.setUpdateBy("miniprogram_user");
        }
        return bathOrderMapper.updateBathOrder(updateOrder);
    }

    /**
     * 根据预约ID查询订单
     * 
     * @param appointmentId 预约ID
     * @return 订单
     */
    @Override
    public PetBathOrder selectBathOrderByAppointmentId(Long appointmentId)
    {
        return bathOrderMapper.selectBathOrderByAppointmentId(appointmentId);
    }
}

