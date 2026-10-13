# JeecgBoot 3.0 → 3.9.5 生产切换 Runbook

适用：`erp.jijianxia.com` 单机 docker（服务器 `/home/base/docker/`，compose 项目名 `docker`，命令为 **`docker-compose`**（独立版，服务器没有 `docker compose` 子命令））。

思路：老库 `jeecg-boot` **只读不动**，新建库 `jeecg-boot-v3`；erp-api 换成 JDK17 + 3.9.5 jar；web 目录整体替换。回滚 = 恢复 compose 备份 + 恢复 web 目录 + 重启 erp-api，老库原样可用。

约定：
- `[local]` 表示在本机仓库根目录执行，`[server]` 表示在服务器 root shell 执行。
- 两边都先设置**同一个** `TS`，例如 `export TS=202610140100`。
- 密钥只从 `.env`（本地）和容器自身环境变量（服务器）取，**不要把任何密码打到屏幕、写进文件或提交**。
- Shell：服务器为 bash，本地为 zsh。凡是 mysql/mysqldump 输出经 `|` 管道的代码块，开头都有 `set -o pipefail`（bash 与 zsh 都支持；zsh 也可写 `setopt pipefail`），并在关键步骤后打印 mysql/mysqldump 自身的退出码 `exit=...`。**不要用 `set -e`**，否则交互式 shell 遇错会直接退出。
- 每步都列了预期输出；与预期不符就停在那一步，按该步的“失败处理”或第 5 节回滚。

## 0. 服务器现状（切换前只读核查）

| 项 | 实际值 |
|---|---|
| OS / 架构 | Linux x86_64，shell 为 bash；内存小且无 swap（必须限堆，见下文 JVM 内存） |
| Docker | 独立版 `docker-compose`；**拉 Docker Hub 超时**（无 registry mirror）→ 镜像本地 save 后上传 |
| 容器 | `mysql`(5.7.37) / `redis`(5.0) / `erp-api`(openjdk:8-jdk) / `nginx`(stable)，compose 文件 `/home/base/docker/docker-compose.yml` |
| 老 jar | `/home/java/erp-api/jeecg-boot-module-system-3.0.jar`（目录属主 java:java） |
| web | `/home/www/web/erp`（含历史累积的旧 js，属主 www:web），nginx 以 bind mount 挂成 `/var/www/web`；acme.sh 的 webroot 也是该目录 |
| MySQL | 只有库 `jeecg-boot`；`lower_case_table_names=1`；`max_allowed_packet=4M` |
| Redis | 密码写在 `redis/conf/redis.conf` 的 `requirepass`（compose 里没有） |
| nginx | `location ^~ /portal` → `http://erp-api:8080/portal/`；`/portal/websocket` 同理；`location /` root `/var/www/web/`，带 history 回退到 `index.html`；`/electron` alias 不变 |
| 定时任务 | 老库 `sys_quartz_job`：`org.jeecg.modules.quartz.job.order.StatisticOrderJob`（类名末尾带一个 Tab）每 10 分钟，status 0 |

服务器 compose 与仓库 `jeecg-docker/docker-compose.yml` 的结构差异：
- 服务器 `mysql.environment.MYSQL_ROOT_PASSWORD`、`erp-api` 的 DB/Redis `-D` 密码均为**内联明文**；仓库用 `${...}` 从 `.env` 取，且 `mysql` 多一个 `env_file: .env`。
- 服务器 `redis` 用 `redis-server /etc/redis/redis.conf`（密码在 conf 里）；仓库用 `--requirepass ${REDIS_PASSWORD}`。
- 其余服务、端口、卷一致。

**所以服务器 compose 不能用仓库文件覆盖**，而是以服务器文件为底，只改 `erp-api` 的 4 处（第 2.4 步脚本生成并 diff）：
1. `image: openjdk:8-jdk` → `eclipse-temurin:17-jre`
2. jar 挂载 `jeecg-boot-module-system-3.0.jar` → `jeecg-system-start-3.9.5.jar`
3. `-Dspring.redis.{host,port,password}` → `-Dspring.data.redis.{host,port,password}`（Spring Boot 4 路径；值不变）
4. 首次启动额外插入 `-Dspring.flyway.enabled=true`（prod yml 默认 false），迁移完成后去掉

