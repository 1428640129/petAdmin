-- ============================================
-- 更新菜单表，直接存储前端期望的格式
-- 执行前请备份数据库！
-- ============================================
-- 
-- 说明：数据库直接存储前端期望的格式，后端不需要转换
-- 
-- 前端期望格式：
-- 1. route_name: 路由名称（首字母大写，不包含下划线）
--    - 一级路由：System, Monitor, Tool
--    - 子路由：User, Role, Menu
-- 2. path: 路由路径
--    - 一级路由：/system, /monitor, /tool（以 / 开头）
--    - 子路由：user, role, menu（相对路径，会在父路径下拼接）
-- 3. component: 组件路径（前端期望的格式）
--    - 有子路由的目录：layout.base
--    - 单级路由：layout.base$view.home
--    - 叶子路由：view.manage_user, view.manage_role 等
--    - 外链：layout.blank

-- ============================================
-- 1. 更新 route_name 字段（路由名称）
-- ============================================
-- 路由名称：首字母大写，不包含下划线

-- 一级目录的路由名称
UPDATE sys_menu SET route_name = 'System' WHERE menu_id = 1;  -- 系统管理
UPDATE sys_menu SET route_name = 'Monitor' WHERE menu_id = 2;  -- 系统监控
UPDATE sys_menu SET route_name = 'Tool' WHERE menu_id = 3;  -- 系统工具
UPDATE sys_menu SET route_name = 'Http://Pet.vip' WHERE menu_id = 4;  -- Pet官网（外链）

-- 系统管理下的菜单路由名称
UPDATE sys_menu SET route_name = 'User' WHERE menu_id = 100;  -- 用户管理
UPDATE sys_menu SET route_name = 'Role' WHERE menu_id = 101;  -- 角色管理
UPDATE sys_menu SET route_name = 'Menu' WHERE menu_id = 102;  -- 菜单管理
UPDATE sys_menu SET route_name = 'Dept' WHERE menu_id = 103;  -- 部门管理
UPDATE sys_menu SET route_name = 'Post' WHERE menu_id = 104;  -- 岗位管理
UPDATE sys_menu SET route_name = 'Dict' WHERE menu_id = 105;  -- 字典管理
UPDATE sys_menu SET route_name = 'Config' WHERE menu_id = 106;  -- 参数设置
UPDATE sys_menu SET route_name = 'Notice' WHERE menu_id = 107;  -- 通知公告
UPDATE sys_menu SET route_name = 'Log' WHERE menu_id = 108;  -- 日志管理（目录）
UPDATE sys_menu SET route_name = 'Operlog' WHERE menu_id = 500;  -- 操作日志
UPDATE sys_menu SET route_name = 'Logininfor' WHERE menu_id = 501;  -- 登录日志

-- 系统监控下的菜单路由名称
UPDATE sys_menu SET route_name = 'Online' WHERE menu_id = 109;  -- 在线用户
UPDATE sys_menu SET route_name = 'Job' WHERE menu_id = 110;  -- 定时任务
UPDATE sys_menu SET route_name = 'Druid' WHERE menu_id = 111;  -- 数据监控
UPDATE sys_menu SET route_name = 'Server' WHERE menu_id = 112;  -- 服务监控
UPDATE sys_menu SET route_name = 'Cache' WHERE menu_id = 113;  -- 缓存监控
UPDATE sys_menu SET route_name = 'CacheList' WHERE menu_id = 114;  -- 缓存列表

-- 系统工具下的菜单路由名称
UPDATE sys_menu SET route_name = 'Build' WHERE menu_id = 115;  -- 表单构建
UPDATE sys_menu SET route_name = 'Gen' WHERE menu_id = 116;  -- 代码生成
UPDATE sys_menu SET route_name = 'Swagger' WHERE menu_id = 117;  -- 系统接口

-- ============================================
-- 2. 更新 path 字段（路由路径）
-- ============================================
-- 一级目录：以 / 开头的完整路径
-- 子菜单：相对路径（会在父路径下拼接）

-- 一级目录的路径（完整路径，以 / 开头）
UPDATE sys_menu SET path = '/system' WHERE menu_id = 1;  -- 系统管理
UPDATE sys_menu SET path = '/monitor' WHERE menu_id = 2;  -- 系统监控
UPDATE sys_menu SET path = '/tool' WHERE menu_id = 3;  -- 系统工具
-- 外链路径保持不变
UPDATE sys_menu SET path = 'http://Pet.vip' WHERE menu_id = 4;  -- Pet官网（外链）

-- 系统管理下的菜单路径（相对路径）
UPDATE sys_menu SET path = 'user' WHERE menu_id = 100;  -- 用户管理
UPDATE sys_menu SET path = 'role' WHERE menu_id = 101;  -- 角色管理
UPDATE sys_menu SET path = 'menu' WHERE menu_id = 102;  -- 菜单管理
UPDATE sys_menu SET path = 'dept' WHERE menu_id = 103;  -- 部门管理
UPDATE sys_menu SET path = 'post' WHERE menu_id = 104;  -- 岗位管理
UPDATE sys_menu SET path = 'dict' WHERE menu_id = 105;  -- 字典管理
UPDATE sys_menu SET path = 'config' WHERE menu_id = 106;  -- 参数设置
UPDATE sys_menu SET path = 'notice' WHERE menu_id = 107;  -- 通知公告
UPDATE sys_menu SET path = 'log' WHERE menu_id = 108;  -- 日志管理（目录）
UPDATE sys_menu SET path = 'operlog' WHERE menu_id = 500;  -- 操作日志
UPDATE sys_menu SET path = 'logininfor' WHERE menu_id = 501;  -- 登录日志

