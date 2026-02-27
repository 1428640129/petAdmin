-- ----------------------------
-- Table structure for pet_bath_message
-- ----------------------------
DROP TABLE IF EXISTS `pet_bath_message`;
CREATE TABLE `pet_bath_message` (
  `message_id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID（用户ID或商家ID）',
  `sender_type` char(1) NOT NULL DEFAULT '0' COMMENT '发送者类型（0=用户,1=商家）',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID（用户ID或商家ID）',
  `receiver_type` char(1) NOT NULL DEFAULT '1' COMMENT '接收者类型（0=用户,1=商家）',
  `message_type` char(1) NOT NULL DEFAULT '0' COMMENT '消息类型（0=文字,1=图片,2=视频）',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '消息内容（文字消息或文件URL）',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件URL（图片或视频）',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小（字节）',
  `file_duration` int DEFAULT NULL COMMENT '视频时长（秒，仅视频消息）',
  `thumbnail_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '缩略图URL（视频消息）',
  `is_read` char(1) DEFAULT '0' COMMENT '是否已读（0=未读,1=已读）',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`message_id`),
  KEY `idx_sender` (`sender_id`, `sender_type`),
  KEY `idx_receiver` (`receiver_id`, `receiver_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='聊天消息表';

-- ----------------------------
-- Records of pet_bath_message (示例数据)
-- ----------------------------
INSERT INTO `pet_bath_message` VALUES (1, 1, '0', 1, '1', '0', '您好，我想预约明天的宠物洗澡服务', NULL, NULL, NULL, NULL, '0', NULL, 'user1', '2024-01-20 10:00:00', '', NULL, NULL);
INSERT INTO `pet_bath_message` VALUES (2, 1, '1', 1, '0', '0', '好的，请问您的宠物是什么品种？体重多少？', NULL, NULL, NULL, NULL, '1', '2024-01-20 10:05:00', 'merchant1', '2024-01-20 10:02:00', '', NULL, NULL);
INSERT INTO `pet_bath_message` VALUES (3, 1, '0', 1, '1', '0', '是一只金毛，体重约25kg', NULL, NULL, NULL, NULL, '0', NULL, 'user1', '2024-01-20 10:10:00', '', NULL, NULL);

