-- ============================================
-- 更新菜单表结构和数据，使其与前端路由格式匹配
-- 执行前请备份数据库！
-- ============================================

-- 说明：
-- 1. route_name 字段：路由名称，用于前端路由匹配
--    - 目录（M）：使用首字母大写的名称，如 System, Monitor, Tool
--    - 菜单（C）：使用首字母大写的名称，如 User, Role, Menu
-- 2. path 字段：路由路径
--    - 一级目录：使用完整路径，如 /system, /monitor, /tool
--    - 子菜单：使用相对路径，如 user, role, menu（会在父路径下拼接）
-- 3. component 字段：组件路径
--    - 目录（M）：设置为 NULL 或空字符串
--    - 菜单（C）：保持为 system/user/index 格式（后端会自动转换）

-- ============================================
-- 1. 更新 route_name 字段（路由名称）
-- ============================================

-- 更新一级目录的路由名称
UPDATE sys_menu SET route_name = 'System' WHERE menu_id = 1;  -- 系统管理
UPDATE sys_menu SET route_name = 'Monitor' WHERE menu_id = 2;  -- 系统监控
UPDATE sys_menu SET route_name = 'Tool' WHERE menu_id = 3;  -- 系统工具
UPDATE sys_menu SET route_name = 'Http://Pet.vip' WHERE menu_id = 4;  -- Pet官网（外链）

-- 更新系统管理下的菜单路由名称
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

-- 更新系统监控下的菜单路由名称
UPDATE sys_menu SET route_name = 'Online' WHERE menu_id = 109;  -- 在线用户
UPDATE sys_menu SET route_name = 'Job' WHERE menu_id = 110;  -- 定时任务
UPDATE sys_menu SET route_name = 'Druid' WHERE menu_id = 111;  -- 数据监控
UPDATE sys_menu SET route_name = 'Server' WHERE menu_id = 112;  -- 服务监控
UPDATE sys_menu SET route_name = 'Cache' WHERE menu_id = 113;  -- 缓存监控
UPDATE sys_menu SET route_name = 'CacheList' WHERE menu_id = 114;  -- 缓存列表

-- 更新系统工具下的菜单路由名称
UPDATE sys_menu SET route_name = 'Build' WHERE menu_id = 115;  -- 表单构建
UPDATE sys_menu SET route_name = 'Gen' WHERE menu_id = 116;  -- 代码生成
UPDATE sys_menu SET route_name = 'Swagger' WHERE menu_id = 117;  -- 系统接口

-- ============================================
-- 2. 更新 path 字段（路由路径）
-- ============================================
-- 说明：
-- - 一级目录（parent_id=0）：path 不需要以 / 开头，后端会自动添加
-- - 子菜单：path 使用相对路径，会在父路径下拼接

-- 一级目录的路径（后端会自动添加 / 前缀）
UPDATE sys_menu SET path = 'system' WHERE menu_id = 1;  -- 系统管理
UPDATE sys_menu SET path = 'monitor' WHERE menu_id = 2;  -- 系统监控
UPDATE sys_menu SET path = 'tool' WHERE menu_id = 3;  -- 系统工具
-- 外链路径保持不变，后端会处理
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
-- 3. 更新 component 字段（组件路径）
-- ============================================
-- 说明：
-- - 目录类型（M）：component 设置为 NULL 或空字符串，后端会使用 Layout
-- - 菜单类型（C）：component 保持为 'system/user/index' 格式，后端会自动转换

-- 目录类型的 component 设置为 NULL（后端会使用 Layout）
UPDATE sys_menu SET component = NULL WHERE menu_type = 'M' AND menu_id IN (1, 2, 3, 108);
UPDATE sys_menu SET component = NULL WHERE menu_id = 4;  -- 外链

-- 菜单类型的 component 保持原样（后端会自动转换为 view.xxx 格式）
-- 当前数据已经是正确的格式，如：system/user/index, monitor/online/index 等
-- 不需要修改，保持现有的 component 值即可

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
    menu_type
FROM sys_menu
WHERE menu_type IN ('M', 'C') 
  AND (route_name IS NULL OR route_name = '')
ORDER BY menu_id;

