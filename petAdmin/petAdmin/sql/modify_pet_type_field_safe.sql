-- ----------------------------
-- 安全修改pet_type字段的SQL脚本
-- 适用于字段已存在且为varchar类型的情况
-- ----------------------------

-- 注意：MySQL不支持IF EXISTS语法，需要分步执行
-- 如果字段不存在，执行ADD；如果字段存在，执行MODIFY

-- ============================================
-- 第一步：修改预约表pet_type字段
-- ============================================

-- 如果字段不存在，执行这个（会报错如果字段已存在，忽略即可）
ALTER TABLE `pet_bath_appointment` 
ADD COLUMN `pet_type` char(1) DEFAULT '0' COMMENT '宠物类型（0=短毛,1=长毛）' AFTER `pet_weight`;

-- 如果字段已存在，执行这个（会报错如果字段不存在，忽略即可）
ALTER TABLE `pet_bath_appointment` 
MODIFY COLUMN `pet_type` char(1) DEFAULT '0' COMMENT '宠物类型（0=短毛,1=长毛）';

-- ============================================
-- 第二步：修改价格表pet_type字段
-- ============================================

-- 如果字段不存在，执行这个
ALTER TABLE `pet_bath_service_price` 
ADD COLUMN `pet_type` char(1) DEFAULT '0' COMMENT '宠物类型（0=短毛,1=长毛）' AFTER `service_id`;

-- 如果字段已存在，执行这个
ALTER TABLE `pet_bath_service_price` 
MODIFY COLUMN `pet_type` char(1) DEFAULT '0' COMMENT '宠物类型（0=短毛,1=长毛）';

-- ============================================
-- 第三步：更新现有数据
-- ============================================

-- 更新预约表：将字符串类型转换为数字类型
UPDATE `pet_bath_appointment` 
SET `pet_type` = CASE 
    WHEN `pet_type` = 'long_hair' OR `pet_type` = '1' THEN '1'
    WHEN `pet_type` = 'short_hair' OR `pet_type` = '0' THEN '0'
    WHEN `pet_type` IS NULL OR `pet_type` = '' THEN '0'
    ELSE '0'  -- 默认值
END
WHERE `pet_type` IS NOT NULL;

-- 更新价格表：将字符串类型转换为数字类型
UPDATE `pet_bath_service_price` 
SET `pet_type` = CASE 
    WHEN `pet_type` = 'long_hair' OR `pet_type` = '1' THEN '1'
    WHEN `pet_type` = 'short_hair' OR `pet_type` = '0' THEN '0'
    WHEN `pet_type` IS NULL OR `pet_type` = '' THEN '0'
    ELSE '0'  -- 默认值
END
WHERE `pet_type` IS NOT NULL;

-- ============================================
-- 验证：查询修改后的数据
-- ============================================

-- 查看预约表的pet_type字段信息
SHOW COLUMNS FROM `pet_bath_appointment` LIKE 'pet_type';

-- 查看价格表的pet_type字段信息
SHOW COLUMNS FROM `pet_bath_service_price` LIKE 'pet_type';

-- 查看数据分布
SELECT `pet_type`, COUNT(*) as count FROM `pet_bath_appointment` GROUP BY `pet_type`;
SELECT `pet_type`, COUNT(*) as count FROM `pet_bath_service_price` GROUP BY `pet_type`;

