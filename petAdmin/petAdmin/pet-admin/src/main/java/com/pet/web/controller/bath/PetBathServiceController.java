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
import com.pet.system.domain.PetBathService;
import com.pet.business.service.IPetBathServiceService;
import java.math.BigDecimal;

/**
 * 洗浴服务Controller
 * 
 * @author Pet
 */
@RestController
@RequestMapping("/bath/service")
public class PetBathServiceController extends BaseController
{
    @Autowired
    private IPetBathServiceService bathServiceService;

    /**
     * 查询洗浴服务列表
     */
    @PreAuthorize("@ss.hasPermi('bath:service:list')")
    @GetMapping("/list")
    public TableDataInfo list(PetBathService bathService)
    {
        startPage();
        List<PetBathService> list = bathServiceService.selectBathServiceList(bathService);
        return getDataTable(list);
    }

    /**
     * 查询启用的洗浴服务列表（用于前端展示，无需权限）
     */
    @GetMapping("/enabled")
    public AjaxResult getEnabledList()
    {
        List<PetBathService> list = bathServiceService.selectEnabledBathServiceList();
        return success(list);
    }

    /**
     * 导出洗浴服务列表
     */
    @PreAuthorize("@ss.hasPermi('bath:service:export')")
    @Log(title = "洗浴服务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PetBathService bathService)
    {
        List<PetBathService> list = bathServiceService.selectBathServiceList(bathService);
        ExcelUtil<PetBathService> util = new ExcelUtil<PetBathService>(PetBathService.class);
        util.exportExcel(response, list, "洗浴服务数据");
    }

    /**
     * 获取洗浴服务详细信息
     */
    @PreAuthorize("@ss.hasPermi('bath:service:query')")
    @GetMapping(value = "/{serviceId}")
    public AjaxResult getInfo(@PathVariable("serviceId") Long serviceId)
    {
        return success(bathServiceService.selectBathServiceByServiceId(serviceId));
    }

    /**
     * 根据体重计算价格（默认短毛）
     */
    @GetMapping("/calculatePrice")
    public AjaxResult calculatePrice(Long serviceId, BigDecimal weight)
    {
        BigDecimal price = bathServiceService.calculatePrice(serviceId, weight);
        return success(price);
    }

    /**
     * 根据体重和宠物类型计算价格
     */
    @GetMapping("/calculatePriceWithType")
    public AjaxResult calculatePriceWithType(Long serviceId, String petType, BigDecimal weight)
    {
        BigDecimal price = bathServiceService.calculatePrice(serviceId, petType, weight);
        return success(price);
    }

    /**
     * 新增洗浴服务
     */
    @PreAuthorize("@ss.hasPermi('bath:service:add')")
    @Log(title = "洗浴服务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PetBathService bathService)
    {
        return toAjax(bathServiceService.insertBathService(bathService));
    }

    /**
     * 修改洗浴服务
     */
    @PreAuthorize("@ss.hasPermi('bath:service:edit')")
    @Log(title = "洗浴服务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PetBathService bathService)
    {
        return toAjax(bathServiceService.updateBathService(bathService));
    }

    /**
     * 删除洗浴服务
     */
    @PreAuthorize("@ss.hasPermi('bath:service:remove')")
    @Log(title = "洗浴服务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{serviceIds}")
    public AjaxResult remove(@PathVariable Long[] serviceIds)
    {
        return toAjax(bathServiceService.deleteBathServiceByServiceIds(serviceIds));
    }
}