-- 系统监控下的菜单路径（相对路径）
UPDATE sys_menu SET path = 'online' WHERE menu_id = 109;  -- 在线用户
UPDATE sys_menu SET path = 'job' WHERE menu_id = 110;  -- 定时任务
UPDATE sys_menu SET path = 'druid' WHERE menu_id = 111;  -- 数据监控
UPDATE sys_menu SET path = 'server' WHERE menu_id = 112;  -- 服务监控
UPDATE sys_menu SET path = 'cache' WHERE menu_id = 113;  -- 缓存监控
UPDATE sys_menu SET path = 'cacheList' WHERE menu_id = 114;  -- 缓存列表

-- 系统工具下的菜单路径（相对路径）
UPDATE sys_menu SET path = 'build' WHERE menu_id = 115;  -- 表单构建
UPDATE sys_menu SET path = 'gen' WHERE menu_id = 116;  -- 代码生成
UPDATE sys_menu SET path = 'swagger' WHERE menu_id = 117;  -- 系统接口

-- ============================================
-- 3. 更新 component 字段（组件路径 - 前端格式）
-- ============================================
-- 直接存储前端期望的组件格式：
-- - 有子路由的目录：layout.base
-- - 叶子路由：view.manage_user, view.manage_role 等（system 路径映射为 manage）
-- - 外链：layout.blank

-- 一级目录（有子路由）：使用 layout.base
UPDATE sys_menu SET component = 'layout.base' WHERE menu_type = 'M' AND menu_id IN (1, 2, 3, 108);

-- 外链：使用 layout.blank
UPDATE sys_menu SET component = 'layout.blank' WHERE menu_id = 4;

-- 系统管理下的菜单（叶子路由）：使用 view.manage_xxx 格式
UPDATE sys_menu SET component = 'view.manage_user' WHERE menu_id = 100;  -- 用户管理
UPDATE sys_menu SET component = 'view.manage_role' WHERE menu_id = 101;  -- 角色管理
UPDATE sys_menu SET component = 'view.manage_menu' WHERE menu_id = 102;  -- 菜单管理
UPDATE sys_menu SET component = 'view.manage_dept' WHERE menu_id = 103;  -- 部门管理
UPDATE sys_menu SET component = 'view.manage_post' WHERE menu_id = 104;  -- 岗位管理
UPDATE sys_menu SET component = 'view.manage_dict' WHERE menu_id = 105;  -- 字典管理
UPDATE sys_menu SET component = 'view.manage_config' WHERE menu_id = 106;  -- 参数设置
UPDATE sys_menu SET component = 'view.manage_notice' WHERE menu_id = 107;  -- 通知公告
UPDATE sys_menu SET component = 'view.monitor_operlog' WHERE menu_id = 500;  -- 操作日志
UPDATE sys_menu SET component = 'view.monitor_logininfor' WHERE menu_id = 501;  -- 登录日志

-- 系统监控下的菜单（叶子路由）：使用 view.monitor_xxx 格式
UPDATE sys_menu SET component = 'view.monitor_online' WHERE menu_id = 109;  -- 在线用户
UPDATE sys_menu SET component = 'view.monitor_job' WHERE menu_id = 110;  -- 定时任务
UPDATE sys_menu SET component = 'view.monitor_druid' WHERE menu_id = 111;  -- 数据监控
UPDATE sys_menu SET component = 'view.monitor_server' WHERE menu_id = 112;  -- 服务监控
UPDATE sys_menu SET component = 'view.monitor_cache' WHERE menu_id = 113;  -- 缓存监控
UPDATE sys_menu SET component = 'view.monitor_cache_list' WHERE menu_id = 114;  -- 缓存列表

-- 系统工具下的菜单（叶子路由）：使用 view.tool_xxx 格式
UPDATE sys_menu SET component = 'view.tool_build' WHERE menu_id = 115;  -- 表单构建
UPDATE sys_menu SET component = 'view.tool_gen' WHERE menu_id = 116;  -- 代码生成
UPDATE sys_menu SET component = 'view.tool_swagger' WHERE menu_id = 117;  -- 系统接口

-- ============================================
-- 4. 更新其他字段
-- ============================================

-- 确保外链菜单的 is_frame 为 0（0表示外链）
UPDATE sys_menu SET is_frame = 0 WHERE menu_id = 4;

-- 确保目录类型的 is_frame 为 1（1表示非外链）
UPDATE sys_menu SET is_frame = 1 WHERE menu_type = 'M' AND menu_id IN (1, 2, 3, 108);

-- 确保菜单类型的 is_frame 为 1（1表示非外链）
UPDATE sys_menu SET is_frame = 1 WHERE menu_type = 'C';

-- ============================================
-- 5. 验证更新结果
-- ============================================
SELECT 
    menu_id,
    menu_name,
    parent_id,
    path,
    component,
    route_name,
    menu_type,
    is_frame,
    visible,
    status
FROM sys_menu
WHERE menu_type IN ('M', 'C')
ORDER BY parent_id, order_num, menu_id;

-- ============================================
-- 6. 检查是否有遗漏的路由名称
-- ============================================
SELECT 
    menu_id,
    menu_name,
    route_name,
    path,
    component,
    menu_type
FROM sys_menu
WHERE menu_type IN ('M', 'C') 
  AND (route_name IS NULL OR route_name = '' OR component IS NULL OR component = '')
ORDER BY menu_id;