JVM 内存：`-Xms1024m` 改为 `-Xms512m -Xmx1g -XX:MaxMetaspaceSize=320m`（本地实测：不限堆时 G1 把堆撑到 5G；Full GC 后存活约 125M，内存小且无 swap 的服务器必须限堆）。不变：`-Dspring.profiles.active=prod`、`-Duser.timezone=GMT+8`、`-Dspring.datasource.dynamic.datasource.master.username/password`。库名 `jeecg-boot-v3` 与主机 `mysql`/`redis` 已写在 jar 内 `application-prod.yml`，无需 `-D`。

---

## 1. 本地准备（不停机，可提前一天做）

### 1.1 设置变量与 SSH

```bash
# [local] 仓库根目录
export TS=202610140100            # 改成本次切换的时间戳，server 端用同一个值
export REPO=$(pwd)
export OUT=$HOME/erp-upgrade-$TS  # 产物与备份目录（仓库外）
mkdir -p "$OUT"
set -a; source jeecg-docker/.env; set +a   # 只为拿到 $SSH_HOST，不要 echo 其它变量
ssh root@"$SSH_HOST" 'echo ok'
```
预期：输出 `ok`。

### 1.2 构建后端 jar（JDK17）

```bash
# [local]
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
(cd jeecg-boot-v3 && mvn clean package -P prod -DskipTests)
J=jeecg-boot-v3/jeecg-module-system/jeecg-system-start/target/jeecg-system-start-3.9.5.jar
ls -la "$J"
unzip -l "$J" | grep -c 'V3.9.5_100__erp_init.sql'
unzip -l "$J" | grep -c 'BOOT-INF/lib/jeecg-module-erp'
cp "$J" "$OUT/"
```
预期：`BUILD SUCCESS`；jar 约 380MB；两个 `grep -c` 都输出 `1`（ERP Flyway 脚本与 ERP 模块已打进 jar）。
失败处理：输出 `0` 说明 Task 3/4 产物未合入，停止。

### 1.3 构建前端

```bash
# [local]
(cd erp-web-v3 && pnpm install --frozen-lockfile && pnpm build)
grep -o 'erp.jijianxia.com/portal' erp-web-v3/dist/_app.config.js | head -1
tar -czf "$OUT/erp-web-dist.tgz" -C erp-web-v3/dist .
ls -la "$OUT/erp-web-dist.tgz"
```
预期：build 成功；grep 输出 `erp.jijianxia.com/portal`（API 地址来自 `.env.production`，若为空说明地址没配对，停止）；tgz 约 30~40MB。

### 1.4 生成生产版 SQL（库名替换）

上游基础脚本前两行是 `CREATE database ... \`jeecg-boot\`; USE \`jeecg-boot\`;`，后面有 143 条 `DROP TABLE IF EXISTS`。**原样执行会清空生产老库！必须先替换库名。**

```bash
# [local]
sed -e '1,2s/`jeecg-boot`/`jeecg-boot-v3`/' jeecg-boot-v3/db/jeecgboot-mysql-5.7.sql > "$OUT/base.prod.sql"
head -2 "$OUT/base.prod.sql"
grep -c '`jeecg-boot`' "$OUT/base.prod.sql"

# 迁移/核对脚本：本地约定 jeecg_old → jeecg_v3，生产为 `jeecg-boot` → `jeecg-boot-v3`
RW='s/'"'"'jeecg_old'"'"'/'"'"'jeecg-boot'"'"'/g; s/'"'"'jeecg_v3'"'"'/'"'"'jeecg-boot-v3'"'"'/g; s/"jeecg_old"/"jeecg-boot"/g; s/"jeecg_v3"/"jeecg-boot-v3"/g; s/`?\bjeecg_old\b`?/`jeecg-boot`/g; s/`?\bjeecg_v3\b`?/`jeecg-boot-v3`/g'
perl -pe "$RW" jeecg-boot-v3/db/erp/migrate_from_3.0.sql > "$OUT/migrate.prod.sql"
perl -pe "$RW" jeecg-boot-v3/db/erp/verify_migration.sql  > "$OUT/verify.prod.sql"
grep -c 'jeecg_old\|jeecg_v3' "$OUT/migrate.prod.sql" "$OUT/verify.prod.sql"
grep -c 'jeecg-boot-v3' "$OUT/migrate.prod.sql"
diff <(perl -pe "$RW" jeecg-boot-v3/db/erp/migrate_from_3.0.sql) "$OUT/migrate.prod.sql" && echo same
```
预期：
- `head -2` 两行都是 `` `jeecg-boot-v3` ``；第一个 `grep -c` 输出 `0`。
- 两个迁移文件 `jeecg_old|jeecg_v3` 计数均为 `0`；`jeecg-boot-v3` 计数 > 0。
- 若 `jeecg-boot-v3/db/erp/README.md` 写了额外的替换规则，以 README 为准，再肉眼看一遍 `diff jeecg-boot-v3/db/erp/migrate_from_3.0.sql "$OUT/migrate.prod.sql"`，确认只有库名变化。

