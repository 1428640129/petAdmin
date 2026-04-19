package com.pet.common.core.domain.vo;

import java.util.List;

/**
 * 前端路由元数据VO
 * 
 * @author Pet
 */
public class RouteMetaVo
{
    /**
     * 路由标题
     */
    private String title;

    /**
     * 路由图标
     */
    private String icon;

    /**
     * 是否缓存
     */
    private Boolean keepAlive;

    /**
     * 排序
     */
    private Integer order;

    /**
     * 角色列表
     */
    private List<String> roles;

    /**
     * 是否固定路由
     */
    private Boolean constant;

    /**
     * 是否在菜单中隐藏
     */
    private Boolean hideInMenu;

    /**
     * 外链地址
     */
    private String href;

    /**
     * 国际化键名
     */
    private String i18nKey;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    public Boolean getKeepAlive()
    {
        return keepAlive;
    }

    public void setKeepAlive(Boolean keepAlive)
    {
        this.keepAlive = keepAlive;
    }

    public Integer getOrder()
    {
        return order;
    }

    public void setOrder(Integer order)
    {
        this.order = order;
    }

    public List<String> getRoles()
    {
        return roles;
    }

    public void setRoles(List<String> roles)
    {
        this.roles = roles;
    }

    public Boolean getConstant()
    {
        return constant;
    }

    public void setConstant(Boolean constant)
    {
        this.constant = constant;
    }

    public Boolean getHideInMenu()
    {
        return hideInMenu;
    }

    public void setHideInMenu(Boolean hideInMenu)
    {
        this.hideInMenu = hideInMenu;
    }

    public String getHref()
    {
        return href;
    }

    public void setHref(String href)
    {
        this.href = href;
    }

    public String getI18nKey()
    {
        return i18nKey;
    }

    public void setI18nKey(String i18nKey)
    {
        this.i18nKey = i18nKey;
    }
}
















