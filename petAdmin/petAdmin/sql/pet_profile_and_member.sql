 -- ============================================
-- 宠物档案和会员管理功能 - 数据库表结构
-- ============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 宠物档案表
-- ----------------------------
DROP TABLE IF EXISTS `pet_profile`;
CREATE TABLE `pet_profile` (
  `pet_id` bigint NOT NULL AUTO_INCREMENT COMMENT '宠物ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `pet_name` varchar(50) NOT NULL COMMENT '宠物名称',
  `pet_breed` varchar(50) DEFAULT NULL COMMENT '宠物品种',
  `pet_age` int DEFAULT NULL COMMENT '年龄（月）',
  `pet_sex` char(1) DEFAULT NULL COMMENT '性别（0公 1母 2未知）',
  `pet_weight` decimal(10,2) DEFAULT NULL COMMENT '体重（kg）',
  `hair_type` char(1) DEFAULT '0' COMMENT '毛发类型（0短毛 1长毛）',
  `pet_photo` varchar(255) DEFAULT NULL COMMENT '宠物照片',
  `health_status` varchar(500) DEFAULT NULL COMMENT '健康状况',
  `special_needs` varchar(500) DEFAULT NULL COMMENT '特殊需求',
  `allergy_history` varchar(500) DEFAULT NULL COMMENT '过敏史',
  `is_default` char(1) DEFAULT '0' COMMENT '是否默认宠物（0否 1是）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`pet_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_is_default` (`user_id`, `is_default`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物档案表';

-- ----------------------------
-- 2. 会员信息表
-- ----------------------------
DROP TABLE IF EXISTS `member_info`;
CREATE TABLE `member_info` (
  `member_id` bigint NOT NULL AUTO_INCREMENT COMMENT '会员ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `member_level` varchar(20) DEFAULT '普通' COMMENT '会员等级（普通、银卡、金卡、钻石）',
  `points` int DEFAULT 0 COMMENT '积分',
  `total_consumption` decimal(10,2) DEFAULT 0.00 COMMENT '累计消费（元）',
  `member_since` datetime DEFAULT NULL COMMENT '成为会员时间',
  `expire_time` datetime DEFAULT NULL COMMENT '会员到期时间',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1过期）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`member_id`) USING BTREE,
  UNIQUE KEY `uk_user_id` (`user_id`) USING BTREE,
  KEY `idx_member_level` (`member_level`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员信息表';

-- ----------------------------
-- 3. 积分记录表
-- ----------------------------
DROP TABLE IF EXISTS `points_record`;
CREATE TABLE `points_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `points` int NOT NULL COMMENT '积分变动（正数获得，负数消费）',
  `points_type` varchar(20) DEFAULT NULL COMMENT '积分类型（消费获得、兑换消费、签到获得等）',
  `order_id` bigint DEFAULT NULL COMMENT '关联订单ID',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_order_id` (`order_id`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';

-- ----------------------------
-- 4. 为现有用户创建会员记录（可选）
-- ----------------------------
-- INSERT INTO `member_info` (`user_id`, `member_level`, `points`, `total_consumption`, `member_since`, `status`, `create_by`, `create_time`)
-- SELECT `user_id`, '普通', 0, 0.00, NOW(), '0', 'system', NOW()
-- FROM `pet_bath_user`
-- WHERE `user_type` = '0'
-- ON DUPLICATE KEY UPDATE `update_time` = NOW();

SET FOREIGN_KEY_CHECKS = 1;

