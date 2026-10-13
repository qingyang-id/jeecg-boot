# MySQL 5.7.37 → 8.4.11 / Redis 5.0 → 7.4.11 生产升级 Runbook

适用：`erp.jijianxia.com` 单机 docker（服务器 `/home/base/docker/`，命令为独立版 **`docker-compose`**）。约定、密钥规则、`TS` 变量、`set -o pipefail` 规则同 `UPGRADE-3.9.5.md`。

思路：MySQL 8.4 不能直接读 5.7 数据目录，所以走**逻辑迁移**——停 erp-api → `mysqldump` 两个库 → 新数据目录 `mysql84/data` 起 8.4 → 导入 → 校验行数。旧目录 `mysql/data` 原样保留，回滚 = compose 改回 5.7 + 旧目录 + 旧 jar/web。Redis 只放缓存 / 登录 token / 验证码，7.4 直接读 5.0 的 RDB。

## 0. 与本次升级一起发布的代码

`RANK` 是 MySQL 8.0+ 保留字，`jsh_customer.rank` 列让客户模块所有查询在 8.x 上报 `SQLSyntaxErrorException`（本地 8.4 实测）。修复随本次发布：

- Flyway `V3.9.5_102__erp_customer_rank_rename.sql`：列 `rank` → `sort_no`（5.7 / 8.4 均可执行）。
- 实体 `JshCustomer.rank` → `sortNo`；前端 `customer.data.ts` / `order.data.ts` 同步改。
- **新 jar 只能配新库，旧 jar 只能配旧库**：jar、web、Flyway 102 必须在同一个停机窗口内一起上线，回滚时一起回退。

本地验证结果（`jeecg-boot-v3/docker/local-verify/docker-compose.v84.yml`，导入本地的生产 dump）：

| 项 | 结果 |
|---|---|
| dump 5.7 → 导入 8.4 | 无报错；259 张表行数逐表一致；`jsh_order` / `jsh_customer` / `jsh_product` CHECKSUM 一致 |
| 保留字扫描（8.4 `information_schema.keywords`） | 只有 `jsh_customer.rank` 命中 |
| 回归（ERP 8 个列表、客户按 sortNo 排序/过滤、客户增改删、订单详情与字典翻译、导出 Excel、系统接口） | 25/25 通过，日志无 SQL 错误 |
| `StatisticOrderJob` | 8.4 上定时执行成功，耗时约 1 秒 |
| Redis 7.4 | 服务器同款 `redis.conf` 正常加载；5.0.14 生成的 RDB 正常加载 |
| 内存 | MySQL 8.4 比 5.7 多约 90MB（654 vs 567 MiB，buffer pool 均为 128M） |
| JDBC | 现有 `mysql-connector-java 8.0.27` + `allowPublicKeyRetrieval=true` 可正常连接 8.4（`caching_sha2_password`） |

## 1. 发布前准备

**[local]** 构建产物（与 `UPGRADE-3.9.5.md` 相同的命令），并在本地导出镜像（服务器拉不了 Docker Hub）：

```bash
docker pull --platform linux/amd64 mysql:8.4.11
docker pull --platform linux/amd64 redis:7.4.11
docker save mysql:8.4.11 redis:7.4.11 | gzip > db-images-$TS.tgz
```

把 `db-images-$TS.tgz`、新 jar、新 web 包上传到服务器后：

**[server]**

```bash
docker load < db-images-$TS.tgz
docker images | grep -E 'mysql +8.4.11|redis +7.4.11'   # 两行都要有
free -m                                                  # available 需 ≥ 300MB 余量
df -h /home                                              # 需 ≥ 3 倍 mysql/data 大小
du -sh /home/base/docker/mysql/data
docker exec mysql sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" -N -e "select user,host,plugin from mysql.user"'
```

预期 `mysql.user` 只有 `root` 和 `mysql.*` 系统账号。如果有其他业务账号，要记下来，在第 3 步重新创建（不迁移 `mysql` 库）。

## 2. 停机并导出

**[server]**

```bash
cd /home/base/docker
cp docker-compose.yml docker-compose.yml.bak-$TS
docker-compose stop erp-api
set -o pipefail
docker exec mysql sh -c 'mysqldump -uroot -p"$MYSQL_ROOT_PASSWORD" --single-transaction --routines --triggers --events --hex-blob --default-character-set=utf8mb4 --databases jeecg-boot jeecg-boot-v3' | gzip > /home/base/backup-mysql57-$TS.sql.gz; echo exit=$?
ls -lh /home/base/backup-mysql57-$TS.sql.gz
```

预期 `exit=0`，文件大小与以往备份相近。然后用精确 `COUNT(*)` 记录逐表行数基线（`information_schema.table_rows` 是估算值，不能用）：

```bash
q() { docker exec -i mysql sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" -N 2>/dev/null'; }
rowcount() {
  set -o pipefail
  echo "select concat('select ''',table_schema,'.',table_name,''',count(*) from \`',table_schema,'\`.\`',table_name,'\`;') from information_schema.tables where table_schema in ('jeecg-boot','jeecg-boot-v3') and table_type='BASE TABLE'" | q | q | sort
}
rowcount > /home/base/rowcount-57-$TS.txt; echo exit=$? lines=$(wc -l < /home/base/rowcount-57-$TS.txt)
```

