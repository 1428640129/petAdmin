-- ============================================
-- 隐藏会员积分菜单和宠物信息菜单
-- visible: 0=显示, 1=隐藏
-- ============================================

-- 隐藏会员积分菜单（根据路径 /bath/member 查找）
UPDATE sys_menu SET visible = '1' WHERE path = 'member' AND parent_id = 2000;

-- 隐藏宠物信息菜单（根据路径 /bath/pet 查找）
UPDATE sys_menu SET visible = '1' WHERE path = 'pet' AND parent_id = 2000;

-- 如果上面的条件不够精确，可以使用菜单名称匹配
-- UPDATE sys_menu SET visible = '1' WHERE menu_name LIKE '%会员%' AND parent_id = 2000;
-- UPDATE sys_menu SET visible = '1' WHERE menu_name LIKE '%宠物%' AND parent_id = 2000;

-- 或者如果知道具体的menu_id，可以直接使用ID
-- UPDATE sys_menu SET visible = '1' WHERE menu_id IN (会员积分菜单ID, 宠物信息菜单ID);


