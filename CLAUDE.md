# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

A customized ERP built on JeecgBoot **3.9.5** (low-code platform: Spring Boot 4 + MyBatis-Plus + Shiro/JWT, Vue 3 + Ant Design Vue 4). Active sub-projects:

- `jeecg-boot-v3/` — JDK 17 Maven multi-module backend (upstream `jeecg-boot/` of tag v3.9.5 + ERP module).
- `erp-web-v3/` — Vue 3 / Vite web frontend (upstream `jeecgboot-vue3`, package name `erp-admin`, pnpm). Web only; Electron is out of scope for now.

Legacy (旧版，待切换完成后删除 — do not modify): `jeecg-boot/` (JeecgBoot 3.0, Java 8) and `ant-design-vue-jeecg/` (Vue 2 + Electron). The root `package.json` only holds stray Electron deps of the legacy app.

## Commands

Backend (run in `jeecg-boot-v3/`, JDK 17 required):

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
mvn clean install -DskipTests                 # build all modules (dev profile)
mvn clean package -P prod -DskipTests         # prod build -> jeecg-module-system/jeecg-system-start/target/jeecg-system-start-3.9.5.jar
mvn -pl jeecg-boot-module/jeecg-module-erp test -Dtest=OrderSummaryServiceTest   # single ERP test
```

Entry point: `jeecg-module-system/jeecg-system-start/src/main/java/org/jeecg/JeecgSystemApplication.java`.

Local verify env: `jeecg-boot-v3/docker/local-verify/` — `docker-compose.yml` (MySQL 5.7.37 on 13306 / Redis 5.0 on 16379, current prod), `docker-compose.v84.yml` (MySQL 8.4.11 on 23306 / Redis 7.4.11 on 26379, upgrade target), `restore_prod_dump.sh`.

Frontend (run in `erp-web-v3/`, pnpm lockfile present):

```bash
pnpm install
pnpm dev                        # Vite dev server; proxy settings in .env.development
pnpm build                      # web build -> dist
```

## Architecture

### Backend modules (`jeecg-boot-v3/pom.xml`)

- `jeecg-boot-base-core/`: shared framework — auth, base entities, utilities, MyBatis-Plus tenant config (`MybatisPlusSaasConfig.TENANT_TABLE` includes the `jsh_*` tables).
- `jeecg-module-system/` (`jeecg-system-api`, `jeecg-system-biz`, `jeecg-system-start`): stock system modules; `jeecg-system-start` is the runnable app and depends on the ERP module.
- `jeecg-boot-module/jeecg-module-erp/`: **custom ERP code**, package `org.jeecg.modules.business` (`customer`, `order`, `product`, `sequence`, `version`, `job/StatisticOrderJob`, `util`), controller / entity / mapper / service (+ `vo`) layering. REST paths stay `/business/**` (same as the legacy app).
- `jeecg-boot-module/{jeecg-module-demo,jeecg-boot-module-airag}`: upstream modules; `jeecg-server-cloud/` is a microservice variant not built by default.

Profiles: `application.yml` + `application-{dev,test,prod}.yml` in `jeecg-module-system/jeecg-system-start/src/main/resources`. Prod uses `server.servlet.context-path: /portal`, DB `jeecg-boot-v3` on host `mysql`, Redis (`spring.data.redis.*`) on host `redis`, `uploadType: alioss`, `spring.flyway.enabled: false`.

SQL:
- `jeecg-boot-v3/db/jeecgboot-mysql-5.7.sql`: upstream base schema. Its first two lines `CREATE`/`USE` the database `` `jeecg-boot` `` and it contains `DROP TABLE` statements — rewrite the DB name before running it anywhere near prod.
- Flyway scripts: `jeecg-module-system/jeecg-system-start/src/main/resources/flyway/sql/mysql/`; ERP tables, dicts, menus, roles and quartz job are created by `V3.9.5_100__erp_init.sql`. Flyway is run by `org.jeecg.config.flyway.FlywayConfig`; a failure is only logged and does not stop startup, so check `flyway_schema_history`.
- `jeecg-boot-v3/db/erp/`: one-off 3.0 → 3.9.5 data migration (`migrate_from_3.0.sql`, `verify_migration.sql`, `README.md`). Written for local DBs `jeecg_old` → `jeecg_v3`; prod is `` `jeecg-boot` `` → `` `jeecg-boot-v3` ``.
- All SQL must run on both MySQL 5.7.37 and 8.4 until the 8.4 upgrade (`jeecg-docker/UPGRADE-MYSQL84-REDIS74.md`) is done: no `utf8mb4_0900_*`, window functions, CTE, `JSON_TABLE`, and no column/alias that is an 8.x reserved word (check `information_schema.keywords` on 8.4) — MyBatis-Plus does not quote column names.

### Frontend (`erp-web-v3/src/`)

Upstream Vben-based layout. ERP pages are in `views/business/{customer,product,order}` (`*.api.ts` / `*.data.ts` / `*List.vue` / `*Modal.vue`); menus are delivered by the backend (`sys_permission`), with `component` paths like `business/order/JshOrderList`. API base: `VITE_GLOB_API_URL=/portal` and `VITE_GLOB_DOMAIN_URL` from `.env.production`, emitted into `dist/_app.config.js` at build time.

## Deployment

Production is NOT deployed with the Dockerfiles / other `docker-compose*.yml` in this repo (upstream leftovers). The real setup is `jeecg-docker/` (mirrored on the server at `/home/base/docker/`; server host is `SSH_HOST` in `jeecg-docker/.env`, which is gitignored along with TLS certs — copy `.env.example`). Secrets come from `.env` (`MYSQL_ROOT_PASSWORD`, `ERP_DB_PASSWORD`, `REDIS_PASSWORD`); the server's own compose still has them inline (and its Redis password lives in `redis/conf/redis.conf`), so never copy the repo compose over it — edit the server file in place. The server uses standalone `docker-compose` (v2.2.3) and cannot pull from Docker Hub (load images via `docker save`/`docker load`). TLS for `erp.jijianxia.com` is issued by acme.sh on the server (`/root/.acme.sh`, Let's Encrypt, root cron auto-renews into `nginx/conf/cert/`, webroot `/home/www/web/erp`) and depends on the `/.well-known/acme-challenge/` location in the port-80 server block. Domain: `erp.jijianxia.com`.

The 3.0 → 3.9.5 cutover procedure (backup, new DB, Flyway, data migration, verification, rollback) is `jeecg-docker/UPGRADE-3.9.5.md`.

Server compose runs four containers: `mysql` (5.7.37; repo compose already targets 8.4.11 with data dir `mysql84/data`), `redis` (5.0; repo targets 7.4.11), `erp-api` (`eclipse-temurin:17-jre` after the cutover; `openjdk:8-jdk` before), `nginx`. No images are built; artifacts are copied onto host paths that are bind-mounted:

| Artifact | Build | Host path |
|---|---|---|
| Backend jar | `mvn clean package -P prod -DskipTests` in `jeecg-boot-v3/` | `/home/java/erp-api/jeecg-system-start-3.9.5.jar` (mounted as `/app/erp-api.jar`; restart `erp-api` after replacing) |
| Web frontend | `pnpm build` in `erp-web-v3/` (`dist`) | `/home/www/web/erp` (bind-mounted into nginx: replace the contents in place, keep `.well-known`; `mv` of the directory is not seen until nginx restarts) |

Wiring that must stay consistent:

- nginx proxies `/portal` (and `/portal/websocket`) to `erp-api:8080/portal`, matching `server.servlet.context-path: /portal` in `application-prod.yml`; `location /` falls back to `index.html` for history routes.
- `application-prod.yml` uses DB `jeecg-boot-v3` and hosts `mysql` / `redis` = container names; DB and Redis credentials are overridden by `-D` flags in the compose `entrypoint` (`spring.datasource.dynamic.datasource.master.{username,password}`, `spring.data.redis.{host,port,password}`). Add `-Dspring.flyway.enabled=true` only for a run that should apply new Flyway scripts.
- Frontend prod API URL comes from `erp-web-v3/.env.production` (`https://erp.jijianxia.com/portal`).
- MySQL init scripts (`mysql/init/`) only run on an empty data dir; schema changes go through Flyway (or are applied manually).
- The legacy 3.0 DB `jeecg-boot`, jar `jeecg-boot-module-system-3.0.jar` and `/home/www/web/electron` stay on the server until the cutover is confirmed; they are the rollback path.
