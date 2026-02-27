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
import com.pet.system.domain.PetBathCarousel;
import com.pet.business.service.IPetBathCarouselService;

/**
 * 轮播图Controller
 * 
 * @author Pet
 */
@RestController
@RequestMapping("/bath/carousel")
public class PetBathCarouselController extends BaseController
{
    @Autowired
    private IPetBathCarouselService bathCarouselService;

    /**
     * 查询轮播图列表
     */
    @PreAuthorize("@ss.hasPermi('bath:carousel:list')")
    @GetMapping("/list")
    public TableDataInfo list(PetBathCarousel bathCarousel)
    {
        startPage();
        List<PetBathCarousel> list = bathCarouselService.selectBathCarouselList(bathCarousel);
        return getDataTable(list);
    }

    /**
     * 查询启用的轮播图列表（用于前端展示，无需权限）
     */
    @GetMapping("/enabled")
    public AjaxResult getEnabledList()
    {
        List<PetBathCarousel> list = bathCarouselService.selectEnabledBathCarouselList();
        return success(list);
    }

    /**
     * 导出轮播图列表
     */
    @PreAuthorize("@ss.hasPermi('bath:carousel:export')")
    @Log(title = "轮播图", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PetBathCarousel bathCarousel)
    {
        List<PetBathCarousel> list = bathCarouselService.selectBathCarouselList(bathCarousel);
        ExcelUtil<PetBathCarousel> util = new ExcelUtil<PetBathCarousel>(PetBathCarousel.class);
        util.exportExcel(response, list, "轮播图数据");
    }

    /**
     * 获取轮播图详细信息
     */
    @PreAuthorize("@ss.hasPermi('bath:carousel:query')")
    @GetMapping(value = "/{carouselId}")
    public AjaxResult getInfo(@PathVariable("carouselId") Long carouselId)
    {
        return success(bathCarouselService.selectBathCarouselByCarouselId(carouselId));
    }

    /**
     * 新增轮播图
     */
    @PreAuthorize("@ss.hasPermi('bath:carousel:add')")
    @Log(title = "轮播图", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PetBathCarousel bathCarousel)
    {
        return toAjax(bathCarouselService.insertBathCarousel(bathCarousel));
    }

    /**
     * 修改轮播图
     */
    @PreAuthorize("@ss.hasPermi('bath:carousel:edit')")
    @Log(title = "轮播图", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PetBathCarousel bathCarousel)
    {
        return toAjax(bathCarouselService.updateBathCarousel(bathCarousel));
    }

    /**
     * 删除轮播图
     */
    @PreAuthorize("@ss.hasPermi('bath:carousel:remove')")
    @Log(title = "轮播图", businessType = BusinessType.DELETE)
    @DeleteMapping("/{carouselIds}")
    public AjaxResult remove(@PathVariable Long[] carouselIds)
    {
        return toAjax(bathCarouselService.deleteBathCarouselByCarouselIds(carouselIds));
    }
}










