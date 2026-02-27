-- ============================================
-- 前台用户表（小程序顾客/商家）
-- ============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 前台用户表
-- ----------------------------
DROP TABLE IF EXISTS `pet_bath_user`;
CREATE TABLE `pet_bath_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_name` varchar(50) NOT NULL COMMENT '登录账号',
  `nick_name` varchar(50) DEFAULT NULL COMMENT '昵称',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `password` varchar(100) NOT NULL COMMENT '登录密码（BCrypt加密）',
  `user_type` char(1) NOT NULL DEFAULT '0' COMMENT '用户类型（0顾客 1商家）',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像地址',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_user_name` (`user_name`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='前台用户表（小程序顾客/商家）';

-- ----------------------------
-- 初始化前台用户数据
-- ----------------------------
-- 顾客用户（密码：123456）
-- 注意：BCrypt密码需要使用正确的格式，这里使用标准的BCrypt哈希值
INSERT INTO `pet_bath_user` (
  `user_name`, `nick_name`, `phone`, `password`, `user_type`, `status`, `create_by`, `create_time`, `remark`
) VALUES (
  'customer1', '测试顾客', '13800000001', '$2a$10$slYQmyNdGzin7olVN3p5aOpOf7YRLzJ6m.UnQc3mC2SI.4Sl2Q0Fm', '0', '0', 'system', NOW(), '示例顾客用户'
);

-- 商家用户（密码：123456）
INSERT INTO `pet_bath_user` (
  `user_name`, `nick_name`, `phone`, `password`, `user_type`, `status`, `create_by`, `create_time`, `remark`
) VALUES (
  'merchant1', '测试商家', '13800000002', '$2a$10$slYQmyNdGzin7olVN3p5aOpOf7YRLzJ6m.UnQc3mC2SI.4Sl2Q0Fm', '1', '0', 'system', NOW(), '示例商家用户'
);

SET FOREIGN_KEY_CHECKS = 1;

