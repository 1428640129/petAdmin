package com.pet.common.core.domain.vo;

import java.util.List;

/**
 * 用户路由VO（包含路由列表和首页路由）
 * 
 * @author Pet
 */
public class UserRouteVo
{
    /**
     * 路由列表
     */
    private List<FrontendRouteVo> routes;

    /**
     * 首页路由名称
     */
    private String home;

    public List<FrontendRouteVo> getRoutes()
    {
        return routes;
    }

    public void setRoutes(List<FrontendRouteVo> routes)
    {
        this.routes = routes;
    }

    public String getHome()
    {
        return home;
    }

    public void setHome(String home)
    {
        this.home = home;
    }
}
















