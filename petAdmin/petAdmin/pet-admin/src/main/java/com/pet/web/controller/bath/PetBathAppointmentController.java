package com.pet.web.controller.bath;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.pet.common.annotation.Log;
import com.pet.common.constant.HttpStatus;
import com.pet.common.core.controller.BaseController;
import com.pet.common.core.domain.AjaxResult;
import com.pet.common.core.page.TableDataInfo;
import com.pet.common.enums.BusinessType;
import com.pet.common.utils.poi.ExcelUtil;
import com.pet.common.utils.ServletUtils;
import java.util.Map;
import com.pet.system.domain.PetBathAppointment;
import com.pet.system.domain.PetBathReview;
import com.pet.business.service.IPetBathAppointmentService;
import com.pet.business.service.IPetBathReviewService;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 预约Controller
 * 
 * @author Pet
 */
@RestController
@RequestMapping("/bath/appointment")
public class PetBathAppointmentController extends BaseController
{
    @Autowired
    private IPetBathAppointmentService bathAppointmentService;

    @Autowired
    private IPetBathReviewService bathReviewService;

    /**
     * 查询预约列表
     */
    @PreAuthorize("@ss.hasPermi('bath:appointment:list')")
    @GetMapping("/list")
    public TableDataInfo list(PetBathAppointment appointment)
    {
        startPage();
        List<PetBathAppointment> list = bathAppointmentService.selectBathAppointmentList(appointment);
        return getDataTable(list);
    }

