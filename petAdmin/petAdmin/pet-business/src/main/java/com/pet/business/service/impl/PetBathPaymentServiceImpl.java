package com.pet.business.service.impl;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pet.common.utils.DateUtils;
import com.pet.common.utils.SecurityUtils;
import com.pet.business.mapper.PetBathPaymentMapper;
import com.pet.system.domain.PetBathPayment;
import com.pet.business.service.IPetBathPaymentService;

/**
 * 支付记录Service业务层处理
 * 
 * @author Pet
 */
@Service
public class PetBathPaymentServiceImpl implements IPetBathPaymentService
{
    @Autowired
    private PetBathPaymentMapper bathPaymentMapper;

    /**
     * 查询支付记录
     * 
     * @param paymentId 支付主键
     * @return 支付记录
     */
    @Override
    public PetBathPayment selectBathPaymentByPaymentId(Long paymentId)
    {
        return bathPaymentMapper.selectBathPaymentById(paymentId);
    }

    /**
     * 查询支付记录列表
     * 
     * @param payment 支付记录
     * @return 支付记录
     */
    @Override
    public List<PetBathPayment> selectBathPaymentList(PetBathPayment payment)
    {
        return bathPaymentMapper.selectBathPaymentList(payment);
    }

    /**
     * 新增支付记录
     * 
     * @param payment 支付记录
     * @return 结果
     */
    @Transactional
    @Override
    public int insertBathPayment(PetBathPayment payment)
    {
        // 生成支付单号
        if (payment.getPaymentNo() == null || payment.getPaymentNo().isEmpty())
        {
            payment.setPaymentNo("PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        if (payment.getStatus() == null || payment.getStatus().isEmpty())
        {
            payment.setStatus("pending");
        }
        return bathPaymentMapper.insertBathPayment(payment);
    }

    /**
     * 修改支付记录
     * 
     * @param payment 支付记录
     * @return 结果
     */
    @Override
    public int updateBathPayment(PetBathPayment payment)
    {
        return bathPaymentMapper.updateBathPayment(payment);
    }

    /**
     * 批量删除支付记录
     * 
     * @param paymentIds 需要删除的支付主键
     * @return 结果
     */
    @Override
    public int deleteBathPaymentByPaymentIds(Long[] paymentIds)
    {
        return bathPaymentMapper.deleteBathPaymentByIds(paymentIds);
    }

    /**
     * 删除支付记录信息
     * 
     * @param paymentId 支付主键
     * @return 结果
     */
    @Override
    public int deleteBathPaymentByPaymentId(Long paymentId)
    {
        return bathPaymentMapper.deleteBathPaymentById(paymentId);
    }

    /**
     * 处理退款
     */
    @Override
    public int processRefund(Long paymentId, String refundReason)
    {
        PetBathPayment payment = new PetBathPayment();
        payment.setPaymentId(paymentId);
        payment.setStatus("refunded");
        payment.setRefundReason(refundReason);
        payment.setRefundTime(DateUtils.getNowDate());
        return bathPaymentMapper.updateBathPayment(payment);
    }
}










