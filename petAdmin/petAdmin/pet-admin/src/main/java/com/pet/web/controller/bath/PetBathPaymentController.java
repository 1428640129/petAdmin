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
import com.pet.system.domain.PetBathPayment;
import com.pet.business.service.IPetBathPaymentService;

/**
 * 支付记录Controller
 * 
 * @author Pet
 */
@RestController
@RequestMapping("/bath/payment")
public class PetBathPaymentController extends BaseController
{
    @Autowired
    private IPetBathPaymentService bathPaymentService;

    /**
     * 查询支付记录列表
     */
    @PreAuthorize("@ss.hasPermi('bath:payment:list')")
    @GetMapping("/list")
    public TableDataInfo list(PetBathPayment payment)
    {
        startPage();
        List<PetBathPayment> list = bathPaymentService.selectBathPaymentList(payment);
        return getDataTable(list);
    }

    /**
     * 导出支付记录列表
     */
    @PreAuthorize("@ss.hasPermi('bath:payment:export')")
    @Log(title = "支付管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PetBathPayment payment)
    {
        List<PetBathPayment> list = bathPaymentService.selectBathPaymentList(payment);
        ExcelUtil<PetBathPayment> util = new ExcelUtil<PetBathPayment>(PetBathPayment.class);
        util.exportExcel(response, list, "支付数据");
    }

    /**
     * 获取支付记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('bath:payment:query')")
    @GetMapping(value = "/{paymentId}")
    public AjaxResult getInfo(@PathVariable("paymentId") Long paymentId)
    {
        return success(bathPaymentService.selectBathPaymentByPaymentId(paymentId));
    }

    /**
     * 新增支付记录
     */
    @PreAuthorize("@ss.hasPermi('bath:payment:add')")
    @Log(title = "支付管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PetBathPayment payment)
    {
        return toAjax(bathPaymentService.insertBathPayment(payment));
    }

    /**
     * 修改支付记录
     */
    @PreAuthorize("@ss.hasPermi('bath:payment:edit')")
    @Log(title = "支付管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PetBathPayment payment)
    {
        return toAjax(bathPaymentService.updateBathPayment(payment));
    }

    /**
     * 删除支付记录
     */
    @PreAuthorize("@ss.hasPermi('bath:payment:remove')")
    @Log(title = "支付管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{paymentIds}")
    public AjaxResult remove(@PathVariable Long[] paymentIds)
    {
        return toAjax(bathPaymentService.deleteBathPaymentByPaymentIds(paymentIds));
    }

    /**
     * 处理退款
     */
    @PreAuthorize("@ss.hasPermi('bath:payment:refund')")
    @Log(title = "支付管理", businessType = BusinessType.UPDATE)
    @PutMapping("/refund/{paymentId}")
    public AjaxResult refund(@PathVariable Long paymentId, @RequestBody(required = false) String refundReason)
    {
        return toAjax(bathPaymentService.processRefund(paymentId, refundReason));
    }
}

