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
import org.springframework.web.multipart.MultipartFile;
import com.pet.common.annotation.Log;
import com.pet.common.config.PetConfig;
import com.pet.common.utils.file.FileUploadUtils;
import com.pet.common.utils.file.MimeTypeUtils;
import com.pet.framework.config.ServerConfig;
import com.pet.common.core.controller.BaseController;
import com.pet.common.core.domain.AjaxResult;
import com.pet.common.core.page.TableDataInfo;
import com.pet.common.enums.BusinessType;
import com.pet.common.utils.poi.ExcelUtil;
import com.pet.common.utils.SecurityUtils;
import com.pet.system.domain.PetBathUser;
import com.pet.business.service.IPetBathUserService;

/**
 * 前台用户Controller
 * 
 * @author Pet
 */
@RestController
@RequestMapping("/bath/user")
public class PetBathUserController extends BaseController
{
    @Autowired
    private IPetBathUserService userService;

    @Autowired
    private ServerConfig serverConfig;

    /**
     * 查询前台用户列表
     */
    @PreAuthorize("@ss.hasPermi('bath:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(PetBathUser user)
    {
        startPage();
        List<PetBathUser> list = userService.selectPetBathUserList(user);
        return getDataTable(list);
    }

    /**
     * 导出前台用户列表
     */
    @PreAuthorize("@ss.hasPermi('bath:user:export')")
    @Log(title = "前台用户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PetBathUser user)
    {
        List<PetBathUser> list = userService.selectPetBathUserList(user);
        ExcelUtil<PetBathUser> util = new ExcelUtil<PetBathUser>(PetBathUser.class);
        util.exportExcel(response, list, "前台用户数据");
    }

    /**
     * 获取前台用户详细信息
     */
    @PreAuthorize("@ss.hasPermi('bath:user:query')")
    @GetMapping(value = "/{userId}")
    public AjaxResult getInfo(@PathVariable("userId") Long userId)
    {
        return success(userService.selectPetBathUserById(userId));
    }

    /**
     * 新增前台用户
     */
    @PreAuthorize("@ss.hasPermi('bath:user:add')")
    @Log(title = "前台用户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PetBathUser user)
    {
        return toAjax(userService.insertPetBathUser(user));
    }

    /**
     * 修改前台用户
     */
    @PreAuthorize("@ss.hasPermi('bath:user:edit')")
    @Log(title = "前台用户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PetBathUser user)
    {
        return toAjax(userService.updatePetBathUser(user));
    }

    /**
     * 删除前台用户
     */
    @PreAuthorize("@ss.hasPermi('bath:user:remove')")
    @Log(title = "前台用户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        return toAjax(userService.deletePetBathUserByIds(userIds));
    }

    /**
     * 前台注册接口（无需权限，小程序使用）
     * userType: 0=顾客,1=商家
     */
    @PostMapping("/register")
    public AjaxResult register(@RequestBody PetBathUser user)
    {
        // 校验
        if (user.getUserName() == null || user.getUserName().trim().isEmpty())
        {
            return error("账号不能为空");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6)
        {
            return error("密码至少6位");
        }
        // 检查用户名是否已存在
        PetBathUser existUser = userService.selectPetBathUserByUserName(user.getUserName());
        if (existUser != null)
        {
            return error("账号已存在");
        }
        // 检查手机号是否已存在
        if (user.getPhone() != null && !user.getPhone().trim().isEmpty())
        {
            PetBathUser existPhone = new PetBathUser();
            existPhone.setPhone(user.getPhone());
            List<PetBathUser> phoneUsers = userService.selectPetBathUserList(existPhone);
            if (phoneUsers != null && !phoneUsers.isEmpty())
            {
                return error("手机号已被注册");
            }
        }
        // 默认用户类型为顾客
        if (user.getUserType() == null || user.getUserType().isEmpty())
        {
            user.setUserType("0");
        }
        int rows = userService.insertPetBathUser(user);
        return rows > 0 ? success() : error("注册失败");
    }

    /**
     * 前台登录接口（无需权限，小程序使用）
     * 返回用户信息，不返回token（如需token，可集成JWT）
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody PetBathUser loginBody)
    {
        if (loginBody.getUserName() == null || loginBody.getUserName().trim().isEmpty())
        {
            return error("账号不能为空");
        }
        if (loginBody.getPassword() == null || loginBody.getPassword().isEmpty())
        {
            return error("密码不能为空");
        }
        
        PetBathUser user = userService.selectPetBathUserByUserName(loginBody.getUserName());
        if (user == null)
        {
            return error("账号不存在");
        }
        
        if (!SecurityUtils.matchesPassword(loginBody.getPassword(), user.getPassword()))
        {
            return error("账号或密码错误");
        }
        
        if ("1".equals(user.getStatus()))
        {
            return error("账号已停用");
        }
        
        // 清空密码，返回用户信息
        user.setPassword(null);
        return success(user);
    }

    /**
     * 小程序端：上传头像（无需权限，仅允许图片）
     * 返回完整头像URL，用于保存到用户profile
     */
    @PostMapping("/uploadAvatar")
    public AjaxResult uploadAvatar(@RequestParam("file") MultipartFile file)
    {
        if (file == null || file.isEmpty())
        {
            return error("请选择要上传的图片");
        }
        try
        {
            String fileName = FileUploadUtils.upload(PetConfig.getAvatarPath(), file, MimeTypeUtils.IMAGE_EXTENSION, true);
            String url = serverConfig.getUrl() + fileName;
            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", url);
            return ajax;
        }
        catch (Exception e)
        {
            return error(e.getMessage() != null ? e.getMessage() : "上传失败");
        }
    }

    /**
     * 小程序端：获取个人信息（无需权限）
     */
    @GetMapping("/profile")
    public AjaxResult getProfile(Long userId)
    {
        if (userId == null)
        {
            return error("用户ID不能为空");
        }
        PetBathUser user = userService.selectPetBathUserById(userId);
        if (user == null)
        {
            return error("用户不存在");
        }
        user.setPassword(null);
        return success(user);
    }

    /**
     * 小程序端：修改个人信息（无需权限，需传userId）
     */
    @PutMapping("/profile")
    public AjaxResult updateProfile(@RequestBody PetBathUser user)
    {
        if (user.getUserId() == null)
        {
            return error("用户ID不能为空");
        }
        PetBathUser existUser = userService.selectPetBathUserById(user.getUserId());
        if (existUser == null)
        {
            return error("用户不存在");
        }
        // 检查手机号唯一性（排除自己）
        if (user.getPhone() != null && !user.getPhone().trim().isEmpty())
        {
            PetBathUser query = new PetBathUser();
            query.setPhone(user.getPhone());
            List<PetBathUser> list = userService.selectPetBathUserList(query);
            if (list != null && !list.isEmpty() && !list.get(0).getUserId().equals(user.getUserId()))
            {
                return error("手机号已被其他用户使用");
            }
        }
        // 只更新允许的字段，不更新密码
        existUser.setNickName(user.getNickName() != null ? user.getNickName() : existUser.getNickName());
        existUser.setPhone(user.getPhone() != null ? user.getPhone() : existUser.getPhone());
        existUser.setAvatar(user.getAvatar() != null ? user.getAvatar() : existUser.getAvatar());
        existUser.setUpdateBy("app");
        user.setPassword(null);
        return toAjax(userService.updatePetBathUser(existUser));
    }

    /**
     * 小程序端：修改密码（无需权限，需传userId、oldPassword、newPassword）
     */
    @PutMapping("/password")
    public AjaxResult updatePassword(@RequestBody java.util.Map<String, String> body)
    {
        String userIdStr = body.get("userId");
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (userIdStr == null || userIdStr.isEmpty())
        {
            return error("用户ID不能为空");
        }
        if (oldPassword == null || oldPassword.isEmpty())
        {
            return error("原密码不能为空");
        }
        if (newPassword == null || newPassword.length() < 6)
        {
            return error("新密码至少6位");
        }
        Long userId = Long.parseLong(userIdStr);
        PetBathUser user = userService.selectPetBathUserById(userId);
        if (user == null)
        {
            return error("用户不存在");
        }
        if (!SecurityUtils.matchesPassword(oldPassword, user.getPassword()))
        {
            return error("原密码错误");
        }
        user.setPassword(SecurityUtils.encryptPassword(newPassword));
        user.setUpdateBy("app");
        return toAjax(userService.updatePetBathUser(user));
    }

    /**
     * 临时接口：生成BCrypt密码哈希（用于SQL初始化）
     * 使用后可以删除此方法
     */
    @PostMapping("/generatePassword")
    public AjaxResult generatePassword(@RequestBody(required = false) String password)
    {
        if (password == null || password.isEmpty())
        {
            password = "123456"; // 默认密码
        }
        String encoded = SecurityUtils.encryptPassword(password);
        return success("密码: " + password + "\nBCrypt哈希: " + encoded);
    }
}

