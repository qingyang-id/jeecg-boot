# 新旧后端接口一致性（parity）检查

`parity_check.py` 用同一批账号登录老后端（JeecgBoot 3.0，连 `jeecg_old`）和新后端（3.9.5，连 `jeecg_v3`），调用 ERP 模块的全部 GET 接口，归一化以后逐条比较结果。脚本只用标准库，只输出计数、接口路径、字段名和 id，不输出行内容。

## 前置条件

1. 本地 MySQL 容器 `erp-verify-mysql`（127.0.0.1:13306）里有 `jeecg_old`（生产快照）和 `jeecg_v3`（Flyway 加迁移完成），见 `../db/erp/README.md`。
2. 两个库里，参与比较的账号用同一个测试密码。老库用老 `PasswordUtil.encrypt(username, password, salt)` 计算后写入，只改本地库。算法和 3.9.5 相同，盐也相同，所以两边的 hash 一致。
3. 起新后端（8080，连 `jeecg_v3`）：
   ```bash
   export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
   java -jar jeecg-module-system/jeecg-system-start/target/jeecg-system-start-3.9.5.jar --spring.profiles.active=dev
   ```
4. 起老后端（8081，连 `jeecg_old`，Redis 用 database 1，避免和新后端冲突）。只构建、运行老目录，不修改：
   ```bash
   export JAVA_HOME=/opt/homebrew/opt/openjdk@11/libexec/openjdk.jdk/Contents/Home
   (cd jeecg-boot && mvn -q clean package -DskipTests)
   java -jar legacy/jeecg-boot/jeecg-boot-module-system/target/jeecg-boot-module-system-3.0.jar \
     --server.port=8081 --spring.profiles.active=dev \
     --spring.datasource.dynamic.datasource.master.url='jdbc:mysql://127.0.0.1:13306/jeecg_old?characterEncoding=UTF-8&useUnicode=true&useSSL=false&serverTimezone=Asia/Shanghai' \
     --spring.datasource.dynamic.datasource.master.username=root --spring.datasource.dynamic.datasource.master.password=root \
     --spring.redis.host=127.0.0.1 --spring.redis.port=16379 --spring.redis.password= --spring.redis.database=1 \
     --server.servlet.context-path=/portal
   ```
   老库的 `StatisticOrderJob` 是 status 0，老后端启动后每 10 分钟会重算一次 `jeecg_old` 的统计表（只改 `update_time`，以及快照之后变化的当天数据）。

## 运行

账号从命令行传入，脚本里没有真实用户名。`--user name` 表示无租户账号（admin），`--user name:1001` 表示带租户的账号。

```bash
python3 -I jeecg-boot/parity/parity_check.py \
  --old http://127.0.0.1:8081/portal --new http://127.0.0.1:8080/portal \
  --user admin --user <erp用户>:1001 --password 'Test@12345' \
  --db-cmd "docker exec -i erp-verify-mysql mysql -uroot -proot" \
  --include-sequence --write-chain --stats
```

退出码 0，最后一行是 `ALL PARITY CHECKS PASSED`，说明全部一致。

| 参数 | 作用 | 会写库吗 |
|---|---|---|
| （默认） | 扫描 `jeecg-module-erp` 的 controller，生成 GET 接口清单（当前 22 个），清单和用例必须一一对应。每个账号逐个接口比较。订单列表有 4 组参数：无条件、按客户、按日期范围、第 2 页。 | 否（`@AutoLog` 会写 `sys_log`） |
| `--db-cmd` | 租户检查：每个账号 8 个列表接口的 total，两边都要等于 `jeecg_old` 里 `tenant_id=<租户> AND del_flag=0` 的行数；订单的完整 id 集合也要和库一致。admin 的租户按 0 处理，期望为空。 | 否 |
| `--include-sequence` | 两边各调一次 `/business/sequence/num`，比较生成的单号。 | 是：两库的 `jsh_sequence` 各加 1 |
| `--write-chain` | 用带租户的账号在新后端依次执行：新增客户 → 新增订单（提交结构和老前端 `JshOrderForm.classifyIntoFormData` 一致，含明细和扩展）→ 用字符串 id 编辑 → `queryById` → 导出 Excel（文件类型和表头与老后端同一接口逐格比较，并确认新订单在导出文件里）→ 删除订单和客户。每一步都回查 `jeecg_v3`，最后物理删除本次创建的行。 | 是：新库，结束时清理；新增订单会让 `jsh_sequence` 加 1 |
| `--stats` | admin 调两边的 `/sys/quartzJob/execute` 执行 `StatisticOrderJob`，然后比较两库最近 45 天的 `jsh_order_summary_daily`、最近两个月的 `jsh_order_summary_monthly`，以及 `--stats-date` 当天的行。执行前先把新库当天的行清零，执行后行数和值一致，说明确实重算过。 | 是：两库的统计表 |

带写操作的参数跑完以后，按 `../db/erp/README.md` 恢复新库：先把老库 `jsh_sequence` 恢复到运行前的值，再执行 `migrate_from_3.0.sql` 和 `verify_migration.sql`。`verify` 要在老后端停掉以后跑，否则老库统计表可能在两步之间被定时任务改掉，导致校验和对不上。

## 归一化规则

- 老系统全局把 `Long` 序列化成字符串，并且不输出 null（`NON_NULL`）。新系统只把 `id` 和 `*Id` 序列化成字符串，null 照常输出。比较前：去掉值为 null 的键；所有整数都按十进制字符串比较；ISO 时间统一成 `yyyy-MM-dd HH:mm:ss`。
- 分页只比较 `total`、`size`、`current`、`pages` 和 `records`。`records` 按返回顺序逐条比较，同一批 id 但顺序不同也算失败。
- 单独断言：新后端返回里，所有非 null 的 `id` 和 `*Id` 都必须是 JSON 字符串（Review Focus 3）。

## 预期差异（已归一化或忽略）

| 差异 | 原因 |
|---|---|
| 非 id 的 Long 字段（`totalPrice`、`totalArea`、`width` 等）：老系统返回字符串，新系统返回数字 | 老系统用全局 `Long→String`。新系统只对 ERP 接口的 `id` 和 `*Id` 转字符串（`ErpLongIdToStringAdvice`），数值字段保持数字，方便新前端计算。这些值都远小于 2^53，不会丢精度。 |
| null 字段：老系统不输出，新系统输出 `null` | 老系统用 `NON_NULL`，3.9.5 的全局 ObjectMapper 不这样做。改全局配置会影响上游接口，所以不改。 |
| 分页对象：老系统多出 `orders`、`optimizeCountSql`、`searchCount` 等字段 | MyBatis-Plus 版本不同。前端只用 `records` 和 `total`。 |
| 老系统字典翻译分页里，`orderId`、`customerId` 等是数字 | 老 DictAspect 的行为。新系统统一返回字符串，更安全。 |
| 统计表的 `updateTime` | 这个字段记录定时任务最后一次运行的时间，取决于各自后端的运行时刻，不是业务数据。统计值本身由 `--stats` 比较。 |
| admin（无租户）看到的 ERP 列表为空，`queryById` 返回“未找到对应数据” | 两边一致：admin 登录后租户为 0，所有 `jsh_*` 数据都属于租户 1001。 |
| 写链路里没有“用导出文件再导入” | 本轮任务范围只要求导出。导入接口仍然存在，但没有做自动化比较。 |
