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
import com.pet.system.domain.PetBathReview;
import com.pet.business.service.IPetBathReviewService;

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
}

