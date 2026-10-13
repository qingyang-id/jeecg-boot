-- RANK 是 MySQL 8.0+ 保留字，MyBatis-Plus 生成的 SQL 不加反引号，jsh_customer 的所有查询在 8.x 上报 SQLSyntaxErrorException。
-- 列改名为 sort_no，实体属性对应改为 sortNo（前端 column/dataIndex 同步改）。5.7 / 8.4 均可执行。
ALTER TABLE `jsh_customer` CHANGE COLUMN `rank` `sort_no` int(10) NOT NULL DEFAULT '0' COMMENT '排序';
