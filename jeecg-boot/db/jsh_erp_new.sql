-- ----------------------------
-- Table structure for jsh_product
-- ----------------------------
DROP TABLE IF EXISTS `jsh_product`;
CREATE TABLE `jsh_product`
(
    `id`                   bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `create_by`            varchar(50)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`          datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`            varchar(50)  NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`          datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`             int(1) NOT NULL DEFAULT 0 COMMENT '删除状态:0未删除,1删除',
    `name`                 varchar(50)  NOT NULL DEFAULT '' COMMENT '名称',
    `barcode`              varchar(50)  NOT NULL DEFAULT '' COMMENT '商品条码',
    `purchase_price`       bigint(20) NOT NULL DEFAULT 0 COMMENT '采购价格(分)',
    `wholesale_price`      bigint(20) NOT NULL DEFAULT 0 COMMENT '销售价格(分)',
    `aluminum_height_diff` bigint(20) NOT NULL DEFAULT 0 COMMENT '铝材高差值(mm)',
    `aluminum_width_diff`  bigint(20) NOT NULL DEFAULT 0 COMMENT '铝材宽差值(mm)',
    `glass_height_diff`    bigint(20) NOT NULL DEFAULT 0 COMMENT '玻璃高差值(mm)',
    `glass_width_diff`     bigint(20) NOT NULL DEFAULT 0 COMMENT '玻璃宽差值(mm)',
    `remark`               varchar(100) NOT NULL DEFAULT '' COMMENT '备注',
    `status`               tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态:1启用,0禁用',
    `tenant_id`            bigint(20) NOT NULL DEFAULT 0 COMMENT '租户id',
    PRIMARY KEY (`id`),
    KEY                    `idx_name` (`name`),
    KEY                    `idx_barcode` (`barcode`),
    KEY                    `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';

-- ----------------------------
-- Records of jsh_product
-- ----------------------------
INSERT INTO `jsh_product`(`id`, `create_by`, `update_by`, `del_flag`, `name`, `barcode`, `purchase_price`,
                          `wholesale_price`, `aluminum_height_diff`, `aluminum_width_diff`, `glass_height_diff`,
                          `glass_width_diff`, `remark`, `status`, `tenant_id`)
VALUES (1, 'admin', 'admin', 0, '普通90', '1001', 9000, 9000, -43, -12, -35, -4, '', 1, 1001);
INSERT INTO `jsh_product`(`id`, `create_by`, `update_by`, `del_flag`, `name`, `barcode`, `purchase_price`,
                          `wholesale_price`, `aluminum_height_diff`, `aluminum_width_diff`, `glass_height_diff`,
                          `glass_width_diff`, `remark`, `status`, `tenant_id`)
VALUES (2, 'admin', 'admin', 0, '普通100', '1002', 10000, 10000, -43, -12, -35, -4, '', 1, 1001);
INSERT INTO `jsh_product`(`id`, `create_by`, `update_by`, `del_flag`, `name`, `barcode`, `purchase_price`,
                          `wholesale_price`, `aluminum_height_diff`, `aluminum_width_diff`, `glass_height_diff`,
                          `glass_width_diff`, `remark`, `status`, `tenant_id`)
VALUES (3, 'admin', 'admin', 0, '普通110', '1003', 11000, 11000, -43, -12, -35, -4, '', 1, 1001);
INSERT INTO `jsh_product`(`id`, `create_by`, `update_by`, `del_flag`, `name`, `barcode`, `purchase_price`,
                          `wholesale_price`, `aluminum_height_diff`, `aluminum_width_diff`, `glass_height_diff`,
                          `glass_width_diff`, `remark`, `status`, `tenant_id`)
VALUES (4, 'admin', 'admin', 0, '连体型', '1004', 13000, 13000, -29, 3, -33, -2, '', 1, 1001);
INSERT INTO `jsh_product`(`id`, `create_by`, `update_by`, `del_flag`, `name`, `barcode`, `purchase_price`,
                          `wholesale_price`, `aluminum_height_diff`, `aluminum_width_diff`, `glass_height_diff`,
                          `glass_width_diff`, `remark`, `status`, `tenant_id`)
VALUES (5, 'admin', 'admin', 0, 'G型', '1005', 13000, 13000, -29, 3, -33, -2, '', 1, 1001);
INSERT INTO `jsh_product`(`id`, `create_by`, `update_by`, `del_flag`, `name`, `barcode`, `purchase_price`,
                          `wholesale_price`, `aluminum_height_diff`, `aluminum_width_diff`, `glass_height_diff`,
                          `glass_width_diff`, `remark`, `status`, `tenant_id`)
VALUES (6, 'admin', 'admin', 0, 'M型', '1006', 14000, 14000, 3, 3, -2, -2, '', 1, 1001);
INSERT INTO `jsh_product`(`id`, `create_by`, `update_by`, `del_flag`, `name`, `barcode`, `purchase_price`,
                          `wholesale_price`, `aluminum_height_diff`, `aluminum_width_diff`, `glass_height_diff`,
                          `glass_width_diff`, `remark`, `status`, `tenant_id`)
VALUES (7, 'admin', 'admin', 0, 'F型', '1007', 14000, 14000, 3, 3, -2, -2, '', 1, 1001);
INSERT INTO `jsh_product`(`id`, `create_by`, `update_by`, `del_flag`, `name`, `barcode`, `purchase_price`,
                          `wholesale_price`, `aluminum_height_diff`, `aluminum_width_diff`, `glass_height_diff`,
                          `glass_width_diff`, `remark`, `status`, `tenant_id`)
VALUES (8, 'admin', 'admin', 0, '笑脸', '1008', 14000, 14000, 3, 3, -2, -2, '', 1, 1001);
INSERT INTO `jsh_product`(`id`, `create_by`, `update_by`, `del_flag`, `name`, `barcode`, `purchase_price`,
                          `wholesale_price`, `aluminum_height_diff`, `aluminum_width_diff`, `glass_height_diff`,
                          `glass_width_diff`, `remark`, `status`, `tenant_id`)
VALUES (9, 'admin', 'admin', 0, '拇指型', '1009', 14000, 14000, 3, 3, -2, -2, '', 1, 1001);
INSERT INTO `jsh_product`(`id`, `create_by`, `update_by`, `del_flag`, `name`, `barcode`, `purchase_price`,
                          `wholesale_price`, `aluminum_height_diff`, `aluminum_width_diff`, `glass_height_diff`,
                          `glass_width_diff`, `remark`, `status`, `tenant_id`)
VALUES (10, 'admin', 'admin', 0, '月牙型', '1010', 14000, 14000, 3, 3, -2, -2, '', 1, 1001);

-- ----------------------------
-- Table structure for jsh_customer
-- ----------------------------
DROP TABLE IF EXISTS `jsh_customer`;
CREATE TABLE `jsh_customer`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `create_by`   varchar(50)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(50)  NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`    int(1) NOT NULL DEFAULT 0 COMMENT '删除状态:0未删除,1删除',
    `name`        varchar(32)  NOT NULL DEFAULT '' COMMENT '客户名字',
    `sex`         tinyint(1) NOT NULL DEFAULT 0 COMMENT '客户性别:1男,2女,0未知',
    `address`     varchar(300) NOT NULL DEFAULT '' COMMENT '地址',
    `phone`       varchar(32)  NOT NULL DEFAULT '' COMMENT '手机',
    `tenant_id`   bigint(20) NOT NULL DEFAULT 0 COMMENT '租户id',
    PRIMARY KEY (`id`) USING BTREE,
    KEY           `idx_name` (`name`),
    KEY           `idx_tenant_id` (`tenant_id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- ----------------------------
-- Records of jsh_customer
-- ----------------------------
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '公平', 1, '钟楼', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '光大', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '好太太', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '恒兴', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '红福', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '洪艺', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '华伟', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '华艺', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '简一', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '金玲', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '快鱼', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '领域', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '龙秀', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '美缝', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '美好', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '诺艺', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '欧雅', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '润泽', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '四方', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '天工', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '天一', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '天宇', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '兄弟', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '阳光天一', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '耀强', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '一方', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '园林', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '赵德义', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '振鑫', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '振兴', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '周广辉', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '周磊', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '周萌', 1, '', '', 1001);
INSERT INTO `jsh_customer`(`create_by`, `update_by`, `del_flag`, `name`, `sex`, `address`, `phone`, `tenant_id`)
VALUES ('admin', 'admin', 0, '周羽', 1, '', '', 1001);

-- ----------------------------
-- Table structure for jsh_customer_address
-- ----------------------------
DROP TABLE IF EXISTS `jsh_customer_address`;
CREATE TABLE `jsh_customer_address`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `create_by`   varchar(50)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(50)  NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`    int(1) NOT NULL DEFAULT 0 COMMENT '删除状态:0未删除,1删除',
    `customer_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '客户id',
    `address`     varchar(300) NOT NULL DEFAULT '' COMMENT '地址',
    PRIMARY KEY (`id`) USING BTREE,
    KEY           `idx_customer_id` (`customer_id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户地址表';

-- ----------------------------
-- Table structure for jsh_order
-- ----------------------------
DROP TABLE IF EXISTS `jsh_order`;
CREATE TABLE `jsh_order`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `create_by`   varchar(50)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(50)  NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`    int(1) NOT NULL DEFAULT 0 COMMENT '删除状态:0未删除,1删除',
    `order_code`  varchar(32)  NOT NULL DEFAULT '' COMMENT '订单编码',
    `customer_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '客户id',
    `total_area`  bigint(20) NOT NULL DEFAULT 0 COMMENT '总面积(mm²)',
    `total_price` bigint(20) NOT NULL DEFAULT 0 COMMENT '订单总额(分)',
    `order_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    `address`     varchar(128) COMMENT '地址',
    `remark`      varchar(500) NOT NULL DEFAULT '' COMMENT '备注',
    `tenant_id`   bigint(20) NOT NULL DEFAULT 0 COMMENT '租户id',
    PRIMARY KEY (`id`) USING BTREE,
    KEY           `idx_order_code` (`order_code`),
    KEY           `idx_tenant_id` (`tenant_id`),
    KEY           `idx_customer_id` (`customer_id`),
    KEY           `idx_order_time` (`order_time`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------
-- Table structure for jsh_order_product
-- ----------------------------
DROP TABLE IF EXISTS `jsh_order_product`;
CREATE TABLE `jsh_order_product`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `create_by`   varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`    int(1) NOT NULL DEFAULT 0 COMMENT '删除状态:0未删除,1删除',
    `customer_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '客户id',
    `order_id`    bigint(20) NOT NULL DEFAULT 0 COMMENT '订单ID',
    `product_id`  bigint(20) NOT NULL DEFAULT 0 COMMENT '产品id',
    `width`       bigint(20) NOT NULL DEFAULT 0 COMMENT '宽(mm)',
    `height`      bigint(20) NOT NULL DEFAULT 0 COMMENT '高(mm)',
    `num`         int(11) NOT NULL DEFAULT 0 COMMENT '数量',
    `direction`   tinyint(1) NOT NULL DEFAULT 0 COMMENT '方向:0其他,1双开,2左开,3右开',
    `color`       varchar(50) NOT NULL DEFAULT '' COMMENT '颜色',
    `glass_color` varchar(50) NOT NULL DEFAULT '' COMMENT '玻璃颜色',
    `price`       bigint(20) NOT NULL DEFAULT 0 COMMENT '单价(分)',
    `total_area`  bigint(20) NOT NULL DEFAULT 0 COMMENT '总面积(mm²)',
    `total_price` bigint(20) NOT NULL DEFAULT 0 COMMENT '总价(分)',
    `extend_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '扩展类型:1拉,2抽,3条',
    `extend_num`  int(11) NOT NULL DEFAULT 0 COMMENT '扩展数量',
    PRIMARY KEY (`id`) USING BTREE,
    KEY           `idx_order_id` (`order_id`),
    KEY           `idx_product_id` (`product_id`),
    KEY           `idx_customer_id` (`customer_id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品表';

-- ----------------------------
-- Table structure for jsh_order_product_extend
-- ----------------------------
DROP TABLE IF EXISTS `jsh_order_product_extend`;
CREATE TABLE `jsh_order_product_extend`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `create_by`        varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`        varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`         int(1) NOT NULL DEFAULT 0 COMMENT '删除状态:0未删除,1删除',
    `order_id`         bigint(20) NOT NULL DEFAULT 0 COMMENT '订单ID',
    `order_product_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '订单产品表ID',
    `type`             tinyint(1) NOT NULL DEFAULT 0 COMMENT '类型:1拉,2抽,3条',
    `num`              int(11) NOT NULL DEFAULT 0 COMMENT '数量',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                `idx_order_id` (`order_id`),
    KEY                `idx_order_product_id` (`order_product_id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品扩展信息表';

-- ----------------------------
-- Table structure for jsh_order_product_detail
-- ----------------------------
DROP TABLE IF EXISTS `jsh_order_product_detail`;
CREATE TABLE `jsh_order_product_detail`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `create_by`        varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`        varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`         int(1) NOT NULL DEFAULT 0 COMMENT '删除状态:0未删除,1删除',
    `customer_id`      bigint(20) NOT NULL DEFAULT 0 COMMENT '客户id',
    `order_id`         bigint(20) NOT NULL DEFAULT 0 COMMENT '订单ID',
    `order_product_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '订单产品表ID',
    `product_id`       bigint(20) NOT NULL DEFAULT 0 COMMENT '产品id',
    `type`             tinyint(1) NOT NULL DEFAULT 0 COMMENT '类型:1铝材,2玻璃',
    `reference_width`  bigint(20) NOT NULL DEFAULT 0 COMMENT '参考宽度',
    `reference_height` bigint(20) NOT NULL DEFAULT 0 COMMENT '参考高度',
    `width`            bigint(20) NOT NULL DEFAULT 0 COMMENT '宽',
    `height`           bigint(20) NOT NULL DEFAULT 0 COMMENT '高',
    `num`              int(11) NOT NULL DEFAULT 0 COMMENT '数量',
    `direction`        tinyint(1) NOT NULL DEFAULT 0 COMMENT '方向:0其他,1双开,2左开,3右开',
    `color`            varchar(50) NOT NULL DEFAULT '' COMMENT '颜色',
    `extend_type`      tinyint(1) NOT NULL DEFAULT 0 COMMENT '扩展类型:1拉,2抽,3条',
    `extend_num`       int(11) NOT NULL DEFAULT 0 COMMENT '扩展数量',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                `idx_order_id` (`order_id`),
    KEY                `idx_order_product_id` (`order_product_id`),
    KEY                `idx_product_id` (`product_id`),
    KEY                `idx_customer_id` (`customer_id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品明细表';

-- ----------------------------
-- Table structure for jsh_sequence
-- ----------------------------
DROP TABLE IF EXISTS `jsh_sequence`;
CREATE TABLE `jsh_sequence`
(
    `seq_name`      varchar(50) NOT NULL COMMENT '序列名称',
    `min_value`     bigint(20) NOT NULL COMMENT '最小值',
    `max_value`     bigint(20) NOT NULL COMMENT '最大值',
    `current_val`   bigint(20) NOT NULL COMMENT '当前值',
    `increment_val` int(11) NOT NULL DEFAULT '1' COMMENT '增长步数',
    `remark`        varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`seq_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='单据编号表';

-- ----------------------------
-- Records of jsh_sequence
-- ----------------------------
INSERT INTO `jsh_sequence`
VALUES ('order_number_seq', '1', '999999999999999999', '0', '1', '订单编号sequence');
INSERT INTO `jsh_sequence`
VALUES ('barcode_number_seq', '1001', '999999999999999999', '1010', '1', '条码sequence');

-- ----------------------------
-- Table structure for jsh_version
-- ----------------------------
DROP TABLE IF EXISTS `jsh_version`;
CREATE TABLE `jsh_version`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `platform`    varchar(8)   NOT NULL DEFAULT '' COMMENT '平台',
    `version`     varchar(8)   NOT NULL DEFAULT '' COMMENT '版本号',
    `version_no`  bigint(10) NOT NULL DEFAULT 0 COMMENT '版本编号',
    `description` varchar(500) NOT NULL DEFAULT '' COMMENT '描述',
    `download_url` varchar(128) NOT NULL DEFAULT '' COMMENT '下载地址',
    `create_by`   varchar(50)  NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(50)  NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`    int(1) NOT NULL DEFAULT 0 COMMENT '删除状态:0未删除,1删除',
    `status`      tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态:1启用,0禁用',
    PRIMARY KEY (`id`),
    KEY           `idx_version` (`version`, `platform`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='版本号表';

-- ----------------------------
-- Records of jsh_sequence
-- ----------------------------
INSERT INTO `jeecg-boot`.`jsh_version`(`id`, `platform`, `version`, `version_no`, `description`, `download_url`, `create_by`, `create_time`,
                                       `update_by`, `update_time`, `del_flag`, `status`)
VALUES (1, 'darwin', '1.0.0', 1001000000, '1. 人更新修复\n2. 修复部分bug', 'https://erp.jijianxia.com/electron/app.zip', '', '2022-03-07 22:33:21', '', '2022-03-07 22:33:21', 0, 1);
