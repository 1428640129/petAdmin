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
import com.pet.system.domain.PetProfile;
import com.pet.business.service.IPetProfileService;

/**
 * 宠物档案Controller
 * 
 * @author Pet
 */
@RestController
@RequestMapping("/bath/pet")
public class PetProfileController extends BaseController
{
    @Autowired
    private IPetProfileService petProfileService;

    /**
     * 查询宠物档案列表（管理后台）
     */
    @PreAuthorize("@ss.hasPermi('bath:pet:list')")
    @GetMapping("/list")
    public TableDataInfo list(PetProfile petProfile)
    {
        startPage();
        List<PetProfile> list = petProfileService.selectPetProfileList(petProfile);
        return getDataTable(list);
    }

    /**
     * 导出宠物档案列表
     */
    @PreAuthorize("@ss.hasPermi('bath:pet:export')")
    @Log(title = "宠物档案", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PetProfile petProfile)
    {
        List<PetProfile> list = petProfileService.selectPetProfileList(petProfile);
        ExcelUtil<PetProfile> util = new ExcelUtil<PetProfile>(PetProfile.class);
        util.exportExcel(response, list, "宠物档案数据");
    }

    /**
     * 获取宠物档案详细信息
     */
    @PreAuthorize("@ss.hasPermi('bath:pet:query')")
    @GetMapping(value = "/{petId}")
    public AjaxResult getInfo(@PathVariable("petId") Long petId)
    {
        return success(petProfileService.selectPetProfileById(petId));
    }

    /**
     * 新增宠物档案
     */
    @PreAuthorize("@ss.hasPermi('bath:pet:add')")
    @Log(title = "宠物档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PetProfile petProfile)
    {
        return toAjax(petProfileService.insertPetProfile(petProfile));
    }

    /**
     * 修改宠物档案
     */
    @PreAuthorize("@ss.hasPermi('bath:pet:edit')")
    @Log(title = "宠物档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PetProfile petProfile)
    {
        return toAjax(petProfileService.updatePetProfile(petProfile));
    }

    /**
     * 删除宠物档案
     */
    @PreAuthorize("@ss.hasPermi('bath:pet:remove')")
    @Log(title = "宠物档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{petIds}")
    public AjaxResult remove(@PathVariable Long[] petIds)
    {
        return toAjax(petProfileService.deletePetProfileByIds(petIds));
    }

    /**
     * 小程序端：根据用户ID查询宠物档案列表（无需权限）
     */
    @GetMapping("/listByUser")
    public AjaxResult listByUser(Long userId)
    {
        if (userId == null)
        {
            return error("用户ID不能为空");
        }
        List<PetProfile> list = petProfileService.selectPetProfileListByUserId(userId);
        return success(list);
    }

    /**
     * 小程序端：获取默认宠物（无需权限）
     */
    @GetMapping("/default")
    public AjaxResult getDefault(Long userId)
    {
        if (userId == null)
        {
            return error("用户ID不能为空");
        }
        PetProfile pet = petProfileService.selectDefaultPetByUserId(userId);
        return success(pet);
    }

    /**
     * 小程序端：新增宠物档案（无需权限）
     */
    @PostMapping("/add")
    public AjaxResult addPet(@RequestBody PetProfile petProfile)
    {
        if (petProfile.getUserId() == null)
        {
            return error("用户ID不能为空");
        }
        if (petProfile.getPetName() == null || petProfile.getPetName().trim().isEmpty())
        {
            return error("宠物名称不能为空");
        }
        petProfile.setCreateBy("app");
        return toAjax(petProfileService.insertPetProfile(petProfile));
    }

    /**
     * 小程序端：修改宠物档案（无需权限）
     */
    @PutMapping("/update")
    public AjaxResult updatePet(@RequestBody PetProfile petProfile)
    {
        if (petProfile.getPetId() == null)
        {
            return error("宠物ID不能为空");
        }
        petProfile.setUpdateBy("app");
        return toAjax(petProfileService.updatePetProfile(petProfile));
    }

    /**
     * 小程序端：删除宠物档案（无需权限）
     */
    @DeleteMapping("/delete/{petId}")
    public AjaxResult deletePet(@PathVariable("petId") Long petId)
    {
        return toAjax(petProfileService.deletePetProfileById(petId));
    }
}





