package com.pet.business.service;

import java.util.List;
import com.pet.system.domain.PetBathAppointment;

/**
 * 预约Service接口
 * 
 * @author Pet
 */
public interface IPetBathAppointmentService
{
    /**
     * 查询预约
     * 
     * @param appointmentId 预约主键
     * @return 预约
     */
    public PetBathAppointment selectBathAppointmentByAppointmentId(Long appointmentId);

    /**
     * 查询预约列表
     * 
     * @param appointment 预约
     * @return 预约集合
     */
    public List<PetBathAppointment> selectBathAppointmentList(PetBathAppointment appointment);

    /**
     * 新增预约
     * 
     * @param appointment 预约
     * @return 结果
     */
    public int insertBathAppointment(PetBathAppointment appointment);

    /**
     * 修改预约
     * 
     * @param appointment 预约
     * @return 结果
     */
    public int updateBathAppointment(PetBathAppointment appointment);

    /**
     * 批量删除预约
     * 
     * @param appointmentIds 需要删除的预约主键集合
     * @return 结果
     */
    public int deleteBathAppointmentByAppointmentIds(Long[] appointmentIds);

    /**
     * 删除预约信息
     * 
     * @param appointmentId 预约主键
     * @return 结果
     */
    public int deleteBathAppointmentByAppointmentId(Long appointmentId);

    /**
     * 确认预约
     * 
     * @param appointmentId 预约ID
     * @return 结果
     */
    public int confirmAppointment(Long appointmentId);

    /**
     * 取消预约
     * 
     * @param appointmentId 预约ID
     * @param cancelReason 取消原因
     * @return 结果
     */
    public int cancelAppointment(Long appointmentId, String cancelReason);

    /**
     * 开始服务
     * 
     * @param appointmentId 预约ID
     * @return 结果
     */
    public int startService(Long appointmentId);

    /**
     * 完成服务
     * 
     * @param appointmentId 预约ID
     * @return 结果
     */
    public int completeService(Long appointmentId);

    /**
     * 完成服务（带实际价格）
     * 
     * @param appointmentId 预约ID
     * @param actualPrice 实际价格
     * @return 结果
     */
    public int completeService(Long appointmentId, java.math.BigDecimal actualPrice);

    /**
     * 更新宠物体重和类型并重新计算价格
     * 
     * @param appointmentId 预约ID
     * @param petWeight 新的宠物体重
     * @param petType 新的宠物类型（0=短毛,1=长毛）
     * @return 结果和新价格
     */
    public java.util.Map<String, Object> updatePetInfoAndRecalculatePrice(Long appointmentId, java.math.BigDecimal petWeight, String petType);
}

