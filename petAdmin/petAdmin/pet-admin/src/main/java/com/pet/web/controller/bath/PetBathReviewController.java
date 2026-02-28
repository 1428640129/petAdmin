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
import com.pet.system.domain.PetBathReview;
import com.pet.business.service.IPetBathReviewService;
import com.pet.business.service.IPetBathAppointmentService;
import com.pet.business.service.IPetBathOrderService;
import com.pet.system.domain.PetBathAppointment;
import com.pet.system.domain.PetBathOrder;

/**
 * 评价评论Controller
 * 
 * @author Pet
 */
@RestController
@RequestMapping("/bath/review")
public class PetBathReviewController extends BaseController
{
    @Autowired
    private IPetBathReviewService bathReviewService;

    @Autowired
    private IPetBathAppointmentService bathAppointmentService;

    @Autowired
    private IPetBathOrderService bathOrderService;

    /**
     * 查询评价评论列表
     */
    @PreAuthorize("@ss.hasPermi('bath:review:list')")
    @GetMapping("/list")
    public TableDataInfo list(PetBathReview review)
    {
        startPage();
        List<PetBathReview> list = bathReviewService.selectBathReviewList(review);
        return getDataTable(list);
    }

    /**
     * 导出评价评论列表
     */
    @PreAuthorize("@ss.hasPermi('bath:review:export')")
    @Log(title = "评价管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PetBathReview review)
    {
        List<PetBathReview> list = bathReviewService.selectBathReviewList(review);
        ExcelUtil<PetBathReview> util = new ExcelUtil<PetBathReview>(PetBathReview.class);
        util.exportExcel(response, list, "评价数据");
    }

    /**
     * 获取评价评论详细信息
     */
    @PreAuthorize("@ss.hasPermi('bath:review:query')")
    @GetMapping(value = "/{reviewId}")
    public AjaxResult getInfo(@PathVariable("reviewId") Long reviewId)
    {
        return success(bathReviewService.selectBathReviewByReviewId(reviewId));
    }

    /**
     * 新增评价评论
     */
    @PreAuthorize("@ss.hasPermi('bath:review:add')")
    @Log(title = "评价管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PetBathReview review)
    {
        return toAjax(bathReviewService.insertBathReview(review));
    }

    /**
     * 修改评价评论
     */
    @PreAuthorize("@ss.hasPermi('bath:review:edit')")
    @Log(title = "评价管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PetBathReview review)
    {
        return toAjax(bathReviewService.updateBathReview(review));
    }

    /**
     * 删除评价评论
     */
    @PreAuthorize("@ss.hasPermi('bath:review:remove')")
    @Log(title = "评价管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{reviewIds}")
    public AjaxResult remove(@PathVariable Long[] reviewIds)
    {
        return toAjax(bathReviewService.deleteBathReviewByReviewIds(reviewIds));
    }

    /**
     * 回复评价
     */
    @PreAuthorize("@ss.hasPermi('bath:review:reply')")
    @Log(title = "评价管理", businessType = BusinessType.UPDATE)
    @PutMapping("/reply/{reviewId}")
    public AjaxResult reply(@PathVariable Long reviewId, @RequestBody String replyContent)
    {
        return toAjax(bathReviewService.replyReview(reviewId, replyContent));
    }

    /**
     * 小程序查询评价列表（无需权限验证）
     */
    @GetMapping("/miniprogram/list")
    public TableDataInfo listForMiniprogram(
        @RequestParam(required = false) Long appointmentId,
        @RequestParam(required = false) Long serviceId,
        @RequestParam(defaultValue = "1") Integer pageNum,
        @RequestParam(defaultValue = "10") Integer pageSize
    )
    {
        try
        {
            PetBathReview review = new PetBathReview();
            if (appointmentId != null)
            {
                review.setAppointmentId(appointmentId);
            }
            if (serviceId != null)
            {
                review.setServiceId(serviceId);
            }
            // 只查询正常状态的评价
            review.setStatus("0");
            
            // 设置分页参数
            com.github.pagehelper.PageHelper.startPage(pageNum, pageSize);
            
            // 查询评价列表
            List<PetBathReview> list = bathReviewService.selectBathReviewList(review);
            return getDataTable(list);
        }
        catch (Exception e)
        {
            // 返回空的TableDataInfo而不是AjaxResult
            TableDataInfo dataTable = new TableDataInfo();
            dataTable.setCode(HttpStatus.ERROR);
            dataTable.setMsg("查询评价列表失败：" + e.getMessage());
            dataTable.setRows(new java.util.ArrayList<>());
            dataTable.setTotal(0L);
            return dataTable;
        }
    }

    /**
     * 小程序根据预约ID查询评价（无需权限验证）
     */
    @GetMapping("/miniprogram/byAppointment/{appointmentId}")
    public AjaxResult getReviewByAppointmentId(@PathVariable Long appointmentId)
    {
        PetBathReview review = new PetBathReview();
        review.setAppointmentId(appointmentId);
        review.setStatus("0");
        List<PetBathReview> list = bathReviewService.selectBathReviewList(review);
        if (list != null && !list.isEmpty())
        {
            return success(list.get(0));
        }
        return success(null);
    }

    /**
     * 小程序新增评价（无需权限验证）
     */
    @PostMapping("/miniprogram")
    public AjaxResult addFromMiniprogram(@RequestBody PetBathReview review)
    {
        // 验证必填字段
        if (review.getAppointmentId() == null)
        {
            return error("预约ID不能为空");
        }
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5)
        {
            return error("评分必须在1-5之间");
        }
        if (com.pet.common.utils.StringUtils.isEmpty(review.getContent()))
        {
            return error("评价内容不能为空");
        }
        
        // 检查是否已评价
        PetBathReview existReview = new PetBathReview();
        existReview.setAppointmentId(review.getAppointmentId());
        existReview.setStatus("0");
        List<PetBathReview> existList = bathReviewService.selectBathReviewList(existReview);
        if (existList != null && !existList.isEmpty())
        {
            return error("该预约已评价，不能重复评价");
        }
        
        // 从预约信息中获取用户ID和服务ID
        PetBathAppointment appointment = bathAppointmentService.selectBathAppointmentByAppointmentId(review.getAppointmentId());
        if (appointment == null)
        {
            return error("预约信息不存在");
        }
        
        // 检查预约状态是否为已完成
        if (!"3".equals(appointment.getStatus()))
        {
            return error("只有已完成的服务才能评价");
        }
        
        // 设置用户ID和服务ID
        review.setUserId(appointment.getUserId());
        if (review.getServiceId() == null && appointment.getServiceId() != null)
        {
            review.setServiceId(appointment.getServiceId());
        }
        
        // 从订单信息中获取订单ID
        PetBathOrder order = bathOrderService.selectBathOrderByAppointmentId(review.getAppointmentId());
        if (order != null && order.getOrderId() != null)
        {
            review.setOrderId(order.getOrderId());
        }
        
        // 设置默认状态
        review.setStatus("0");
        
        return toAjax(bathReviewService.insertBathReview(review));
    }
}

