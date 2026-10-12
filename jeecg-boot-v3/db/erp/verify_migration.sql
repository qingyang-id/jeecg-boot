-- =====================================================================
-- ERP 3.0 -> 3.9.5 迁移核对（只读）。源 jeecg_old，目标 jeecg_v3
--   用法：mysql -t < verify_migration.sql
--   期望：每个 result 列均为 OK；按租户汇总两边逐行相同
--   只输出计数 / 校验和 / 金额汇总，不输出任何行内容
-- =====================================================================
SET NAMES utf8mb4;

-- 1. 业务表：行数 + 整行 CRC32 校验和（11 张）
SELECT tbl, old_cnt, new_cnt, IF(old_cnt = new_cnt AND old_crc <=> new_crc, 'OK', 'MISMATCH') AS result FROM (
SELECT 'jsh_customer' AS tbl,
  (SELECT COUNT(*) FROM jeecg_old.jsh_customer) AS old_cnt,
  (SELECT COUNT(*) FROM jeecg_v3.jsh_customer) AS new_cnt,
  (SELECT SUM(CRC32(CONCAT_WS('|', `id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `rank`, `status`, `tenant_id`))) FROM jeecg_old.jsh_customer) AS old_crc,
  (SELECT SUM(CRC32(CONCAT_WS('|', `id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `sex`, `address`, `phone`, `rank`, `status`, `tenant_id`))) FROM jeecg_v3.jsh_customer) AS new_crc
UNION ALL
SELECT 'jsh_customer_address' AS tbl,
  (SELECT COUNT(*) FROM jeecg_old.jsh_customer_address) AS old_cnt,
  (SELECT COUNT(*) FROM jeecg_v3.jsh_customer_address) AS new_cnt,
  (SELECT SUM(CRC32(CONCAT_WS('|', `id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `customer_id`, `address`, `tenant_id`))) FROM jeecg_old.jsh_customer_address) AS old_crc,
  (SELECT SUM(CRC32(CONCAT_WS('|', `id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `customer_id`, `address`, `tenant_id`))) FROM jeecg_v3.jsh_customer_address) AS new_crc
UNION ALL
SELECT 'jsh_order' AS tbl,
  (SELECT COUNT(*) FROM jeecg_old.jsh_order) AS old_cnt,
  (SELECT COUNT(*) FROM jeecg_v3.jsh_order) AS new_cnt,
  (SELECT SUM(CRC32(CONCAT_WS('|', `id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `order_code`, `customer_id`, `total_area`, `total_price`, `order_time`, `address`, `remark`, `tenant_id`))) FROM jeecg_old.jsh_order) AS old_crc,
  (SELECT SUM(CRC32(CONCAT_WS('|', `id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `order_code`, `customer_id`, `total_area`, `total_price`, `order_time`, `address`, `remark`, `tenant_id`))) FROM jeecg_v3.jsh_order) AS new_crc
UNION ALL
SELECT 'jsh_order_product' AS tbl,
  (SELECT COUNT(*) FROM jeecg_old.jsh_order_product) AS old_cnt,
  (SELECT COUNT(*) FROM jeecg_v3.jsh_order_product) AS new_cnt,
  (SELECT SUM(CRC32(CONCAT_WS('|', `id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `customer_id`, `order_id`, `product_id`, `width`, `height`, `num`, `direction`, `color`, `glass_color`, `price`, `extend_price`, `total_area`, `total_price`, `extend_type`, `extend_num`, `tenant_id`))) FROM jeecg_old.jsh_order_product) AS old_crc,
  (SELECT SUM(CRC32(CONCAT_WS('|', `id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `customer_id`, `order_id`, `product_id`, `width`, `height`, `num`, `direction`, `color`, `glass_color`, `price`, `extend_price`, `total_area`, `total_price`, `extend_type`, `extend_num`, `tenant_id`))) FROM jeecg_v3.jsh_order_product) AS new_crc
UNION ALL
SELECT 'jsh_order_product_detail' AS tbl,
  (SELECT COUNT(*) FROM jeecg_old.jsh_order_product_detail) AS old_cnt,
  (SELECT COUNT(*) FROM jeecg_v3.jsh_order_product_detail) AS new_cnt,
  (SELECT SUM(CRC32(CONCAT_WS('|', `id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `customer_id`, `order_id`, `order_product_id`, `product_id`, `type`, `reference_width`, `reference_height`, `width`, `height`, `num`, `direction`, `color`, `extend_type`, `extend_num`, `tenant_id`))) FROM jeecg_old.jsh_order_product_detail) AS old_crc,
  (SELECT SUM(CRC32(CONCAT_WS('|', `id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `customer_id`, `order_id`, `order_product_id`, `product_id`, `type`, `reference_width`, `reference_height`, `width`, `height`, `num`, `direction`, `color`, `extend_type`, `extend_num`, `tenant_id`))) FROM jeecg_v3.jsh_order_product_detail) AS new_crc
UNION ALL
SELECT 'jsh_order_product_extend' AS tbl,
  (SELECT COUNT(*) FROM jeecg_old.jsh_order_product_extend) AS old_cnt,
  (SELECT COUNT(*) FROM jeecg_v3.jsh_order_product_extend) AS new_cnt,
  (SELECT SUM(CRC32(CONCAT_WS('|', `id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `order_id`, `order_product_id`, `type`, `num`, `tenant_id`))) FROM jeecg_old.jsh_order_product_extend) AS old_crc,
  (SELECT SUM(CRC32(CONCAT_WS('|', `id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `order_id`, `order_product_id`, `type`, `num`, `tenant_id`))) FROM jeecg_v3.jsh_order_product_extend) AS new_crc
UNION ALL
SELECT 'jsh_order_summary_daily' AS tbl,
  (SELECT COUNT(*) FROM jeecg_old.jsh_order_summary_daily) AS old_cnt,
  (SELECT COUNT(*) FROM jeecg_v3.jsh_order_summary_daily) AS new_cnt,
  (SELECT SUM(CRC32(CONCAT_WS('|', `id`, `time`, `tenant_id`, `customer_id`, `total_num`, `total_area`, `total_price`, `del_flag`, `update_time`, `create_time`))) FROM jeecg_old.jsh_order_summary_daily) AS old_crc,
  (SELECT SUM(CRC32(CONCAT_WS('|', `id`, `time`, `tenant_id`, `customer_id`, `total_num`, `total_area`, `total_price`, `del_flag`, `update_time`, `create_time`))) FROM jeecg_v3.jsh_order_summary_daily) AS new_crc
UNION ALL
SELECT 'jsh_order_summary_monthly' AS tbl,
  (SELECT COUNT(*) FROM jeecg_old.jsh_order_summary_monthly) AS old_cnt,
  (SELECT COUNT(*) FROM jeecg_v3.jsh_order_summary_monthly) AS new_cnt,
  (SELECT SUM(CRC32(CONCAT_WS('|', `id`, `time`, `tenant_id`, `customer_id`, `total_num`, `total_area`, `total_price`, `del_flag`, `update_time`, `create_time`))) FROM jeecg_old.jsh_order_summary_monthly) AS old_crc,
  (SELECT SUM(CRC32(CONCAT_WS('|', `id`, `time`, `tenant_id`, `customer_id`, `total_num`, `total_area`, `total_price`, `del_flag`, `update_time`, `create_time`))) FROM jeecg_v3.jsh_order_summary_monthly) AS new_crc
UNION ALL
SELECT 'jsh_product' AS tbl,
  (SELECT COUNT(*) FROM jeecg_old.jsh_product) AS old_cnt,
  (SELECT COUNT(*) FROM jeecg_v3.jsh_product) AS new_cnt,
  (SELECT SUM(CRC32(CONCAT_WS('|', `id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `barcode`, `purchase_price`, `wholesale_price`, `aluminum_height_diff`, `aluminum_width_diff`, `glass_height_diff`, `glass_width_diff`, `remark`, `status`, `tenant_id`))) FROM jeecg_old.jsh_product) AS old_crc,
  (SELECT SUM(CRC32(CONCAT_WS('|', `id`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `name`, `barcode`, `purchase_price`, `wholesale_price`, `aluminum_height_diff`, `aluminum_width_diff`, `glass_height_diff`, `glass_width_diff`, `remark`, `status`, `tenant_id`))) FROM jeecg_v3.jsh_product) AS new_crc
UNION ALL
SELECT 'jsh_sequence' AS tbl,
  (SELECT COUNT(*) FROM jeecg_old.jsh_sequence) AS old_cnt,
  (SELECT COUNT(*) FROM jeecg_v3.jsh_sequence) AS new_cnt,
  (SELECT SUM(CRC32(CONCAT_WS('|', `seq_name`, `min_value`, `max_value`, `current_val`, `increment_val`, `remark`))) FROM jeecg_old.jsh_sequence) AS old_crc,
  (SELECT SUM(CRC32(CONCAT_WS('|', `seq_name`, `min_value`, `max_value`, `current_val`, `increment_val`, `remark`))) FROM jeecg_v3.jsh_sequence) AS new_crc
UNION ALL
SELECT 'jsh_version' AS tbl,
  (SELECT COUNT(*) FROM jeecg_old.jsh_version) AS old_cnt,
  (SELECT COUNT(*) FROM jeecg_v3.jsh_version) AS new_cnt,
  (SELECT SUM(CRC32(CONCAT_WS('|', `id`, `platform`, `version`, `version_no`, `description`, `download_url`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `status`))) FROM jeecg_old.jsh_version) AS old_crc,
  (SELECT SUM(CRC32(CONCAT_WS('|', `id`, `platform`, `version`, `version_no`, `description`, `download_url`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `status`))) FROM jeecg_v3.jsh_version) AS new_crc
) t;

-- 2. 系统表：老库行在新库中的命中数（新库另有上游种子，故比“命中数 = 老库行数”）
SELECT tbl, old_cnt, matched_in_new, IF(old_cnt = matched_in_new, 'OK', 'MISMATCH') AS result FROM (
  SELECT 'sys_user(username+password+salt)' AS tbl,
    (SELECT COUNT(*) FROM jeecg_old.sys_user) AS old_cnt,
    (SELECT COUNT(*) FROM jeecg_old.sys_user o JOIN jeecg_v3.sys_user n
       ON n.id = o.id AND n.username = o.username AND n.password = o.password AND n.salt = o.salt
      AND n.status <=> o.status AND n.del_flag <=> o.del_flag) AS matched_in_new
  UNION ALL
  SELECT 'sys_user(new has no extra users)', 0,
    (SELECT COUNT(*) FROM jeecg_v3.sys_user WHERE username NOT IN (SELECT username FROM jeecg_old.sys_user))
  UNION ALL
  SELECT 'sys_tenant(id+name+status)',
    (SELECT COUNT(*) FROM jeecg_old.sys_tenant),
    (SELECT COUNT(*) FROM jeecg_old.sys_tenant o JOIN jeecg_v3.sys_tenant n
       ON n.id = o.id AND n.name = o.name AND n.status = o.status AND n.del_flag = 0)
  UNION ALL
  SELECT 'sys_role(role_code)',
    (SELECT COUNT(*) FROM jeecg_old.sys_role),
    (SELECT COUNT(*) FROM jeecg_old.sys_role o JOIN jeecg_v3.sys_role n ON n.role_code = o.role_code)
  UNION ALL
  -- 老库 sys_user_role 有指向已删除用户的孤儿行，只核对有效用户的行
  SELECT 'sys_user_role(user+role_code)',
    (SELECT COUNT(*) FROM jeecg_old.sys_user_role ur JOIN jeecg_old.sys_role r ON r.id = ur.role_id
       WHERE ur.user_id IN (SELECT id FROM jeecg_old.sys_user)),
    (SELECT COUNT(*) FROM jeecg_old.sys_user_role ur JOIN jeecg_old.sys_role r ON r.id = ur.role_id
       JOIN jeecg_v3.sys_role nr ON nr.role_code = r.role_code
       JOIN jeecg_v3.sys_user_role nur ON nur.user_id = ur.user_id AND nur.role_id = nr.id
       WHERE ur.user_id IN (SELECT id FROM jeecg_old.sys_user))
  UNION ALL
  SELECT 'sys_depart(id or org_code)',
    (SELECT COUNT(*) FROM jeecg_old.sys_depart),
    (SELECT COUNT(*) FROM jeecg_old.sys_depart o WHERE EXISTS
       (SELECT 1 FROM jeecg_v3.sys_depart n WHERE n.id = o.id OR n.org_code = o.org_code))
  UNION ALL
  SELECT 'sys_user_depart(user+dep)',
    (SELECT COUNT(*) FROM jeecg_old.sys_user_depart),
    (SELECT COUNT(*) FROM jeecg_old.sys_user_depart o JOIN jeecg_v3.sys_user_depart n
       ON n.user_id = o.user_id AND n.dep_id = o.dep_id)
  UNION ALL
  -- 用户-租户关系条数 = 老库 rel_tenant_ids 中租户 id 总个数（且均为 status='1'）
  SELECT 'sys_user_tenant(rel_tenant_ids split)',
    (SELECT IFNULL(SUM(1 + LENGTH(rel_tenant_ids) - LENGTH(REPLACE(rel_tenant_ids, ',', ''))), 0)
       FROM jeecg_old.sys_user WHERE rel_tenant_ids IS NOT NULL AND rel_tenant_ids <> ''),
    (SELECT COUNT(*) FROM jeecg_v3.sys_user_tenant WHERE create_by = 'migrate' AND status = '1')
  UNION ALL
  SELECT 'sys_dict(old codes present)',
    (SELECT COUNT(*) FROM jeecg_old.sys_dict),
    (SELECT COUNT(*) FROM jeecg_old.sys_dict o WHERE o.dict_code IN (SELECT dict_code FROM jeecg_v3.sys_dict))
  UNION ALL
  SELECT 'sys_dict_item(ERP 4 dicts)',
    (SELECT COUNT(*) FROM jeecg_old.sys_dict_item i JOIN jeecg_old.sys_dict d ON d.id = i.dict_id
       WHERE d.dict_code IN ('direction', 'product_extend', 'product_type', 'record_status')),
    (SELECT COUNT(*) FROM jeecg_old.sys_dict_item i JOIN jeecg_old.sys_dict d ON d.id = i.dict_id
       JOIN jeecg_v3.sys_dict nd ON nd.dict_code = d.dict_code
       JOIN jeecg_v3.sys_dict_item ni ON ni.dict_id = nd.id AND ni.item_value = i.item_value
       WHERE d.dict_code IN ('direction', 'product_extend', 'product_type', 'record_status'))
  UNION ALL
  SELECT 'sys_permission(ERP menus)',
    (SELECT COUNT(*) FROM jeecg_old.sys_permission WHERE url LIKE '/business%'),
    (SELECT COUNT(*) FROM jeecg_old.sys_permission o JOIN jeecg_v3.sys_permission n ON n.id = o.id AND n.url = o.url
       WHERE o.url LIKE '/business%')
  UNION ALL
  SELECT 'sys_role_permission(ERP menus)',
    (SELECT COUNT(*) FROM jeecg_old.sys_role_permission rp JOIN jeecg_old.sys_permission p ON p.id = rp.permission_id
       WHERE p.url LIKE '/business%'),
    (SELECT COUNT(*) FROM jeecg_old.sys_role_permission rp JOIN jeecg_old.sys_permission p ON p.id = rp.permission_id
       JOIN jeecg_old.sys_role r ON r.id = rp.role_id JOIN jeecg_v3.sys_role nr ON nr.role_code = r.role_code
       JOIN jeecg_v3.sys_role_permission nrp ON nrp.role_id = nr.id AND nrp.permission_id = rp.permission_id
       WHERE p.url LIKE '/business%')
  UNION ALL
  SELECT 'sys_quartz_job(StatisticOrderJob)', 1,
    (SELECT COUNT(*) FROM jeecg_v3.sys_quartz_job
       WHERE job_class_name = 'org.jeecg.modules.business.job.StatisticOrderJob'
         AND cron_expression = (SELECT cron_expression FROM jeecg_old.sys_quartz_job WHERE id = '1511267922611875842'))
  UNION ALL
  SELECT 'jsh_sequence(current_val)',
    (SELECT COUNT(*) FROM jeecg_old.jsh_sequence),
    (SELECT COUNT(*) FROM jeecg_old.jsh_sequence o JOIN jeecg_v3.jsh_sequence n
       ON n.seq_name = o.seq_name AND n.current_val = o.current_val)
) t;

-- 3. 按租户的订单数与金额汇总（两边逐行应相同；金额单位：分，面积：mm²）
SELECT 'old' AS src, tenant_id, COUNT(*) AS orders, SUM(total_price) AS sum_total_price, SUM(total_area) AS sum_total_area
FROM jeecg_old.jsh_order GROUP BY tenant_id
UNION ALL
SELECT 'new', tenant_id, COUNT(*), SUM(total_price), SUM(total_area)
FROM jeecg_v3.jsh_order GROUP BY tenant_id
ORDER BY tenant_id, src DESC;

SELECT 'old' AS src, tenant_id, COUNT(*) AS order_products, SUM(num) AS sum_num, SUM(total_price) AS sum_total_price
FROM jeecg_old.jsh_order_product GROUP BY tenant_id
UNION ALL
SELECT 'new', tenant_id, COUNT(*), SUM(num), SUM(total_price)
FROM jeecg_v3.jsh_order_product GROUP BY tenant_id
ORDER BY tenant_id, src DESC;

SELECT 'old' AS src, tenant_id, COUNT(*) AS monthly_rows, SUM(total_num) AS sum_total_num, SUM(total_price) AS sum_total_price
FROM jeecg_old.jsh_order_summary_monthly WHERE customer_id = 0 GROUP BY tenant_id
UNION ALL
SELECT 'new', tenant_id, COUNT(*), SUM(total_num), SUM(total_price)
FROM jeecg_v3.jsh_order_summary_monthly WHERE customer_id = 0 GROUP BY tenant_id
ORDER BY tenant_id, src DESC;
