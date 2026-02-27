package com.pet.business.mapper;

import java.util.List;
import com.pet.system.domain.PetBathAppointment;

/**
 * 预约 数据层
 * 
 * @author Pet
 */
public interface PetBathAppointmentMapper
{
    /**
     * 查询预约信息
     * 
     * @param appointmentId 预约ID
     * @return 预约信息
     */
    public PetBathAppointment selectBathAppointmentById(Long appointmentId);

    /**
     * 查询预约列表
     * 
     * @param appointment 预约信息
     * @return 预约集合
     */
    public List<PetBathAppointment> selectBathAppointmentList(PetBathAppointment appointment);

    /**
     * 新增预约
     * 
     * @param appointment 预约信息
     * @return 结果
     */
    public int insertBathAppointment(PetBathAppointment appointment);

    /**
     * 修改预约
     * 
     * @param appointment 预约信息
     * @return 结果
     */
    public int updateBathAppointment(PetBathAppointment appointment);

    /**
     * 删除预约
     * 
     * @param appointmentId 预约ID
     * @return 结果
     */
    public int deleteBathAppointmentById(Long appointmentId);

    /**
     * 批量删除预约
     * 
     * @param appointmentIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteBathAppointmentByIds(Long[] appointmentIds);
}

