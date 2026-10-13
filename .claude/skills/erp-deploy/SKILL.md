---
name: erp-deploy
description: Use when deploying this jeecg-boot ERP to production (erp.jijianxia.com) — after a PR is merged into electron, when asked to 部署/发布/上线/上生产, when a new Flyway script must reach the prod DB, or when rolling back erp-api or the web frontend.
---

# ERP 生产部署

后端用 `jeecg-docker/deploy-api.sh`，前端用 `jeecg-docker/deploy-web.sh`。两个脚本都读 `jeecg-docker/.env`（`SSH_HOST`、`REDIS_PASSWORD`），root 免密 SSH。不要手敲 scp / docker-compose 代替脚本。

## 顺序

1. `electron` 已合并且本地同步：`git switch electron && git pull --ff-only`，`git status` 干净。
2. **本地按生产数据验证**（有 Flyway 脚本或改了权限、登录时必做）：
   - `erp-v84-mysql`(23306) 的 `jeecg_v3` 先 `mysqldump` 备份，确认 `flyway_schema_history` 与生产同版本（`deploy-api.sh status` 看生产版本）。
   - 本地 jar 带 `-Dspring.flyway.enabled=true` 启动，连 23306 / Redis 26379；日志要有 `Successfully applied N migrations`，不能有 `flyway执行sql脚本失败`。
   - 浏览器冒烟：`erp` 账号（租户 1001）看订单/客户/商品；admin 看改动涉及的系统页面。
   - 测完用备份恢复本地库。
3. `jeecg-docker/deploy-api.sh`：构建 prod jar → Flyway 预检 → 备份库和旧 jar → 上传 → 有待执行脚本就用 `docker-compose.yml.flyway`（只多 `-Dspring.flyway.enabled=true`）启动一次 → 核对版本前进 → 原 compose 重建 → 清 Redis 缓存 → 线上接口验证。jar 已构建好时加 `SKIP_BUILD=1`。
4. `jeecg-docker/deploy-web.sh`：前端构建、发布新版本目录、切软链接，验证失败自动切回。改了前端才需要。
5. 线上冒烟：`https://erp.jijianxia.com` 登录、订单列表；改动涉及的页面逐个点一遍。

## 规则

| 规则 | 原因 |
|---|---|
| 新 Flyway 脚本版本号必须高于生产已执行的最大版本（看 `deploy-api.sh status`）；上游带来的低版本脚本要改名 | `FlywayConfig` 没开 `outOfOrder`，低版本未执行脚本让 validate 失败，**整个 migrate 中止且只写日志，应用照常启动** |
| 已在任何库执行过的脚本不改内容；要改就新增脚本。部署前 `git diff <上次发布>..HEAD -- '*flyway/sql/mysql/*'` 只能有新增文件 | checksum 不符同样让 validate 失败；`deploy-api.sh` 预检只查版本号，**查不出改过内容** |
| 脚本执行到一半失败会留 `success=0` 记录，预检会拒绝发布，需人工检查后删掉该行再重试 | MySQL DDL 不随事务回滚 |
| 改了 SQL 后先重新打 jar 再启动 | 旧 jar 里是旧脚本 |
| SQL 改了部门名、字典、租户名，部署后清 `sys:cache:departPathName:*`、`sys:cache:dict*`（脚本已做） | 缓存不随 SQL 更新，界面继续显示旧名 |
| 用户显示名、字典文字改动，用户要刷新页面或重新登录 | 前端也缓存了用户信息和字典 |
| 绝不用仓库 compose 覆盖服务器 compose | 服务器文件内联了密码 |
| 不改 `username`，只改 `realname` | `jsh_*.create_by` 存的是 username |

## 回滚

- 后端：`jeecg-docker/deploy-api.sh rollback`，换回 `releases/` 中与线上不同的最新 jar，并用原 compose（Flyway 关闭）重建。数据库不自动回滚：新脚本留在库里，旧 jar 通常兼容；必须回滚库时先停 `erp-api`，再用 `/home/java/erp-api/backup/<版本>.sql.gz` 恢复（会丢失备份之后的数据）。
- 前端：`jeecg-docker/deploy-web.sh rollback [版本]`。

## 常见现象

| 现象 | 原因 |
|---|---|
| 部署或重启期间刷新页面 404 | 后端未就绪，菜单接口失败；就绪后刷新即恢复 |
| `Detected resolved migration not applied to database: x` | 低版本脚本未执行，见规则第一条 |
| admin 看不到 ERP 数据 | ERP 数据在租户 1001，admin 不属于该租户，属正常 |
| 本地 zsh 里 `M="docker exec ..."; $M ...` 没执行 | zsh 不做单词拆分，直接写完整命令 |
