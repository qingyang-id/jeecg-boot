#!/usr/bin/env bash
# 前端发布：build -> 打包 -> 上传 -> 解压成新版本目录 -> 切换 current 软链接 -> 线上验证（失败自动切回）
#
#   jeecg-docker/deploy-web.sh               发布当前 HEAD（jeecgboot-vue3 不能有未提交改动）
#   jeecg-docker/deploy-web.sh list          列出服务器上的版本，* 为当前版本
#   jeecg-docker/deploy-web.sh rollback      切回上一个版本
#   jeecg-docker/deploy-web.sh rollback <版本> 切到指定版本
#
# 服务器目录（nginx 挂载为 /var/www/site）：
#   /home/www/web/erp-site/current   -> releases/<时间戳>-<提交>
#   /home/www/web/erp-site/releases/ 每次发布一个目录，保留最近 KEEP 个，更早的挪进 trash/
#   /home/www/web/erp-site/shared/   累积所有版本的 assets/ 和 js/ 哈希文件，nginx 在 current 找不到时回落到这里
#   /home/www/web/erp-site/acme/     acme.sh webroot（.well-known）
# 依赖 jeecg-docker/.env 里的 SSH_HOST，root 免密登录。
set -euo pipefail

ROOT=$(cd "$(dirname "$0")/.." && pwd)
WEB="$ROOT/jeecgboot-vue3"
SITE=/home/www/web/erp-site
DOMAIN=${DOMAIN:-https://erp.jijianxia.com}
KEEP=${KEEP:-5}

set -a
# shellcheck disable=SC1091
. "$ROOT/jeecg-docker/.env"
set +a
: "${SSH_HOST:?jeecg-docker/.env 缺少 SSH_HOST}"
REMOTE="root@$SSH_HOST"

# 在服务器上执行一段 shell 命令；REMOTE_SH 可替换执行方式（本地测试用，如 REMOTE_SH='docker exec -i web-test'）
remote() {
  if [ -n "${REMOTE_SH:-}" ]; then $REMOTE_SH bash -c "$1"; else ssh -o BatchMode=yes "$REMOTE" "$1"; fi
}

# 切换 current 到指定版本（原子替换软链接），打印切换前的版本
switch_to() {
  remote "bash -s -- $SITE $1" <<'EOF'
set -euo pipefail
SITE=$1 REL=$2
test -f "$SITE/releases/$REL/index.html" || { echo "版本不存在: $REL" >&2; exit 1; }
readlink "$SITE/current" 2>/dev/null | sed 's#^releases/##' || true
ln -sfn "releases/$REL" "$SITE/current.tmp"
mv -Tf "$SITE/current.tmp" "$SITE/current"
EOF
}

# 线上验证：首页、API 地址、入口页引用的第一个 js 都能取到
verify() {
  local code js
  code=$(curl -sk -o /dev/null -w '%{http_code}' "$DOMAIN/")
  [ "$code" = 200 ] || { echo "首页 HTTP $code" >&2; return 1; }
  curl -sk "$DOMAIN/_app.config.js" | grep -q 'erp.jijianxia.com/portal' || { echo "_app.config.js 不含 API 地址" >&2; return 1; }
  js=$(curl -sk "$DOMAIN/index.html" | grep -o '/js/[^"]*\.js' | head -1)
  [ -n "$js" ] || { echo "index.html 没有引用 /js/" >&2; return 1; }
  code=$(curl -sk -o /dev/null -w '%{http_code}' "$DOMAIN$js")
  [ "$code" = 200 ] || { echo "$js HTTP $code" >&2; return 1; }
  code=$(curl -sk -o /dev/null -w '%{http_code}' "$DOMAIN/business/order/list")
  [ "$code" = 200 ] || { echo "history 路由 HTTP $code" >&2; return 1; }
  echo "verify OK ($js)"
}

case "${1:-deploy}" in
  list)
    remote "cd $SITE && cur=\$(readlink current | sed 's#^releases/##'); for r in \$(cd releases && ls -d */ | sed 's#/\$##'); do [ \"\$r\" = \"\$cur\" ] && echo \"* \$r\" || echo \"  \$r\"; done"
    exit 0
    ;;
  rollback)
    target=${2:-$(remote "cd $SITE/releases && cur=\$(readlink ../current | sed 's#^releases/##'); ls -d */ | sed 's#/\$##' | grep -B1 -x \"\$cur\" | grep -vx \"\$cur\" || true")}
    [ -n "$target" ] || { echo "没有更早的版本" >&2; exit 1; }
    prev=$(switch_to "$target")
    echo "current: $prev -> $target"
    verify
    exit 0
    ;;
  deploy) ;;
  *) echo "用法: $0 [deploy|list|rollback [版本]]" >&2; exit 2 ;;
esac

cd "$ROOT"
if [ -n "$(git status --porcelain -- jeecgboot-vue3)" ]; then
  echo "jeecgboot-vue3 有未提交改动，先提交或清理" >&2
  exit 1
fi
REL="$(date +%Y%m%d%H%M%S)-$(git rev-parse --short HEAD)"
echo "== 版本 $REL"

[ -n "${SKIP_BUILD:-}" ] || (cd "$WEB" && pnpm install --frozen-lockfile && pnpm build)
grep -q 'erp.jijianxia.com/portal' "$WEB/dist/_app.config.js" || { echo "dist/_app.config.js 不含生产 API 地址" >&2; exit 1; }

PKG="$(mktemp -d)/erp-web-$REL.tgz"
# COPYFILE_DISABLE / --no-xattrs：macOS tar 不带 ._* 元数据文件
COPYFILE_DISABLE=1 tar --no-xattrs -czf "$PKG" -C "$WEB/dist" .
SUM=$(shasum -a 256 "$PKG" | cut -d' ' -f1)

remote "cat > /tmp/erp-web-$REL.tgz" < "$PKG"
remote "bash -s -- $SITE $REL $SUM" <<'EOF'
set -euo pipefail
SITE=$1 REL=$2 SUM=$3
PKG=/tmp/erp-web-$REL.tgz
echo "$SUM  $PKG" | sha256sum -c --quiet -
mkdir -p "$SITE/releases/$REL" "$SITE/shared"
tar -xzf "$PKG" -C "$SITE/releases/$REL"
mv "$PKG" "$SITE/releases/$REL.tgz"
# 哈希文件只增不改，-n 不覆盖已有文件
cp -an "$SITE/releases/$REL/assets" "$SITE/releases/$REL/js" "$SITE/shared/"
chown -R www:web "$SITE/releases/$REL" "$SITE/shared"
EOF

prev=$(switch_to "$REL")
echo "current: ${prev:-<none>} -> $REL"
if ! verify; then
  if [ -n "$prev" ]; then
    switch_to "$prev" >/dev/null
    echo "验证失败，已切回 $prev" >&2
  fi
  exit 1
fi

# 只保留最近 KEEP 个版本，更早的挪进 trash/（需要时手动清空）
remote "bash -s -- $SITE $KEEP" <<'EOF'
set -euo pipefail
SITE=$1 KEEP=$2
cd "$SITE/releases"
mkdir -p "$SITE/trash"
ls -d */ | sed 's#/$##' | head -n -"$KEEP" | while read -r r; do
  mv "$r" "$SITE/trash/"
  [ -f "$r.tgz" ] && mv "$r.tgz" "$SITE/trash/"
  echo "moved to trash: $r"
done
EOF
echo "== 发布完成 $REL"
