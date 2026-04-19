# 修改pet_type字段执行说明

## 情况说明

如果 `pet_bath_appointment` 和 `pet_bath_service_price` 表中已经存在 `pet_type` 字段（可能是 `varchar(20)` 类型），需要将其修改为 `char(1)` 类型，并更新注释。

## 执行步骤

### 方法一：使用 update_appointment_add_pet_type.sql（推荐）

这个脚本已经更新，会：
1. 先尝试添加字段（如果不存在）
2. 然后修改字段（如果已存在）
3. 更新现有数据

**执行方式**：
```sql
-- 直接执行整个脚本
source update_appointment_add_pet_type.sql;
```

**注意事项**：
- 如果字段不存在，ADD会成功，MODIFY会报错（可忽略）
- 如果字段已存在，ADD会报错（可忽略），MODIFY会成功
- 这是正常的，脚本设计为兼容两种情况

### 方法二：手动执行（更安全）

#### 步骤1：检查字段是否存在
```sql
-- 查看预约表字段信息
SHOW COLUMNS FROM `pet_bath_appointment` LIKE 'pet_type';

-- 查看价格表字段信息
SHOW COLUMNS FROM `pet_bath_service_price` LIKE 'pet_type';
```

#### 步骤2：根据情况执行

**如果字段不存在**，执行：
```sql
-- 添加预约表字段
ALTER TABLE `pet_bath_appointment` 
ADD COLUMN `pet_type` char(1) DEFAULT '0' COMMENT '宠物类型（0=短毛,1=长毛）' AFTER `pet_weight`;

-- 添加价格表字段
ALTER TABLE `pet_bath_service_price` 
ADD COLUMN `pet_type` char(1) DEFAULT '0' COMMENT '宠物类型（0=短毛,1=长毛）' AFTER `service_id`;
```

**如果字段已存在**，执行：
```sql
-- 修改预约表字段
ALTER TABLE `pet_bath_appointment` 
MODIFY COLUMN `pet_type` char(1) DEFAULT '0' COMMENT '宠物类型（0=短毛,1=长毛）';

-- 修改价格表字段
ALTER TABLE `pet_bath_service_price` 
MODIFY COLUMN `pet_type` char(1) DEFAULT '0' COMMENT '宠物类型（0=短毛,1=长毛）';
```

#### 步骤3：更新现有数据
```sql
-- 更新预约表：将字符串类型转换为数字类型
UPDATE `pet_bath_appointment` 
SET `pet_type` = CASE 
    WHEN `pet_type` = 'long_hair' OR `pet_type` = '1' THEN '1'
    WHEN `pet_type` = 'short_hair' OR `pet_type` = '0' THEN '0'
    WHEN `pet_type` IS NULL OR `pet_type` = '' THEN '0'
    ELSE '0'
END;

-- 更新价格表：将字符串类型转换为数字类型
UPDATE `pet_bath_service_price` 
SET `pet_type` = CASE 
    WHEN `pet_type` = 'long_hair' OR `pet_type` = '1' THEN '1'
    WHEN `pet_type` = 'short_hair' OR `pet_type` = '0' THEN '0'
    WHEN `pet_type` IS NULL OR `pet_type` = '' THEN '0'
    ELSE '0'
END;
```

#### 步骤4：验证修改结果
```sql
-- 查看字段信息
SHOW COLUMNS FROM `pet_bath_appointment` LIKE 'pet_type';
SHOW COLUMNS FROM `pet_bath_service_price` LIKE 'pet_type';

-- 查看数据分布
SELECT `pet_type`, COUNT(*) as count FROM `pet_bath_appointment` GROUP BY `pet_type`;
SELECT `pet_type`, COUNT(*) as count FROM `pet_bath_service_price` GROUP BY `pet_type`;
```

## 字段类型对比

| 修改前 | 修改后 |
|--------|--------|
| `varchar(20)` | `char(1)` |
| `long_hair` / `short_hair` | `1` / `0` |
| 注释：`long_hair=长毛,short_hair=短毛` | 注释：`0=短毛,1=长毛` |

## 注意事项

1. **备份数据**：执行前建议备份数据库
2. **数据转换**：如果表中已有数据，UPDATE语句会将 `long_hair` 转为 `1`，`short_hair` 转为 `0`
3. **默认值**：修改后默认值为 `'0'`（短毛）
4. **兼容性**：脚本兼容字段存在和不存在两种情况

## 常见问题

**Q: 执行ADD时提示字段已存在怎么办？**
A: 这是正常的，继续执行MODIFY语句即可。

**Q: 执行MODIFY时提示字段不存在怎么办？**
A: 先执行ADD语句添加字段。

**Q: 如何确认修改成功？**
A: 执行 `SHOW COLUMNS` 查看字段类型和注释是否已更新。
















