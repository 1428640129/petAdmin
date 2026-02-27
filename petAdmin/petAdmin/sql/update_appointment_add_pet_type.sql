-- ----------------------------
-- 修改预约表pet_type字段
-- 如果字段不存在则添加，如果存在则修改类型和注释
-- ----------------------------

-- 尝试添加字段（如果字段已存在会报错，可以忽略）
ALTER TABLE `pet_bath_appointment` 
ADD COLUMN `pet_type` char(1) DEFAULT '0' COMMENT '宠物类型（0=短毛,1=长毛）' AFTER `pet_weight`;

-- 修改字段类型和注释（如果字段已存在）
ALTER TABLE `pet_bath_appointment` 
MODIFY COLUMN `pet_type` char(1) DEFAULT '0' COMMENT '宠物类型（0=短毛,1=长毛）';

-- ----------------------------
-- 修改价格表pet_type字段
-- ----------------------------

-- 尝试添加字段（如果字段已存在会报错，可以忽略）
ALTER TABLE `pet_bath_service_price` 
ADD COLUMN `pet_type` char(1) DEFAULT '0' COMMENT '宠物类型（0=短毛,1=长毛）' AFTER `service_id`;

-- 修改字段类型和注释（如果字段已存在）
ALTER TABLE `pet_bath_service_price` 
MODIFY COLUMN `pet_type` char(1) DEFAULT '0' COMMENT '宠物类型（0=短毛,1=长毛）';

-- ----------------------------
-- 更新现有数据：将字符串类型转换为数字类型
-- ----------------------------

-- 更新预约表：将long_hair/short_hair转换为1/0
UPDATE `pet_bath_appointment` 
SET `pet_type` = CASE 
    WHEN `pet_type` = 'long_hair' OR `pet_type` = '1' THEN '1'
    WHEN `pet_type` = 'short_hair' OR `pet_type` = '0' THEN '0'
    WHEN `pet_type` IS NULL OR `pet_type` = '' THEN '0'
    ELSE '0'
END;

-- 更新价格表：将long_hair/short_hair转换为1/0
UPDATE `pet_bath_service_price` 
SET `pet_type` = CASE 
    WHEN `pet_type` = 'long_hair' OR `pet_type` = '1' THEN '1'
    WHEN `pet_type` = 'short_hair' OR `pet_type` = '0' THEN '0'
    WHEN `pet_type` IS NULL OR `pet_type` = '' THEN '0'
    ELSE '0'
END;

