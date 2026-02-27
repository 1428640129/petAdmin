-- pet_bath_user 头像字段说明
-- avatar 字段已包含在标准建表脚本 pet_bath_user.sql 中
-- 若您的表是通过 pet_bath_user.sql 创建的，则无需执行任何操作
-- 若表为早期创建且缺少 avatar 字段，可取消下方注释并执行：
-- ALTER TABLE `pet_bath_user` ADD COLUMN `avatar` varchar(255) DEFAULT NULL COMMENT '头像地址' AFTER `user_type`;

