-- erp 账号点“账户设置”404：隐藏路由“用户设置”(/system/usersetting) 挂在“系统管理”下，erp 角色没有该目录，
-- 后端构建菜单树时父级未授权的子菜单会被丢弃。把它挪到 erp 已有的“主页”目录下并授权给 erp 角色；
-- 该菜单是隐藏路由，admin 菜单显示不变。
UPDATE `sys_permission` SET `parent_id` = '1438108176273760258'
WHERE `id` = '1596141938193747970' AND `parent_id` = 'd7d6e2e4e2934f2c9385a623fd98c6f3';

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT MD5(CONCAT(r.id, '-', p.id)), r.id, p.id, NULL, NOW(), '127.0.0.1'
FROM `sys_role` r
JOIN `sys_permission` p ON p.id = '1596141938193747970'
WHERE r.role_code = 'erp'
  AND NOT EXISTS (SELECT 1 FROM `sys_role_permission` x WHERE x.role_id = r.id AND x.permission_id = p.id);
