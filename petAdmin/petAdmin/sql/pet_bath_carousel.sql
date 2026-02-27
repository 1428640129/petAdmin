-- ----------------------------
-- 轮播图表
-- ----------------------------
DROP TABLE IF EXISTS `pet_bath_carousel`;
CREATE TABLE `pet_bath_carousel` (
  `carousel_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `title` varchar(255) NOT NULL COMMENT '轮播图标题',
  `image_url` varchar(500) NOT NULL COMMENT '图片URL',
  `link_url` varchar(500) DEFAULT NULL COMMENT '跳转链接（可选）',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序（数字越小越靠前）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`carousel_id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- ----------------------------
-- 初始化轮播图数据
-- ----------------------------
INSERT INTO `pet_bath_carousel` VALUES 
(1, '专业宠物洗澡', 'https://ai-public.mastergo.com/ai/img_res/13b5469fd6d2406f55b473faf54a6268.jpg', NULL, 1, '0', 'admin', NOW(), '', NULL, '首页轮播图1'),
(2, '舒适环境', 'https://ai-public.mastergo.com/ai/img_res/53bd0b97c2d667bd8c504f7c54618df2.jpg', NULL, 2, '0', 'admin', NOW(), '', NULL, '首页轮播图2'),
(3, '专业护理', 'https://ai-public.mastergo.com/ai/img_res/ac37ed5d71b644bf1a737970f78d4b1b.jpg', NULL, 3, '0', 'admin', NOW(), '', NULL, '首页轮播图3');

