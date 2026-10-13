-- erp / admin 所在部门是上游演示数据“北京国炬软件”（A01，机构类别=部门），名字有误导，改为“总经办”。
-- 公司这一层由租户“雅居乐橱饰”表示，部门按职能命名。
UPDATE `sys_depart` SET `depart_name` = '总经办' WHERE `id` = 'c6d7cb4deeac411cb3384b1b31278596' AND `depart_name` = '北京国炬软件';
