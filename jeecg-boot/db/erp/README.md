# ERP 数据库：初始化与 3.0 → 3.9.5 迁移

| 文件 | 作用 |
|---|---|
| `../../jeecg-module-system/jeecg-system-start/src/main/resources/flyway/sql/mysql/V3.9.5_100__erp_init.sql` | Flyway：11 张 `jsh_*` 表、ERP 4 个字典、7 条菜单、`erp` 角色及授权、订单统计定时任务、`jsh_sequence` 种子。幂等。 |
| `migrate_from_3.0.sql` | 一次性数据迁移：老库 → 新库。可重复执行。 |
| `verify_migration.sql` | 只读核对：行数、整行 CRC32 校验和、系统表命中数、按租户金额汇总。只输出计数和汇总，不输出行内容。 |

所有脚本都按 MySQL 5.7.37 写，不用 `utf8mb4_0900_*`、CTE、窗口函数、`JSON_TABLE`。迁移全部是服务器端执行的 `INSERT ... SELECT`，没有大的客户端多行 INSERT，所以不受生产 `max_allowed_packet=4M` 限制。

## 执行顺序

1. 建空库（utf8mb4 / utf8mb4_general_ci）。
2. 导入上游基础脚本 `jeecg-boot/db/jeecgboot-mysql-5.7.sql`。
   - **这个文件第 1、2 行是 `CREATE database if NOT EXISTS \`jeecg-boot\`` 和 ``USE `jeecg-boot`;``，后面跟着约 143 条 `DROP TABLE IF EXISTS`。原样导入会建库 `jeecg-boot`，并清空同名库里的所有表。** 生产 3.0 库正好叫 `jeecg-boot`，所以导入前必须改写这两行。
   - 本地做法：删掉这两行，再导入到 `jeecg_v3`：
     ```bash
     sed -e '1,2{/^CREATE database/d;/^USE /d;}' jeecg-boot/db/jeecgboot-mysql-5.7.sql \
       | docker exec -i erp-verify-mysql mysql -uroot -proot jeecg_v3
     ```
   - 生产做法：把这两行改写成 `jeecg-boot-v3`，或者像上面一样删掉后用 `mysql ... jeecg-boot-v3` 导入。导入前先检查改写结果：`head -5` 看不到 `jeecg-boot` 后面不带 `-v3` 的写法；`grep -n '^USE ' <改写后文件>` 只有指向 `jeecg-boot-v3` 的行，或者一行都没有。具体步骤见 `jeecg-docker/UPGRADE-3.9.5.md`。
3. 启动新后端，并开启 Flyway（`spring.flyway.enabled=true`。dev 默认开，prod 默认关，生产首次启动要用 `-Dspring.flyway.enabled=true` 打开）。上游 `FlywayConfig` 在迁移失败时只打日志、不阻止启动，**所以必须查表确认**：
   ```sql
   SELECT version, success FROM jeecg_v3.flyway_schema_history ORDER BY installed_rank DESC LIMIT 3;
   -- 最新一行必须是 3.9.5.100 / 1
   ```
4. 执行迁移：`mysql -uroot -p < migrate_from_3.0.sql`。不要加 `--force`。脚本开头有两个断言，不满足时会以 `Subquery returns more than 1 row` 报错并中止：Flyway 3.9.5.100 已成功；`rel_tenant_ids` 最大分段数 <= 10。
5. 执行核对：`mysql -uroot -p -t < verify_migration.sql`。期望所有 `result` 列都是 `OK`，按租户的汇总两边逐行相同。
6. 清 Redis 缓存或重启后端，避免缓存里还是迁移前的用户和字典。
7. 校验订单统计任务已注册。Flyway 写入时 `status=0`，后端启动时由 `ErpQuartzJobBootstrap` 自动注册到 Quartz，无需管理员手工启动（原因见下文“定时任务”）。执行 `SELECT JOB_NAME FROM qrtz_job_details WHERE JOB_NAME='1511267922611875842'; SELECT TRIGGER_STATE FROM qrtz_triggers WHERE TRIGGER_NAME='1511267922611875842';`，期望各返回一行，`TRIGGER_STATE` 为 `WAITING`（或执行中的 `ACQUIRED`）。后端日志里应有 `ERP quartz bootstrap: registered job 1511267922611875842`（首次）或 `already exists in scheduler`（之后每次启动）。

