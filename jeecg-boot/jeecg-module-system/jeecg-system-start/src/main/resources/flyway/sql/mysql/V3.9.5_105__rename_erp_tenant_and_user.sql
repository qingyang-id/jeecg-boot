-- 租户 1001 与 erp 账号显示名都叫“北疯”，容易混淆。租户改为公司名，erp 账号只改真实姓名，
-- 登录名 erp 不变（jsh_* 的 create_by 记录的是登录名）。
UPDATE `sys_tenant` SET `name` = '雅居乐橱饰' WHERE `id` = 1001;
UPDATE `sys_user` SET `realname` = '蒋总' WHERE `username` = 'erp';
