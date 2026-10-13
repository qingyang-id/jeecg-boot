#!/usr/bin/env bash
# 后端发布：build -> 预检 Flyway -> 备份库与旧 jar -> 上传 -> 带 Flyway 启动一次 -> 核对版本 -> 常规启动 -> 清缓存 -> 线上验证
#
#   jeecg-docker/deploy-api.sh               发布当前 HEAD（jeecg-boot 不能有未提交改动）
#   jeecg-docker/deploy-api.sh check         只做本地构建与 Flyway 预检，不改服务器
#   jeecg-docker/deploy-api.sh status        当前 jar、Flyway 版本、容器状态
#   jeecg-docker/deploy-api.sh rollback      换回上一个 jar（数据库不动，需要时手动用备份恢复）
#
# 服务器：
#   /home/base/docker/docker-compose.yml         服务器自己的 compose（密码内联，不能用仓库文件覆盖）
#   /home/java/erp-api/jeecg-system-start-3.9.5.jar   挂载为 /app/erp-api.jar
#   /home/java/erp-api/releases/<版本>.jar        每次发布的 jar，rollback 从这里取
#   /home/java/erp-api/backup/<版本>.sql.gz       发布前的 jeecg-boot-v3 全库备份
# 依赖 jeecg-docker/.env 里的 SSH_HOST、REDIS_PASSWORD，root 免密登录。
set -euo pipefail

ROOT=$(cd "$(dirname "$0")/.." && pwd)
API="$ROOT/jeecg-boot"
JAR="$API/jeecg-module-system/jeecg-system-start/target/jeecg-system-start-3.9.5.jar"
DIR=/home/java/erp-api
LIVE=$DIR/jeecg-system-start-3.9.5.jar
COMPOSE=/home/base/docker
DB=jeecg-boot-v3
DOMAIN=${DOMAIN:-https://erp.jijianxia.com}

set -a
# shellcheck disable=SC1091
. "$ROOT/jeecg-docker/.env"
set +a
: "${SSH_HOST:?jeecg-docker/.env 缺少 SSH_HOST}"
: "${REDIS_PASSWORD:?jeecg-docker/.env 缺少 REDIS_PASSWORD}"
REMOTE="root@$SSH_HOST"

remote() { ssh -o BatchMode=yes "$REMOTE" "$@"; }

# 生产库已成功执行的最大 Flyway 版本，如 3.9.5.108
db_version() {
  remote "docker exec mysql sh -c 'mysql -uroot -p\"\$MYSQL_ROOT_PASSWORD\" -N -e \"SELECT version FROM \\\`$DB\\\`.flyway_schema_history WHERE success=1\"' 2>/dev/null" | sort -V | tail -1
}

# 等容器里的应用就绪（或失败），打印 Flyway 相关日志；失败返回 1
wait_ready() {
  remote "bash -s" <<'EOF'
for i in $(seq 1 90); do
  log=$(docker logs erp-api 2>&1)
  if echo "$log" | grep -q 'Application Jeecg-Boot is running'; then
    echo "$log" | grep -E 'Successfully applied|Migrating schema|flyway执行sql脚本失败|数据库升级' | sed 's/\x1b\[[0-9;]*m//g' | cut -c1-200
    echo "$log" | grep -q 'flyway执行sql脚本失败' && exit 1
    exit 0
  fi
  echo "$log" | grep -qE 'APPLICATION FAILED TO START' && { echo "$log" | tail -40; exit 1; }
  sleep 2
done
echo "等待 180s 未就绪"; docker logs --tail 40 erp-api; exit 1
EOF
}

# 用指定 compose 文件重建 erp-api（--force-recreate：新容器，日志从零开始，也一定加载新 jar）
up_api() { remote "cd $COMPOSE && docker-compose -f $1 up -d --force-recreate erp-api"; }

case "${1:-deploy}" in
  status)
    remote "ls -la $LIVE $DIR/releases 2>/dev/null | tail -8; docker ps --filter name=erp-api --format '{{.Names}} {{.Status}}'"
    echo "flyway: $(db_version)"
    exit 0
    ;;
  rollback)
    # 取 releases/ 里最新的、且与线上 jar 内容不同的那个
    prev=$(remote "cur=\$(sha256sum $LIVE | cut -d' ' -f1); for f in \$(ls -1 $DIR/releases/*.jar | sort -r); do [ \"\$(sha256sum \$f | cut -d' ' -f1)\" != \"\$cur\" ] && echo \$f && break; done")
    [ -n "$prev" ] || { echo "releases/ 下没有可回滚的 jar" >&2; exit 1; }
    remote "cp $prev $LIVE && chown java:java $LIVE 2>/dev/null || true"
    up_api docker-compose.yml
    wait_ready
    echo "已回滚到 ${prev}（数据库未回滚；需要时用 $DIR/backup/ 下的备份恢复）"
    exit 0
    ;;
  deploy) ;;
  check) CHECK_ONLY=1 ;;
  *) echo "用法: $0 [deploy|check|status|rollback]" >&2; exit 2 ;;
