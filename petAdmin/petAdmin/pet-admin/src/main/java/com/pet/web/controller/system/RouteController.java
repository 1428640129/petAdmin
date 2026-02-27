package com.pet.web.controller.system;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pet.common.core.domain.AjaxResult;
import com.pet.common.core.domain.entity.SysMenu;
import com.pet.common.core.domain.vo.FrontendRouteVo;
import com.pet.common.core.domain.vo.RouteMetaVo;
import com.pet.common.core.domain.vo.UserRouteVo;
import com.pet.common.utils.SecurityUtils;
import com.pet.common.utils.StringUtils;
import com.pet.system.domain.vo.MetaVo;
import com.pet.system.domain.vo.RouterVo;
import com.pet.system.service.ISysMenuService;

/**
 * 路由接口（适配前端动态路由格式）
 * 
 * @author Pet
 */
@RestController
@RequestMapping("/route")
public class RouteController
{
    @Autowired
    private ISysMenuService menuService;

    /**
     * 获取用户菜单（适配前端菜单格式）
     * 
     * @return 菜单信息
     */
    @GetMapping("/getMenus")
    public AjaxResult getMenus()
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        List<RouterVo> routerVos = menuService.buildMenus(menus);
        
        // 转换为前端期望的菜单格式
        List<com.pet.common.core.domain.vo.FrontendMenuVo> frontendMenus = convertToFrontendMenus(routerVos, menus);
        
