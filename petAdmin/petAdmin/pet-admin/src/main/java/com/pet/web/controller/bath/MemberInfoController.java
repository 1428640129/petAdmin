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
import com.pet.system.domain.MemberInfo;
import com.pet.business.service.IMemberInfoService;

/**
 * 会员信息Controller
 * 
 * @author Pet
 */
@RestController
@RequestMapping("/bath/member")
public class MemberInfoController extends BaseController
{
    @Autowired
    private IMemberInfoService memberInfoService;

    /**
     * 查询会员信息列表
     */
    @PreAuthorize("@ss.hasPermi('bath:member:list')")
    @GetMapping("/list")
    public TableDataInfo list(MemberInfo memberInfo)
    {
        startPage();
        List<MemberInfo> list = memberInfoService.selectMemberInfoList(memberInfo);
        return getDataTable(list);
    }

    /**
     * 导出会员信息列表
     */
    @PreAuthorize("@ss.hasPermi('bath:member:export')")
    @Log(title = "会员信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MemberInfo memberInfo)
    {
        List<MemberInfo> list = memberInfoService.selectMemberInfoList(memberInfo);
        ExcelUtil<MemberInfo> util = new ExcelUtil<MemberInfo>(MemberInfo.class);
        util.exportExcel(response, list, "会员信息数据");
    }

    /**
     * 获取会员信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('bath:member:query')")
    @GetMapping(value = "/{memberId}")
    public AjaxResult getInfo(@PathVariable("memberId") Long memberId)
    {
        return success(memberInfoService.selectMemberInfoById(memberId));
    }

    /**
     * 新增会员信息
     */
    @PreAuthorize("@ss.hasPermi('bath:member:add')")
    @Log(title = "会员信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MemberInfo memberInfo)
    {
        return toAjax(memberInfoService.insertMemberInfo(memberInfo));
    }

    /**
     * 修改会员信息
     */
    @PreAuthorize("@ss.hasPermi('bath:member:edit')")
    @Log(title = "会员信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MemberInfo memberInfo)
    {
        return toAjax(memberInfoService.updateMemberInfo(memberInfo));
    }

    /**
     * 删除会员信息
     */
    @PreAuthorize("@ss.hasPermi('bath:member:remove')")
    @Log(title = "会员信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{memberIds}")
    public AjaxResult remove(@PathVariable Long[] memberIds)
    {
        return toAjax(memberInfoService.deleteMemberInfoByIds(memberIds));
    }

    /**
     * 小程序端：根据用户ID查询会员信息（无需权限）
     */
    @GetMapping("/info")
    public AjaxResult getMemberInfo(Long userId)
    {
        if (userId == null)
        {
            return error("用户ID不能为空");
        }
        MemberInfo memberInfo = memberInfoService.selectMemberInfoByUserId(userId);
        if (memberInfo == null)
        {
            // 如果会员信息不存在，自动初始化
            memberInfoService.initMemberInfo(userId);
            memberInfo = memberInfoService.selectMemberInfoByUserId(userId);
        }
        return success(memberInfo);
    }
}





