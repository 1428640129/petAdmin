-- ============================================
-- 更新 pet_bath_user 表的密码为正确的 BCrypt 格式
-- 密码：123456
-- ============================================

-- 更新顾客用户密码（customer1）
-- 密码 123456 的 BCrypt 哈希值
UPDATE `pet_bath_user` 
SET `password` = '$2a$10$slYQmyNdGzin7olVN3p5aOpOf7YRLzJ6m.UnQc3mC2SI.4Sl2Q0Fm'
WHERE `user_name` = 'customer1';

-- 更新商家用户密码（merchant1）
UPDATE `pet_bath_user` 
SET `password` = '$2a$10$slYQmyNdGzin7olVN3p5aOpOf7YRLzJ6m.UnQc3mC2SI.4Sl2Q0Fm'
WHERE `user_name` = 'merchant1';

-- 验证：查询用户信息（不显示密码）
SELECT `user_id`, `user_name`, `nick_name`, `user_type`, `status` 
FROM `pet_bath_user` 
WHERE `user_name` IN ('customer1', 'merchant1');