        return AjaxResult.success(frontendMenus);
    }

    /**
     * 获取用户路由（适配前端动态路由格式）
     * 
     * @return 路由信息
     */
    @GetMapping("/getUserRoutes")
    public AjaxResult getUserRoutes()
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        List<RouterVo> routerVos = menuService.buildMenus(menus);
        
        // 转换为前端期望的格式
        List<FrontendRouteVo> frontendRoutes = convertToFrontendRoutes(routerVos, menus);
        
        // 获取首页路由
        // 优先查找名为 "home" 的路由，如果没有则取第一个路由的第一个子路由
        String home = "home"; // 默认使用 home
        boolean foundHome = false;
        
        // 先查找是否有名为 "home" 的路由
        for (FrontendRouteVo route : frontendRoutes)
        {
            if ("home".equals(route.getName()))
            {
                home = "home";
                foundHome = true;
                break;
            }
            // 在子路由中查找
            if (route.getChildren() != null && !route.getChildren().isEmpty())
            {
                for (FrontendRouteVo child : route.getChildren())
                {
                    if ("home".equals(child.getName()))
                    {
                        home = "home";
                        foundHome = true;
                        break;
                    }
                }
                if (foundHome)
                {
                    break;
                }
            }
        }
        
        // 如果没有找到 home 路由，使用第一个路由的第一个子路由
        if (!foundHome && !frontendRoutes.isEmpty())
        {
            FrontendRouteVo firstRoute = frontendRoutes.get(0);
            if (firstRoute.getChildren() != null && !firstRoute.getChildren().isEmpty())
            {
                home = firstRoute.getChildren().get(0).getName();
            }
            else
            {
                home = firstRoute.getName();
            }
        }
        
        UserRouteVo userRouteVo = new UserRouteVo();
        userRouteVo.setRoutes(frontendRoutes);
        userRouteVo.setHome(home);
        
        return AjaxResult.success(userRouteVo);
    }

    /**
     * 将RouterVo转换为FrontendRouteVo
     */
    private List<FrontendRouteVo> convertToFrontendRoutes(List<RouterVo> routerVos, List<SysMenu> menus)
    {
        List<FrontendRouteVo> result = new ArrayList<>();
        
        // 创建菜单ID到菜单的映射，用于获取orderNum等信息
        java.util.Map<Long, SysMenu> menuMap = new java.util.HashMap<>();
        buildMenuMap(menus, menuMap);
        
        for (RouterVo routerVo : routerVos)
        {
            FrontendRouteVo frontendRoute = convertRouterVoToFrontendRoute(routerVo, menuMap);
            if (frontendRoute != null)
            {
                result.add(frontendRoute);
            }
        }
        
        return result;
    }

    /**
     * 递归构建菜单映射
     */
    private void buildMenuMap(List<SysMenu> menus, java.util.Map<Long, SysMenu> menuMap)
    {
        for (SysMenu menu : menus)
        {
            menuMap.put(menu.getMenuId(), menu);
            if (menu.getChildren() != null && !menu.getChildren().isEmpty())
            {
                buildMenuMap(menu.getChildren(), menuMap);
            }
        }
    }

    /**
     * 将单个RouterVo转换为FrontendRouteVo
     * 直接使用数据库中的值，不做转换
     */
    private FrontendRouteVo convertRouterVoToFrontendRoute(RouterVo routerVo, java.util.Map<Long, SysMenu> menuMap)
    {
        FrontendRouteVo frontendRoute = new FrontendRouteVo();
        
        // 直接使用数据库中的路由名称
        frontendRoute.setName(routerVo.getName());
        
        // 直接使用数据库中的路径（数据库已存储前端期望的格式）
        String routePath = routerVo.getPath();
        boolean isExternalLink = routerVo.getMeta() != null && 
                                 StringUtils.isNotEmpty(routerVo.getMeta().getLink()) &&
                                 (routerVo.getMeta().getLink().startsWith("http://") || 
                                  routerVo.getMeta().getLink().startsWith("https://"));
        
        // 外链路径需要特殊处理
        if (isExternalLink && (routePath == null || !routePath.startsWith("/")))
        {
            String safeName = routerVo.getName().replaceAll("[^a-zA-Z0-9-_]", "-").toLowerCase();
            routePath = "/" + safeName;
        }
        // 其他路径直接使用（数据库已存储正确格式）
        else if (routePath == null || routePath.isEmpty())
        {
            // 如果路径为空，使用路由名称
            routePath = "/" + routerVo.getName().toLowerCase();
        }
        frontendRoute.setPath(routePath);
        
        // 直接使用数据库中的组件路径（数据库已存储前端期望的格式）
        String component = routerVo.getComponent();
        
        // 如果组件为空，根据是否有子路由设置默认值
        if (StringUtils.isEmpty(component))
        {
            boolean hasChildren = routerVo.getChildren() != null && !routerVo.getChildren().isEmpty();
            if (isExternalLink)
            {
                component = "layout.blank";
            }
            else if (hasChildren)
            {
                component = "layout.base";
            }
            else
            {
                // 单级路由，需要构造 layout.base$view.xxx 格式
                String viewKey = convertToViewKey(null, routerVo.getPath(), routerVo.getName());
                component = "layout.base$view." + viewKey;
            }
        }
        // 如果组件是 Layout 或 ParentView，转换为 layout.base
        else if ("Layout".equals(component) || "ParentView".equals(component))
        {
            component = "layout.base";
        }
        // 其他情况直接使用数据库中的值（已经是前端期望的格式）
        
        frontendRoute.setComponent(component);
        
        // 设置元数据
        if (routerVo.getMeta() != null)
        {
            RouteMetaVo meta = new RouteMetaVo();
            meta.setTitle(routerVo.getMeta().getTitle());
            meta.setIcon(routerVo.getMeta().getIcon());
            meta.setKeepAlive(!routerVo.getMeta().isNoCache());
            meta.setHideInMenu(routerVo.getHidden());
            meta.setHref(routerVo.getMeta().getLink());
            meta.setConstant(false); // 动态路由都是非固定的
            
            // 设置i18nKey：格式为 route.路由名称（小写）
            // 例如：bath_order -> route.bath_order
            if (StringUtils.isNotEmpty(routerVo.getName()))
            {
                String i18nKey = "route." + routerVo.getName().toLowerCase();
                meta.setI18nKey(i18nKey);
            }
            
            // 从菜单中获取排序信息（这里简化处理，实际可以通过路由名称匹配菜单）
            meta.setOrder(0);
            
            // 角色信息暂时为空，如果需要可以从菜单权限中获取
            meta.setRoles(null);
            
            frontendRoute.setMeta(meta);
        }
        
        // 递归处理子路由
        if (routerVo.getChildren() != null && !routerVo.getChildren().isEmpty())
        {
            List<FrontendRouteVo> children = new ArrayList<>();
            for (RouterVo child : routerVo.getChildren())
            {
                FrontendRouteVo childRoute = convertRouterVoToFrontendRoute(child, menuMap);
                if (childRoute != null)
                {
                    children.add(childRoute);
                }
            }
            frontendRoute.setChildren(children);
        }
        
        // 生成ID（使用路由名称作为ID）
        frontendRoute.setId(routerVo.getName());
        
        return frontendRoute;
    }
    
    /**
     * 将组件路径转换为前端视图键名格式
     * 前端已有的视图键名格式：manage_user, manage_role, manage_menu
     * 需要将 system 路径映射为 manage 视图键名
     */
    private String convertToViewKey(String component, String path, String name)
    {
        String viewKey = null;
        
        if (StringUtils.isNotEmpty(component))
        {
            // 将组件路径转换为视图键名格式
            // 例如：system/user/index -> system_user_index
            //      system-user-index -> system_user_index
            viewKey = component.replace("/", "_")
                              .replace("-", "_")
                              .replace(".vue", "")
                              .toLowerCase();
        }
        else if (StringUtils.isNotEmpty(path))
        {
            // 从路径中提取：/system/user -> system_user
            viewKey = path.replaceFirst("^/", "")
                         .replace("/", "_")
                         .replace("-", "_")
                         .toLowerCase();
        }
        else if (StringUtils.isNotEmpty(name))
        {
            // 使用路由名称，转换为小写
            viewKey = name.toLowerCase();
        }
        
        if (StringUtils.isEmpty(viewKey))
        {
            return "home";
        }
        
        // 将 system_xxx 映射为 manage_xxx（匹配前端已有的视图键名）
        if (viewKey.startsWith("system_"))
        {
            viewKey = "manage" + viewKey.substring(6); // 将 "system" 替换为 "manage"
            // 去掉 _index 后缀（如果存在），因为前端视图键名是 manage_user 而不是 manage_user_index
            if (viewKey.endsWith("_index"))
            {
                viewKey = viewKey.substring(0, viewKey.length() - 6);
            }
        }
        // 如果路径是 /system/xxx，也映射为 manage_xxx
        else if (StringUtils.isNotEmpty(path) && path.startsWith("/system/"))
        {
            String pathKey = path.replaceFirst("^/system/", "")
                                 .replace("/", "_")
                                 .replace("-", "_")
                                 .toLowerCase();
            viewKey = "manage_" + pathKey;
            // 去掉 _index 后缀（如果存在）
            if (viewKey.endsWith("_index"))
            {
                viewKey = viewKey.substring(0, viewKey.length() - 6);
            }
        }
        // 如果视图键名以 _index 结尾，去掉后缀（前端视图键名不包含 _index）
        else if (viewKey.endsWith("_index"))
        {
            viewKey = viewKey.substring(0, viewKey.length() - 6);
        }
        
        return viewKey;
    }

    /**
     * 将RouterVo转换为FrontendMenuVo（菜单格式）
     */
    private List<com.pet.common.core.domain.vo.FrontendMenuVo> convertToFrontendMenus(
            List<RouterVo> routerVos, List<SysMenu> menus)
    {
        List<com.pet.common.core.domain.vo.FrontendMenuVo> result = new ArrayList<>();
        
        // 创建菜单ID到菜单的映射
        java.util.Map<Long, SysMenu> menuMap = new java.util.HashMap<>();
        buildMenuMap(menus, menuMap);
        
        for (RouterVo routerVo : routerVos)
        {
            // 只处理不在菜单中隐藏的路由
            if (!routerVo.getHidden())
            {
                com.pet.common.core.domain.vo.FrontendMenuVo menu = convertRouterVoToFrontendMenu(routerVo, menuMap);
                if (menu != null)
                {
                    result.add(menu);
                }
            }
        }
        
        return result;
    }

    /**
     * 将单个RouterVo转换为FrontendMenuVo
     */
    private com.pet.common.core.domain.vo.FrontendMenuVo convertRouterVoToFrontendMenu(
            RouterVo routerVo, java.util.Map<Long, SysMenu> menuMap)
    {
        com.pet.common.core.domain.vo.FrontendMenuVo menu = new com.pet.common.core.domain.vo.FrontendMenuVo();
        
        // 设置基本信息
        menu.setKey(routerVo.getName());
        menu.setRouteKey(routerVo.getName());
        menu.setRoutePath(routerVo.getPath());
        
        // 设置i18nKey：格式为 route.路由名称（小写）
        // 例如：Bath -> route.bath, Service -> route.service
        if (StringUtils.isNotEmpty(routerVo.getName()))
        {
            String i18nKey = "route." + routerVo.getName().toLowerCase();
            menu.setI18nKey(i18nKey);
        }
        
        // 设置菜单标签和图标
        if (routerVo.getMeta() != null)
        {
            menu.setLabel(routerVo.getMeta().getTitle());
            menu.setIcon(routerVo.getMeta().getIcon());
            // 如果有外链，可以设置localIcon
            if (StringUtils.isNotEmpty(routerVo.getMeta().getLink()))
            {
                menu.setLocalIcon("link");
            }
        }
        
        // 递归处理子菜单
        if (routerVo.getChildren() != null && !routerVo.getChildren().isEmpty())
        {
            List<com.pet.common.core.domain.vo.FrontendMenuVo> children = new ArrayList<>();
            for (RouterVo child : routerVo.getChildren())
            {
                // 只处理不在菜单中隐藏的子路由
                if (!child.getHidden())
                {
                    com.pet.common.core.domain.vo.FrontendMenuVo childMenu = convertRouterVoToFrontendMenu(child, menuMap);
                    if (childMenu != null)
                    {
                        children.add(childMenu);
                    }
                }
            }
            if (!children.isEmpty())
            {
                menu.setChildren(children);
            }
        }
        
        return menu;
    }
}