    /**
     * 导出预约列表
     */
    @PreAuthorize("@ss.hasPermi('bath:appointment:export')")
    @Log(title = "预约管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PetBathAppointment appointment)
    {
        List<PetBathAppointment> list = bathAppointmentService.selectBathAppointmentList(appointment);
        ExcelUtil<PetBathAppointment> util = new ExcelUtil<PetBathAppointment>(PetBathAppointment.class);
        util.exportExcel(response, list, "预约数据");
    }

    /**
     * 获取预约详细信息
     */
    @PreAuthorize("@ss.hasPermi('bath:appointment:query')")
    @GetMapping(value = "/{appointmentId}")
    public AjaxResult getInfo(@PathVariable("appointmentId") Long appointmentId)
    {
        return success(bathAppointmentService.selectBathAppointmentByAppointmentId(appointmentId));
    }

    /**
     * 小程序查询预约列表（无需权限验证）
     */
    @GetMapping("/miniprogram/list")
    public TableDataInfo listForMiniprogram(
        @RequestParam(required = false) String status,
        @RequestParam(required = false, defaultValue = "1") Integer pageNum,
        @RequestParam(required = false, defaultValue = "10") Integer pageSize)
    {
        try {
            PetBathAppointment appointment = new PetBathAppointment();
            
            // 设置状态筛选
            if (status != null && !status.isEmpty()) {
                appointment.setStatus(status);
            }
            
            // 尝试从token获取用户ID
            Long userId = null;
            try {
                // 先尝试从Spring Security获取（后台管理系统用户）
                userId = getUserId();
            } catch (Exception e) {
                // 如果Spring Security获取失败，尝试从小程序token中解析
                try {
                    jakarta.servlet.http.HttpServletRequest request = ServletUtils.getRequest();
                    String authHeader = request.getHeader("Authorization");
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);
                        logger.info("收到token: " + token);
                        // 解析小程序token格式：pet_bath_{userId}_{timestamp}_{uuid}
                        if (token.startsWith("pet_bath_")) {
                            String[] parts = token.split("_");
                            logger.info("token分割后长度: " + parts.length);
                            if (parts.length >= 3) {
                                userId = Long.parseLong(parts[2]);
                                logger.info("解析到userId: " + userId);
                            } else {
                                logger.warn("token格式不正确，parts长度不足3: " + parts.length);
                            }
                        } else {
                            logger.warn("token格式不正确，不以pet_bath_开头: " + token);
                        }
                    } else {
                        logger.warn("Authorization头不存在或格式不正确: " + authHeader);
                    }
                } catch (Exception ex) {
                    // 解析token失败
                    logger.error("解析小程序token失败: " + ex.getMessage(), ex);
                }
                
                // 如果还是无法获取userId，返回错误
                if (userId == null) {
                    logger.error("无法获取用户ID，无法查询预约列表");
                    TableDataInfo dataTable = new TableDataInfo();
                    dataTable.setCode(HttpStatus.UNAUTHORIZED);
                    dataTable.setMsg("用户未登录，请先登录");
                    dataTable.setRows(new ArrayList<>());
                    dataTable.setTotal(0L);
                    return dataTable;
                }
            }
            
            appointment.setUserId(userId);
            logger.info("最终使用的userId: " + userId);
            
            // 设置分页参数
            com.github.pagehelper.PageHelper.startPage(pageNum, pageSize);
            
            // 查询预约列表
            List<PetBathAppointment> list = bathAppointmentService.selectBathAppointmentList(appointment);
            logger.info("查询到的预约列表数量: " + (list != null ? list.size() : 0));
            
            // 为已完成状态的预约添加评价状态标记
            if (list != null && !list.isEmpty()) {
                // 收集所有已完成状态的预约ID
                List<Long> completedAppointmentIds = new ArrayList<>();
                for (PetBathAppointment apt : list) {
                    if ("3".equals(apt.getStatus())) { // 3=已完成
                        completedAppointmentIds.add(apt.getAppointmentId());
                    }
                }
                
                // 批量查询评价状态（一次性查询所有已完成预约的评价）
                java.util.Set<Long> reviewedAppointmentIds = new java.util.HashSet<>();
                if (!completedAppointmentIds.isEmpty() && userId != null && userId > 0) {
                    // 查询当前用户对已完成预约的评价（状态为0的正常评价）
                    PetBathReview reviewQuery = new PetBathReview();
                    reviewQuery.setUserId(userId); // 只查询当前用户的评价
                    reviewQuery.setStatus("0"); // 0=正常状态
                    List<PetBathReview> allReviews = bathReviewService.selectBathReviewList(reviewQuery);
                    if (allReviews != null) {
                        for (PetBathReview review : allReviews) {
                            if (review.getAppointmentId() != null && completedAppointmentIds.contains(review.getAppointmentId())) {
                                reviewedAppointmentIds.add(review.getAppointmentId());
                            }
                        }
                    }
                }
                
                // 将预约对象转换为Map，添加hasReview字段
                List<Map<String, Object>> resultList = new ArrayList<>();
                logger.info("开始转换预约列表，原始list大小: " + list.size());
                for (PetBathAppointment apt : list) {
                    Map<String, Object> itemMap = new HashMap<>();
                    // 复制所有字段
                    itemMap.put("appointmentId", apt.getAppointmentId());
                    itemMap.put("appointmentNo", apt.getAppointmentNo());
                    itemMap.put("userId", apt.getUserId());
                    itemMap.put("petId", apt.getPetId());
                    itemMap.put("petName", apt.getPetName());
                    itemMap.put("petWeight", apt.getPetWeight());
                    itemMap.put("petType", apt.getPetType());
                    itemMap.put("serviceId", apt.getServiceId());
                    itemMap.put("serviceName", apt.getServiceName());
                    itemMap.put("appointmentTime", apt.getAppointmentTime());
                    itemMap.put("expectedPrice", apt.getExpectedPrice());
                    itemMap.put("actualPrice", apt.getActualPrice());
                    itemMap.put("status", apt.getStatus());
                    itemMap.put("cancelReason", apt.getCancelReason());
                    itemMap.put("cancelTime", apt.getCancelTime());
                    itemMap.put("remark", apt.getRemark());
                    itemMap.put("createTime", apt.getCreateTime());
                    itemMap.put("updateTime", apt.getUpdateTime());
                    // 添加评价状态标记（仅对已完成状态的预约）
                    if ("3".equals(apt.getStatus())) {
                        itemMap.put("hasReview", reviewedAppointmentIds.contains(apt.getAppointmentId()));
                    } else {
                        itemMap.put("hasReview", false);
                    }
                    resultList.add(itemMap);
                }
                logger.info("转换后的resultList大小: " + resultList.size());
                
                TableDataInfo dataTable = getDataTable(list);
                logger.info("getDataTable返回的total: " + dataTable.getTotal() + ", rows大小: " + (dataTable.getRows() != null ? dataTable.getRows().size() : 0));
                dataTable.setRows(resultList);
                logger.info("设置resultList后的total: " + dataTable.getTotal() + ", rows大小: " + (dataTable.getRows() != null ? dataTable.getRows().size() : 0));
                return dataTable;
            }
            
            TableDataInfo dataTable = getDataTable(list);
            logger.info("返回的TableDataInfo（空列表） - code: " + dataTable.getCode() + ", total: " + dataTable.getTotal() + ", rows数量: " + (dataTable.getRows() != null ? dataTable.getRows().size() : 0));
            return dataTable;
        } catch (Exception e) {
            // 返回空的TableDataInfo而不是AjaxResult
            TableDataInfo dataTable = new TableDataInfo();
            dataTable.setCode(HttpStatus.ERROR);
            dataTable.setMsg("查询预约列表失败：" + e.getMessage());
            dataTable.setRows(new java.util.ArrayList<>());
            dataTable.setTotal(0L);
            return dataTable;
        }
    }

    /**
     * 小程序查询预约详情（无需权限验证，与 miniprogram 列表同一路由前缀）
     */
    @GetMapping("/miniprogram/detail/{appointmentId}")
    public AjaxResult getMiniprogramAppointmentDetail(@PathVariable Long appointmentId)
    {
        PetBathAppointment apt = bathAppointmentService.selectBathAppointmentByAppointmentId(appointmentId);
        if (apt == null)
        {
            return error("预约不存在");
        }
        return success(apt);
    }

    /**
     * 小程序新增预约（无需权限验证）
     */
    @PostMapping("/miniprogram")
    public AjaxResult addFromMiniprogram(@RequestBody Map<String, Object> params)
    {
        try {
            PetBathAppointment appointment = new PetBathAppointment();
            
            // 基本字段
            if (params.containsKey("serviceId")) {
                Object serviceIdObj = params.get("serviceId");
                if (serviceIdObj instanceof Number) {
                    appointment.setServiceId(((Number) serviceIdObj).longValue());
                }
            }
            
            if (params.containsKey("appointmentTime")) {
                String appointmentTimeStr = params.get("appointmentTime").toString();
                try {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    appointment.setAppointmentTime(sdf.parse(appointmentTimeStr));
                } catch (Exception e) {
                    return error("预约时间格式错误，请使用 yyyy-MM-dd HH:mm:ss 格式");
                }
            }
            
            if (params.containsKey("petName")) {
                appointment.setPetName(params.get("petName").toString());
            }
            
            if (params.containsKey("petWeight")) {
                Object weightObj = params.get("petWeight");
                if (weightObj instanceof Number) {
                    appointment.setPetWeight(new java.math.BigDecimal(weightObj.toString()));
                } else if (weightObj != null) {
                    appointment.setPetWeight(new java.math.BigDecimal(weightObj.toString()));
                }
            }
            
            if (params.containsKey("petType")) {
                appointment.setPetType(params.get("petType").toString());
            }
            
            // 构建备注信息，包含 petBreed 和 contactPhone
            StringBuilder remarkBuilder = new StringBuilder();
            if (params.containsKey("petBreed") && params.get("petBreed") != null) {
                remarkBuilder.append("宠物品种：").append(params.get("petBreed").toString()).append("；");
            }
            if (params.containsKey("contactPhone") && params.get("contactPhone") != null) {
                remarkBuilder.append("联系电话：").append(params.get("contactPhone").toString()).append("；");
            }
            if (params.containsKey("remark") && params.get("remark") != null) {
                String userRemark = params.get("remark").toString().trim();
                if (!userRemark.isEmpty()) {
                    remarkBuilder.append("备注：").append(userRemark);
                }
            }
            appointment.setRemark(remarkBuilder.toString());
            
            // 尝试从 token 中获取用户ID（如果用户已登录）
            Long userId = null;
            String createBy = "miniprogram_user"; // 默认创建者
            try {
                // 先尝试从Spring Security获取（后台管理系统用户）
                userId = getUserId();
                createBy = getUsername(); // 如果用户已登录，使用用户名
                logger.info("从Spring Security获取到userId: " + userId);
            } catch (Exception e) {
                // 如果Spring Security获取失败，尝试从小程序token中解析
                logger.info("Spring Security获取userId失败，尝试解析小程序token");
                try {
                    jakarta.servlet.http.HttpServletRequest request = ServletUtils.getRequest();
                    String authHeader = request.getHeader("Authorization");
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);
                        logger.info("收到token: " + token);
                        // 解析小程序token格式：pet_bath_{userId}_{timestamp}_{uuid}
                        if (token.startsWith("pet_bath_")) {
                            String[] parts = token.split("_");
                            logger.info("token分割后parts数量: " + parts.length);
                            if (parts.length >= 3) {
                                userId = Long.parseLong(parts[2]);
                                logger.info("成功解析到userId: " + userId);
                            } else {
                                logger.warn("token格式不正确，parts长度不足3，实际长度: " + parts.length);
                            }
                        } else {
                            logger.warn("token格式不正确，不以pet_bath_开头");
                        }
                    } else {
                        logger.warn("Authorization头不存在或格式不正确");
                    }
                } catch (NumberFormatException ex) {
                    logger.error("解析userId失败，数字格式错误: " + ex.getMessage());
                } catch (Exception ex) {
                    logger.error("解析小程序token失败: " + ex.getMessage(), ex);
                }
                
                // 如果还是无法获取userId，返回错误
                if (userId == null) {
                    logger.error("无法获取用户ID，无法提交预约");
                    return error("用户未登录，请先登录");
                }
            }
            appointment.setUserId(userId);
            appointment.setCreateBy(createBy);
            logger.info("最终使用的userId: " + userId);
            
            return toAjax(bathAppointmentService.insertBathAppointment(appointment));
        } catch (Exception e) {
            return error("预约失败：" + e.getMessage());
        }
    }

    /**
     * 新增预约
     */
    @PreAuthorize("@ss.hasPermi('bath:appointment:add')")
    @Log(title = "预约管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PetBathAppointment appointment)
    {
        return toAjax(bathAppointmentService.insertBathAppointment(appointment));
    }

    /**
     * 修改预约
     */
    @PreAuthorize("@ss.hasPermi('bath:appointment:edit')")
    @Log(title = "预约管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PetBathAppointment appointment)
    {
        return toAjax(bathAppointmentService.updateBathAppointment(appointment));
    }

    /**
     * 删除预约
     */
    @PreAuthorize("@ss.hasPermi('bath:appointment:remove')")
    @Log(title = "预约管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{appointmentIds}")
    public AjaxResult remove(@PathVariable Long[] appointmentIds)
    {
        return toAjax(bathAppointmentService.deleteBathAppointmentByAppointmentIds(appointmentIds));
    }

    /**
     * 确认预约
     */
    @PreAuthorize("@ss.hasPermi('bath:appointment:confirm')")
    @Log(title = "预约管理", businessType = BusinessType.UPDATE)
    @PutMapping("/confirm/{appointmentId}")
    public AjaxResult confirm(@PathVariable Long appointmentId)
    {
        return toAjax(bathAppointmentService.confirmAppointment(appointmentId));
    }

    /**
     * 取消预约
     */
    @PreAuthorize("@ss.hasPermi('bath:appointment:cancel')")
    @Log(title = "预约管理", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{appointmentId}")
    public AjaxResult cancel(@PathVariable Long appointmentId, @RequestBody(required = false) String cancelReason)
    {
        return toAjax(bathAppointmentService.cancelAppointment(appointmentId, cancelReason));
    }

    /**
     * 开始服务
     */
    @PreAuthorize("@ss.hasPermi('bath:appointment:start')")
    @Log(title = "预约管理", businessType = BusinessType.UPDATE)
    @PutMapping("/start/{appointmentId}")
    public AjaxResult startService(@PathVariable Long appointmentId)
    {
        return toAjax(bathAppointmentService.startService(appointmentId));
    }

    /**
     * 完成服务
     */
    @PreAuthorize("@ss.hasPermi('bath:appointment:complete')")
    @Log(title = "预约管理", businessType = BusinessType.UPDATE)
    @PutMapping("/complete/{appointmentId}")
    public AjaxResult completeService(@PathVariable Long appointmentId)
    {
        return toAjax(bathAppointmentService.completeService(appointmentId));
    }

    /**
     * 完成服务（带实际价格）
     */
    @PreAuthorize("@ss.hasPermi('bath:appointment:complete')")
    @Log(title = "预约管理", businessType = BusinessType.UPDATE)
    @PutMapping("/complete/{appointmentId}/{actualPrice}")
    public AjaxResult completeServiceWithPrice(@PathVariable Long appointmentId, @PathVariable java.math.BigDecimal actualPrice)
    {
        return toAjax(bathAppointmentService.completeService(appointmentId, actualPrice));
    }

    /**
     * 更新宠物体重和类型并重新计算价格
     */
    @PreAuthorize("@ss.hasPermi('bath:appointment:edit')")
    @Log(title = "预约管理", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePetInfo/{appointmentId}")
    public AjaxResult updatePetInfoAndRecalculatePrice(
        @PathVariable Long appointmentId,
        @RequestBody Map<String, Object> params)
    {
        java.math.BigDecimal petWeight = null;
        String petType = null;
        
        if (params.containsKey("petWeight"))
        {
            Object weightObj = params.get("petWeight");
            if (weightObj instanceof Number)
            {
                petWeight = new java.math.BigDecimal(weightObj.toString());
            }
        }
        
        if (params.containsKey("petType"))
        {
            petType = params.get("petType").toString();
        }
        
        Map<String, Object> result = bathAppointmentService.updatePetInfoAndRecalculatePrice(appointmentId, petWeight, petType);
        return success(result);
    }
}

