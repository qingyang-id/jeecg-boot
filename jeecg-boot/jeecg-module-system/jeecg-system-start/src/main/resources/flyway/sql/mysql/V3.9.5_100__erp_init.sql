-- =====================================================================
-- ERP 初始化（JeecgBoot 3.9.5）：jsh_* 业务表、ERP 字典、菜单、erp 角色、定时任务
-- 全部幂等；MySQL 5.7 兼容（无 0900 排序规则 / CTE / 窗口函数 / JSON_TABLE）
-- 表结构取自生产 dump（去掉 AUTO_INCREMENT=N）；字典 / 菜单 / 角色 id 沿用 3.0 生产库
-- 生产数据由 db/erp/migrate_from_3.0.sql 搬运，本脚本只建结构与种子
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. 业务表（11 张）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `jsh_customer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` int(1) NOT NULL DEFAULT '0' COMMENT '删除状态:0未删除,1删除',
  `name` varchar(32) NOT NULL DEFAULT '' COMMENT '客户名字',
  `sex` tinyint(1) NOT NULL DEFAULT '0' COMMENT '客户性别:1男,2女,0未知',
  `address` varchar(300) NOT NULL DEFAULT '' COMMENT '地址',
  `phone` varchar(32) NOT NULL DEFAULT '' COMMENT '手机',
  `rank` int(10) NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态:1启用,0禁用',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_name` (`name`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_rank` (`rank`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';
