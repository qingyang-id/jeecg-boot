-- 总经办（A01）下的演示节点“销售公关岗位”（A01A06，机构类别=岗位）改为部门“生产部”。
UPDATE `sys_depart` SET `depart_name` = '生产部', `org_category` = '2'
WHERE `id` = '1582683631414632450' AND `depart_name` = '销售公关岗位';
