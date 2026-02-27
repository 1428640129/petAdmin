-- ----------------------------
-- 统一状态字段为数字类型
-- 将字符串状态改为char(1)数字类型
-- ----------------------------

-- ============================================
-- 1. 修改订单状态字段 (pet_bath_order.status)
-- ============================================

-- 修改字段类型和注释
ALTER TABLE `pet_bath_order` 
MODIFY COLUMN `status` char(1) DEFAULT '0' COMMENT '订单状态（0=待支付,1=已支付,2=服务中,3=已完成,4=已退款,5=已取消）';

-- 更新现有数据：将字符串转换为数字
UPDATE `pet_bath_order` 
SET `status` = CASE 
    WHEN `status` = 'unpaid' OR `status` = '0' THEN '0'
    WHEN `status` = 'paid' OR `status` = '1' THEN '1'
    WHEN `status` = 'in_service' OR `status` = '2' THEN '2'
    WHEN `status` = 'completed' OR `status` = '3' THEN '3'
    WHEN `status` = 'refunded' OR `status` = '4' THEN '4'
    WHEN `status` = 'cancelled' OR `status` = '5' THEN '5'
    ELSE '0'  -- 默认值
END;

-- ============================================
-- 2. 修改预约状态字段 (pet_bath_appointment.status)
-- ============================================

-- 修改字段类型和注释
ALTER TABLE `pet_bath_appointment` 
MODIFY COLUMN `status` char(1) DEFAULT '0' COMMENT '预约状态（0=待确认,1=已确认,2=服务中,3=已完成,4=已取消）';

-- 更新现有数据：将字符串转换为数字
UPDATE `pet_bath_appointment` 
SET `status` = CASE 
    WHEN `status` = 'pending' OR `status` = '0' THEN '0'
    WHEN `status` = 'confirmed' OR `status` = '1' THEN '1'
    WHEN `status` = 'in_service' OR `status` = '2' THEN '2'
    WHEN `status` = 'completed' OR `status` = '3' THEN '3'
    WHEN `status` = 'cancelled' OR `status` = '4' THEN '4'
    ELSE '0'  -- 默认值
END;

-- ============================================
-- 3. 修改支付方式字段 (pet_bath_payment.payment_type)
-- ============================================

-- 修改字段类型和注释
ALTER TABLE `pet_bath_payment` 
MODIFY COLUMN `payment_type` char(1) DEFAULT '0' COMMENT '支付方式（0=支付宝,1=微信,2=余额）';

-- 更新现有数据：将字符串转换为数字
UPDATE `pet_bath_payment` 
SET `payment_type` = CASE 
    WHEN `payment_type` = 'alipay' OR `payment_type` = '0' THEN '0'
    WHEN `payment_type` = 'wechat' OR `payment_type` = '1' THEN '1'
    WHEN `payment_type` = 'balance' OR `payment_type` = '2' THEN '2'
    ELSE '0'  -- 默认值
END;

-- ============================================
-- 4. 修改支付状态字段 (pet_bath_payment.status)
-- ============================================

-- 修改字段类型和注释
ALTER TABLE `pet_bath_payment` 
MODIFY COLUMN `status` char(1) DEFAULT '0' COMMENT '支付状态（0=待支付,1=已支付,2=支付失败,3=已退款）';

-- 更新现有数据：将字符串转换为数字
UPDATE `pet_bath_payment` 
SET `status` = CASE 
    WHEN `status` = 'pending' OR `status` = '0' THEN '0'
    WHEN `status` = 'paid' OR `status` = '1' THEN '1'
    WHEN `status` = 'failed' OR `status` = '2' THEN '2'
    WHEN `status` = 'refunded' OR `status` = '3' THEN '3'
    ELSE '0'  -- 默认值
END;

-- ============================================
-- 5. 修改通知类型字段 (pet_bath_notification.notification_type)
-- ============================================

-- 修改字段类型和注释
ALTER TABLE `pet_bath_notification` 
MODIFY COLUMN `notification_type` char(1) DEFAULT '0' COMMENT '通知类型（0=预约创建,1=预约确认,2=服务开始,3=服务完成,4=订单支付,5=订单取消）';

-- 更新现有数据：将字符串转换为数字
UPDATE `pet_bath_notification` 
SET `notification_type` = CASE 
    WHEN `notification_type` = 'appointment_created' OR `notification_type` = '0' THEN '0'
    WHEN `notification_type` = 'appointment_confirmed' OR `notification_type` = '1' THEN '1'
    WHEN `notification_type` = 'service_started' OR `notification_type` = '2' THEN '2'
    WHEN `notification_type` = 'service_completed' OR `notification_type` = '3' THEN '3'
    WHEN `notification_type` = 'order_paid' OR `notification_type` = '4' THEN '4'
    WHEN `notification_type` = 'order_cancelled' OR `notification_type` = '5' THEN '5'
    ELSE '0'  -- 默认值
END;

-- ============================================
-- 验证：查看修改后的字段信息
-- ============================================

-- 查看订单表状态字段
SHOW COLUMNS FROM `pet_bath_order` LIKE 'status';

-- 查看预约表状态字段
SHOW COLUMNS FROM `pet_bath_appointment` LIKE 'status';

-- 查看支付表字段
SHOW COLUMNS FROM `pet_bath_payment` LIKE 'payment_type';
SHOW COLUMNS FROM `pet_bath_payment` LIKE 'status';

-- 查看通知表字段
SHOW COLUMNS FROM `pet_bath_notification` LIKE 'notification_type';

-- 查看数据分布
SELECT `status`, COUNT(*) as count FROM `pet_bath_order` GROUP BY `status`;
SELECT `status`, COUNT(*) as count FROM `pet_bath_appointment` GROUP BY `status`;
SELECT `payment_type`, COUNT(*) as count FROM `pet_bath_payment` GROUP BY `payment_type`;
SELECT `status`, COUNT(*) as count FROM `pet_bath_payment` GROUP BY `status`;
SELECT `notification_type`, COUNT(*) as count FROM `pet_bath_notification` GROUP BY `notification_type`;

