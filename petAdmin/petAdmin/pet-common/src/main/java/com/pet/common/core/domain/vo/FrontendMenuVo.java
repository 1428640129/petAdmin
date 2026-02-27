package com.pet.common.core.domain.vo;

import java.util.List;

/**
 * 前端菜单VO
 * 
 * @author Pet
 */
public class FrontendMenuVo
{
    /**
     * 菜单key（路由名称）
     */
    private String key;

    /**
     * 菜单标签
     */
    private String label;

    /**
     * 国际化key
     */
    private String i18nKey;

    /**
     * 路由key
     */
    private String routeKey;

    /**
     * 路由路径
     */
    private String routePath;

    /**
     * 图标名称
     */
    private String icon;

    /**
     * 本地图标
     */
    private String localIcon;

    /**
     * 子菜单
     */
    private List<FrontendMenuVo> children;

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getI18nKey()
    {
        return i18nKey;
    }

    public void setI18nKey(String i18nKey)
    {
        this.i18nKey = i18nKey;
    }

    public String getRouteKey()
    {
        return routeKey;
    }

    public void setRouteKey(String routeKey)
    {
        this.routeKey = routeKey;
    }

    public String getRoutePath()
    {
        return routePath;
    }

    public void setRoutePath(String routePath)
    {
        this.routePath = routePath;
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    public String getLocalIcon()
    {
        return localIcon;
    }

    public void setLocalIcon(String localIcon)
    {
        this.localIcon = localIcon;
    }

    public List<FrontendMenuVo> getChildren()
    {
        return children;
    }

    public void setChildren(List<FrontendMenuVo> children)
    {
        this.children = children;
    }
}