### 1.5 准备 JRE 镜像（服务器拉不到 Docker Hub）

```bash
# [local]
docker pull --platform linux/amd64 eclipse-temurin:17-jre
docker image inspect eclipse-temurin:17-jre --format '{{.Architecture}} {{.Id}}'
docker save eclipse-temurin:17-jre | gzip > "$OUT/temurin17jre.tar.gz"
(cd "$OUT" && shasum -a 256 *.jar *.tgz *.sql *.tar.gz > SHA256SUMS && cat SHA256SUMS)
```
预期：Architecture 为 `amd64`；SHA256SUMS 列出 6 个文件。

### 1.6（强烈建议）本地用 prod profile 演练一次

在 `jeecg-boot-v3/docker/local-verify` 环境（`erp-verify-mysql` 13306 / `erp-verify-redis` 16379）上，按第 3 节同样顺序跑：新建库 → 基础脚本（本地用 `sed -e '1,2s/`jeecg-boot`/`jeecg_v3`/' jeecg-boot-v3/db/jeecgboot-mysql-5.7.sql | docker exec -i erp-verify-mysql mysql -uroot -proot`）→ jar 以 **prod profile** + flyway 启动 → 迁移 → 核对。jar 启动命令：

```bash
# [local] 已有 jeecg_old（生产快照）的前提下；本地库名就是 jeecg_v3，所以用 -D 覆盖 url
java -Xms512m -Xmx1g -XX:MaxMetaspaceSize=320m -Dspring.profiles.active=prod -Dspring.flyway.enabled=true -Duser.timezone=GMT+8 \
  "-Dspring.datasource.dynamic.datasource.master.url=jdbc:mysql://127.0.0.1:13306/jeecg_v3?characterEncoding=UTF-8&useUnicode=true&useSSL=false&tinyInt1isBit=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai" \
  -Dspring.datasource.dynamic.datasource.master.username=root -Dspring.datasource.dynamic.datasource.master.password=root \
  -Dspring.data.redis.host=127.0.0.1 -Dspring.data.redis.port=16379 \
  -jar "$OUT/jeecg-system-start-3.9.5.jar"
```
预期：日志出现 `Application Jeecg-Boot is running!`，无 `【数据库升级】flyway执行sql脚本失败`；`curl -s http://127.0.0.1:8080/portal/sys/randomImage/1` 返回 `"success":true`。prod profile 与 dev 的差异（alioss、prod 日志级别、Flyway 关闭）只有这里能提前暴露。

---

## 2. 服务器准备（不停机）

### 2.1 预检

```bash
# [server]
export TS=202610140100            # 与本地相同
export W=/root/erp-upgrade-$TS
mkdir -p "$W"
cd /home/base/docker
docker ps --format '{{.Names}}\t{{.Image}}\t{{.Status}}'
df -h / | tail -1
M() { docker exec -i mysql sh -c 'exec mysql -uroot -p"$MYSQL_ROOT_PASSWORD" "$@"' mysql "$@"; }
M -N -e "SHOW DATABASES" 2>/dev/null
```
预期：4 个容器 Up；可用空间 ≥ 5G；数据库列表**没有** `jeecg-boot-v3`（有的话说明之前演练过，先确认再 `DROP`，绝不能动 `jeecg-boot`）。
注：`M` 函数用容器内的 `MYSQL_ROOT_PASSWORD`，密码不出现在命令行历史。下面所有 server 步骤都假定同一个 shell 里已定义 `M`、`TS`、`W`。

### 2.2 上传产物

```bash
# [local]
scp "$OUT"/jeecg-system-start-3.9.5.jar root@"$SSH_HOST":/home/java/erp-api/
scp "$OUT"/erp-web-dist.tgz "$OUT"/base.prod.sql "$OUT"/migrate.prod.sql "$OUT"/verify.prod.sql \
    "$OUT"/temurin17jre.tar.gz "$OUT"/SHA256SUMS root@"$SSH_HOST":/root/erp-upgrade-$TS/
```
```bash
# [server]
chown java:java /home/java/erp-api/jeecg-system-start-3.9.5.jar
cd "$W" && cp /home/java/erp-api/jeecg-system-start-3.9.5.jar . && sha256sum -c SHA256SUMS && rm -f jeecg-system-start-3.9.5.jar
ls -la /home/java/erp-api/
```
预期：6 行 `OK`；`/home/java/erp-api/` 下新旧两个 jar 并存（老 jar 不删，回滚用）。
失败处理：校验不过就重新 scp。

