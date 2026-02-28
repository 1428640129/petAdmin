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
import org.springframework.web.bind.annotation.RestController;
import com.pet.common.annotation.Log;
import com.pet.common.core.controller.BaseController;
import com.pet.common.core.domain.AjaxResult;
import com.pet.common.core.page.TableDataInfo;
import com.pet.common.enums.BusinessType;
import com.pet.common.utils.poi.ExcelUtil;
import com.pet.system.domain.PetBathNotification;
import com.pet.business.service.IPetBathNotificationService;

/**
 * 通知记录Controller
 * 
 * @author Pet
 */
@RestController
@RequestMapping("/bath/notification")
public class PetBathNotificationController extends BaseController
{
    @Autowired
    private IPetBathNotificationService bathNotificationService;

    /**
     * 查询通知记录列表
     * 默认只显示预约确认和服务完成两种类型的通知
     */
    @PreAuthorize("@ss.hasPermi('bath:notification:list')")
    @GetMapping("/list")
    public TableDataInfo list(PetBathNotification notification)
    {
        startPage();
        List<PetBathNotification> list = bathNotificationService.selectBathNotificationList(notification);
        return getDataTable(list);
    }

    /**
     * 导出通知记录列表
     */
    @PreAuthorize("@ss.hasPermi('bath:notification:export')")
    @Log(title = "通知管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PetBathNotification notification)
    {
        List<PetBathNotification> list = bathNotificationService.selectBathNotificationList(notification);
        ExcelUtil<PetBathNotification> util = new ExcelUtil<PetBathNotification>(PetBathNotification.class);
        util.exportExcel(response, list, "通知数据");
    }

    /**
     * 获取通知记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('bath:notification:query')")
    @GetMapping(value = "/{notificationId}")
    public AjaxResult getInfo(@PathVariable("notificationId") Long notificationId)
    {
        return success(bathNotificationService.selectBathNotificationByNotificationId(notificationId));
    }

    /**
     * 新增通知记录
     */
    @PreAuthorize("@ss.hasPermi('bath:notification:add')")
    @Log(title = "通知管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PetBathNotification notification)
    {
        return toAjax(bathNotificationService.insertBathNotification(notification));
    }

    /**
     * 修改通知记录
     */
    @PreAuthorize("@ss.hasPermi('bath:notification:edit')")
    @Log(title = "通知管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PetBathNotification notification)
    {
        return toAjax(bathNotificationService.updateBathNotification(notification));
    }

    /**
     * 删除通知记录
     */
    @PreAuthorize("@ss.hasPermi('bath:notification:remove')")
    @Log(title = "通知管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{notificationIds}")
    public AjaxResult remove(@PathVariable Long[] notificationIds)
    {
        return toAjax(bathNotificationService.deleteBathNotificationByNotificationIds(notificationIds));
    }

    /**
     * 发送通知
     */
    @PreAuthorize("@ss.hasPermi('bath:notification:send')")
    @Log(title = "通知管理", businessType = BusinessType.INSERT)
    @PostMapping("/send")
    public AjaxResult send(@RequestBody PetBathNotification notification)
    {
        return toAjax(bathNotificationService.sendNotification(
            notification.getUserId(),
            notification.getNotificationType(),
            notification.getTitle(),
            notification.getContent(),
            notification.getAppointmentId(),
            notification.getOrderId()
        ));
    }
}

