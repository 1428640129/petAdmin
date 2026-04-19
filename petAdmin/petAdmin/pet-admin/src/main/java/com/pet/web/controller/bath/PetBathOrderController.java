package com.pet.web.controller.bath;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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
import com.pet.system.domain.PetBathOrder;
import com.pet.business.service.IPetBathOrderService;

/**
 * 订单Controller
 * 
 * @author Pet
 */
@RestController
@RequestMapping("/bath/order")
public class PetBathOrderController extends BaseController
{
    @Autowired
    private IPetBathOrderService bathOrderService;

    /**
     * 查询订单列表
     */
    @PreAuthorize("@ss.hasPermi('bath:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(PetBathOrder order)
    {
        startPage();
        List<PetBathOrder> list = bathOrderService.selectBathOrderList(order);
        return getDataTable(list);
    }

    /**
     * 导出订单列表
     */
    @PreAuthorize("@ss.hasPermi('bath:order:export')")
    @Log(title = "订单管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PetBathOrder order)
    {
        List<PetBathOrder> list = bathOrderService.selectBathOrderList(order);
        ExcelUtil<PetBathOrder> util = new ExcelUtil<PetBathOrder>(PetBathOrder.class);
        util.exportExcel(response, list, "订单数据");
    }

    /**
     * 获取订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('bath:order:query')")
    @GetMapping(value = "/{orderId}")
    public AjaxResult getInfo(@PathVariable("orderId") Long orderId)
    {
        return success(bathOrderService.selectBathOrderByOrderId(orderId));
    }

    /**
     * 新增订单
     */
    @PreAuthorize("@ss.hasPermi('bath:order:add')")
    @Log(title = "订单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PetBathOrder order)
    {
        return toAjax(bathOrderService.insertBathOrder(order));
    }

    /**
     * 修改订单
     */
    @PreAuthorize("@ss.hasPermi('bath:order:edit')")
    @Log(title = "订单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PetBathOrder order)
    {
        return toAjax(bathOrderService.updateBathOrder(order));
    }

    /**
     * 删除订单
     */
    @PreAuthorize("@ss.hasPermi('bath:order:remove')")
    @Log(title = "订单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds)
    {
        return toAjax(bathOrderService.deleteBathOrderByOrderIds(orderIds));
    }

    /**
     * 支付订单
     */
    @PreAuthorize("@ss.hasPermi('bath:order:pay')")
    @Log(title = "订单管理", businessType = BusinessType.UPDATE)
    @PutMapping("/pay/{orderId}")
    public AjaxResult payOrder(@PathVariable Long orderId, @RequestBody Map<String, Object> body)
    {
        BigDecimal payAmount = parsePayAmount(body);
        if (payAmount == null)
        {
            return error("支付金额不能为空");
        }
        return toAjax(bathOrderService.payOrder(orderId, payAmount));
    }

    /**
     * 根据预约ID查询订单
     */
    @GetMapping("/byAppointment/{appointmentId}")
    public AjaxResult getOrderByAppointmentId(@PathVariable Long appointmentId)
    {
        return success(bathOrderService.selectBathOrderByAppointmentId(appointmentId));
    }

    /**
     * 小程序根据预约ID查询订单（无需权限验证）
     */
    @GetMapping("/miniprogram/byAppointment/{appointmentId}")
    public AjaxResult getOrderByAppointmentIdForMiniprogram(@PathVariable Long appointmentId)
    {
        return success(bathOrderService.selectBathOrderByAppointmentId(appointmentId));
    }

    /**
     * 小程序支付订单（无需权限验证）
     */
    @PutMapping("/miniprogram/pay/{orderId}")
    public AjaxResult payOrderForMiniprogram(@PathVariable Long orderId, @RequestBody Map<String, Object> body)
    {
        BigDecimal payAmount = parsePayAmount(body);
        if (payAmount == null)
        {
            return error("支付金额不能为空");
        }
        return toAjax(bathOrderService.payOrder(orderId, payAmount));
    }

    /** 从 JSON 体中解析 payAmount（兼容 Integer/Double/BigDecimal/字符串） */
    private static BigDecimal parsePayAmount(Map<String, Object> body)
    {
        if (body == null)
        {
            return null;
        }
        Object raw = body.get("payAmount");
        if (raw == null)
        {
            return null;
        }
        if (raw instanceof BigDecimal)
        {
            return (BigDecimal) raw;
        }
        if (raw instanceof Number)
        {
            return new BigDecimal(raw.toString());
        }
        String s = String.valueOf(raw).trim();
        if (s.isEmpty())
        {
            return null;
        }
        return new BigDecimal(s);
    }

    /**
     * 小程序创建支付宝沙盒支付订单（无需权限验证）
     */
    @PostMapping("/miniprogram/alipay/create/{orderId}")
    public AjaxResult createAlipayPayment(@PathVariable Long orderId)
    {
        PetBathOrder order = bathOrderService.selectBathOrderByOrderId(orderId);
        if (order == null)
        {
            return error("订单不存在");
        }
        if (!"0".equals(order.getStatus()))
        {
            return error("订单状态不正确，无法支付");
        }

        // 生成支付参数（支付宝沙盒模拟）
        java.util.Map<String, Object> paymentParams = new java.util.HashMap<>();
        paymentParams.put("orderId", orderId);
        paymentParams.put("orderNo", order.getOrderNo());
        paymentParams.put("totalAmount", order.getTotalAmount());
        paymentParams.put("subject", "宠物洗澡服务订单");
        paymentParams.put("body", "订单号：" + order.getOrderNo());
        
        // 支付宝沙盒支付参数（模拟）
        // 实际项目中应该调用支付宝SDK生成支付参数
        paymentParams.put("tradeNo", "ALIPAY_SANDBOX_" + System.currentTimeMillis());
        paymentParams.put("paymentString", "模拟支付字符串，实际应使用支付宝SDK生成");
        
        return success(paymentParams);
    }

    /**
     * 小程序支付宝支付回调（无需权限验证）
     */
    @PostMapping("/miniprogram/alipay/callback")
    public AjaxResult alipayCallback(@RequestBody java.util.Map<String, Object> callbackData)
    {
        try {
            String orderId = callbackData.get("orderId").toString();
            String tradeNo = callbackData.get("tradeNo") != null ? callbackData.get("tradeNo").toString() : "";
            String tradeStatus = callbackData.get("tradeStatus") != null ? callbackData.get("tradeStatus").toString() : "TRADE_SUCCESS";
            
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus))
            {
                // 支付成功，更新订单状态
                PetBathOrder order = bathOrderService.selectBathOrderByOrderId(Long.parseLong(orderId));
                if (order != null && "0".equals(order.getStatus()))
                {
                    bathOrderService.payOrder(Long.parseLong(orderId), order.getTotalAmount());
                    return success("支付成功");
                }
            }
            return error("支付失败");
        } catch (Exception e) {
            return error("处理支付回调失败：" + e.getMessage());
        }
    }
}