## 库名替换（生产）

两个迁移脚本只用字面库名 `jeecg_old.`（源）和 `jeecg_v3.`（目标），没有别名或变量，临时表也写成 `jeecg_v3.tmp_migrate_dict`。生产上源库是 `jeecg-boot`，目标库是 `jeecg-boot-v3`，用下面的规则改写：

```bash
RW='s/`?\bjeecg_old\b`?/`jeecg-boot`/g; s/`?\bjeecg_v3\b`?/`jeecg-boot-v3`/g'
perl -pe "$RW" migrate_from_3.0.sql  > migrate.prod.sql
perl -pe "$RW" verify_migration.sql  > verify.prod.sql
grep -c 'jeecg_old\|jeecg_v3' migrate.prod.sql verify.prod.sql   # 两个都必须是 0
```

## 迁移内容与规则

| 段 | 规则 |
|---|---|
| 11 张 `jsh_*` | 先 `TRUNCATE`，再按显式列整表搬。两边 DDL 相同，都取自生产 dump。`jsh_sequence` 用生产当前值覆盖 Flyway 种子；`jsh_version` 保留老数据。 |
| `sys_tenant` | `INSERT ... ON DUPLICATE KEY UPDATE`：生产租户覆盖同 id 的上游示例租户（1001），新库独有的 `del_flag` 置 0。 |
| `sys_user` | 以生产为准：先删除生产库里没有的上游示例用户及其角色、部门、租户关系，再删除与生产同名或同 id 的用户，然后按交集列整行重插。admin 因此会用生产的密码和盐。老库独有的 `post`、`rel_tenant_ids` 列不搬；`birthday` 的类型从 datetime 改为 date。 |
| `sys_user_tenant` | 把 `rel_tenant_ids` 按 1..10 数字表拆成多行，写 `status='1'`、`create_by='migrate'`，id 取 `MD5(user_id-tenant_id)`。表上没有唯一键，所以先删除 `create_by='migrate'` 的行和迁移用户的上游残留关系。 |
| 角色 | 老库独有的角色按 `role_code` 补入。用户角色按用户整组替换，`role_id` 按 `role_code` 映射。角色权限只补“菜单 id 在新库仍存在”的授权。 |
| 部门 | 老库独有的部门（id 和 `org_code` 都不在新库）补入，并重算 `iz_leaf`。用户部门按用户整组替换。 |
| 字典 | 老库独有的字典整组搬入。ERP 4 个字典补齐生产新增项（按 `item_value` 判断）。同名的上游系统字典不合并。 |
| 定时任务 | 迁移脚本**不搬** `sys_quartz_job` 的老行（类名已失效，而且末尾带一个 TAB），由 Flyway 写入新类 `org.jeecg.modules.business.job.StatisticOrderJob`；cron、参数、描述沿用老行。 |

### 定时任务为什么由启动时自动注册

3.9.5 的 Quartz 用 JDBC 存储。只写 `sys_quartz_job` 不会真正调度，上游启动时也没有同步逻辑，正常要管理员调 `/sys/quartzJob/resume` 才会进入 `qrtz_*` 表；生产部署时没有管理员密码。所以 Flyway 写 `status=0`，ERP 模块的 `org.jeecg.modules.business.job.ErpQuartzJobBootstrap`（`ApplicationRunner`）在启动后读取 `job_class_name='org.jeecg.modules.business.job.StatisticOrderJob'`、`del_flag=0` 的行：`status=0` 且 Quartz 中不存在时，按上游 `QuartzJobServiceImpl.schedulerAdd` 的方式注册；已存在则不动；`status!=0`（管理员在页面上停止过）不处理。注册失败只记 ERROR 日志，不影响启动。

