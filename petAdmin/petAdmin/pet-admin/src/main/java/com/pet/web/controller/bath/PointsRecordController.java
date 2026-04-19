package com.pet.web.controller.bath;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pet.common.annotation.Log;
import com.pet.common.core.controller.BaseController;
import com.pet.common.core.domain.AjaxResult;
import com.pet.common.core.page.TableDataInfo;
import com.pet.common.enums.BusinessType;
import com.pet.common.utils.poi.ExcelUtil;
import com.pet.system.domain.PointsRecord;
import com.pet.business.service.IPointsRecordService;

/**
 * 积分记录Controller
 * 
 * @author Pet
 */
@RestController
@RequestMapping("/bath/points")
public class PointsRecordController extends BaseController
{
    @Autowired
    private IPointsRecordService pointsRecordService;

    /**
     * 查询积分记录列表
     */
    @PreAuthorize("@ss.hasPermi('bath:points:list')")
    @GetMapping("/list")
    public TableDataInfo list(PointsRecord pointsRecord)
    {
        startPage();
        List<PointsRecord> list = pointsRecordService.selectPointsRecordList(pointsRecord);
        return getDataTable(list);
    }

    /**
     * 导出积分记录列表
     */
    @PreAuthorize("@ss.hasPermi('bath:points:export')")
    @Log(title = "积分记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PointsRecord pointsRecord)
    {
        List<PointsRecord> list = pointsRecordService.selectPointsRecordList(pointsRecord);
        ExcelUtil<PointsRecord> util = new ExcelUtil<PointsRecord>(PointsRecord.class);
        util.exportExcel(response, list, "积分记录数据");
    }

    /**
     * 获取积分记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('bath:points:query')")
    @GetMapping(value = "/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Long recordId)
    {
        return success(pointsRecordService.selectPointsRecordById(recordId));
    }

    /**
     * 删除积分记录
     */
    @PreAuthorize("@ss.hasPermi('bath:points:remove')")
    @Log(title = "积分记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds)
    {
        return toAjax(pointsRecordService.deletePointsRecordByIds(recordIds));
    }

    /**
     * 小程序端：根据用户ID查询积分记录列表（无需权限）
     */
    @GetMapping("/listByUser")
    public AjaxResult listByUser(Long userId)
    {
        if (userId == null)
        {
            return error("用户ID不能为空");
        }
        List<PointsRecord> list = pointsRecordService.selectPointsRecordListByUserId(userId);
        return success(list);
    }
}





