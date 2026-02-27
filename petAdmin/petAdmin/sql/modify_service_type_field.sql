-- ----------------------------
-- 服务类型字段说明
-- 注意：服务类型是业务数据，建议保持字符串类型
-- 但如果需要统一为数字，可以使用此脚本
-- ----------------------------

-- ============================================
-- 方案一：保持字符串类型（推荐）
-- 服务类型是业务数据，可能有很多种，保持varchar更灵活
-- 当前字段定义：service_type varchar(50)
-- ============================================

-- 如果需要修改字段注释，可以执行：
ALTER TABLE `pet_bath_service` 
MODIFY COLUMN `service_type` varchar(50) DEFAULT NULL COMMENT '服务类型（如：基础洗浴、深度护理、豪华套餐等）';

-- ============================================
-- 方案二：改为数字类型（执行此方案）
-- 将服务类型改为char(1)，使用数字编码
-- ============================================

-- 1. 修改字段类型为char(1)
ALTER TABLE `pet_bath_service` 
MODIFY COLUMN `service_type` char(1) DEFAULT '0' COMMENT '服务类型（0=基础洗浴,1=深度护理,2=豪华套餐）';

-- 2. 更新现有数据：将字符串转换为数字
UPDATE `pet_bath_service` 
SET `service_type` = CASE 
    WHEN `service_type` = '基础服务' OR `service_type` = '基础洗浴' OR `service_type` = '0' THEN '0'
    WHEN `service_type` = '深度服务' OR `service_type` = '深度护理' OR `service_type` = '1' THEN '1'
    WHEN `service_type` = '豪华服务' OR `service_type` = '豪华套餐' OR `service_type` = '2' THEN '2'
    ELSE '0'  -- 默认值
END;

-- ============================================
-- 验证：查看修改后的字段信息
-- ============================================

-- 查看字段信息
SHOW COLUMNS FROM `pet_bath_service` LIKE 'service_type';

-- 查看数据分布
SELECT `service_type`, COUNT(*) as count FROM `pet_bath_service` GROUP BY `service_type`;