## 生产数据盘点（迁移前快照，只列结构性结论）

公开仓库不记录业务行数与金额；行数一致性由 `verify_migration.sql` 在执行时核对。

| 项 | 结果 |
|---|---|
| `jsh_*` 数据 | 11 张表都有数据；`jsh_order` 含 `del_flag=1` 的软删除行（迁移原样搬，列表按 `del_flag=0` 过滤）。 |
| 租户 | 2 个：id 1、1001。所有 `jsh_*` 数据都属于 1001。 |
| 用户 | 少量；`rel_tenant_ids` 最大分段数 1，只有租户 1001 下的业务用户有租户。admin 没有租户，登录后租户为 0，ERP 列表为空，和 3.0 行为一致。 |
| 角色 | 6 个：admin、caiwu、erp、hr、test、third_role。admin、test 在新库已有同 id 同编码；erp 由 Flyway 建；caiwu、hr、third_role 由迁移补入。 |
| 部门 | 9 个，其中 8 个与上游种子同 id 同编码，1 个为老库独有，已迁移。 |
| 字典 | 老库 45 个。与 3.9.5 基础库做差集，老库有而新库没有的只有 ERP 4 个：`direction`(4)、`product_extend`(7)、`product_type`(2)、`record_status`(2)，都放进 Flyway。`msgType` 在老库有 4 项（1短信/2邮件/3微信/4系统），上游 3.9.5 已改为 1文本/2富文本，语义不同，不合并。 |
| ERP 菜单 | 7 条（3 个目录 + 4 个页面），没有 ERP 按钮权限（menu_type=2）。ERP 后端也没有 `@RequiresPermissions`。 |
| 其它老库菜单 | 老库有、新库没有的菜单 id：目录 15、页面 108、按钮 6，其中 ERP 7 条已迁移。其余都是 3.0 上游的 Vue2 示例页或系统页（如 `jeecg/*` 示例、Redis 监控 iframe、`system/SysUserOnlineList`），**未迁移**，3.9.5 已有对应的新页面。 |
| 定时任务 | 4 条：StatisticOrderJob（status 0，每 10 分钟一次，类名末尾带 TAB），以及 SendMsgJob、SampleJob、SampleParamJob（都是 -1，上游自带）。 |

## 菜单（供前端使用）

| id | 名称 | url | component | menu_type | perms |
|---|---|---|---|---|---|
| 1489610778376429569 | 订单管理 | `/business/order` | `layouts/default/index` | 0 | – |
| 1489611118064721922 | 订单信息 | `/business/order/list` | `business/order/JshOrderList` | 1 | – |
| 1489611118064721923 | 订单统计 | `/business/order/summary` | `business/order/JshOrderSummaryList` | 1 | – |
| 1489624225004699650 | 商品管理 | `/business/product` | `layouts/default/index` | 0 | – |
| 1489624438280863746 | 商品信息 | `/business/product/list` | `business/product/JshProductList` | 1 | – |
| 1489122625907675137 | 客户管理 | `/business/customer` | `layouts/default/index` | 0 | – |
| 1489123190154809346 | 客户信息 | `/business/customer/list` | `business/customer/JshCustomerList` | 1 | – |

url、id、名称、排序都沿用生产库。实施计划里写的“基本资料 `/business/base`”在生产库中实际是“客户管理 `/business/customer`”，以生产为准。图标改成 Vue3 的写法 `ant-design:{solution,rocket,team}-outlined`。erp 角色另外授予 3.9.5 的“主页”目录和“首页”（`/dashboard/analysis`），对应 3.0 中 erp 角色原有的首页权限。