### 2.3 加载 JRE 镜像

```bash
# [server]
gunzip -c "$W/temurin17jre.tar.gz" | docker load
docker run --rm eclipse-temurin:17-jre java -version
```
预期：`Loaded image: eclipse-temurin:17-jre`；`openjdk version "17.0.x"`。

### 2.4 生成新 compose（只改 erp-api，先不替换）

```bash
# [server]
cd /home/base/docker
cat > "$W/gen_compose.py" <<'PY'
import sys
src = sys.argv[1]
s = open(src).read()
rep = [('image: openjdk:8-jdk', 'image: eclipse-temurin:17-jre'),
       ('/home/java/erp-api/jeecg-boot-module-system-3.0.jar:', '/home/java/erp-api/jeecg-system-start-3.9.5.jar:'),
       ('"-Dspring.redis.host=', '"-Dspring.data.redis.host='),
       ('"-Dspring.redis.port=', '"-Dspring.data.redis.port='),
       ('"-Dspring.redis.password=', '"-Dspring.data.redis.password='),
       ('      - "-Xms1024m"\n', '      - "-Xms512m"\n      - "-Xmx1g"\n      - "-XX:MaxMetaspaceSize=320m"\n')]
for a, b in rep:
    n = s.count(a)
    if n != 1:
        sys.exit('expect 1 match for %r, got %d' % (a, n))
    s = s.replace(a, b)
open(src + '.v3', 'w').write(s)
anchor = '      - "-Duser.timezone=GMT+8"\n'
if s.count(anchor) != 1:
    sys.exit('anchor not found')
open(src + '.v3-flyway', 'w').write(s.replace(anchor, anchor + '      - "-Dspring.flyway.enabled=true"\n'))
print('ok')
PY
python3 "$W/gen_compose.py" docker-compose.yml
diff docker-compose.yml docker-compose.yml.v3-flyway | sed -E 's/(password=)[^"]*/\1***/'
diff docker-compose.yml.v3 docker-compose.yml.v3-flyway
docker-compose -f docker-compose.yml.v3-flyway config -q && echo CONFIG_OK
```
预期（已用服务器文件的脱敏副本在本地验证过）：
```
ok
33c33
<     image: openjdk:8-jdk
---
>     image: eclipse-temurin:17-jre
43c43
<       - "/home/java/erp-api/jeecg-boot-module-system-3.0.jar:/app/erp-api.jar"
---
>       - "/home/java/erp-api/jeecg-system-start-3.9.5.jar:/app/erp-api.jar"
48a49
>       - "-Dspring.flyway.enabled=true"
51,53c52,54
<       - "-Dspring.redis.host=redis"
<       - "-Dspring.redis.port=6379"
<       - "-Dspring.redis.password=***"
---
>       - "-Dspring.data.redis.host=redis"
>       - "-Dspring.data.redis.port=6379"
>       - "-Dspring.data.redis.password=***"
48a49
>       - "-Dspring.flyway.enabled=true"
CONFIG_OK
```
失败处理：脚本报 `expect 1 match` 说明服务器文件已被改过，停下人工核对；**不要**用仓库 compose 覆盖。

### 2.5 在线预备份（不停机）并拉回本地

```bash
# [server]
set -o pipefail
docker exec mysql sh -c 'exec mysqldump -uroot -p"$MYSQL_ROOT_PASSWORD" --single-transaction --routines --triggers --events --databases jeecg-boot' 2>"$W/dump_pre.err" | gzip > "$W/jeecg-boot_pre_$TS.sql.gz"
echo "exit=${PIPESTATUS[0]}/${PIPESTATUS[1]}"   # mysqldump/gzip，紧跟上一行，中间不要插别的命令
grep -v 'Using a password' "$W/dump_pre.err"; ls -la "$W/jeecg-boot_pre_$TS.sql.gz"
gunzip -c "$W/jeecg-boot_pre_$TS.sql.gz" | tail -1
```
```bash
# [local]
scp root@"$SSH_HOST":/root/erp-upgrade-$TS/jeecg-boot_pre_$TS.sql.gz "$OUT/"
gunzip -t "$OUT/jeecg-boot_pre_$TS.sql.gz" && echo GZ_OK
```
预期：`exit=0/0`（任一非 0 即 dump 失败，停止）；err 文件除密码警告外无内容；最后一行 `-- Dump completed on ...`；本地 `GZ_OK`。dump 含真实客户数据，只放 `$OUT`，不进仓库、不外传。

