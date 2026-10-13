-- 在线用户：admin 角色缺少 system:online:list / system:online:forceLogout 按钮授权（3.0 迁移数据未带），
-- SysUserOnlineController 的 @RequiresPermissions 导致 admin 打开“在线用户”提示“没有权限，请联系管理员分配权限！”。
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT MD5(CONCAT(r.id, '-', p.id)), r.id, p.id, NULL, NOW(), '127.0.0.1'
FROM `sys_role` r
JOIN `sys_permission` p ON p.perms IN ('system:online:list', 'system:online:forceLogout') AND p.del_flag = 0
WHERE r.role_code = 'admin'
  AND NOT EXISTS (SELECT 1 FROM `sys_role_permission` x WHERE x.role_id = r.id AND x.permission_id = p.id);
