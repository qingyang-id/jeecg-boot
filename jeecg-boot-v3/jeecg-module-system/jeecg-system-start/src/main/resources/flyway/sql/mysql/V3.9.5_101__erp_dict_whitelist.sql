-- ERP 表字典白名单。lowCodeMode=prod 时 DictTableWhiteListHandlerImpl 只放行 sys_table_white_list 中登记的表/字段，
-- 未登记会返回“校验失败，存在SQL注入风险！白名单校验未通过！”（dev 模式会自动登记，所以本地不暴露）。
-- 对应 @Dict(dictTable=...) 及前端 dict:'jsh_customer,name,id'。
INSERT IGNORE INTO `sys_table_white_list` (`id`, `table_name`, `field_name`, `status`, `create_by`, `create_time`) VALUES
('2109688620266319874', 'jsh_customer', 'name,id', '1', 'admin', NOW()),
('2109689760114655234', 'jsh_order', 'order_code,id', '1', 'admin', NOW()),
('2109689760290816001', 'jsh_product', 'name,id', '1', 'admin', NOW());
