-- =====================================================================
-- ERP 3.0 -> 3.9.5 数据迁移（一次性、可重复执行）
--   源库 jeecg_old（3.0 生产库），目标库 jeecg_v3（已执行上游基础脚本 + Flyway 3.9.5.100）
--   生产执行时库名替换方式见 db/erp/README.md
--   MySQL 5.7 兼容；mysql 客户端遇错即停（不要加 --force），断言失败会以
--   "Subquery returns more than 1 row" 报错中止
-- =====================================================================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------
-- 0. 前置断言
-- ---------------------------------------------------------------------
-- 0.1 目标库已跑过 ERP Flyway 脚本
SELECT IF((SELECT COUNT(*) FROM jeecg_v3.flyway_schema_history WHERE version = '3.9.5.100' AND success = 1) = 1,
          'ok: flyway 3.9.5.100', (SELECT 1 UNION ALL SELECT 2)) AS assert_flyway;
-- 0.2 rel_tenant_ids 最大分段数 <= 10（下方数字表只覆盖 1..10）
SELECT IF(IFNULL((SELECT MAX(1 + LENGTH(rel_tenant_ids) - LENGTH(REPLACE(rel_tenant_ids, ',', '')))
                  FROM jeecg_old.sys_user WHERE rel_tenant_ids IS NOT NULL AND rel_tenant_ids <> ''), 0) <= 10,
          'ok: rel_tenant_ids <= 10', (SELECT 1 UNION ALL SELECT 2)) AS assert_tenant_segments;

