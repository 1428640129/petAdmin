-- ============================================
-- 更新预约表中服务名称为空的记录
-- ============================================

-- 更新预约表中 service_name 为空的记录，根据 service_id 关联查询服务表获取服务名称
UPDATE pet_bath_appointment a
INNER JOIN pet_bath_service s ON a.service_id = s.service_id
SET a.service_name = s.service_name
WHERE (a.service_name IS NULL OR a.service_name = '') 
  AND a.service_id IS NOT NULL;

-- 查看更新结果
SELECT 
    appointment_id,
    appointment_no,
    service_id,
    service_name,
    pet_name,
    status
FROM pet_bath_appointment
WHERE service_id IS NOT NULL
ORDER BY appointment_id DESC
LIMIT 20;