---

## 3. 切换（停机窗口，预计 30~60 分钟）

### 3.1 停老 erp-api

```bash
# [server]
cd /home/base/docker
docker-compose stop erp-api
docker ps -a --filter name=erp-api --format '{{.Names}} {{.Status}}'
```
预期：`erp-api Exited (...)`。此刻起网站接口 502（预期内）。
回滚：`docker-compose start erp-api`。

### 3.2 停机后最终 dump 并拉回

```bash
# [server]
set -o pipefail
docker exec mysql sh -c 'exec mysqldump -uroot -p"$MYSQL_ROOT_PASSWORD" --single-transaction --routines --triggers --events --databases jeecg-boot' 2>"$W/dump_final.err" | gzip > "$W/jeecg-boot_final_$TS.sql.gz"
echo "exit=${PIPESTATUS[0]}/${PIPESTATUS[1]}"   # mysqldump/gzip
grep -v 'Using a password' "$W/dump_final.err"; gunzip -c "$W/jeecg-boot_final_$TS.sql.gz" | tail -1
```
```bash
# [local]
scp root@"$SSH_HOST":/root/erp-upgrade-$TS/jeecg-boot_final_$TS.sql.gz "$OUT/" && gunzip -t "$OUT/jeecg-boot_final_$TS.sql.gz" && echo GZ_OK
```
预期：同 2.5（`exit=0/0`）。

### 3.3 Redis：备份后清空 db0

新旧版本共用 redis db0，老版本缓存（字典、用户、token）的序列化格式与 3.9.5 不兼容，会导致反序列化报错。

```bash
# [server]
R() { docker exec -i redis sh -c 'exec redis-cli -a "$(sed -n "s/^[[:space:]]*requirepass[[:space:]]\{1,\}//p" /etc/redis/redis.conf | tr -d "\"")" --no-auth-warning "$@"' redis-cli "$@"; }
R PING
R DBSIZE
R SAVE
cp -a /home/base/docker/redis/data/dump.rdb "$W/redis-dump-$TS.rdb"
R FLUSHDB
R DBSIZE
```
预期：`PONG`；`SAVE` 返回 `OK`；最后 `DBSIZE` 为 `0`。
回滚影响：老系统缓存会自动重建，用户需重新登录，无数据损失。

### 3.4 建新库并导入上游基础脚本

```bash
# [server]
set -o pipefail
head -2 "$W/base.prod.sql"
grep -c '`jeecg-boot`' "$W/base.prod.sql"
M < "$W/base.prod.sql" 2>&1 | grep -v 'Using a password'
echo "exit=${PIPESTATUS[0]}"   # mysql 自身退出码；grep -v 无输出时返回 1，属正常，所以不看 $?
M -N -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='jeecg-boot-v3'; SELECT version,success FROM \`jeecg-boot-v3\`.flyway_schema_history ORDER BY installed_rank DESC LIMIT 1; SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='jeecg-boot';" 2>/dev/null
```
预期：`head -2` 两行都是 `` `jeecg-boot-v3` ``，`grep -c` 为 `0`（**不是 0 立即停止**）；导入无 ERROR 且 `exit=0`；新库表数 > 100；flyway 最新行 `3.9.5.0 1`；老库表数与导入前一致（不变）。
失败处理：`M -e 'DROP DATABASE \`jeecg-boot-v3\`'` 后修正重来（只 drop 新库）。

### 3.5 首次启动 3.9.5（带 Flyway）

```bash
# [server]
cd /home/base/docker
cp -a docker-compose.yml docker-compose.yml.bak-$TS
cp docker-compose.yml.v3-flyway docker-compose.yml
docker-compose up -d --no-deps erp-api
docker inspect erp-api --format '{{.Config.Image}} {{range .Mounts}}{{.Source}}{{end}}'
timeout 600 sh -c 'until docker logs erp-api 2>&1 | grep -q "Jeecg-Boot is running"; do sleep 5; done' && echo STARTED
docker logs erp-api 2>&1 | sed 's/\x1b\[[0-9;]*m//g' | grep -E '数据库升级| ERROR ' | head -20
```
预期：Image 为 `eclipse-temurin:17-jre`，挂载源为 `jeecg-system-start-3.9.5.jar`；`STARTED`；日志有 `【数据库升级】平台集成了MySQL库的Flyway`，**没有** `flyway执行sql脚本失败`。
注意：Flyway 失败只记 ERROR 不阻止启动，所以必须做 3.6 的查询。
失败处理（起不来/OOM/Flyway 报错）：`docker logs --tail 200 erp-api` 留证据后走第 5 节回滚。

