-- ============================================
-- 宠物预约洗澡功能 - 菜单配置
-- 直接存储前端期望的格式
-- ============================================

-- ============================================
-- 洗浴服务管理模块菜单配置
-- 直接存储前端期望的格式
-- ============================================

-- 洗浴服务管理模块（一级目录）
INSERT INTO `sys_menu` VALUES 
(2000, '洗浴服务管理', 0, 5, '/bath', 'layout.base', '', 'Bath', 1, 0, 'M', '0', '0', '', 'shower', 'admin', NOW(), '', NULL, '洗浴服务管理目录');

-- 洗浴服务管理（服务信息管理）
INSERT INTO `sys_menu` VALUES 
(2001, '服务管理', 2000, 1, 'service', 'view.bath_service', '', 'Service', 1, 0, 'C', '0', '0', 'bath:service:list', 'service', 'admin', NOW(), '', NULL, '洗浴服务信息管理');

-- 预约管理
INSERT INTO `sys_menu` VALUES 
(2002, '预约管理', 2000, 2, 'appointment', 'view.bath_appointment', '', 'Appointment', 1, 0, 'C', '0', '0', 'bath:appointment:list', 'calendar', 'admin', NOW(), '', NULL, '预约信息管理');

-- 订单管理
INSERT INTO `sys_menu` VALUES 
(2003, '订单管理', 2000, 3, 'order', 'view.bath_order', '', 'Order', 1, 0, 'C', '0', '0', 'bath:order:list', 'shopping', 'admin', NOW(), '', NULL, '订单信息管理');

-- 支付管理
INSERT INTO `sys_menu` VALUES 
(2004, '支付管理', 2000, 4, 'payment', 'view.bath_payment', '', 'Payment', 1, 0, 'C', '0', '0', 'bath:payment:list', 'money', 'admin', NOW(), '', NULL, '支付记录管理');

-- 评价管理
INSERT INTO `sys_menu` VALUES 
(2005, '评价管理', 2000, 5, 'review', 'view.bath_review', '', 'Review', 1, 0, 'C', '0', '0', 'bath:review:list', 'star', 'admin', NOW(), '', NULL, '评价评论管理');

-- 通知管理
INSERT INTO `sys_menu` VALUES 
(2006, '通知管理', 2000, 6, 'notification', 'view.bath_notification', '', 'Notification', 1, 0, 'C', '0', '0', 'bath:notification:list', 'message', 'admin', NOW(), '', NULL, '通知记录管理');

-- 服务管理的按钮权限
INSERT INTO `sys_menu` VALUES 
(2100, '服务查询', 2001, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'bath:service:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES 
(2101, '服务新增', 2001, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'bath:service:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES 
(2102, '服务修改', 2001, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'bath:service:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES 
(2103, '服务删除', 2001, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'bath:service:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES 
(2104, '价格管理', 2001, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'bath:service:price', '#', 'admin', NOW(), '', NULL, '');

-- 预约管理的按钮权限
INSERT INTO `sys_menu` VALUES 
(2110, '预约查询', 2002, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'bath:appointment:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES 
(2111, '预约确认', 2002, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'bath:appointment:confirm', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES 
(2112, '预约取消', 2002, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'bath:appointment:cancel', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES 
(2113, '开始服务', 2002, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'bath:appointment:start', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES 
(2114, '完成服务', 2002, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'bath:appointment:complete', '#', 'admin', NOW(), '', NULL, '');

-- 订单管理的按钮权限
INSERT INTO `sys_menu` VALUES 
(2120, '订单查询', 2003, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'bath:order:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES 
(2121, '订单详情', 2003, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'bath:order:detail', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES 
(2122, '订单取消', 2003, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'bath:order:cancel', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES 
(2123, '订单退款', 2003, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'bath:order:refund', '#', 'admin', NOW(), '', NULL, '');

-- 支付管理的按钮权限
INSERT INTO `sys_menu` VALUES 
(2130, '支付查询', 2004, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'bath:payment:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES 
(2131, '退款处理', 2004, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'bath:payment:refund', '#', 'admin', NOW(), '', NULL, '');

-- 评价管理的按钮权限
INSERT INTO `sys_menu` VALUES 
(2140, '评价查询', 2005, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'bath:review:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES 
(2141, '评价回复', 2005, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'bath:review:reply', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES 
(2142, '评价删除', 2005, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'bath:review:remove', '#', 'admin', NOW(), '', NULL, '');

-- 通知管理的按钮权限
INSERT INTO `sys_menu` VALUES 
(2150, '通知查询', 2006, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'bath:notification:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES 
(2151, '通知发送', 2006, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'bath:notification:send', '#', 'admin', NOW(), '', NULL, '');