CREATE TABLE IF NOT EXISTS `jsh_customer_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` int(1) NOT NULL DEFAULT '0' COMMENT '删除状态:0未删除,1删除',
  `customer_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '客户id',
  `address` varchar(300) NOT NULL DEFAULT '' COMMENT '地址',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户地址表';
CREATE TABLE IF NOT EXISTS `jsh_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` int(1) NOT NULL DEFAULT '0' COMMENT '删除状态:0未删除,1删除',
  `order_code` varchar(32) NOT NULL DEFAULT '' COMMENT '订单编码',
  `customer_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '客户id',
  `total_area` bigint(20) NOT NULL DEFAULT '0' COMMENT '总面积(mm²)',
  `total_price` bigint(20) NOT NULL DEFAULT '0' COMMENT '订单总额(分)',
  `order_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `address` varchar(128) DEFAULT NULL COMMENT '地址',
  `remark` varchar(500) NOT NULL DEFAULT '' COMMENT '备注',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_order_code` (`order_code`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_order_time` (`order_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
CREATE TABLE IF NOT EXISTS `jsh_order_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` int(1) NOT NULL DEFAULT '0' COMMENT '删除状态:0未删除,1删除',
  `customer_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '客户id',
  `order_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '订单ID',
  `product_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '产品id',
  `width` bigint(20) NOT NULL DEFAULT '0' COMMENT '宽(mm)',
  `height` bigint(20) NOT NULL DEFAULT '0' COMMENT '高(mm)',
  `num` int(11) NOT NULL DEFAULT '0' COMMENT '数量',
  `direction` tinyint(1) NOT NULL DEFAULT '0' COMMENT '方向:0其他,1双开,2左开,3右开',
  `color` varchar(50) NOT NULL DEFAULT '' COMMENT '颜色',
  `glass_color` varchar(50) NOT NULL DEFAULT '' COMMENT '玻璃颜色',
  `price` bigint(20) NOT NULL DEFAULT '0' COMMENT '单价(元)',
  `extend_price` bigint(20) NOT NULL DEFAULT '0' COMMENT '附加(分)',
  `total_area` bigint(20) NOT NULL DEFAULT '0' COMMENT '总面积(mm²)',
  `total_price` bigint(20) NOT NULL DEFAULT '0' COMMENT '总价(元)',
  `extend_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '扩展类型:1拉,2抽,3条',
  `extend_num` int(11) NOT NULL DEFAULT '0' COMMENT '扩展数量',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_order_id` (`order_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品表';
CREATE TABLE IF NOT EXISTS `jsh_order_product_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` int(1) NOT NULL DEFAULT '0' COMMENT '删除状态:0未删除,1删除',
  `customer_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '客户id',
  `order_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '订单ID',
  `order_product_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '订单产品表ID',
  `product_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '产品id',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '类型:1铝材,2玻璃',
  `reference_width` bigint(20) NOT NULL DEFAULT '0' COMMENT '参考宽度',
  `reference_height` bigint(20) NOT NULL DEFAULT '0' COMMENT '参考高度',
  `width` bigint(20) NOT NULL DEFAULT '0' COMMENT '宽',
  `height` bigint(20) NOT NULL DEFAULT '0' COMMENT '高',
  `num` int(11) NOT NULL DEFAULT '0' COMMENT '数量',
  `direction` tinyint(1) NOT NULL DEFAULT '0' COMMENT '方向:0其他,1双开,2左开,3右开',
  `color` varchar(50) NOT NULL DEFAULT '' COMMENT '颜色',
  `extend_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '扩展类型:1拉,2抽,3条',
  `extend_num` int(11) NOT NULL DEFAULT '0' COMMENT '扩展数量',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_product_id` (`order_product_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品明细表';
CREATE TABLE IF NOT EXISTS `jsh_order_product_extend` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` int(1) NOT NULL DEFAULT '0' COMMENT '删除状态:0未删除,1删除',
  `order_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '订单ID',
  `order_product_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '订单产品表ID',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '类型:1拉,2抽,3条',
  `num` int(11) NOT NULL DEFAULT '0' COMMENT '数量',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_product_id` (`order_product_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品扩展信息表';
CREATE TABLE IF NOT EXISTS `jsh_order_summary_daily` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `time` date NOT NULL COMMENT '时间',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户id',
  `customer_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '客户id, 0代表所有',
  `total_num` bigint(20) NOT NULL DEFAULT '0' COMMENT '总订单数',
  `total_area` bigint(20) NOT NULL DEFAULT '0' COMMENT '总面积(mm²)',
  `total_price` bigint(20) NOT NULL DEFAULT '0' COMMENT '订单总额(分)',
  `del_flag` int(1) NOT NULL DEFAULT '0' COMMENT '删除状态:0未删除,1删除',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_time_tenant_id_customer_id` (`time`,`tenant_id`,`customer_id`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单每日汇总表';
CREATE TABLE IF NOT EXISTS `jsh_order_summary_monthly` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `time` date NOT NULL COMMENT '时间',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户id',
  `customer_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '客户id, 0代表所有',
  `total_num` bigint(20) NOT NULL DEFAULT '0' COMMENT '总订单数',
  `total_area` bigint(20) NOT NULL DEFAULT '0' COMMENT '总面积(mm²)',
  `total_price` bigint(20) NOT NULL DEFAULT '0' COMMENT '订单总额(分)',
  `del_flag` int(1) NOT NULL DEFAULT '0' COMMENT '删除状态:0未删除,1删除',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_time_tenant_id_customer_id` (`time`,`tenant_id`,`customer_id`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单每月汇总表';
CREATE TABLE IF NOT EXISTS `jsh_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` int(1) NOT NULL DEFAULT '0' COMMENT '删除状态:0未删除,1删除',
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '名称',
  `barcode` varchar(50) NOT NULL DEFAULT '' COMMENT '商品条码',
  `purchase_price` bigint(20) NOT NULL DEFAULT '0' COMMENT '采购价格(分)',
  `wholesale_price` bigint(20) NOT NULL DEFAULT '0' COMMENT '销售价格(分)',
  `aluminum_height_diff` bigint(20) NOT NULL DEFAULT '0' COMMENT '铝材高差值(mm)',
  `aluminum_width_diff` bigint(20) NOT NULL DEFAULT '0' COMMENT '铝材宽差值(mm)',
  `glass_height_diff` bigint(20) NOT NULL DEFAULT '0' COMMENT '玻璃高差值(mm)',
  `glass_width_diff` bigint(20) NOT NULL DEFAULT '0' COMMENT '玻璃宽差值(mm)',
  `remark` varchar(100) NOT NULL DEFAULT '' COMMENT '备注',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态:1启用,0禁用',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户id',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_barcode` (`barcode`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';
CREATE TABLE IF NOT EXISTS `jsh_sequence` (
  `seq_name` varchar(50) NOT NULL COMMENT '序列名称',
  `min_value` bigint(20) NOT NULL COMMENT '最小值',
  `max_value` bigint(20) NOT NULL COMMENT '最大值',
  `current_val` bigint(20) NOT NULL COMMENT '当前值',
  `increment_val` int(11) NOT NULL DEFAULT '1' COMMENT '增长步数',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`seq_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='单据编号表';
CREATE TABLE IF NOT EXISTS `jsh_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `platform` varchar(8) NOT NULL DEFAULT '' COMMENT '平台',
  `version` varchar(8) NOT NULL DEFAULT '' COMMENT '版本号',
  `version_no` bigint(10) NOT NULL DEFAULT '0' COMMENT '版本编号',
  `description` varchar(500) NOT NULL DEFAULT '' COMMENT '描述',
  `download_url` varchar(128) NOT NULL DEFAULT '' COMMENT '下载地址',
  `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` int(1) NOT NULL DEFAULT '0' COMMENT '删除状态:0未删除,1删除',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态:1启用,0禁用',
  PRIMARY KEY (`id`),
  KEY `idx_version` (`version`,`platform`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='版本号表';

-- 单据编号序列种子（新装环境可直接开单；迁移脚本会用生产当前值覆盖）
INSERT IGNORE INTO `jsh_sequence` (`seq_name`, `min_value`, `max_value`, `current_val`, `increment_val`, `remark`) VALUES
('barcode_number_seq', 1001, 999999999999999999, 1001, 1, '条码sequence'),
('order_number_seq', 1, 999999999999999999, 1, 1, '订单编号sequence');

-- ---------------------------------------------------------------------
-- 2. ERP 字典（4 个）及字典项
-- ---------------------------------------------------------------------
INSERT IGNORE INTO `sys_dict` (`id`, `dict_name`, `dict_code`, `description`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `type`) VALUES
('1489753899752067074','方向','direction','柜门方向',0,'admin','2022-02-12 00:22:12','admin','2022-02-12 00:22:12',0),
('1490217214244601858','商品扩展属性','product_extend','商品扩展属性: 拉/抽/条',0,'admin','2022-02-12 00:22:12','admin','2022-02-12 00:22:12',0),
('1491400616654225410','产品类型','product_type','铝材/玻璃',0,'admin','2022-02-12 00:22:12','admin','2022-02-12 00:22:12',0),
('1489640150625984513','状态','record_status','通用记录状态',0,'admin','2022-02-12 00:22:12','admin','2022-02-12 00:22:12',0);

-- 字典项：仅当所属字典（按 id）存在且该值未存在时插入
INSERT INTO `sys_dict_item` (`id`, `dict_id`, `item_text`, `item_value`, `description`, `sort_order`, `status`, `create_by`, `create_time`, `update_by`, `update_time`)
SELECT t.* FROM (
  SELECT '1489754206141779969' id,'1489753899752067074' dict_id,'无' item_text,'0' item_value,'' description,1 sort_order,1 status,'admin' create_by,'2022-02-12 00:22:16' create_time,'admin' update_by,'2022-04-03 16:09:31' update_time
  UNION ALL SELECT '1489754075728285698','1489753899752067074','双开','1','',2,1,'admin','2022-02-12 00:22:16','admin','2022-02-12 00:22:16'
  UNION ALL SELECT '1489754120234045441','1489753899752067074','左开','2','',3,1,'admin','2022-02-12 00:22:16','admin','2022-02-12 00:22:16'
  UNION ALL SELECT '1489754157915672577','1489753899752067074','右开','3','',4,1,'admin','2022-02-12 00:22:16','admin','2022-02-12 00:22:16'
  UNION ALL SELECT '1491253079385354241','1490217214244601858','无','0','',1,1,'admin','2022-02-12 00:22:16','admin','2023-07-17 23:17:10'
  UNION ALL SELECT '1490217319085424642','1490217214244601858','抽','1','',2,1,'admin','2022-02-12 00:22:16','admin','2022-02-12 00:22:16'
  UNION ALL SELECT '1490217282473345026','1490217214244601858','拉','2','',3,1,'admin','2022-02-12 00:22:16','admin','2022-02-12 00:22:16'
  UNION ALL SELECT '1735304604017594369','1490217214244601858','拉(免费)','5','',3,1,'admin','2023-12-14 22:23:53','admin','2023-12-14 22:37:15'
  UNION ALL SELECT '1490217351255736322','1490217214244601858','条','3','',4,1,'admin','2022-02-12 00:22:16','admin','2022-02-12 00:22:16'
  UNION ALL SELECT '1537766953288531970','1490217214244601858','味','4','调味蓝',5,1,'admin','2022-06-17 19:59:47','admin','2023-12-14 22:24:04'
  UNION ALL SELECT 'ca7124c6c64b11f186870242ac120003','1490217214244601858','透气网','6','透气网 10元/个',6,1,'admin','2026-10-12 22:47:04',NULL,'2026-10-12 22:47:04'
  UNION ALL SELECT '1491400666226704386','1491400616654225410','铝材','1','',1,1,'admin','2022-02-12 00:22:16','admin','2022-02-12 00:22:16'
  UNION ALL SELECT '1491400706177449986','1491400616654225410','玻璃','2','',1,1,'admin','2022-02-12 00:22:16','admin','2022-02-12 00:22:16'
  UNION ALL SELECT '1489640253197688834','1489640150625984513','启用','1','',1,1,'admin','2022-02-12 00:22:16','admin','2022-02-12 00:22:16'
  UNION ALL SELECT '1489640322613420033','1489640150625984513','禁用','0','禁用',2,1,'admin','2022-02-12 00:22:16','admin','2022-02-12 00:22:16'
) t
WHERE EXISTS (SELECT 1 FROM `sys_dict` d WHERE d.id = t.dict_id)
  AND NOT EXISTS (SELECT 1 FROM `sys_dict_item` i WHERE i.id = t.id OR (i.dict_id = t.dict_id AND i.item_value = t.item_value));

-- ---------------------------------------------------------------------
-- 3. 菜单（3 个目录 + 4 个页面；3.0 无 ERP 按钮权限）
--    url / id / 排序沿用 3.0 生产；component / icon 改为 Vue3 前端约定
-- ---------------------------------------------------------------------
INSERT IGNORE INTO `sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `is_route`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`) VALUES
('1489610778376429569','','订单管理','/business/order','layouts/default/index',1,NULL,NULL,0,NULL,'1',0.10,0,'ant-design:solution-outlined',0,0,0,0,NULL,'admin','2022-02-04 08:44:20','admin','2022-02-07 04:17:11',0,0,'1',0),
('1489611118064721922','1489610778376429569','订单信息','/business/order/list','business/order/JshOrderList',1,NULL,NULL,1,NULL,'1',1.00,0,NULL,1,0,0,0,NULL,'admin','2022-02-04 08:45:41','admin','2022-02-07 03:39:56',0,0,'1',0),
('1489611118064721923','1489610778376429569','订单统计','/business/order/summary','business/order/JshOrderSummaryList',1,NULL,NULL,1,NULL,'1',2.00,0,NULL,1,0,0,0,NULL,'admin','2022-02-04 08:45:41','admin','2022-02-07 03:39:56',0,0,'1',0),
('1489624225004699650','','商品管理','/business/product','layouts/default/index',1,NULL,NULL,0,NULL,'1',0.80,0,'ant-design:rocket-outlined',0,0,0,0,NULL,'admin','2022-02-04 09:37:46','admin','2022-02-04 09:38:56',0,0,'1',0),
('1489624438280863746','1489624225004699650','商品信息','/business/product/list','business/product/JshProductList',1,NULL,NULL,1,NULL,'1',1.00,0,NULL,1,0,0,0,NULL,'admin','2022-02-04 09:38:37','admin','2022-02-07 03:40:06',0,0,'1',0),
('1489122625907675137','','客户管理','/business/customer','layouts/default/index',1,NULL,NULL,0,NULL,'1',0.90,0,'ant-design:team-outlined',0,0,0,0,NULL,'admin','2022-02-03 00:24:35','admin','2022-04-05 22:08:45',0,0,'1',0),
('1489123190154809346','1489122625907675137','客户信息','/business/customer/list','business/customer/JshCustomerList',1,NULL,NULL,1,NULL,'1',1.00,0,NULL,1,0,0,0,NULL,'admin','2022-02-03 00:26:50','admin','2022-02-07 03:40:52',0,0,'1',0);

-- ---------------------------------------------------------------------
-- 4. 角色 erp（id 沿用 3.0），授权 ERP 菜单；admin 角色（按 role_code 取新库 id）同样授权
-- ---------------------------------------------------------------------
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `description`, `create_by`, `create_time`, `update_by`, `update_time`)
SELECT '1493105192382017537','ERP管理员','erp','ERP管理员','admin','2022-02-14 00:09:53','admin','2022-02-14 00:11:08'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role` WHERE `role_code` = 'erp' OR `id` = '1493105192382017537');

INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT MD5(CONCAT(r.id, '-', p.id)), r.id, p.id, NULL, NOW(), '127.0.0.1'
FROM `sys_role` r
JOIN `sys_permission` p ON p.id IN ('1489610778376429569','1489611118064721922','1489611118064721923',
                                    '1489624225004699650','1489624438280863746',
                                    '1489122625907675137','1489123190154809346')
WHERE r.role_code IN ('erp', 'admin')
  AND NOT EXISTS (SELECT 1 FROM `sys_role_permission` x WHERE x.role_id = r.id AND x.permission_id = p.id);

-- erp 角色在 3.0 拥有“首页”；3.9.5 对应为“主页”目录 + “首页”(/dashboard/analysis)，存在才授权
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT MD5(CONCAT(r.id, '-', p.id)), r.id, p.id, NULL, NOW(), '127.0.0.1'
FROM `sys_role` r
JOIN `sys_permission` p ON p.id IN ('1438108176273760258', '9502685863ab87f0ad1134142788a385')
WHERE r.role_code = 'erp'
  AND NOT EXISTS (SELECT 1 FROM `sys_role_permission` x WHERE x.role_id = r.id AND x.permission_id = p.id);

-- ---------------------------------------------------------------------
-- 5. 定时任务：订单统计（id / cron / 参数 / 描述沿用 3.0，类名改为 ERP 模块）
--    status = 0（运行）：Quartz 用 JDBC 存储，仅写 sys_quartz_job 不会真正调度；
--    由 ErpQuartzJobBootstrap 在应用启动时自动注册到 Quartz（qrtz_* 中不存在时才注册，status!=0 不处理）
-- ---------------------------------------------------------------------
INSERT INTO `sys_quartz_job` (`id`, `create_by`, `create_time`, `del_flag`, `update_by`, `update_time`, `job_class_name`, `cron_expression`, `parameter`, `description`, `status`)
SELECT '1511267922611875842','admin','2022-04-05 04:02:05',0,'admin','2022-04-10 18:30:22',
       'org.jeecg.modules.business.job.StatisticOrderJob','0 */10 * * * ? *',NULL,'订单数据统计任务',0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_quartz_job` WHERE `id` = '1511267922611875842' OR `job_class_name` = 'org.jeecg.modules.business.job.StatisticOrderJob');
