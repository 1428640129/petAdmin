-- ============================================
-- 宠物预约洗澡功能 - 数据库表结构
-- ============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 洗浴服务表
-- ----------------------------
DROP TABLE IF EXISTS `pet_bath_service`;
CREATE TABLE `pet_bath_service` (
  `service_id` bigint NOT NULL AUTO_INCREMENT COMMENT '服务ID',
  `service_name` varchar(100) NOT NULL COMMENT '服务名称',
  `service_desc` varchar(500) DEFAULT NULL COMMENT '服务描述',
  `service_type` varchar(50) DEFAULT NULL COMMENT '服务类型（如：基础洗浴、深度护理等）',
  `service_images` text DEFAULT NULL COMMENT '服务图片（JSON格式存储多张图片URL）',
  `duration` int DEFAULT 60 COMMENT '服务时长（分钟）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`service_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='洗浴服务表';

-- ----------------------------
-- 2. 洗浴服务价格梯度表
-- ----------------------------
DROP TABLE IF EXISTS `pet_bath_service_price`;
CREATE TABLE `pet_bath_service_price` (
  `price_id` bigint NOT NULL AUTO_INCREMENT COMMENT '价格ID',
  `service_id` bigint NOT NULL COMMENT '服务ID',
  `weight_min` decimal(10,2) NOT NULL COMMENT '最小体重（kg）',
  `weight_max` decimal(10,2) NOT NULL COMMENT '最大体重（kg）',
  `price` decimal(10,2) NOT NULL COMMENT '价格（元）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`price_id`) USING BTREE,
  KEY `idx_service_id` (`service_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='洗浴服务价格梯度表';

-- ----------------------------
-- 3. 预约表
-- ----------------------------
DROP TABLE IF EXISTS `pet_bath_appointment`;
CREATE TABLE `pet_bath_appointment` (
  `appointment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `appointment_no` varchar(50) NOT NULL COMMENT '预约单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `pet_id` bigint DEFAULT NULL COMMENT '宠物ID',
  `pet_name` varchar(100) DEFAULT NULL COMMENT '宠物名称',
  `pet_weight` decimal(10,2) DEFAULT NULL COMMENT '宠物体重（kg）',
  `service_id` bigint NOT NULL COMMENT '服务ID',
  `service_name` varchar(100) DEFAULT NULL COMMENT '服务名称',
  `appointment_time` datetime NOT NULL COMMENT '预约时间',
  `expected_price` decimal(10,2) DEFAULT NULL COMMENT '预计价格',
  `actual_price` decimal(10,2) DEFAULT NULL COMMENT '实际价格',
  `status` varchar(20) DEFAULT 'pending' COMMENT '预约状态（pending待确认、confirmed已确认、in_service服务中、completed已完成、cancelled已取消）',
  `cancel_reason` varchar(500) DEFAULT NULL COMMENT '取消原因',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`appointment_id`) USING BTREE,
  UNIQUE KEY `uk_appointment_no` (`appointment_no`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_service_id` (`service_id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_appointment_time` (`appointment_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

-- ----------------------------
-- 4. 订单表
-- ----------------------------
DROP TABLE IF EXISTS `pet_bath_order`;
CREATE TABLE `pet_bath_order` (
  `order_id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `appointment_id` bigint DEFAULT NULL COMMENT '预约ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `service_id` bigint NOT NULL COMMENT '服务ID',
  `service_name` varchar(100) DEFAULT NULL COMMENT '服务名称',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `paid_amount` decimal(10,2) DEFAULT 0.00 COMMENT '已支付金额',
  `refund_amount` decimal(10,2) DEFAULT 0.00 COMMENT '退款金额',
  `status` varchar(20) DEFAULT 'unpaid' COMMENT '订单状态（unpaid待支付、paid已支付、in_service服务中、completed已完成、refunded已退款、cancelled已取消）',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` varchar(500) DEFAULT NULL COMMENT '取消原因',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`order_id`) USING BTREE,
  UNIQUE KEY `uk_order_no` (`order_no`) USING BTREE,
  KEY `idx_appointment_id` (`appointment_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------
-- 5. 支付记录表
-- ----------------------------
DROP TABLE IF EXISTS `pet_bath_payment`;
CREATE TABLE `pet_bath_payment` (
  `payment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '支付ID',
  `payment_no` varchar(50) NOT NULL COMMENT '支付单号',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `order_no` varchar(50) DEFAULT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `payment_type` varchar(20) DEFAULT 'alipay' COMMENT '支付方式（alipay支付宝、wechat微信、balance余额）',
  `payment_amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `status` varchar(20) DEFAULT 'pending' COMMENT '支付状态（pending待支付、paid已支付、failed支付失败、refunded已退款）',
  `transaction_id` varchar(100) DEFAULT NULL COMMENT '第三方交易号',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `refund_reason` varchar(500) DEFAULT NULL COMMENT '退款原因',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`payment_id`) USING BTREE,
  UNIQUE KEY `uk_payment_no` (`payment_no`) USING BTREE,
  KEY `idx_order_id` (`order_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- ----------------------------
-- 6. 通知记录表
-- ----------------------------
DROP TABLE IF EXISTS `pet_bath_notification`;
CREATE TABLE `pet_bath_notification` (
  `notification_id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `appointment_id` bigint DEFAULT NULL COMMENT '预约ID',
  `order_id` bigint DEFAULT NULL COMMENT '订单ID',
  `notification_type` varchar(50) NOT NULL COMMENT '通知类型（appointment_created预约创建、appointment_confirmed预约确认、service_started服务开始、service_completed服务完成、order_paid订单支付等）',
  `title` varchar(200) NOT NULL COMMENT '通知标题',
  `content` varchar(1000) DEFAULT NULL COMMENT '通知内容',
  `is_read` char(1) DEFAULT '0' COMMENT '是否已读（0未读 1已读）',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`notification_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_appointment_id` (`appointment_id`) USING BTREE,
  KEY `idx_order_id` (`order_id`) USING BTREE,
  KEY `idx_is_read` (`is_read`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知记录表';

-- ----------------------------
-- 7. 评价评论表
-- ----------------------------
DROP TABLE IF EXISTS `pet_bath_review`;
CREATE TABLE `pet_bath_review` (
  `review_id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `appointment_id` bigint DEFAULT NULL COMMENT '预约ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `service_id` bigint NOT NULL COMMENT '服务ID',
  `rating` int NOT NULL COMMENT '评分（1-5星）',
  `content` varchar(1000) DEFAULT NULL COMMENT '评论内容',
  `images` varchar(1000) DEFAULT NULL COMMENT '评价图片（多个用逗号分隔）',
  `reply_content` varchar(1000) DEFAULT NULL COMMENT '商家回复内容',
  `reply_time` datetime DEFAULT NULL COMMENT '回复时间',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1隐藏）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`review_id`) USING BTREE,
  KEY `idx_order_id` (`order_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_service_id` (`service_id`) USING BTREE,
  KEY `idx_rating` (`rating`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价评论表';

-- ----------------------------
-- 初始化数据
-- ----------------------------

-- 插入洗浴服务示例数据（service_type使用数字：0=基础洗浴,1=深度护理,2=豪华套餐）
INSERT INTO `pet_bath_service` VALUES 
(1, '基础洗浴', '包含洗澡、吹干、基础护理', '0', 60, '0', 1, 'admin', NOW(), '', NULL, ''),
(2, '深度护理', '包含洗澡、吹干、剪指甲、清理耳朵、基础护理', '1', 90, '0', 2, 'admin', NOW(), '', NULL, ''),
(3, '豪华套餐', '包含洗澡、吹干、剪指甲、清理耳朵、美容造型、SPA护理', '2', 120, '0', 3, 'admin', NOW(), '', NULL, '');

-- 插入价格梯度数据（基础洗浴）
INSERT INTO `pet_bath_service_price` VALUES 
(1, 1, 0.00, 5.00, 50.00, 'admin', NOW(), '', NULL),
(2, 1, 5.01, 15.00, 80.00, 'admin', NOW(), '', NULL),
(3, 1, 15.01, 30.00, 120.00, 'admin', NOW(), '', NULL),
(4, 1, 30.01, 999.00, 180.00, 'admin', NOW(), '', NULL);

-- 插入价格梯度数据（深度护理）
INSERT INTO `pet_bath_service_price` VALUES 
(5, 2, 0.00, 5.00, 80.00, 'admin', NOW(), '', NULL),
(6, 2, 5.01, 15.00, 120.00, 'admin', NOW(), '', NULL),
(7, 2, 15.01, 30.00, 180.00, 'admin', NOW(), '', NULL),
(8, 2, 30.01, 999.00, 260.00, 'admin', NOW(), '', NULL);

-- 插入价格梯度数据（豪华套餐）
INSERT INTO `pet_bath_service_price` VALUES 
(9, 3, 0.00, 5.00, 120.00, 'admin', NOW(), '', NULL),
(10, 3, 5.01, 15.00, 180.00, 'admin', NOW(), '', NULL),
(11, 3, 15.01, 30.00, 260.00, 'admin', NOW(), '', NULL),
(12, 3, 30.01, 999.00, 360.00, 'admin', NOW(), '', NULL);

SET FOREIGN_KEY_CHECKS = 1;