### 3.6 校验 Flyway 结果

```bash
# [server]
M -t -e "
SELECT version,description,success FROM \`jeecg-boot-v3\`.flyway_schema_history ORDER BY installed_rank DESC LIMIT 3;
SELECT COUNT(*) jsh_tables FROM information_schema.tables WHERE table_schema='jeecg-boot-v3' AND table_name IN ('jsh_customer','jsh_customer_address','jsh_order','jsh_order_product','jsh_order_product_detail','jsh_order_product_extend','jsh_order_summary_daily','jsh_order_summary_monthly','jsh_product','jsh_sequence','jsh_version');
SELECT url,component FROM \`jeecg-boot-v3\`.sys_permission WHERE url LIKE '/business%' AND menu_type IN (0,1) ORDER BY url;
SELECT job_class_name,cron_expression,status FROM \`jeecg-boot-v3\`.sys_quartz_job WHERE job_class_name LIKE '%StatisticOrderJob%';" 2>/dev/null
```
预期：
- 最新一行 `3.9.5.100 | erp init | 1`；
- `jsh_tables` = `11`；
- 7 条菜单：`/business/customer`、`/business/customer/list`、`/business/order`、`/business/order/list`、`/business/order/summary`、`/business/product`、`/business/product/list`，component 为 `business/...` / `layouts/default/index`（以 Task 4 Interfaces 表为准）；
- 定时任务类名为 `org.jeecg.modules.business.job.StatisticOrderJob`（不是老的 `org.jeecg.modules.quartz.job.order...`）。

任一不符：停止，走第 5 节回滚。

### 3.7 停 erp-api 后执行数据迁移

迁移会 TRUNCATE `jsh_*`，期间不能有应用写库（定时任务每 10 分钟写统计表），所以先停。

```bash
# [server]
cd /home/base/docker
docker-compose stop erp-api
M -N -e "SELECT MAX(1+LENGTH(rel_tenant_ids)-LENGTH(REPLACE(rel_tenant_ids,',',''))) FROM \`jeecg-boot\`.sys_user WHERE rel_tenant_ids<>''" 2>/dev/null
grep -c 'jeecg_old\|jeecg_v3' "$W/migrate.prod.sql"
set -o pipefail
M < "$W/migrate.prod.sql" 2>&1 | grep -v 'Using a password' | tee "$W/migrate.out"
echo "exit=${PIPESTATUS[0]}"   # mysql 自身退出码
```
预期：第一个查询 ≤ `10`；`grep -c` 为 `0`；迁移输出无 `ERROR`，且 `exit=0`（非 0 即迁移中止，不要继续 3.8）。
失败处理：迁移只写新库，老库不受影响。修脚本后可直接重跑（脚本设计为可重复执行）；或 `DROP DATABASE \`jeecg-boot-v3\`` 从 3.4 重来；或第 5 节回滚。

### 3.8 核对

```bash
# [server]
set -o pipefail
M -t < "$W/verify.prod.sql" 2>/dev/null | tee "$W/verify.out"
echo "exit=${PIPESTATUS[0]}"   # mysql 自身退出码
```
预期：`exit=0`；对照表每行 `old_cnt = new_cnt`（11 张 `jsh_*` + `sys_user` + `sys_tenant` + `sys_role`）；按租户的订单数与金额汇总两边完全相同；用户-租户关系条数等于老库 `rel_tenant_ids` 中租户 id 总数。
失败处理：任何一行不等 → 不要上线，走第 5 节回滚，把 `verify.out` 拉回本地分析。

### 3.9 正式启动（去掉 Flyway 开关）

```bash
# [server]
cd /home/base/docker
cp docker-compose.yml.v3 docker-compose.yml
diff docker-compose.yml.bak-$TS docker-compose.yml | sed -E 's/(password=)[^"]*/\1***/' | grep -c flyway
docker-compose up -d --no-deps erp-api
timeout 600 sh -c 'until docker logs erp-api 2>&1 | grep -q "Jeecg-Boot is running"; do sleep 5; done' && echo STARTED
docker logs erp-api 2>&1 | sed 's/\x1b\[[0-9;]*m//g' | grep -c ' ERROR '
```
预期：`grep -c flyway` 为 `0`（最终 compose 不再带 Flyway 开关）；`STARTED`；ERROR 计数为 `0`（若非 0，`docker logs erp-api 2>&1 | sed 's/\x1b\[[0-9;]*m//g' | grep -A5 ' ERROR '` 判断是否致命）。