-- ---------------------------------------------------------------------
-- 1. 业务表：整表搬运（先清空 Flyway 建好的空表 / 种子）
--    两边 DDL 相同（均取自生产 dump），列清单写死
-- ---------------------------------------------------------------------
TRUNCATE TABLE jeecg_v3.jsh_customer;
INSERT INTO jeecg_v3.jsh_customer (`id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `rank`, `status`, `tenant_id`)
SELECT `id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `rank`, `status`, `tenant_id` FROM jeecg_old.jsh_customer;

TRUNCATE TABLE jeecg_v3.jsh_customer_address;
INSERT INTO jeecg_v3.jsh_customer_address (`id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `customer_id`, `address`, `tenant_id`)
SELECT `id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `customer_id`, `address`, `tenant_id` FROM jeecg_old.jsh_customer_address;

TRUNCATE TABLE jeecg_v3.jsh_order;
INSERT INTO jeecg_v3.jsh_order (`id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `order_code`, `customer_id`, `total_area`, `total_price`, `order_time`, `address`, `remark`, `tenant_id`)
SELECT `id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `order_code`, `customer_id`, `total_area`, `total_price`, `order_time`, `address`, `remark`, `tenant_id` FROM jeecg_old.jsh_order;

TRUNCATE TABLE jeecg_v3.jsh_order_product;
INSERT INTO jeecg_v3.jsh_order_product (`id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `customer_id`, `order_id`, `product_id`, `width`, `height`, `num`, `direction`, `color`, `glass_color`, `price`, `extend_price`, `total_area`, `total_price`, `extend_type`, `extend_num`, `tenant_id`)
SELECT `id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `customer_id`, `order_id`, `product_id`, `width`, `height`, `num`, `direction`, `color`, `glass_color`, `price`, `extend_price`, `total_area`, `total_price`, `extend_type`, `extend_num`, `tenant_id` FROM jeecg_old.jsh_order_product;

TRUNCATE TABLE jeecg_v3.jsh_order_product_detail;
INSERT INTO jeecg_v3.jsh_order_product_detail (`id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `customer_id`, `order_id`, `order_product_id`, `product_id`, `type`, `reference_width`, `reference_height`, `width`, `height`, `num`, `direction`, `color`, `extend_type`, `extend_num`, `tenant_id`)
SELECT `id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `customer_id`, `order_id`, `order_product_id`, `product_id`, `type`, `reference_width`, `reference_height`, `width`, `height`, `num`, `direction`, `color`, `extend_type`, `extend_num`, `tenant_id` FROM jeecg_old.jsh_order_product_detail;

TRUNCATE TABLE jeecg_v3.jsh_order_product_extend;
INSERT INTO jeecg_v3.jsh_order_product_extend (`id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `order_id`, `order_product_id`, `type`, `num`, `tenant_id`)
SELECT `id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `order_id`, `order_product_id`, `type`, `num`, `tenant_id` FROM jeecg_old.jsh_order_product_extend;

TRUNCATE TABLE jeecg_v3.jsh_order_summary_daily;
INSERT INTO jeecg_v3.jsh_order_summary_daily (`id`, `time`, `tenant_id`, `customer_id`, `total_num`, `total_area`, `total_price`, `del_flag`, `update_time`, `create_time`)
SELECT `id`, `time`, `tenant_id`, `customer_id`, `total_num`, `total_area`, `total_price`, `del_flag`, `update_time`, `create_time` FROM jeecg_old.jsh_order_summary_daily;

TRUNCATE TABLE jeecg_v3.jsh_order_summary_monthly;
INSERT INTO jeecg_v3.jsh_order_summary_monthly (`id`, `time`, `tenant_id`, `customer_id`, `total_num`, `total_area`, `total_price`, `del_flag`, `update_time`, `create_time`)
SELECT `id`, `time`, `tenant_id`, `customer_id`, `total_num`, `total_area`, `total_price`, `del_flag`, `update_time`, `create_time` FROM jeecg_old.jsh_order_summary_monthly;

TRUNCATE TABLE jeecg_v3.jsh_product;
INSERT INTO jeecg_v3.jsh_product (`id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `barcode`, `purchase_price`, `wholesale_price`, `aluminum_height_diff`, `aluminum_width_diff`, `glass_height_diff`, `glass_width_diff`, `remark`, `status`, `tenant_id`)
SELECT `id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `barcode`, `purchase_price`, `wholesale_price`, `aluminum_height_diff`, `aluminum_width_diff`, `glass_height_diff`, `glass_width_diff`, `remark`, `status`, `tenant_id` FROM jeecg_old.jsh_product;

-- 序列当前值必须用生产值覆盖 Flyway 种子，否则单号回绕
TRUNCATE TABLE jeecg_v3.jsh_sequence;
INSERT INTO jeecg_v3.jsh_sequence (`seq_name`, `min_value`, `max_value`, `current_val`, `increment_val`, `remark`)
SELECT `seq_name`, `min_value`, `max_value`, `current_val`, `increment_val`, `remark` FROM jeecg_old.jsh_sequence;

-- jsh_version 保留老数据（老客户端平台键 darwin/win32 不动）
TRUNCATE TABLE jeecg_v3.jsh_version;
INSERT INTO jeecg_v3.jsh_version (`id`, `platform`, `version`, `version_no`, `description`, `download_url`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `status`)
SELECT `id`, `platform`, `version`, `version_no`, `description`, `download_url`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `status` FROM jeecg_old.jsh_version;

-- ---------------------------------------------------------------------
-- 2. 租户：生产租户覆盖同 id 的上游示例租户（新库独有列 del_flag 置 0 保证可登录）
-- ---------------------------------------------------------------------
INSERT INTO jeecg_v3.sys_tenant (`id`, `name`, `create_time`, `create_by`, `begin_date`, `end_date`, `status`, `del_flag`)
SELECT `id`, `name`, `create_time`, `create_by`, `begin_date`, `end_date`, `status`, 0 FROM jeecg_old.sys_tenant
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `create_time` = VALUES(`create_time`), `create_by` = VALUES(`create_by`),
  `begin_date` = VALUES(`begin_date`), `end_date` = VALUES(`end_date`), `status` = VALUES(`status`), `del_flag` = 0;

-- ---------------------------------------------------------------------
-- 3. 用户：以生产为准。密码算法 / 盐两版一致，直接搬（admin 即用生产密码覆盖）
--    3.1 先删掉新库中“生产没有”的上游示例用户及其关联（否则其默认密码可登录生产租户，且 work_no 唯一键冲突）
--    3.2 再删掉与生产同名 / 同 id 的用户后整行重插
-- ---------------------------------------------------------------------
DELETE FROM jeecg_v3.sys_user_role   WHERE user_id IN (SELECT id FROM jeecg_v3.sys_user WHERE username NOT IN (SELECT username FROM jeecg_old.sys_user));
DELETE FROM jeecg_v3.sys_user_depart WHERE user_id IN (SELECT id FROM jeecg_v3.sys_user WHERE username NOT IN (SELECT username FROM jeecg_old.sys_user));
DELETE FROM jeecg_v3.sys_user_tenant WHERE user_id IN (SELECT id FROM jeecg_v3.sys_user WHERE username NOT IN (SELECT username FROM jeecg_old.sys_user));
DELETE FROM jeecg_v3.sys_user WHERE username NOT IN (SELECT username FROM jeecg_old.sys_user);

DELETE FROM jeecg_v3.sys_user WHERE username IN (SELECT username FROM jeecg_old.sys_user) OR id IN (SELECT id FROM jeecg_old.sys_user);
-- 交集列（information_schema 求得）；老库独有 post / rel_tenant_ids 不搬（租户关系见第 4 步）；birthday 由 datetime 变 date
INSERT INTO jeecg_v3.sys_user (`id`, `username`, `realname`, `password`, `salt`, `avatar`, `birthday`, `sex`, `email`, `phone`, `org_code`, `status`, `del_flag`, `third_id`, `third_type`, `activiti_sync`, `work_no`, `telephone`, `create_by`, `create_time`, `update_by`, `update_time`, `user_identity`, `depart_ids`, `client_id`)
SELECT `id`, `username`, `realname`, `password`, `salt`, `avatar`, DATE(`birthday`), `sex`, `email`, `phone`, `org_code`, `status`, `del_flag`, `third_id`, `third_type`, `activiti_sync`, `work_no`, `telephone`, `create_by`, `create_time`, `update_by`, `update_time`, `user_identity`, `depart_ids`, `client_id`
FROM jeecg_old.sys_user;

-- ---------------------------------------------------------------------
-- 4. 用户-租户：rel_tenant_ids 逗号串拆行（5.7 无 JSON_TABLE，用 1..10 数字表）
--    表无唯一键：先删本脚本写入的行与迁移用户的上游残留关系，保证可重复执行
--    id 用 MD5(user_id-tenant_id)，确定性
-- ---------------------------------------------------------------------
DELETE FROM jeecg_v3.sys_user_tenant WHERE create_by = 'migrate' OR user_id IN (SELECT id FROM jeecg_old.sys_user);
INSERT INTO jeecg_v3.sys_user_tenant (`id`, `user_id`, `tenant_id`, `status`, `create_by`, `create_time`)
SELECT MD5(CONCAT(x.user_id, '-', x.tenant_id)), x.user_id, x.tenant_id, '1', 'migrate', NOW()
FROM (
  SELECT DISTINCT u.id AS user_id,
         CAST(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(u.rel_tenant_ids, ',', n.n), ',', -1)) AS UNSIGNED) AS tenant_id
  FROM jeecg_old.sys_user u
  JOIN (SELECT 1 n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
        UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10) n
    ON n.n <= 1 + LENGTH(u.rel_tenant_ids) - LENGTH(REPLACE(u.rel_tenant_ids, ',', ''))
  WHERE u.rel_tenant_ids IS NOT NULL AND u.rel_tenant_ids <> ''
) x
WHERE x.tenant_id > 0;

-- ---------------------------------------------------------------------
-- 5. 角色、用户角色、角色权限
-- ---------------------------------------------------------------------
-- 5.1 老库独有角色（按 role_code 判定；erp 已由 Flyway 建好）
INSERT INTO jeecg_v3.sys_role (`id`, `role_name`, `role_code`, `description`, `create_by`, `create_time`, `update_by`, `update_time`)
SELECT o.`id`, o.`role_name`, o.`role_code`, o.`description`, o.`create_by`, o.`create_time`, o.`update_by`, o.`update_time`
FROM jeecg_old.sys_role o
WHERE o.role_code NOT IN (SELECT role_code FROM jeecg_v3.sys_role)
  AND o.id NOT IN (SELECT id FROM jeecg_v3.sys_role);

-- 5.2 用户角色：迁移用户的角色整组替换；role_id 按 role_code 映射到新库 id
DELETE FROM jeecg_v3.sys_user_role WHERE user_id IN (SELECT id FROM jeecg_old.sys_user);
INSERT INTO jeecg_v3.sys_user_role (`id`, `user_id`, `role_id`)
SELECT ur.id, ur.user_id, nr.id
FROM jeecg_old.sys_user_role ur
JOIN jeecg_old.sys_role orr ON orr.id = ur.role_id
JOIN jeecg_v3.sys_role nr ON nr.role_code = orr.role_code
WHERE ur.user_id IN (SELECT id FROM jeecg_old.sys_user);

-- 5.3 角色权限：老库授权中，菜单 id 在新库仍存在的补上（3.0 独有菜单不迁移，见 README）
INSERT INTO jeecg_v3.sys_role_permission (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`)
SELECT MD5(CONCAT(nr.id, '-', rp.permission_id)), nr.id, rp.permission_id, NULL, NOW(), '127.0.0.1'
FROM jeecg_old.sys_role_permission rp
JOIN jeecg_old.sys_role orr ON orr.id = rp.role_id
JOIN jeecg_v3.sys_role nr ON nr.role_code = orr.role_code
WHERE rp.permission_id IN (SELECT id FROM jeecg_v3.sys_permission)
  AND NOT EXISTS (SELECT 1 FROM jeecg_v3.sys_role_permission x WHERE x.role_id = nr.id AND x.permission_id = rp.permission_id);

-- ---------------------------------------------------------------------
-- 6. 部门、用户部门
-- ---------------------------------------------------------------------
INSERT INTO jeecg_v3.sys_depart (`id`, `parent_id`, `depart_name`, `depart_name_en`, `depart_name_abbr`, `depart_order`, `description`, `org_category`, `org_type`, `org_code`, `mobile`, `fax`, `address`, `memo`, `status`, `del_flag`, `qywx_identifier`, `create_by`, `create_time`, `update_by`, `update_time`)
SELECT `id`, `parent_id`, `depart_name`, `depart_name_en`, `depart_name_abbr`, `depart_order`, `description`, `org_category`, `org_type`, `org_code`, `mobile`, `fax`, `address`, `memo`, `status`, `del_flag`, `qywx_identifier`, `create_by`, `create_time`, `update_by`, `update_time`
FROM jeecg_old.sys_depart o
WHERE o.id NOT IN (SELECT id FROM jeecg_v3.sys_depart)
  AND o.org_code NOT IN (SELECT org_code FROM jeecg_v3.sys_depart);

-- 新库独有列 iz_leaf：按是否有子部门重算（仅迁入的部门及其父部门）
UPDATE jeecg_v3.sys_depart d
LEFT JOIN (SELECT DISTINCT parent_id FROM jeecg_v3.sys_depart WHERE parent_id IS NOT NULL AND parent_id <> '') c ON c.parent_id = d.id
SET d.iz_leaf = IF(c.parent_id IS NULL, 1, 0)
WHERE d.id IN (SELECT id FROM jeecg_old.sys_depart);

DELETE FROM jeecg_v3.sys_user_depart WHERE user_id IN (SELECT id FROM jeecg_old.sys_user);
INSERT INTO jeecg_v3.sys_user_depart (`ID`, `user_id`, `dep_id`)
SELECT ud.`ID`, ud.`user_id`, ud.`dep_id`
FROM jeecg_old.sys_user_depart ud
WHERE ud.user_id IN (SELECT id FROM jeecg_old.sys_user)
  AND ud.dep_id IN (SELECT id FROM jeecg_v3.sys_depart);

-- ---------------------------------------------------------------------
-- 7. 字典：老库独有字典整组搬；ERP 4 个字典补齐生产新增项
--    同名的上游系统字典（如 msgType）语义已变，不合并
-- ---------------------------------------------------------------------
DROP TEMPORARY TABLE IF EXISTS jeecg_v3.tmp_migrate_dict;
CREATE TEMPORARY TABLE jeecg_v3.tmp_migrate_dict (dict_code VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL PRIMARY KEY) ENGINE=MEMORY;
INSERT INTO jeecg_v3.tmp_migrate_dict (dict_code)
SELECT dict_code FROM jeecg_old.sys_dict WHERE dict_code NOT IN (SELECT dict_code FROM jeecg_v3.sys_dict)
UNION
SELECT 'direction' UNION SELECT 'product_extend' UNION SELECT 'product_type' UNION SELECT 'record_status';

INSERT INTO jeecg_v3.sys_dict (`id`, `dict_name`, `dict_code`, `description`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `type`)
SELECT o.`id`, o.`dict_name`, o.`dict_code`, o.`description`, o.`del_flag`, o.`create_by`, o.`create_time`, o.`update_by`, o.`update_time`, o.`type`
FROM jeecg_old.sys_dict o
WHERE o.dict_code IN (SELECT dict_code FROM jeecg_v3.tmp_migrate_dict)
  AND o.dict_code NOT IN (SELECT dict_code FROM jeecg_v3.sys_dict)
  AND o.id NOT IN (SELECT id FROM jeecg_v3.sys_dict);

INSERT INTO jeecg_v3.sys_dict_item (`id`, `dict_id`, `item_text`, `item_value`, `description`, `sort_order`, `status`, `create_by`, `create_time`, `update_by`, `update_time`)
SELECT oi.`id`, nd.id, oi.`item_text`, oi.`item_value`, oi.`description`, oi.`sort_order`, oi.`status`, oi.`create_by`, oi.`create_time`, oi.`update_by`, oi.`update_time`
FROM jeecg_old.sys_dict_item oi
JOIN jeecg_old.sys_dict od ON od.id = oi.dict_id
JOIN jeecg_v3.tmp_migrate_dict t ON t.dict_code = od.dict_code
JOIN jeecg_v3.sys_dict nd ON nd.dict_code = od.dict_code
WHERE oi.id NOT IN (SELECT id FROM jeecg_v3.sys_dict_item)
  AND NOT EXISTS (SELECT 1 FROM jeecg_v3.sys_dict_item ni WHERE ni.dict_id = nd.id AND ni.item_value = oi.item_value);

DROP TEMPORARY TABLE IF EXISTS jeecg_v3.tmp_migrate_dict;

-- ---------------------------------------------------------------------
-- 8. 菜单：ERP 7 条由 Flyway 建立；3.0 其它菜单均为上游 Vue2 示例 / 系统页，不迁移（见 README）
-- 9. 定时任务：由 Flyway 写入（status=0），由 ErpQuartzJobBootstrap 启动时自动注册（见 README）
-- ---------------------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 1;
SELECT 'migrate_from_3.0 done' AS result;
