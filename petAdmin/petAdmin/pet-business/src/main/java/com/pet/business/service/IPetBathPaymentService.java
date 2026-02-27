package com.pet.business.service;

import java.util.List;
import com.pet.system.domain.PetBathPayment;

/**
 * 支付记录Service接口
 * 
 * @author Pet
 */
public interface IPetBathPaymentService
{
    /**
     * 查询支付记录
     * 
     * @param paymentId 支付主键
     * @return 支付记录
     */
    public PetBathPayment selectBathPaymentByPaymentId(Long paymentId);

    /**
     * 查询支付记录列表
     * 
     * @param payment 支付记录
     * @return 支付记录集合
     */
    public List<PetBathPayment> selectBathPaymentList(PetBathPayment payment);

    /**
     * 新增支付记录
     * 
     * @param payment 支付记录
     * @return 结果
     */
    public int insertBathPayment(PetBathPayment payment);

    /**
     * 修改支付记录
     * 
     * @param payment 支付记录
     * @return 结果
     */
    public int updateBathPayment(PetBathPayment payment);

    /**
     * 批量删除支付记录
     * 
     * @param paymentIds 需要删除的支付主键集合
     * @return 结果
     */
    public int deleteBathPaymentByPaymentIds(Long[] paymentIds);

    /**
     * 删除支付记录信息
     * 
     * @param paymentId 支付主键
     * @return 结果
     */
    public int deleteBathPaymentByPaymentId(Long paymentId);

    /**
     * 处理退款
     * 
     * @param paymentId 支付ID
     * @param refundReason 退款原因
     * @return 结果
     */
    public int processRefund(Long paymentId, String refundReason);
}

