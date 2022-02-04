-- ----------------------------
-- Table structure for jsh_product
-- ----------------------------
DROP TABLE IF EXISTS `jsh_product`;
CREATE TABLE `jsh_product` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',
                                `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                `del_flag`  int(1) NOT NULL DEFAULT 0 COMMENT '删除状态:0未删除,1删除',
                                `name` varchar(50) NOT NULL DEFAULT '' COMMENT '名称',
                                `color` varchar(50) NOT NULL DEFAULT '' COMMENT '颜色',
                                `bar_code` varchar(50) NOT NULL DEFAULT '' COMMENT '商品条码',
                                `purchase_price` bigint(20) NOT NULL COMMENT '采购价格',
                                `wholesale_price` bigint(20) NOT NULL COMMENT '销售价格',
                                `remark` varchar(100) COMMENT '备注',
                                `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态:1启用,0禁用',
                                `tenant_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '租户id',
                                PRIMARY KEY (`id`),
                                KEY `idx_name` (`name`),
                                KEY `idx_bar_code` (`bar_code`),
                                KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';

-- ----------------------------
-- Table structure for jsh_customer
-- ----------------------------
DROP TABLE IF EXISTS `jsh_customer`;
CREATE TABLE `jsh_customer`  (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
                                       `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',
                                       `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                       `del_flag`  int(1) NOT NULL DEFAULT 0 COMMENT '删除状态:0未删除,1删除',
                                       `name` varchar(32) NOT NULL DEFAULT '' COMMENT '客户名字',
                                       `sex` tinyint(1) NOT NULL DEFAULT 0 COMMENT '客户性别:1男,2女,0未知',
                                       `address` varchar(300) NOT NULL DEFAULT '' COMMENT '常用地址',
                                       `phone` varchar(32) NOT NULL DEFAULT '' COMMENT '手机',
                                       `tenant_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '租户id',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `idx_name` (`name`),
                                       KEY `idx_tenant_id` (`tenant_id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- ----------------------------
-- Records of jsh_customer
-- ----------------------------
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '公平', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '光大', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '好太太', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '恒兴', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '红福', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '洪艺', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '华伟', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '华艺', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '简一', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '金玲', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '快鱼', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '领域', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '龙秀', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '美缝', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '美好', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '诺艺', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '欧雅', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '润泽', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '四方', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '天工', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '天一', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '天宇', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '兄弟', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '阳光天一', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '耀强', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '一方', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '园林', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '赵德义', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '振鑫', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '振兴', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '周广辉', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '周磊', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '周萌', 1, '', '13661685114', 0);
INSERT INTO `jsh_customer`(`create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`) VALUES ('admin', '2022-02-04 10:00:00', 'admin', '2022-02-04 01:26:06', 0, '周羽', 1, '', '13661685114', 0);

-- ----------------------------
-- Table structure for jsh_order
-- ----------------------------
DROP TABLE IF EXISTS `jsh_order`;
CREATE TABLE `jsh_order`  (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                              `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
                              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',
                              `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                              `del_flag`  int(1) NOT NULL DEFAULT 0 COMMENT '删除状态:0未删除,1删除',
                              `order_code` varchar(32) NOT NULL DEFAULT '' COMMENT '订单编码',
                              `customer_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '客户id',
                              `total_price` bigint(20) NOT NULL DEFAULT 0 COMMENT '订单总额',
                              `order_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
                              `address` varchar(128) COMMENT '地址',
                              `remark` varchar(500) NOT NULL DEFAULT '' COMMENT '备注',
                              `tenant_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '租户id',
                              PRIMARY KEY (`id`) USING BTREE,
                              KEY `idx_order_code` (`order_code`),
                              KEY `idx_tenant_id` (`tenant_id`),
                              KEY `idx_customer_id` (`customer_id`),
                              KEY `idx_order_time` (`order_time`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------
-- Table structure for jsh_order_product
-- ----------------------------
DROP TABLE IF EXISTS `jsh_order_product`;
CREATE TABLE `jsh_order_product`  (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',
                                    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `del_flag`  int(1) NOT NULL DEFAULT 0 COMMENT '删除状态:0未删除,1删除',
                                    `customer_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '客户id',
                                    `order_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '订单ID',
                                    `product_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '产品id',
                                    `width` bigint(20) NOT NULL DEFAULT 0 COMMENT '宽',
                                    `height` bigint(20) NOT NULL DEFAULT 0 COMMENT '高',
                                    `num` int(11) NOT NULL DEFAULT 0 COMMENT '数量',
                                    `direction` tinyint(1) NOT NULL DEFAULT 0 COMMENT '方向:0其他,1双开,2左开,3右开',
                                    `color` varchar(50) NOT NULL DEFAULT '' COMMENT '颜色',
                                    `price` bigint(20) NOT NULL DEFAULT 0 COMMENT '单价',
                                    `total_price` bigint(20) NOT NULL DEFAULT 0 COMMENT '总价',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    KEY `idx_order_id` (`order_id`),
                                    KEY `idx_product_id` (`product_id`),
                                    KEY `idx_customer_id` (`customer_id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品表';

-- ----------------------------
-- Table structure for jsh_order_product_extend
-- ----------------------------
DROP TABLE IF EXISTS `jsh_order_product_extend`;
CREATE TABLE `jsh_order_product_extend`  (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',
                                    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `del_flag`  int(1) NOT NULL DEFAULT 0 COMMENT '删除状态:0未删除,1删除',
                                    `order_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '订单ID',
                                    `order_product_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '订单产品表ID',
                                    `type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '类型:1拉,2抽,3条',
                                    `num` int(11) NOT NULL DEFAULT 0 COMMENT '数量',
                                     PRIMARY KEY (`id`) USING BTREE,
                                    KEY `idx_order_id` (`order_id`),
                                    KEY `idx_order_product_id` (`order_product_id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品扩展信息表';

-- ----------------------------
-- Table structure for jsh_order_product_detail
-- ----------------------------
DROP TABLE IF EXISTS `jsh_order_product_detail`;
CREATE TABLE `jsh_order_product_detail`  (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                      `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
                                      `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',
                                      `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                      `del_flag`  int(1) NOT NULL DEFAULT 0 COMMENT '删除状态:0未删除,1删除',
                                      `customer_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '客户id',
                                      `order_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '订单ID',
                                      `order_product_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '订单产品表ID',
                                      `product_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '产品id',
                                      `type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '类型:1铝材,2玻璃',
                                      `reference_width` bigint(20) NOT NULL DEFAULT 0 COMMENT '参考宽度',
                                      `reference_height` bigint(20) NOT NULL DEFAULT 0 COMMENT '参考高度',
                                      `width` bigint(20) NOT NULL DEFAULT 0 COMMENT '宽',
                                      `height` bigint(20) NOT NULL DEFAULT 0 COMMENT '高',
                                      `num` int(11) NOT NULL DEFAULT 0 COMMENT '数量',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      KEY `idx_order_id` (`order_id`),
                                      KEY `idx_order_product_id` (`order_product_id`),
                                      KEY `idx_product_id` (`product_id`),
                                      KEY `idx_customer_id` (`customer_id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品明细表';
