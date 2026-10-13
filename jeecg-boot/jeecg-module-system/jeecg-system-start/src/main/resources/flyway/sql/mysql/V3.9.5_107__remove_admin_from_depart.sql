-- admin 是平台超管，不属于业务部门：移出“总经办”（A01），清空登录部门、负责部门，身份改回普通用户。
-- 超管权限来自角色 admin，与部门无关。
DELETE FROM `sys_user_depart`
WHERE `user_id` = (SELECT `id` FROM (SELECT `id` FROM `sys_user` WHERE `username` = 'admin') u)
  AND `dep_id` = 'c6d7cb4deeac411cb3384b1b31278596';
UPDATE `sys_user` SET `org_code` = NULL, `depart_ids` = NULL, `user_identity` = 1
WHERE `username` = 'admin' AND `org_code` = 'A01';
