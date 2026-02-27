package com.pet.business.mapper;

import java.util.List;
import com.pet.system.domain.PetBathPayment;

/**
 * 支付记录 数据层
 * 
 * @author Pet
 */
public interface PetBathPaymentMapper
{
    /**
     * 查询支付记录信息
     * 
     * @param paymentId 支付ID
     * @return 支付记录信息
     */
    public PetBathPayment selectBathPaymentById(Long paymentId);

    /**
     * 查询支付记录列表
     * 
     * @param payment 支付记录信息
     * @return 支付记录集合
     */
    public List<PetBathPayment> selectBathPaymentList(PetBathPayment payment);

    /**
     * 新增支付记录
     * 
     * @param payment 支付记录信息
     * @return 结果
     */
    public int insertBathPayment(PetBathPayment payment);

    /**
     * 修改支付记录
     * 
     * @param payment 支付记录信息
     * @return 结果
     */
    public int updateBathPayment(PetBathPayment payment);

    /**
     * 删除支付记录
     * 
     * @param paymentId 支付ID
     * @return 结果
     */
    public int deleteBathPaymentById(Long paymentId);

    /**
     * 批量删除支付记录
     * 
     * @param paymentIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteBathPaymentByIds(Long[] paymentIds);
}

