package com.pet.common.core.domain.vo;

import java.util.List;

/**
 * 前端路由VO
 * 
 * @author Pet
 */
public class FrontendRouteVo
{
    /**
     * 路由ID
     */
    private String id;

    /**
     * 路由名称
     */
    private String name;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 路由元数据
     */
    private RouteMetaVo meta;

    /**
     * 子路由
     */
    private List<FrontendRouteVo> children;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getComponent()
    {
        return component;
    }

    public void setComponent(String component)
    {
        this.component = component;
    }

    public RouteMetaVo getMeta()
    {
        return meta;
    }

    public void setMeta(RouteMetaVo meta)
    {
        this.meta = meta;
    }

    public List<FrontendRouteVo> getChildren()
    {
        return children;
    }

    public void setChildren(List<FrontendRouteVo> children)
    {
        this.children = children;
    }
}










