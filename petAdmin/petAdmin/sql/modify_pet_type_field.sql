-- ----------------------------
-- 修改预约表pet_type字段（如果字段已存在，则修改；不存在则添加）
-- ----------------------------

-- 如果字段已存在，先删除（注意：这会删除数据，建议先备份）
-- ALTER TABLE `pet_bath_appointment` DROP COLUMN `pet_type`;

-- 添加或修改字段
-- 如果字段不存在，使用ADD；如果存在，使用MODIFY
-- 注意：MySQL不支持IF EXISTS，需要手动判断

-- 方案1：如果字段不存在，先添加
ALTER TABLE `pet_bath_appointment` 
ADD COLUMN IF NOT EXISTS `pet_type` char(1) DEFAULT '0' COMMENT '宠物类型（0=短毛,1=长毛）' AFTER `pet_weight`;

-- 方案2：如果字段已存在，修改字段类型和注释（MySQL 5.7+）
-- 如果上面的ADD失败（字段已存在），则执行下面的MODIFY
ALTER TABLE `pet_bath_appointment` 
MODIFY COLUMN `pet_type` char(1) DEFAULT '0' COMMENT '宠物类型（0=短毛,1=长毛）';

-- ----------------------------
-- 修改价格表pet_type字段
-- ----------------------------

-- 添加字段（如果不存在）
ALTER TABLE `pet_bath_service_price` 
ADD COLUMN IF NOT EXISTS `pet_type` char(1) DEFAULT '0' COMMENT '宠物类型（0=短毛,1=长毛）' AFTER `service_id`;

-- 修改字段（如果已存在）
ALTER TABLE `pet_bath_service_price` 
MODIFY COLUMN `pet_type` char(1) DEFAULT '0' COMMENT '宠物类型（0=短毛,1=长毛）';

-- ----------------------------
-- 更新现有数据：将字符串类型转换为数字类型
-- ----------------------------

-- 更新预约表：将long_hair/short_hair转换为1/0
UPDATE `pet_bath_appointment` 
SET `pet_type` = CASE 
    WHEN `pet_type` = 'long_hair' THEN '1'
    WHEN `pet_type` = 'short_hair' THEN '0'
    WHEN `pet_type` IS NULL OR `pet_type` = '' THEN '0'
    ELSE `pet_type`
END;

-- 更新价格表：将long_hair/short_hair转换为1/0
UPDATE `pet_bath_service_price` 
SET `pet_type` = CASE 
    WHEN `pet_type` = 'long_hair' THEN '1'
    WHEN `pet_type` = 'short_hair' THEN '0'
    WHEN `pet_type` IS NULL OR `pet_type` = '' THEN '0'
    ELSE `pet_type`
END;

