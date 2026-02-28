package com.pet.business.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pet.common.constants.NotificationTypeConstants;
import com.pet.common.constants.PetTypeConstants;
import com.pet.common.utils.DateUtils;
import com.pet.common.utils.SecurityUtils;
import com.pet.business.mapper.PetBathAppointmentMapper;
import com.pet.business.mapper.PetBathOrderMapper;
import com.pet.system.domain.PetBathAppointment;
import com.pet.system.domain.PetBathOrder;
import com.pet.business.service.IPetBathAppointmentService;
import com.pet.business.service.IPetBathNotificationService;
import com.pet.business.service.IPetBathServiceService;
import com.pet.business.service.IPetBathUserService;
import com.pet.system.domain.PetBathUser;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 预约Service业务层处理
 * 
 * @author Pet
 */
@Service
public class PetBathAppointmentServiceImpl implements IPetBathAppointmentService
{
    @Autowired
    private PetBathAppointmentMapper bathAppointmentMapper;

    @Autowired
    private PetBathOrderMapper bathOrderMapper;

    @Autowired
    private IPetBathServiceService bathServiceService;

    @Autowired
    private IPetBathNotificationService bathNotificationService;

    @Autowired
    private IPetBathUserService bathUserService;

    private static final Logger log = LoggerFactory.getLogger(PetBathAppointmentServiceImpl.class);
    
    private static final String ORDER_COMPLETED_SMS_URL = "https://push.spug.cc/sms/I767-Eg3T9CwDB7i-xrSsw";
    
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 查询预约
     * 
     * @param appointmentId 预约主键
     * @return 预约
     */
    @Override
    public PetBathAppointment selectBathAppointmentByAppointmentId(Long appointmentId)
    {
        return bathAppointmentMapper.selectBathAppointmentById(appointmentId);
    }

    /**
     * 查询预约列表
     * 
     * @param appointment 预约
     * @return 预约
     */
    @Override
    public List<PetBathAppointment> selectBathAppointmentList(PetBathAppointment appointment)
    {
        return bathAppointmentMapper.selectBathAppointmentList(appointment);
    }