### 3.10 后端冒烟

```bash
# [server]
curl -s http://127.0.0.1:8080/portal/sys/randomImage/smoke$TS | head -c 120; echo
curl -s -o /dev/null -w '%{http_code}\n' http://127.0.0.1:8080/portal/business/order/jshOrder/list
curl -sk https://erp.jijianxia.com/portal/sys/randomImage/smoke$TS | head -c 120; echo
# 订单统计定时任务由 ErpQuartzJobBootstrap 在启动时自动注册（无需登录点“启动”）
M -N -e "SELECT j.JOB_NAME, j.JOB_CLASS_NAME, t.TRIGGER_STATE FROM \`jeecg-boot-v3\`.qrtz_job_details j JOIN \`jeecg-boot-v3\`.qrtz_triggers t ON t.JOB_NAME=j.JOB_NAME WHERE j.JOB_NAME='1511267922611875842';" 2>/dev/null
docker logs erp-api 2>&1 | grep -c ErpQuartzJobBootstrap
```
预期：qrtz 查询返回 1 行，类名 `org.jeecg.modules.business.job.StatisticOrderJob`、状态 `WAITING`（或 `ACQUIRED`）；日志计数 ≥1。第 1、3 条返回 `{"success":true,...,"result":"data:image/...`（同时证明 Redis 连通、nginx `/portal` 反代通）；第 2 条未登录访问 ERP 接口返回 401 类 JSON（HTTP 码 200 或 401 均可，**不能是 404**——404 说明 ERP 模块没加载）。
失败处理：第 5 节回滚。

### 3.11 替换 web

nginx 是 bind mount 挂载 `/home/www/web/erp`：如果 `mv` 整个目录，nginx 容器仍指向旧 inode（继续服务老页面），必须重启 nginx 才生效。这里改为“复制备份 + 原目录内替换内容”，nginx 无需重启/重载；同时保留 acme.sh 用的 `.well-known`。

```bash
# [server]
cd /home/www/web
cp -a erp erp.bak-$TS
du -sh erp erp.bak-$TS
find erp -mindepth 1 -maxdepth 1 ! -name .well-known -exec rm -rf {} +
tar -xzf "$W/erp-web-dist.tgz" -C erp
chown -R www:web erp
ls erp | head; test -f erp/index.html && echo INDEX_OK
curl -sk -o /dev/null -w '%{http_code}\n' https://erp.jijianxia.com/
curl -sk https://erp.jijianxia.com/_app.config.js | head -c 200; echo
curl -sk -o /dev/null -w '%{http_code}\n' https://erp.jijianxia.com/business/order/list
```
预期：备份与原目录大小一致；`INDEX_OK`；首页 `200`；`_app.config.js` 中含 `erp.jijianxia.com/portal`；history 路由刷新 `200`。
回滚：
```bash
cd /home/www/web && find erp -mindepth 1 -maxdepth 1 -exec rm -rf {} + && cp -a erp.bak-$TS/. erp/
```

### 3.12 业务冒烟（浏览器）

浏览器无痕窗口打开 `https://erp.jijianxia.com`（新前端有 service worker，老页面缓存可能干扰，必要时强制刷新）：
1. 两个租户各用一个真实账号登录，确认只看到本租户数据。
2. 客户信息 / 商品信息 / 订单信息 / 订单统计 四个菜单都能打开。
3. 订单列表总条数 = `M -N -e "SELECT tenant_id,COUNT(*) FROM \`jeecg-boot\`.jsh_order GROUP BY tenant_id"` 对应租户的值。
4. 订单导出 Excel 成功。
5. `docker logs --since 10m erp-api 2>&1 | sed 's/\x1b\[[0-9;]*m//g' | grep ' ERROR '` 无输出。
6. 过一个 10 分钟整点后：`docker logs --since 15m erp-api 2>&1 | grep StatisticOrderJob` 有执行记录。

任一失败 → 第 5 节回滚。

---

## 4. 完成后

