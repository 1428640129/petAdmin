package com.pet.web.controller.system;

import java.util.Date;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pet.common.constant.Constants;
import com.pet.common.core.domain.AjaxResult;
import com.pet.common.core.domain.entity.SysMenu;
import com.pet.common.core.domain.entity.SysUser;
import com.pet.common.core.domain.model.LoginUser;
import com.pet.common.core.text.Convert;
import com.pet.common.utils.DateUtils;
import com.pet.common.utils.SecurityUtils;
import com.pet.common.utils.StringUtils;
import com.pet.framework.security.context.AuthenticationContextHolder;
import com.pet.framework.web.service.SysLoginService;
import com.pet.framework.web.service.SysPermissionService;
import com.pet.framework.web.service.TokenService;
import com.pet.system.service.ISysConfigService;
import com.pet.system.service.ISysMenuService;
import com.pet.framework.web.service.SysRegisterService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import com.pet.framework.manager.AsyncManager;
import com.pet.framework.manager.factory.AsyncFactory;
import com.pet.common.constant.Constants;
import com.pet.common.utils.MessageUtils;
import com.pet.common.exception.ServiceException;
import com.pet.common.exception.user.UserPasswordNotMatchException;

import java.util.Map;
import java.util.HashMap;

/**
 * 认证接口（适配前端格式）
 * 
 * @author Pet
 */
@RestController
@RequestMapping("/auth")
public class AuthController
{
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private SysRegisterService registerService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private com.pet.framework.web.service.SmsCodeService smsCodeService;

    @Autowired
    private com.pet.system.service.ISysUserService userService;

    @Autowired
    private com.pet.framework.web.service.UserDetailsServiceImpl userDetailsService;

    /**
     * 登录方法（适配前端格式，不验证验证码）
     * 
     * @param loginData 登录信息 {userName, password}
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody Map<String, String> loginData)
    {
        String userName = loginData.get("userName");
        String password = loginData.get("password");
        
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password))
        {
            return AjaxResult.error("用户名和密码不能为空");
        }
        
        // 登录前置校验（检查用户名密码长度、IP黑名单等，但不验证验证码）
        try
        {
            loginService.loginPreCheck(userName, password);
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
        
        // 直接进行用户验证，跳过验证码验证
        Authentication authentication = null;
        try
        {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userName, password);
            AuthenticationContextHolder.setContext(authenticationToken);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(authenticationToken);
        }
        catch (Exception e)
        {
            if (e instanceof BadCredentialsException)
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(userName, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                return AjaxResult.error("用户名或密码错误");
            }
            else
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(userName, Constants.LOGIN_FAIL, e.getMessage()));
                return AjaxResult.error(e.getMessage());
            }
        }
        finally
        {
            AuthenticationContextHolder.clearContext();
        }
        
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(userName, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        // 记录登录信息
        loginService.recordLoginInfo(loginUser.getUserId());
        // 生成token
        String token = tokenService.createToken(loginUser);
        
        // 构建返回数据，适配前端格式（需要放在data字段中）
        Map<String, String> resultData = new HashMap<>();
        resultData.put("token", token);
        resultData.put("refreshToken", ""); // 暂时返回空
        
        return AjaxResult.success(resultData);
    }

    /**
     * 获取用户信息（适配前端格式）
     * 
     * @return 用户信息
     */
    @GetMapping("/getUserInfo")
    public AjaxResult getUserInfo()
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        
        if (!loginUser.getPermissions().equals(permissions))
        {
            loginUser.setPermissions(permissions);
            tokenService.refreshToken(loginUser);
        }
        
        // 适配前端格式，将数据放在data字段中
        Map<String, Object> userInfoData = new HashMap<>();
        userInfoData.put("userId", user.getUserId().toString());
        userInfoData.put("userName", user.getUserName());
        userInfoData.put("roles", roles.toArray(new String[0]));
        userInfoData.put("buttons", permissions.toArray(new String[0]));
        
