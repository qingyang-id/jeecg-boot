#!/usr/bin/env bash
# Usage: restore_prod_dump.sh <dump.sql.gz>
# Restores a prod dump into local container erp-verify-mysql as jeecg_old, and creates empty jeecg_v3.
set -euo pipefail
DUMP="$1"
M="docker exec -i erp-verify-mysql mysql -uroot -proot"
$M -e "DROP DATABASE IF EXISTS jeecg_old; CREATE DATABASE jeecg_old DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci; DROP DATABASE IF EXISTS jeecg_v3; CREATE DATABASE jeecg_v3 DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;"
gzip -dc "$DUMP" \
  | sed -e '/^CREATE DATABASE /d' -e 's/^USE `jeecg-boot`;/USE `jeecg_old`;/' \
  | $M jeecg_old
$M -N -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='jeecg_old'"
