-- ============================================
-- 开启注册功能
-- ============================================
-- 将系统配置中的注册功能开关设置为开启
-- 影响范围：PC端和小程序端注册接口

UPDATE sys_config 
SET config_value = 'true' 
WHERE config_key = 'sys.account.registerUser';

-- 如果配置项不存在，则插入
INSERT INTO sys_config (config_id, config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, remark)
SELECT 5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'true', 'Y', 'admin', NOW(), '', NULL, '是否开启注册用户功能（true开启，false关闭）'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_config WHERE config_key = 'sys.account.registerUser'
);

-- 验证更新结果
SELECT config_key, config_value, config_name, remark 
FROM sys_config 
WHERE config_key = 'sys.account.registerUser';