        return AjaxResult.success(userInfoData);
    }

    /**
     * 注册方法（适配前端格式，使用短信验证码）
     * 
     * @param registerData 注册信息 {phone, code, password, confirmPassword}
     * @return 结果
     */
    @PostMapping("/register")
    public AjaxResult register(@RequestBody Map<String, String> registerData)
    {
        // 注册功能已默认开启，不再检查配置项
        
        String phone = registerData.get("phone");
        String code = registerData.get("code");
        String password = registerData.get("password");
        String confirmPassword = registerData.get("confirmPassword");
        
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(password))
        {
            return AjaxResult.error("手机号和密码不能为空");
        }
        
        if (StringUtils.isEmpty(code))
        {
            return AjaxResult.error("验证码不能为空");
        }
        
        if (!password.equals(confirmPassword))
        {
            return AjaxResult.error("两次输入的密码不一致");
        }
        
        // 验证短信验证码
        if (!smsCodeService.validateSmsCode(phone, code))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(phone, Constants.REGISTER, "短信验证码错误或已过期"));
            return AjaxResult.error("验证码错误或已过期");
        }
        
        // 检查手机号是否已注册
        SysUser existUser = userService.selectUserByPhonenumber(phone);
        if (existUser != null)
        {
            return AjaxResult.error("该手机号已被注册，请直接登录");
        }
        
        // 创建注册对象
        com.pet.common.core.domain.model.RegisterBody registerBody = new com.pet.common.core.domain.model.RegisterBody();
        registerBody.setUsername(phone); // 使用手机号作为用户名
        registerBody.setPassword(password);
        registerBody.setCode(code); // 这里不再使用code，但保留字段兼容性
        
        // 调用注册服务（跳过图片验证码验证，因为已经验证了短信验证码）
        String msg = registerService.registerBySmsCode(registerBody, phone);
        return StringUtils.isEmpty(msg) ? AjaxResult.success("注册成功") : AjaxResult.error(msg);
    }

    /**
     * 发送短信验证码
     * 
     * @param requestData 请求数据 {phone}
     * @return 结果
     */
    @PostMapping("/sendSmsCode")
    public AjaxResult sendSmsCode(@RequestBody Map<String, String> requestData)
    {
        String phone = requestData.get("phone");
        
        if (StringUtils.isEmpty(phone))
        {
            return AjaxResult.error("手机号不能为空");
        }

        try
        {
            smsCodeService.sendSmsCode(phone);
            return AjaxResult.success("验证码发送成功");
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getMessage());
        }
        catch (Exception e)
        {
            return AjaxResult.error("验证码发送失败：" + e.getMessage());
        }
    }

    /**
     * 短信验证码登录
     * 
     * @param loginData 登录信息 {phone, code}
     * @return 结果
     */
    @PostMapping("/loginBySmsCode")
    public AjaxResult loginBySmsCode(@RequestBody Map<String, String> loginData)
    {
        String phone = loginData.get("phone");
        String code = loginData.get("code");
        
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code))
        {
            return AjaxResult.error("手机号和验证码不能为空");
        }

        // 验证短信验证码
        if (!smsCodeService.validateSmsCode(phone, code))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(phone, Constants.LOGIN_FAIL, "短信验证码错误或已过期"));
            return AjaxResult.error("验证码错误或已过期");
        }

        // 根据手机号查找用户（假设手机号存储在phonenumber字段）
        SysUser user = userService.selectUserByPhonenumber(phone);
        if (user == null)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(phone, Constants.LOGIN_FAIL, "用户不存在"));
            return AjaxResult.error("该手机号未注册，请先注册");
        }

        // 检查用户状态
        if ("1".equals(user.getStatus()))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(phone, Constants.LOGIN_FAIL, "账号已被停用"));
            return AjaxResult.error("账号已被停用，请联系管理员");
        }

        // 使用UserDetailsService创建LoginUser（这样可以正确初始化所有字段）
        LoginUser loginUser = (LoginUser) userDetailsService.createLoginUser(user);

        // 记录登录信息
        loginService.recordLoginInfo(user.getUserId());
        
        // 生成token
        String token = tokenService.createToken(loginUser);
        
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(user.getUserName(), Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        
        // 构建返回数据，适配前端格式（需要放在data字段中）
        Map<String, String> resultData = new HashMap<>();
        resultData.put("token", token);
        resultData.put("refreshToken", ""); // 暂时返回空
        
        return AjaxResult.success(resultData);
    }

    /**
     * 刷新Token
     * 
     * @param refreshData 刷新Token信息 {refreshToken}
     * @return 结果
     */
    @PostMapping("/refreshToken")
    public AjaxResult refreshToken(@RequestBody Map<String, String> refreshData)
    {
        // 这里可以实现刷新Token的逻辑
        // 暂时返回错误，表示不支持刷新Token
        return AjaxResult.error("暂不支持刷新Token");
    }
}