- 观察 24 小时：`docker stats --no-stream erp-api`（内存）、`docker logs --since 1h erp-api 2>&1 | sed 's/\x1b\[[0-9;]*m//g' | grep -c ' ERROR '`。
- 保留至少 2 周：老库 `jeecg-boot`、老 jar、`docker-compose.yml.bak-$TS`、`/home/www/web/erp.bak-$TS`、`$W` 下的 dump。之后再清理。
- 本地 `$OUT` 的 dump 含客户数据，按备份策略保存，不进仓库。
- 仓库 `jeecg-docker/docker-compose.yml` 已与最终状态对应（无 Flyway 开关）。

---

## 5. 回滚（任何一步失败都适用，老库 `jeecg-boot` 全程未改）

```bash
# [server]
cd /home/base/docker
docker-compose stop erp-api
cp -a docker-compose.yml.bak-$TS docker-compose.yml        # 若尚未替换过 compose（3.5 之前）此步可跳过
grep -E 'image: openjdk|jeecg-boot-module-system-3.0.jar|spring.redis.host' docker-compose.yml | wc -l
docker-compose up -d --no-deps erp-api
timeout 600 sh -c 'until docker logs erp-api 2>&1 | grep -qi "Jeecg-Boot is running\|Started JeecgSystemApplication"; do sleep 5; done' && echo STARTED
# 若 3.3 已清过 redis：老系统缓存自动重建，无需处理（用户重新登录即可）
# 若 3.11 已替换 web：
cd /home/www/web && find erp -mindepth 1 -maxdepth 1 -exec rm -rf {} + && cp -a erp.bak-$TS/. erp/ && chown -R www:web erp
curl -s -o /dev/null -w '%{http_code}\n' http://127.0.0.1:8080/portal/sys/randomImage/rb$TS
curl -sk -o /dev/null -w '%{http_code}\n' https://erp.jijianxia.com/
```
预期：`grep ... | wc -l` 为 `3`（老镜像、老 jar、老 redis 前缀都回来了）；`STARTED`；两个 curl 都是 `200`；浏览器老页面可登录。

说明：
- 新库 `jeecg-boot-v3` 留着排查，不影响老系统；下次重试前 `DROP DATABASE \`jeecg-boot-v3\`` 从 3.4 重来。
- 老库只有在 3.1~3.2 之后、回滚之前若有人通过别的途径写入才会变化；正常流程老 erp-api 已停，老库无写入。若老库被意外破坏，用 `$W/jeecg-boot_final_$TS.sql.gz` 恢复：`set -o pipefail; gunzip -c "$W/jeecg-boot_final_$TS.sql.gz" | M; echo "exit=$?"`（预期 `exit=0`）（dump 含 `CREATE DATABASE`/`USE \`jeecg-boot\``，会覆盖同名表）。

## 6. 风险清单

| 风险 | 影响 | 对策 |
|---|---|---|
| 基础脚本自带 `USE \`jeecg-boot\`` + `DROP TABLE` | 原样执行清空生产老库 | 1.4 sed 改库名 + 3.4 执行前 `grep -c` 必须为 0；已有 3.2 最终 dump 兜底 |
| 服务器拉不到 Docker Hub | 新容器起不来 | 1.5 本地 save、2.3 load，切换前已验证 `java -version` |
| 内存小且无 swap | 3.9.5（含 AI 模块）内存占用大 | 已设 `-Xms512m -Xmx1g -XX:MaxMetaspaceSize=320m`（本地联调在该参数下跑通全流程）；3.5/3.9 观察 `docker stats`；OOM 则回滚，再评估 `-Xmx1536m` |
| Flyway 失败不阻止启动；`validateOnMigrate=true` | ERP 表/菜单缺失却“启动成功”；上游脚本被改过会校验失败 | 3.6 强制查 `flyway_schema_history` 与表/菜单/任务 |
| Redis 缓存格式不兼容 | 字典/登录异常 | 3.3 备份后 FLUSHDB |
| nginx bind mount 指向 inode | `mv` web 目录后仍是老页面 | 3.11 原目录内替换，不 mv |
| 老定时任务类名不同（且带 Tab） | 新库若沿用老类名任务会报 ClassNotFound | 3.6 核对类名为 `org.jeecg.modules.business.job.StatisticOrderJob` |
| `max_allowed_packet=4M` | 超大 INSERT 失败 | 基础脚本最长行约 1.1MB，未超；迁移为库内 `INSERT...SELECT`，不受限 |
| 新前端 service worker / 浏览器缓存 | 用户看到老页面 | 告知用户强制刷新；回滚后同理 |
