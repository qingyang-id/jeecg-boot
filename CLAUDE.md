# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

A customized ERP built on JeecgBoot 3.0 (low-code platform: Spring Boot 2.3.5 + MyBatis-Plus + Shiro/JWT, Ant Design Vue 1.x). Two independent sub-projects in one git repo:

- `jeecg-boot/` — Java 8 Maven multi-module backend.
- `ant-design-vue-jeecg/` — Vue 2 frontend (package name `erp-admin`), shipped both as a web app and as an Electron desktop app (`vue-cli-plugin-electron-builder`).

The root `package.json` only holds stray Electron deps; real frontend commands run inside `ant-design-vue-jeecg/`.

## Commands

Backend (run in `jeecg-boot/`):

```bash
mvn clean install -DskipTests                 # build all modules (dev profile is activeByDefault)
mvn clean package -P prod -DskipTests         # build with application-prod.yml (profile sets spring.profiles.active via @profile.name@)
mvn -pl jeecg-boot-module-system test -Dtest=OrderSummaryServiceTest   # single test
```

Entry point: `jeecg-boot-module-system/src/main/java/org/jeecg/JeecgSystemApplication.java`. Output jar: `jeecg-boot-module-system/target/jeecg-boot-module-system-3.0.jar`.

Frontend (run in `ant-design-vue-jeecg/`, yarn lockfile present):

```bash
yarn install
yarn serve                      # web dev server; proxies /portal -> http://localhost:8080
yarn build                      # web build -> dist/web
yarn lint
yarn electron:serve             # Electron dev (main entry src/main/index.dev.js)
yarn electron:build:mac         # Electron package -> dist/electron (main entry src/main/index.js)
yarn electron:build:win64       # also :win32, and :test:* variants using --mode test
```

## Architecture

### Backend modules (`jeecg-boot/pom.xml`)

- `jeecg-boot-base/` (`-api`, `-core`, `-tools`): shared framework — auth, base entities, utilities. Rarely changed.
- `jeecg-boot-module-system/`: the runnable app. Stock Jeecg modules live under `org.jeecg.modules.{system,quartz,message,monitor,oss,cas,...}`. **Custom ERP code lives in `org.jeecg.modules.business`** (`customer`, `order`, `product`, `sequence`, `version`), each following controller / entity / mapper / service (+ `vo`) layering.
- `jeecg-boot-module-demo/`: demo code.
- `jeecg-boot-starter/` and `jeecg-cloud-module/`: microservice variants, commented out of the parent `<modules>` and not built.

Profiles: `application.yml` + `application-{dev,test,prod}.yml` in `jeecg-boot-module-system/src/main/resources`. Prod uses `server.servlet.context-path: /portal`, MySQL host `mysql`, Redis host `redis`, `uploadType: alioss`.

SQL: `jeecg-boot/db/` holds the base schema (`jeecgboot-mysql-5.7.sql`), the ERP schema (`jsh_erp_new.sql`), quartz tables, and `增量SQL/` for incremental migrations.

### Frontend (`ant-design-vue-jeecg/src/`)

Split by Electron process:

- `src/main/` — Electron main process: window creation, tray, shortcuts, IPC, `electron-updater` auto-update (`services/appAutoUpdate.js`) and zip hot-update (`services/appHotUpdate.js`, downloads `https://<API host>/electron/app.zip`).
- `src/renderer/` — the Vue app (also the web build). Custom ERP pages are in `views/modules/business/{customer,order,product}`; API helpers in `api/`.

API base URL comes from `VUE_APP_API_BASE_URL` (in `.env.*` files), assigned to `window._CONFIG['domianURL']` in `src/renderer/config/index.js` and used as axios `baseURL` in `utils/request.js`. Electron `productName`/`appId` come from `VUE_APP_NANE`/`VUE_APP_ID`.

## Deployment

Production is NOT deployed with the Dockerfiles / `docker-compose*.yml` in this repo (those are upstream Jeecg leftovers: wrong `/jeecg-boot` proxy prefix, `jeecg-boot-mysql` hostnames, `dist/` path). The real setup is `jeecg-docker/` (mirrored on the server at `/home/base/docker/`; server host is `SSH_HOST` in `jeecg-docker/.env`, which is gitignored along with TLS certs — copy `.env.example`). Secrets come from `.env` (`MYSQL_ROOT_PASSWORD`, `ERP_DB_PASSWORD`, `REDIS_PASSWORD`); the server's own compose still has them inline, so diff before copying the repo compose over it. TLS for `erp.jijianxia.com` is issued by acme.sh on the server (`/root/.acme.sh`, Let's Encrypt, root cron auto-renews into `nginx/conf/cert/`) and depends on the `/.well-known/acme-challenge/` location in the port-80 server block. Domain: `erp.jijianxia.com`.

Server compose runs four containers: `mysql` (5.7.37), `redis` (5.0), `erp-api` (`openjdk:8-jdk`), `nginx`. No images are built; artifacts are copied onto host paths that are bind-mounted:

| Artifact | Build | Host path |
|---|---|---|
| Backend jar | `mvn clean package -P prod -DskipTests` | `/home/java/erp-api/jeecg-boot-module-system-3.0.jar` (mounted as `/app/erp-api.jar`; restart `erp-api` after replacing) |
| Web frontend | `yarn build` (`dist/web`) | `/home/www/web/erp` |
| Electron hot-update | `yarn electron:build:*` (`afterPack.js` zips resources) | `/home/www/web/electron/app.zip` |

Wiring that must stay consistent:

- nginx proxies `/portal` (and `/portal/websocket`) to `erp-api:8080/portal`, matching `server.servlet.context-path: /portal` in `application-prod.yml`.
- `application-prod.yml` uses hosts `mysql` / `redis` = container names; DB and Redis credentials are overridden by `-D` flags in the server compose `entrypoint`.
- Frontend prod API URL comes from `ant-design-vue-jeecg/.env.production.local` (`https://erp.jijianxia.com/portal`), which overrides `.env.production`. `appHotUpdate.js` derives `https://<host>/electron/app.zip` from it, served by nginx `location /electron`.
- MySQL init scripts (`mysql/init/`) only run on an empty data dir. `jsh_erp_new.sql` there has no `USE` statement, and `jeecg-boot/db/增量SQL/` must be applied manually for schema changes.