esac

cd "$ROOT"
git fetch -q origin electron
if [ "$(git rev-parse HEAD)" != "$(git rev-parse origin/electron)" ]; then
  echo "HEAD 不是 origin/electron（先 git switch electron && git pull --ff-only）" >&2
  exit 1
fi
if [ -n "$(git status --porcelain -- jeecg-boot)" ]; then
  echo "jeecg-boot 有未提交改动，先提交或清理" >&2
  exit 1
fi
REL="$(date +%Y%m%d%H%M%S)-$(git rev-parse --short HEAD)"
echo "== 版本 $REL"

if [ -z "${SKIP_BUILD:-}" ]; then
  (cd "$API" && mvn -q clean package -P prod -DskipTests)
fi
unzip -p "$JAR" BOOT-INF/classes/application.yml | grep -q "active: 'prod'" || { echo "jar 不是 prod profile" >&2; exit 1; }

# Flyway 预检：jar 里每个脚本的版本都必须高于生产已执行的最大版本（或已经执行过），
# 否则 outOfOrder=false 时 validate 失败、整个 migrate 中止且只记日志
CUR=$(db_version)
[ -n "$CUR" ] || { echo "读不到生产 Flyway 版本" >&2; exit 1; }
FAILED=$(remote "docker exec mysql sh -c 'mysql -uroot -p\"\$MYSQL_ROOT_PASSWORD\" -N -e \"SELECT version FROM \\\`$DB\\\`.flyway_schema_history WHERE success=0\"' 2>/dev/null")
[ -z "$FAILED" ] || { echo "flyway_schema_history 有失败记录: ${FAILED}，先人工处理（MySQL DDL 不回滚，需检查后删掉失败行）" >&2; exit 1; }
APPLIED=$(remote "docker exec mysql sh -c 'mysql -uroot -p\"\$MYSQL_ROOT_PASSWORD\" -N -e \"SELECT version FROM \\\`$DB\\\`.flyway_schema_history WHERE success=1\"' 2>/dev/null")
PENDING=""
for v in $(unzip -l "$JAR" | grep -oE 'flyway/sql/mysql/V[0-9._]+__[^ ]*\.sql' | sed -E 's#.*/V##; s#__.*##; s#_#.#g' | sort -uV); do
  echo "$APPLIED" | grep -qx "$v" && continue
  if [ "$(printf '%s\n%s\n' "$v" "$CUR" | sort -V | head -1)" = "$v" ]; then
    echo "Flyway 脚本 $v 低于生产已执行的 $CUR 且未执行，会导致校验失败。改成高于 $CUR 的版本号再发布" >&2
    exit 1
  fi
  PENDING="$PENDING $v"
done
echo "== 生产 Flyway ${CUR}，待执行:${PENDING:- 无}"
[ -z "${CHECK_ONLY:-}" ] || { echo "== check 通过（未改动服务器）"; exit 0; }