预期 `exit=0`，`lines` 等于两个库的表数之和。

## 3. 切换到 MySQL 8.4

**[server]** 编辑**服务器自己的** `docker-compose.yml`（不要用仓库文件覆盖，密码是内联的），只改 3 处：

1. `mysql.image: mysql:5.7.37` → `mysql:8.4.11`
2. `mysql.volumes` 中 `./mysql/data:/var/lib/mysql` → `./mysql84/data:/var/lib/mysql`
3. `redis.image: redis:5.0` → `redis:7.4.11`

`command` 中的 `--lower_case_table_names=1` 等参数保持不变：8.4 要求这个参数在首次初始化时就存在，之后不能改。

```bash
diff docker-compose.yml.bak-$TS docker-compose.yml     # 只应有上面 3 行差异
docker-compose stop mysql redis
docker-compose up -d mysql
until docker exec mysql sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" -e "select 1"' >/dev/null 2>&1; do sleep 3; done
docker exec mysql sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" -N -e "select version(),@@lower_case_table_names,@@character_set_server,@@collation_server"'
```

预期 `8.4.11  1  utf8mb4  utf8mb4_general_ci`。如果 `lower_case_table_names` 不是 1，就停下，删除 `mysql84/data` 后重新初始化。

导入数据（8.4 默认 `max_allowed_packet` 为 64M，不需要调整）：

```bash
set -o pipefail
gzip -dc /home/base/backup-mysql57-$TS.sql.gz | docker exec -i mysql sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" --default-character-set=utf8mb4'; echo exit=$?
rowcount > /home/base/rowcount-84-$TS.txt; echo exit=$?
diff /home/base/rowcount-57-$TS.txt /home/base/rowcount-84-$TS.txt && echo ROWCOUNTS_IDENTICAL
```

预期两个 `exit=0`，并打印 `ROWCOUNTS_IDENTICAL`。如果新开了 shell，要先重新定义第 2 步的 `q` / `rowcount` 函数。

8.4 默认开启 binlog（保留 30 天）。本库很小，磁盘占用可以接受，所以不改这个默认值。

## 4. 切换 Redis 7.4

```bash
docker-compose up -d redis
docker logs redis 2>&1 | grep -E 'version=|Loading RDB|Done loading|Ready'
```

预期出现 `Redis version=7.4.11`、`Loading RDB produced by version 5.0.x` 和 `Ready to accept connections`。

## 5. 上线新 jar + web，并执行 Flyway 102

替换 jar 和 web 的方法同 `UPGRADE-3.9.5.md`。`erp-api` 的 entrypoint 临时加 `-Dspring.flyway.enabled=true`：

```bash
docker-compose up -d erp-api
docker logs -f erp-api 2>&1 | grep -E 'Successfully applied|Migrating schema|Started JeecgSystemApplication|APPLICATION FAILED|ERROR'
docker exec mysql sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" -N -e "select version,success from \`jeecg-boot-v3\`.flyway_schema_history where version like \"3.9.5.1%\"; show columns from \`jeecg-boot-v3\`.jsh_customer like \"sort_no\""'
```

预期 `3.9.5.102  1`（101 如果之前没走 Flyway，本次也会执行，它是 `INSERT IGNORE`，可以重复执行），并且存在 `sort_no` 列。确认后从 entrypoint 去掉 flyway 参数，再执行一次 `docker-compose up -d erp-api`。

## 6. 冒烟

浏览器登录 `https://erp.jijianxia.com`，依次检查：

- 客户列表能打开，并能按“排序”列排序
- 新增一个订单时，客户下拉框有数据
- 订单列表、产品列表正常
- 首页统计数字与切换前一致

10 分钟后检查 `docker logs erp-api | grep StatisticOrderJob` 有 `statistic orders end`。

## 7. 回滚

触发条件：第 3–6 步任一步不符合预期，且无法当场修复。

```bash
docker-compose stop erp-api mysql redis
cp docker-compose.yml.bak-$TS docker-compose.yml        # 回到 5.7.37 + mysql/data + redis:5.0
mv redis/data/dump.rdb redis/data/dump.rdb.v74-$TS      # 7.4 写过的 RDB，5.0 读不了；丢的只是登录态
# jar 和 web 恢复为本次发布前的版本（新 jar 依赖 sort_no 列，不能配旧库）
docker-compose up -d mysql redis erp-api
```

注意：切换后写入 8.4 的业务数据（订单等），回滚后不在 5.7 里。上线后发现问题要尽早决定是否回滚；超过半天就改为前滚修复。

旧 3.0 应用（`jeecg-boot` 库，旧 jar）在 8.4 上同样会因为 `rank` 列报错。所以升级到 8.4 之后，回退到 3.0 必须连同 MySQL 一起回退到 5.7。

## 8. 清理（稳定运行 2 周后）

```bash
mv /home/base/docker/mysql/data /home/base/docker/mysql/data.57-retired-$TS   # 确认无需回滚后再删
```

`mysql:5.7.37` / `redis:5.0` 镜像和 `backup-mysql57-$TS.sql.gz` 同期清理。
