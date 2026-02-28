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
                userId = getUserId();
            } catch (Exception e) {
                // 用户未登录，使用默认值0（匿名用户）
                userId = 0L;
            }
            appointment.setUserId(userId);
            
            // 设置分页参数
            com.github.pagehelper.PageHelper.startPage(pageNum, pageSize);
            
            // 查询预约列表
            List<PetBathAppointment> list = bathAppointmentService.selectBathAppointmentList(appointment);
            
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
                if (!completedAppointmentIds.isEmpty()) {
                    // 查询所有已完成预约的评价（状态为0的正常评价）
                    PetBathReview reviewQuery = new PetBathReview();
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
                
                TableDataInfo dataTable = getDataTable(list);
                dataTable.setRows(resultList);
                return dataTable;
            }
            
            return getDataTable(list);
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
                userId = getUserId();
                createBy = getUsername(); // 如果用户已登录，使用用户名
            } catch (Exception e) {
                // 用户未登录，尝试从请求参数中获取
                if (params.containsKey("userId")) {
                    Object userIdObj = params.get("userId");
                    if (userIdObj instanceof Number) {
                        userId = ((Number) userIdObj).longValue();
                    }
                }
                // 如果还是没有，使用默认值 0（表示匿名用户）
                if (userId == null) {
                    userId = 0L;
                }
            }
            appointment.setUserId(userId);
            appointment.setCreateBy(createBy);
            
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