    /**
     * 新增预约
     * 
     * @param appointment 预约
     * @return 结果
     */
    @Transactional
    @Override
    public int insertBathAppointment(PetBathAppointment appointment)
    {
        // 生成预约单号
        if (appointment.getAppointmentNo() == null || appointment.getAppointmentNo().isEmpty())
        {
            appointment.setAppointmentNo("APT" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        // 设置默认宠物类型
        if (appointment.getPetType() == null || appointment.getPetType().isEmpty())
        {
            appointment.setPetType(PetTypeConstants.SHORT_HAIR);
        }
        // 如果服务名称为空，根据服务ID查询并设置
        if ((appointment.getServiceName() == null || appointment.getServiceName().isEmpty()) 
            && appointment.getServiceId() != null)
        {
            com.pet.system.domain.PetBathService service = bathServiceService.selectBathServiceByServiceId(appointment.getServiceId());
            if (service != null && service.getServiceName() != null)
            {
                appointment.setServiceName(service.getServiceName());
            }
        }
        // 如果预计价格为空，根据体重和类型计算价格
        if (appointment.getExpectedPrice() == null && appointment.getServiceId() != null 
            && appointment.getPetWeight() != null)
        {
            BigDecimal calculatedPrice = bathServiceService.calculatePrice(
                appointment.getServiceId(), 
                appointment.getPetType(), 
                appointment.getPetWeight()
            );
            appointment.setExpectedPrice(calculatedPrice);
        }
        appointment.setStatus("0"); // 0=待确认
        // 如果 createBy 未设置，尝试从 SecurityContext 获取，如果用户未登录则使用默认值
        if (appointment.getCreateBy() == null || appointment.getCreateBy().isEmpty()) {
            try {
                appointment.setCreateBy(SecurityUtils.getUsername());
            } catch (Exception e) {
                // 用户未登录，使用默认值
                appointment.setCreateBy("miniprogram_user");
            }
        }
        return bathAppointmentMapper.insertBathAppointment(appointment);
    }

    /**
     * 修改预约
     * 
     * @param appointment 预约
     * @return 结果
     */
    @Override
    public int updateBathAppointment(PetBathAppointment appointment)
    {
        appointment.setUpdateBy(SecurityUtils.getUsername());
        return bathAppointmentMapper.updateBathAppointment(appointment);
    }

    /**
     * 批量删除预约
     * 
     * @param appointmentIds 需要删除的预约主键
     * @return 结果
     */
    @Override
    public int deleteBathAppointmentByAppointmentIds(Long[] appointmentIds)
    {
        return bathAppointmentMapper.deleteBathAppointmentByIds(appointmentIds);
    }

    /**
     * 删除预约信息
     * 
     * @param appointmentId 预约主键
     * @return 结果
     */
    @Override
    public int deleteBathAppointmentByAppointmentId(Long appointmentId)
    {
        return bathAppointmentMapper.deleteBathAppointmentById(appointmentId);
    }

    /**
     * 确认预约（同时创建订单）
     */
    @Transactional
    @Override
    public int confirmAppointment(Long appointmentId)
    {
        // 1. 查询预约信息
        PetBathAppointment appointment = bathAppointmentMapper.selectBathAppointmentById(appointmentId);
        if (appointment == null)
        {
            throw new RuntimeException("预约不存在");
        }
        if (!"0".equals(appointment.getStatus())) // 0=待确认
        {
            throw new RuntimeException("预约状态不正确，无法确认");
        }

        // 2. 检查是否已存在订单
        PetBathOrder existOrder = bathOrderMapper.selectBathOrderByAppointmentId(appointmentId);
        if (existOrder != null)
        {
            throw new RuntimeException("该预约已存在订单");
        }

        // 3. 更新预约状态为已确认
        PetBathAppointment updateAppointment = new PetBathAppointment();
        updateAppointment.setAppointmentId(appointmentId);
        updateAppointment.setStatus("1"); // 1=已确认
        updateAppointment.setUpdateBy(SecurityUtils.getUsername());
        int result = bathAppointmentMapper.updateBathAppointment(updateAppointment);

        // 4. 重新计算价格（确保价格准确）
        BigDecimal finalPrice = appointment.getExpectedPrice();
        if (appointment.getPetWeight() != null && appointment.getPetType() != null)
        {
            BigDecimal recalculatedPrice = bathServiceService.calculatePrice(
                appointment.getServiceId(), 
                appointment.getPetType(), 
                appointment.getPetWeight()
            );
            if (recalculatedPrice != null && recalculatedPrice.compareTo(BigDecimal.ZERO) > 0)
            {
                finalPrice = recalculatedPrice;
                // 更新预约的预计价格
                PetBathAppointment updatePrice = new PetBathAppointment();
                updatePrice.setAppointmentId(appointmentId);
                updatePrice.setExpectedPrice(finalPrice);
                updatePrice.setUpdateBy(SecurityUtils.getUsername());
                bathAppointmentMapper.updateBathAppointment(updatePrice);
            }
        }

        // 5. 创建订单
        PetBathOrder order = new PetBathOrder();
        order.setAppointmentId(appointmentId);
        order.setUserId(appointment.getUserId());
        order.setServiceId(appointment.getServiceId());
        order.setServiceName(appointment.getServiceName());
        // 订单总金额等于重新计算的预计价格
        order.setTotalAmount(finalPrice != null ? finalPrice : BigDecimal.ZERO);
        order.setStatus("0"); // 0=待支付
        order.setPaidAmount(BigDecimal.ZERO);
        order.setRefundAmount(BigDecimal.ZERO);
        order.setCreateBy(SecurityUtils.getUsername());
        // 生成订单号
        order.setOrderNo("ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        bathOrderMapper.insertBathOrder(order);

        // 6. 发送“预约确认”通知（包含短信）
        if (appointment.getUserId() != null)
        {
            String title = "预约已确认";
            StringBuilder content = new StringBuilder();
            content.append("您预约的")
                   .append(appointment.getServiceName() != null ? appointment.getServiceName() : "宠物洗澡服务")
                   .append("已确认，预约编号：")
                   .append(appointment.getAppointmentNo());
            bathNotificationService.sendNotification(
                appointment.getUserId(),
                NotificationTypeConstants.APPOINTMENT_CONFIRMED,
                title,
                content.toString(),
                appointmentId,
                order.getOrderId()
            );
        }

        return result;
    }

    /**
     * 取消预约
     */
    @Override
    public int cancelAppointment(Long appointmentId, String cancelReason)
    {
        PetBathAppointment appointment = new PetBathAppointment();
        appointment.setAppointmentId(appointmentId);
        appointment.setStatus("4"); // 4=已取消
        appointment.setCancelReason(cancelReason);
        appointment.setCancelTime(DateUtils.getNowDate());
        appointment.setUpdateBy(SecurityUtils.getUsername());
        return bathAppointmentMapper.updateBathAppointment(appointment);
    }

    /**
     * 开始服务（同步更新订单状态）
     */
    @Transactional
    @Override
    public int startService(Long appointmentId)
    {
        // 1. 查询预约信息
        PetBathAppointment appointment = bathAppointmentMapper.selectBathAppointmentById(appointmentId);
        if (appointment == null)
        {
            throw new RuntimeException("预约不存在");
        }
        if (!"1".equals(appointment.getStatus())) // 1=已确认
        {
            throw new RuntimeException("预约状态不正确，无法开始服务");
        }

        // 2. 查询订单，检查是否已支付
        PetBathOrder order = bathOrderMapper.selectBathOrderByAppointmentId(appointmentId);
        if (order == null)
        {
            throw new RuntimeException("订单不存在");
        }
        if (!"1".equals(order.getStatus())) // 1=已支付
        {
            throw new RuntimeException("订单未支付，无法开始服务");
        }

        // 3. 更新预约状态
        PetBathAppointment updateAppointment = new PetBathAppointment();
        updateAppointment.setAppointmentId(appointmentId);
        updateAppointment.setStatus("2"); // 2=服务中
        updateAppointment.setUpdateBy(SecurityUtils.getUsername());
        int result = bathAppointmentMapper.updateBathAppointment(updateAppointment);

        // 4. 同步更新订单状态
        PetBathOrder updateOrder = new PetBathOrder();
        updateOrder.setOrderId(order.getOrderId());
        updateOrder.setStatus("2"); // 2=服务中
        updateOrder.setUpdateBy(SecurityUtils.getUsername());
        bathOrderMapper.updateBathOrder(updateOrder);

        return result;
    }

    /**
     * 完成服务（同步更新订单状态，处理价格差异）
     * 
     * @param appointmentId 预约ID
     * @return 结果
     */
    @Transactional
    @Override
    public int completeService(Long appointmentId)
    {
        return completeServiceWithPrice(appointmentId, null);
    }

    /**
     * 完成服务（带实际价格）
     * 
     * @param appointmentId 预约ID
     * @param actualPrice 实际价格（可选，如果为null则使用预计价格）
     * @return 结果
     */
    @Transactional
    @Override
    public int completeService(Long appointmentId, BigDecimal actualPrice)
    {
        return completeServiceWithPrice(appointmentId, actualPrice);
    }

    /**
     * 完成服务（同步更新订单状态，处理价格差异）
     * 
     * @param appointmentId 预约ID
     * @param actualPrice 实际价格（可选，如果为null则使用预计价格）
     * @return 结果
     */
    private int completeServiceWithPrice(Long appointmentId, BigDecimal actualPrice)
    {
        // 1. 查询预约信息
        PetBathAppointment appointment = bathAppointmentMapper.selectBathAppointmentById(appointmentId);
        if (appointment == null)
        {
            throw new RuntimeException("预约不存在");
        }
        if (!"2".equals(appointment.getStatus())) // 2=服务中
        {
            throw new RuntimeException("预约状态不正确，无法完成服务");
        }

        // 2. 查询订单
        PetBathOrder order = bathOrderMapper.selectBathOrderByAppointmentId(appointmentId);
        if (order == null)
        {
            throw new RuntimeException("订单不存在");
        }

        // 3. 确定实际价格（如果未传入，使用预计价格）
        BigDecimal finalPrice = actualPrice != null ? actualPrice : appointment.getExpectedPrice();
        if (finalPrice == null)
        {
            finalPrice = BigDecimal.ZERO;
        }

        // 4. 更新预约状态和实际价格
        PetBathAppointment updateAppointment = new PetBathAppointment();
        updateAppointment.setAppointmentId(appointmentId);
        updateAppointment.setStatus("3"); // 3=已完成
        updateAppointment.setActualPrice(finalPrice);
        updateAppointment.setUpdateBy(SecurityUtils.getUsername());
        int result = bathAppointmentMapper.updateBathAppointment(updateAppointment);

        // 5. 更新订单状态和总金额
        PetBathOrder updateOrder = new PetBathOrder();
        updateOrder.setOrderId(order.getOrderId());
        updateOrder.setStatus("3"); // 3=已完成
        updateOrder.setTotalAmount(finalPrice);
        updateOrder.setCompleteTime(DateUtils.getNowDate());
        updateOrder.setUpdateBy(SecurityUtils.getUsername());
        bathOrderMapper.updateBathOrder(updateOrder);

        // 6. 处理价格差异
        BigDecimal paidAmount = order.getPaidAmount() != null ? order.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal difference = finalPrice.subtract(paidAmount);
        
        if (difference.compareTo(BigDecimal.ZERO) > 0)
        {
            // 实际价格 > 已支付金额，需要补差价
            // 这里可以触发补差价流程或记录日志
            // TODO: 实现补差价逻辑
        }
        else if (difference.compareTo(BigDecimal.ZERO) < 0)
        {
            // 实际价格 < 已支付金额，需要退款
            // 这里可以触发退款流程
            // TODO: 实现退款逻辑
        }

        // 7. 发送"服务完成"通知（包含短信）
        if (appointment.getUserId() != null)
        {
            String title = "服务已完成";
            StringBuilder content = new StringBuilder();
            content.append("您预约的")
                   .append(appointment.getServiceName() != null ? appointment.getServiceName() : "宠物洗澡服务")
                   .append("已完成，本次费用：")
                   .append(finalPrice.toPlainString())
                   .append(" 元。");
            bathNotificationService.sendNotification(
                appointment.getUserId(),
                NotificationTypeConstants.SERVICE_COMPLETED,
                title,
                content.toString(),
                appointmentId,
                order.getOrderId()
            );
            
            // 8. 发送订单完成短信通知（使用预约时填写的联系电话）
            try
            {
                String phone = null;
                // 从预约备注中提取联系电话
                if (appointment.getRemark() != null && !appointment.getRemark().trim().isEmpty())
                {
                    String remark = appointment.getRemark();
                    // 匹配格式：联系电话：手机号 或 联系电话：手机号
                    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("联系电话[：:]\\s*(1[3-9]\\d{9})");
                    java.util.regex.Matcher matcher = pattern.matcher(remark);
                    if (matcher.find())
                    {
                        phone = matcher.group(1);
                    }
                }
                
                if (phone != null && !phone.trim().isEmpty())
                {
                    sendOrderCompletedSms(phone);
                }
                else
                {
                    log.debug("预约备注中未找到联系电话，跳过短信发送：appointmentId={}", appointmentId);
                }
            }
            catch (Exception e)
            {
                log.error("发送订单完成短信失败，appointmentId={}, userId={}", appointmentId, appointment.getUserId(), e);
            }
        }

        return result;
    }

    /**
     * 更新宠物体重和类型并重新计算价格
     * 
     * @param appointmentId 预约ID
     * @param petWeight 新的宠物体重
     * @param petType 新的宠物类型（0=短毛,1=长毛）
     * @return 结果和新价格
     */
    @Transactional
    @Override
    public Map<String, Object> updatePetInfoAndRecalculatePrice(Long appointmentId, BigDecimal petWeight, String petType)
    {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 查询预约信息
        PetBathAppointment appointment = bathAppointmentMapper.selectBathAppointmentById(appointmentId);
        if (appointment == null)
        {
            throw new RuntimeException("预约不存在");
        }
        
        // 2. 检查预约状态（只有在确认前或已确认但未支付时可以修改）
        String status = appointment.getStatus();
        if (!"0".equals(status) && !"1".equals(status)) // 0=待确认, 1=已确认
        {
            throw new RuntimeException("当前预约状态不允许修改宠物信息");
        }

        // 3. 如果petType为空，使用原值或默认值
        if (petType == null || petType.isEmpty())
        {
            petType = appointment.getPetType() != null ? appointment.getPetType() : PetTypeConstants.SHORT_HAIR;
        }

        // 4. 重新计算价格
        BigDecimal newPrice = bathServiceService.calculatePrice(appointment.getServiceId(), petType, petWeight);
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new RuntimeException("无法计算价格，请检查服务价格配置");
        }

        // 5. 更新预约信息
        PetBathAppointment updateAppointment = new PetBathAppointment();
        updateAppointment.setAppointmentId(appointmentId);
        if (petWeight != null)
        {
            updateAppointment.setPetWeight(petWeight);
        }
        updateAppointment.setPetType(petType);
        updateAppointment.setExpectedPrice(newPrice);
        updateAppointment.setUpdateBy(SecurityUtils.getUsername());
        int updateResult = bathAppointmentMapper.updateBathAppointment(updateAppointment);

        // 6. 如果订单已创建，更新订单价格
        PetBathOrder order = bathOrderMapper.selectBathOrderByAppointmentId(appointmentId);
        if (order != null)
        {
            // 检查订单状态（只有在未支付时可以修改价格）
            if ("0".equals(order.getStatus())) // 0=待支付
            {
                PetBathOrder updateOrder = new PetBathOrder();
                updateOrder.setOrderId(order.getOrderId());
                updateOrder.setTotalAmount(newPrice);
                updateOrder.setUpdateBy(SecurityUtils.getUsername());
                bathOrderMapper.updateBathOrder(updateOrder);
                result.put("orderUpdated", true);
            }
            else
            {
                result.put("orderUpdated", false);
                result.put("orderStatus", order.getStatus());
                result.put("message", "订单已支付，价格无法修改");
            }
        }
        else
        {
            result.put("orderUpdated", false);
            result.put("message", "订单尚未创建");
        }

        result.put("success", updateResult > 0);
        result.put("newPrice", newPrice);
        result.put("oldPrice", appointment.getExpectedPrice());
        
        return result;
    }

    /**
     * 发送订单完成短信通知
     *
     * @param phone 手机号
     */
    private void sendOrderCompletedSms(String phone)
    {
        try
        {
            // 构建 URL，添加 to 参数
            String finalUrl = UriComponentsBuilder.fromHttpUrl(ORDER_COMPLETED_SMS_URL)
                    .queryParam("to", phone)
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();

            log.debug("订单完成短信请求 URL: {}", finalUrl.replace(phone, "***"));

            // 使用 GET 请求
            org.springframework.http.ResponseEntity<String> response = restTemplate.getForEntity(finalUrl, String.class);

            String responseBody = response.getBody();
            boolean httpSuccess = response.getStatusCode().is2xxSuccessful();

            // 检查响应体
            boolean actualSuccess = httpSuccess;
            if (responseBody != null)
            {
                // 检查是否包含错误信息
                if (responseBody.contains("\"code\":") && !responseBody.contains("\"code\":200") && !responseBody.contains("\"code\": 200"))
                {
                    actualSuccess = false;
                }
                if (responseBody.contains("未匹配到推送对象") || responseBody.contains("模板编码错误") 
                    || responseBody.contains("需要订阅会员"))
                {
                    actualSuccess = false;
                }
            }

            if (actualSuccess)
            {
                log.info("订单完成短信发送成功，phone={}", phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
            }
            else
            {
                log.warn("订单完成短信发送失败，phone={}, status={}, response={}", 
                    phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"),
                    response.getStatusCode(), 
                    responseBody);
            }
        }
        catch (Exception e)
        {
            log.error("订单完成短信发送异常，phone={}", 
                phone != null ? phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2") : "null", e);
        }
    }
}