SUM=$(shasum -a 256 "$JAR" | cut -d' ' -f1)
remote "mkdir -p $DIR/releases $DIR/backup"
echo "== 上传 jar"
remote "cat > $DIR/releases/$REL.jar" < "$JAR"
remote "echo '$SUM  $DIR/releases/$REL.jar' | sha256sum -c --quiet -"

echo "== 备份数据库与当前 jar"
remote "bash -s -- $DIR $DB $REL" <<'EOF'
set -euo pipefail
DIR=$1 DB=$2 REL=$3
set -o pipefail
docker exec mysql sh -c "exec mysqldump -uroot -p\"\$MYSQL_ROOT_PASSWORD\" --single-transaction --routines --triggers --events --databases $DB" 2>/dev/null | gzip > "$DIR/backup/$REL.sql.gz"
gzip -t "$DIR/backup/$REL.sql.gz"
ls -la "$DIR/backup/$REL.sql.gz"
# 当前线上 jar 若还不在 releases/ 里，存一份供 rollback
cur=$(sha256sum "$DIR/jeecg-system-start-3.9.5.jar" | cut -d' ' -f1)
found=""
for f in "$DIR"/releases/*.jar; do [ "$(sha256sum "$f" | cut -d' ' -f1)" = "$cur" ] && found=$f && break; done
[ -n "$found" ] || cp -p "$DIR/jeecg-system-start-3.9.5.jar" "$DIR/releases/00000000000000-previous.jar"
EOF

remote "cp $DIR/releases/$REL.jar $LIVE && chown java:java $LIVE 2>/dev/null || true"

if [ -n "$PENDING" ]; then
  echo "== 带 Flyway 启动一次"
  remote "cd $COMPOSE && sed '/\"-Dspring.profiles.active=prod\"/a\\      - \"-Dspring.flyway.enabled=true\"' docker-compose.yml > docker-compose.yml.flyway && grep -c 'spring.flyway.enabled=true' docker-compose.yml.flyway && docker-compose -f docker-compose.yml.flyway config -q"
  up_api docker-compose.yml.flyway
  wait_ready || { echo "带 Flyway 启动失败。容器仍是 docker-compose.yml.flyway（开着 Flyway，重启会再跑迁移）。执行 $0 rollback 会用原 compose 换回旧 jar；数据库备份在 $DIR/backup/$REL.sql.gz" >&2; exit 1; }
  NEW=$(db_version)
  echo "== Flyway: $CUR -> $NEW"
  [ "$NEW" != "$CUR" ] || { echo "Flyway 版本没有前进，检查日志" >&2; exit 1; }
fi

echo "== 常规启动（Flyway 关闭）"
up_api docker-compose.yml
wait_ready

echo "== 清 Redis 缓存（部门路径、字典）"
remote "bash -s" <<EOF
docker exec -e REDISCLI_AUTH='$REDIS_PASSWORD' redis redis-cli ping | grep -q PONG || { echo 'Redis 认证失败，缓存未清' >&2; exit 1; }
for p in 'sys:cache:departPathName:*' 'sys:cache:dict*'; do
  docker exec -e REDISCLI_AUTH='$REDIS_PASSWORD' redis redis-cli --scan --pattern "\$p" | xargs -r docker exec -i -e REDISCLI_AUTH='$REDIS_PASSWORD' redis redis-cli del >/dev/null
done
echo cache cleared
EOF

curl -sk "$DOMAIN/portal/sys/randomImage/1" | grep -q '"success":true' || { echo "线上接口验证失败: $DOMAIN/portal/sys/randomImage/1" >&2; exit 1; }
echo "verify OK"

# releases/ 只保留最近 5 个 jar
remote "cd $DIR/releases && ls -1 *.jar | sort | head -n -5 | xargs -r rm -f"
echo "== 发布完成 ${REL}（数据库备份 $DIR/backup/$REL.sql.gz）"
