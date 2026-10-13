#!/usr/bin/env python3
"""Old (JeecgBoot 3.0) vs new (3.9.5) ERP API parity check.

Read-only part (always): logs in to both backends with every --user, calls every
GET endpoint of the ERP controllers (list scanned from source) and compares the
normalised results. Optional parts: --db-cmd (row-level tenant check against the
old DB), --write-chain (create/edit/export/delete on the new backend), --stats
(trigger the order statistic job on both and compare summary rows).

Only counts, endpoint paths, field names and ids are printed, never row content.
Stdlib only; run with `python3 -I`.
"""
import argparse
import datetime as dt
import io
import json
import re
import shlex
import struct
import subprocess
import sys
import time
import urllib.error
import urllib.parse
import urllib.request
import zipfile
from pathlib import Path

DEFAULT_SRC = Path(__file__).resolve().parent.parent / "jeecg-boot-module/jeecg-module-erp/src/main/java"

FAILURES = []


def ok(msg):
    print(f"OK    {msg}")


def fail(msg):
    FAILURES.append(msg)
    print(f"FAIL  {msg}")


def info(msg):
    print(f"INFO  {msg}")


# ---------------------------------------------------------------- HTTP

class Api:
    def __init__(self, base, tenant_header):
        self.base = base.rstrip("/")
        self.tenant_header = tenant_header
        self.token = None
        self.tenant = None

    def _req(self, method, path, params=None, body=None, raw=False):
        url = self.base + path
        if params:
            url += "?" + urllib.parse.urlencode(params)
        data = None
        headers = {}
        if body is not None:
            data = json.dumps(body).encode()
            headers["Content-Type"] = "application/json"
        if self.token:
            headers["X-Access-Token"] = self.token
        if self.tenant is not None:
            headers[self.tenant_header] = str(self.tenant)
        req = urllib.request.Request(url, data=data, method=method, headers=headers)
        try:
            with urllib.request.urlopen(req, timeout=300) as resp:
                content = resp.read()
                ctype = resp.headers.get("Content-Type", "")
                disp = resp.headers.get("Content-Disposition", "")
        except urllib.error.HTTPError as e:
            content, ctype, disp = e.read(), e.headers.get("Content-Type", ""), ""
            if raw:
                return e.code, ctype, disp, content
            try:
                return json.loads(content)
            except ValueError:
                return {"success": False, "code": e.code, "message": f"HTTP {e.code}"}
        if raw:
            return 200, ctype, disp, content
        return json.loads(content)

    def get(self, path, params=None):
        return self._req("GET", path, params)

    def login(self, username, password, tenant):
        self.token = None
        self.tenant = None
        r = self._req("POST", "/sys/mLogin", body={"username": username, "password": password})
        if not r.get("success"):
            raise SystemExit(f"login failed on {self.base}: {r.get('message')}")
        self.token = r["result"]["token"]
        self.tenant = tenant
        return r


# ---------------------------------------------------------------- endpoint scan

def scan_get_endpoints(src_root):
    """Every @GetMapping under the ERP controllers, as full paths."""
    found = []
    for f in sorted(Path(src_root).rglob("*Controller.java")):
        text = f.read_text(encoding="utf-8")
        m = re.search(r'@RequestMapping\(\s*"([^"]+)"\s*\)\s*(?:@\w+(?:\([^)]*\))?\s*)*public class', text)
        if not m:
            continue
        base = m.group(1)
        for gm in re.finditer(r'@GetMapping\(\s*(?:value\s*=\s*)?"([^"]+)"', text):
            found.append(base + gm.group(1))
    return found


# ---------------------------------------------------------------- normalisation

ISO_RE = re.compile(r"^(\d{4}-\d{2}-\d{2})T(\d{2}:\d{2}:\d{2})(?:\.\d+)?(?:Z|[+-]\d{2}:?\d{2})?$")
PAGE_KEYS = ("total", "size", "current", "pages")
# Summary rows are rewritten by StatisticOrderJob every 10 minutes; updateTime is the time the
# job last ran on that backend, not business data.
IGNORE_FIELDS = {
    "/business/order/jshOrderSummaryDaily/list": {"updateTime"},
    "/business/order/jshOrderSummaryMonthly/list": {"updateTime"},
}


def is_id_key(k):
    return k == "id" or k.endswith("Id")


def norm(v):
    """Old: global Long->String + NON_NULL. New: only id/*Id as string, nulls kept.
    So: drop nulls, compare all integers as decimal strings, unify date formats."""
    if isinstance(v, dict):
        return {k: norm(x) for k, x in v.items() if x is not None}
    if isinstance(v, list):
        return [norm(x) for x in v]
    if isinstance(v, bool):
        return v
    if isinstance(v, int):
        return str(v)
    if isinstance(v, str):
        m = ISO_RE.match(v)
        return f"{m.group(1)} {m.group(2)}" if m else v
    return v


def non_string_ids(v, path="$"):
    """Paths in the new response where an id/*Id value is not a JSON string."""
    bad = []
    if isinstance(v, dict):
        for k, x in v.items():
            if is_id_key(k) and x is not None and not isinstance(x, str) and not isinstance(x, (dict, list)):
                bad.append(f"{path}.{k}")
            else:
                bad.extend(non_string_ids(x, f"{path}.{k}"))
    elif isinstance(v, list):
        for x in v:
            bad.extend(non_string_ids(x, f"{path}[*]"))
    return sorted(set(bad))


def diff_records(old_recs, new_recs):
    """Return list of human-readable diffs (ids + field names only)."""
    diffs = []
    if len(old_recs) != len(new_recs):
        diffs.append(f"record count old={len(old_recs)} new={len(new_recs)}")
    old_ids = [r.get("id") for r in old_recs if isinstance(r, dict)]
    new_ids = [r.get("id") for r in new_recs if isinstance(r, dict)]
    if old_ids != new_ids:
        if sorted(map(str, old_ids)) == sorted(map(str, new_ids)):
            diffs.append("same ids, different order")
        else:
            only_old = sorted(set(map(str, old_ids)) - set(map(str, new_ids)))[:5]
            only_new = sorted(set(map(str, new_ids)) - set(map(str, old_ids)))[:5]
            diffs.append(f"id sets differ only_old={only_old} only_new={only_new}")
        return diffs
    for o, n in zip(old_recs, new_recs):
        if o == n:
            continue
        if not (isinstance(o, dict) and isinstance(n, dict)):
            diffs.append("value differs")
            continue
        fields = sorted(k for k in set(o) | set(n) if o.get(k) != n.get(k))
        diffs.append(f"id={o.get('id')} fields={fields}")
    return diffs


def drop_fields(v, fields):
    if not fields:
        return v
    if isinstance(v, dict):
        return {k: drop_fields(x, fields) for k, x in v.items() if k not in fields}
    if isinstance(v, list):
        return [drop_fields(x, fields) for x in v]
    return v


def compare_result(label, old_r, new_r, ignore=None):
    if old_r.get("success") != new_r.get("success"):
        fail(f"{label}: success old={old_r.get('success')} new={new_r.get('success')} "
             f"(new message: {str(new_r.get('message'))[:80]})")
        return None
    if not new_r.get("success"):
        if old_r.get("message") != new_r.get("message"):
            fail(f"{label}: error message differs")
        else:
            ok(f"{label}: both error '{new_r.get('message')}'")
        return None
    bad = non_string_ids(new_r.get("result"))
    if bad:
        fail(f"{label}: new backend id fields not string: {bad[:6]}")
    o = drop_fields(norm(old_r.get("result")), ignore)
    n = drop_fields(norm(new_r.get("result")), ignore)
    if isinstance(o, dict) and "records" in o:
        diffs = [f"{k}: old={o.get(k)} new={n.get(k)}" for k in PAGE_KEYS if o.get(k) != n.get(k)]
        diffs += diff_records(o.get("records") or [], n.get("records") or [])
        summary = f"total={n.get('total')} records={len(n.get('records') or [])}"
    elif isinstance(o, list):
        diffs = diff_records(o, n if isinstance(n, list) else [])
        summary = f"items={len(n) if isinstance(n, list) else '?'}"
    elif isinstance(o, dict):
        diffs = diff_records([o], [n] if isinstance(n, dict) else [])
        summary = "object"
    else:
        diffs = [] if o == n else ["scalar value differs"]
        summary = "scalar"
    if diffs:
        fail(f"{label}: {'; '.join(diffs[:8])}")
    elif not bad:
        ok(f"{label}: {summary}")
    return new_r.get("result")


# ---------------------------------------------------------------- DB helper

class Db:
    def __init__(self, cmd):
        self.cmd = shlex.split(cmd)

    def rows(self, sql):
        p = subprocess.run(self.cmd + ["-N", "-B", "-e", sql], capture_output=True, text=True)
        if p.returncode != 0:
            raise SystemExit("db query failed: " + p.stderr.strip().splitlines()[-1])
        return [line.split("\t") for line in p.stdout.splitlines() if line != ""]

    def scalar(self, sql):
        r = self.rows(sql)
        return r[0][0] if r else None


# ---------------------------------------------------------------- read-only parity

# endpoint -> list of (param-set name, function(ctx) -> params). ctx holds sample ids.
def build_cases(day_from, day_to, month):
    C = "/business/customer/jshCustomer"
    O = "/business/order"
    return {
        f"{C}/list": [("default", lambda c: {}),
                      ("page2", lambda c: {"pageNo": 2}),
                      ("orderForm", lambda c: {"status": 1, "pageSize": 5000, "column": "rank", "order": "asc"})],
        f"{C}/queryById": [("sample", lambda c: {"id": c["customer"]})],
        f"{C}/listJshCustomerAddressByMainId": [("byCustomer", lambda c: {"customerId": c["customer"], "pageSize": 100})],
        "/business/product/jshProduct/list": [("default", lambda c: {}),
                                              ("orderForm", lambda c: {"pageSize": 5000})],
        "/business/product/jshProduct/queryById": [("sample", lambda c: {"id": c["product"]})],
        f"{O}/jshOrder/list": [("default", lambda c: {}),
                               ("byCustomer", lambda c: {"customerId": c["customer"]}),
                               ("dateRange", lambda c: {"orderTime_begin": f"{day_from} 00:00:00",
                                                        "orderTime_end": f"{day_to} 23:59:59", "pageSize": 50}),
                               ("page2", lambda c: {"pageNo": 2})],
        f"{O}/jshOrder/queryById": [("sample", lambda c: {"id": c["order"]})],
        f"{O}/jshOrder/queryJshOrderProductByMainId": [("sample", lambda c: {"id": c["order"]})],
        f"{O}/jshOrderProduct/list": [("default", lambda c: {}),
                                      ("byOrderIds", lambda c: {"orderIds": c["order"]})],
        f"{O}/jshOrderProduct/queryById": [("sample", lambda c: {"id": c["orderProduct"]})],
        f"{O}/jshOrderProduct/queryJshOrderProductDetailByOrderId": [("sample", lambda c: {"id": c["order"]})],
        f"{O}/jshOrderProduct/queryJshOrderProductExtendByOrderId": [("sample", lambda c: {"id": c["orderExt"]})],
        f"{O}/jshOrderProductDetail/list": [("default", lambda c: {}),
                                            ("byOrderIds", lambda c: {"orderIds": c["order"], "pageSize": 100})],
        f"{O}/jshOrderProductDetail/queryById": [("sample", lambda c: {"id": c["detail"]})],
        f"{O}/jshOrderProductExtend/list": [("default", lambda c: {})],
        f"{O}/jshOrderProductExtend/queryById": [("sample", lambda c: {"id": c["extend"]})],
        f"{O}/jshOrderSummaryDaily/list": [("default", lambda c: {}),
                                           ("byCustomerRange", lambda c: {"customerId": c["customer"],
                                                                          "time_begin": day_from, "time_end": day_to})],
        f"{O}/jshOrderSummaryMonthly/list": [("default", lambda c: {}),
                                             ("byMonth", lambda c: {"time": month})],
        "/business/version/new": [("default", lambda c: {}), ("win", lambda c: {"platform": "win", "versionNo": 1})],
        "/business/version/list": [("default", lambda c: {})],
        "/business/version/queryById": [("sample", lambda c: {"id": c["version"]})],
        # mutates jsh_sequence (+1 on each side): only with --include-sequence
        "/business/sequence/num": [("orderSeq", lambda c: {"seqName": "order_number_seq"})],
    }


def first_id(api, path, params=None, key="id"):
    r = api.get(path, params or {})
    recs = ((r.get("result") or {}).get("records") or [])
    return str(recs[0][key]) if recs else None


def sample_context(api):
    """Sample ids taken from the new backend while logged in as a tenant user."""
    O = "/business/order"
    c = {"customer": first_id(api, "/business/customer/jshCustomer/list"),
         "product": first_id(api, "/business/product/jshProduct/list"),
         "order": first_id(api, f"{O}/jshOrder/list"),
         "orderProduct": first_id(api, f"{O}/jshOrderProduct/list"),
         "detail": first_id(api, f"{O}/jshOrderProductDetail/list"),
         "extend": first_id(api, f"{O}/jshOrderProductExtend/list"),
         "orderExt": first_id(api, f"{O}/jshOrderProductExtend/list", key="orderId"),
         "version": first_id(api, "/business/version/list")}
    return c


TABLE_OF = {
    "/business/customer/jshCustomer/list": ("jsh_customer", ""),
    "/business/product/jshProduct/list": ("jsh_product", ""),
    "/business/order/jshOrder/list": ("jsh_order", ""),
    "/business/order/jshOrderProduct/list": ("jsh_order_product", ""),
    "/business/order/jshOrderProductDetail/list": ("jsh_order_product_detail", ""),
    "/business/order/jshOrderProductExtend/list": ("jsh_order_product_extend", ""),
    "/business/order/jshOrderSummaryDaily/list": ("jsh_order_summary_daily", " AND customer_id<>0"),
    "/business/order/jshOrderSummaryMonthly/list": ("jsh_order_summary_monthly", " AND customer_id<>0"),
}


def tenant_db_checks(label, old, new, db, old_db, tenant):
    """Unfiltered list totals and the full order id set == old DB rows for this tenant."""
    t = int(tenant or 0)
    for path, (table, extra) in TABLE_OF.items():
        expect = int(db.scalar(f"SELECT COUNT(*) FROM {old_db}.{table} WHERE tenant_id={t} AND del_flag=0{extra}"))
        got_new = new.get(path, {"pageSize": 1})["result"]["total"]
        got_old = old.get(path, {"pageSize": 1})["result"]["total"]
        if int(got_new) == expect == int(got_old):
            ok(f"{label} tenant={t} {table}: api total == db rows ({expect})")
        else:
            fail(f"{label} tenant={t} {table}: db={expect} old_api={got_old} new_api={got_new}")
    expect_ids = {r[0] for r in db.rows(f"SELECT id FROM {old_db}.jsh_order WHERE tenant_id={t} AND del_flag=0")}
    for name, api in (("old", old), ("new", new)):
        r = api.get("/business/order/jshOrder/list", {"pageSize": max(len(expect_ids), 1) + 10})
        got = {str(x["id"]) for x in (r["result"]["records"] or [])}
        if got == expect_ids:
            ok(f"{label} tenant={t} {name} order id set == db ({len(got)})")
        else:
            fail(f"{label} tenant={t} {name} order id set differs: api={len(got)} db={len(expect_ids)} "
                 f"only_api={sorted(got - expect_ids)[:5]} only_db={sorted(expect_ids - got)[:5]}")


def read_parity(args, users, endpoints):
    today = dt.date.today()
    day_from = args.date_from or (today - dt.timedelta(days=40)).isoformat()
    day_to = args.date_to or (today - dt.timedelta(days=10)).isoformat()
    month = (args.month or day_to[:7]) + "-01"
    cases = build_cases(day_from, day_to, month)

    # coverage: every scanned GET endpoint must have cases
    missing = [e for e in endpoints if e not in cases]
    extra = [e for e in cases if e not in endpoints]
    if missing:
        fail(f"GET endpoints without parity cases: {missing}")
    if extra:
        fail(f"parity cases for endpoints not in source: {extra}")
    if not missing and not extra:
        ok(f"endpoint coverage: {len(endpoints)} GET endpoints scanned, all covered")

    old, new = Api(args.old, "tenant-id"), Api(args.new, "X-Tenant-Id")
    db = Db(args.db_cmd) if args.db_cmd else None

    # sample ids from the first tenant user
    ctx = None
    for i, (u, tenant) in enumerate(users):
        if tenant:
            new.login(u, args.password, tenant)
            ctx = sample_context(new)
            break
    if ctx is None or None in ctx.values():
        fail(f"could not collect sample ids from a tenant user: {sorted(k for k, v in (ctx or {}).items() if v is None)}")
        return

    for i, (u, tenant) in enumerate(users, 1):
        label = f"user#{i}(tenant={tenant or '-'})"
        old.login(u, args.password, tenant)
        new.login(u, args.password, tenant)
        for path in endpoints:
            if path == "/business/sequence/num" and not args.include_sequence:
                info(f"{label} GET {path}: skipped (mutates jsh_sequence; use --include-sequence)")
                continue
            for name, fn in cases.get(path, []):
                params = fn(ctx)
                compare_result(f"{label} GET {path} [{name}]", old.get(path, params), new.get(path, params),
                               IGNORE_FIELDS.get(path))
        if db:
            tenant_db_checks(label, old, new, db, args.old_db, tenant)


# ---------------------------------------------------------------- Excel header extraction (stdlib)

def _cfb_stream(data, name):
    """Read a named stream from an OLE2 compound file (enough for POI .xls output)."""
    sec_shift, mini_shift = struct.unpack_from("<HH", data, 30)
    ssz, msz = 1 << sec_shift, 1 << mini_shift
    n_fat, dir_start, _, mini_cutoff, mini_fat_start, n_mini_fat, difat_start, n_difat = \
        struct.unpack_from("<IIIIIIII", data, 44)
    difat = list(struct.unpack_from("<109I", data, 76))
    s = difat_start
    for _ in range(n_difat):
        off = 512 + s * ssz
        vals = struct.unpack_from(f"<{ssz // 4}I", data, off)
        difat += vals[:-1]
        s = vals[-1]
    fat = []
    for sec in difat[:n_fat]:
        fat += struct.unpack_from(f"<{ssz // 4}I", data, 512 + sec * ssz)

    def chain(start):
        out, s = [], start
        while s < 0xFFFFFFFA:
            out.append(s)
            s = fat[s]
        return out

    def read(start, size=None):
        b = b"".join(data[512 + s * ssz: 512 + (s + 1) * ssz] for s in chain(start))
        return b if size is None else b[:size]

    dirs = read(dir_start)
    entries = []
    for off in range(0, len(dirs), 128):
        nlen = struct.unpack_from("<H", dirs, off + 64)[0]
        ename = dirs[off: off + max(nlen - 2, 0)].decode("utf-16-le")
        start, size = struct.unpack_from("<II", dirs, off + 116)
        entries.append((ename, start, size))
    root = entries[0]
    for ename, start, size in entries:
        if ename != name:
            continue
        if size >= mini_cutoff:
            return read(start, size)
        mini_stream = read(root[1], root[2])
        mini_fat = []
        for s in chain(mini_fat_start) if n_mini_fat else []:
            mini_fat += struct.unpack_from(f"<{ssz // 4}I", data, 512 + s * ssz)
        out, s = b"", start
        while s < 0xFFFFFFFA:
            out += mini_stream[s * msz:(s + 1) * msz]
            s = mini_fat[s]
        return out[:size]
    raise ValueError(f"stream {name} not found")


def _biff_strings(wb):
    """Parse SST (with CONTINUE) and LABELSST cells of the first worksheet."""
    recs, pos = [], 0
    while pos + 4 <= len(wb):
        rid, ln = struct.unpack_from("<HH", wb, pos)
        recs.append((rid, wb[pos + 4: pos + 4 + ln]))
        pos += 4 + ln
    sst, cells, sheet_no = [], {}, 0
    i = 0
    while i < len(recs):
        rid, body = recs[i]
        if rid == 0x0809 and struct.unpack_from("<H", body, 2)[0] == 0x0010:  # BOF worksheet
            sheet_no += 1
        if rid == 0x00FC:  # SST
            chunks = [body]
            while i + 1 < len(recs) and recs[i + 1][0] == 0x003C:
                i += 1
                chunks.append(recs[i][1])
            sst = _parse_sst(chunks)
        if rid == 0x00FD and sheet_no == 1:  # LABELSST
            r, c, _, idx = struct.unpack_from("<HHHI", body, 0)
            cells[(r, c)] = sst[idx] if idx < len(sst) else ""
        i += 1
    return cells


def _parse_sst(chunks):
    total, uniq = struct.unpack_from("<II", chunks[0], 0)
    ci, pos, data = 0, 8, chunks[0]
    out = []

    def need(n):
        nonlocal ci, pos, data
        if pos + n > len(data):
            ci += 1
            data, pos = chunks[ci], 0

    for _ in range(uniq):
        need(3)
        cch, flags = struct.unpack_from("<HB", data, pos)
        pos += 3
        rich = struct.unpack_from("<H", data, pos)[0] if flags & 0x08 else 0
        pos += 2 if flags & 0x08 else 0
        ext = struct.unpack_from("<I", data, pos)[0] if flags & 0x04 else 0
        pos += 4 if flags & 0x04 else 0
        s, wide = "", flags & 0x01
        while len(s) < cch:
            if pos >= len(data):  # string continues in next CONTINUE, new flag byte
                ci += 1
                data, pos = chunks[ci], 0
                wide = data[pos] & 0x01
                pos += 1
            n = cch - len(s)
            if wide:
                n = min(n, (len(data) - pos) // 2)
                s += data[pos: pos + 2 * n].decode("utf-16-le")
                pos += 2 * n
            else:
                n = min(n, len(data) - pos)
                s += data[pos: pos + n].decode("latin-1")
                pos += n
        skip = rich * 4 + ext
        while skip:
            step = min(skip, len(data) - pos)
            pos += step
            skip -= step
            if skip:
                ci += 1
                data, pos = chunks[ci], 0
        out.append(s)
    return out


def excel_header(content, rows=4):
    """First `rows` rows of string cells from an .xls (OLE2) or .xlsx (zip) export."""
    if content[:4] == b"\xd0\xcf\x11\xe0":
        cells = _biff_strings(_cfb_stream(content, "Workbook"))
        kind = "xls"
    elif content[:2] == b"PK":
        z = zipfile.ZipFile(io.BytesIO(content))
        ss = re.findall(r"<t[^>]*>([^<]*)</t>", z.read("xl/sharedStrings.xml").decode()) \
            if "xl/sharedStrings.xml" in z.namelist() else []
        sheet = z.read("xl/worksheets/sheet1.xml").decode()
        cells = {}
        for ref, typ, val in re.findall(r'<c r="([A-Z]+\d+)"[^>]*?(?: t="(\w+)")?[^>]*>\s*<v>([^<]*)</v>', sheet):
            col = sum((ord(ch) - 64) * 26 ** i for i, ch in enumerate(reversed(re.sub(r"\d", "", ref)))) - 1
            row = int(re.sub(r"[A-Z]", "", ref)) - 1
            if typ == "s":
                cells[(row, col)] = ss[int(val)]
        kind = "xlsx"
    else:
        return None, None
    return kind, [[cells[k] for k in sorted(k for k in cells if k[0] == r)] for r in range(rows)]


# ---------------------------------------------------------------- write chain (new backend)

def write_chain(args, users, db):
    tenant_users = [(u, t) for u, t in users if t]
    if not tenant_users:
        fail("write chain needs a tenant user")
        return
    u, tenant = tenant_users[0]
    new, old = Api(args.new, "X-Tenant-Id"), Api(args.old, "tenant-id")
    new.login(u, args.password, tenant)
    old.login(u, args.password, tenant)
    ndb = args.new_db
    tag = "PARITY-" + time.strftime("%Y%m%d%H%M%S")
    created = {"customer": None, "order": None}
    try:
        # 1. customer
        r = new._req("POST", "/business/customer/jshCustomer/add",
                     body={"name": tag, "sex": 1, "phone": "13800000000", "address": tag, "status": 1, "rank": 999})
        rec = (new.get("/business/customer/jshCustomer/list", {"name": tag})["result"]["records"] or [{}])[0]
        cid = rec.get("id")
        created["customer"] = cid
        row = db.rows(f"SELECT tenant_id, del_flag FROM {ndb}.jsh_customer WHERE id={int(cid)}") if cid else []
        if r.get("success") and isinstance(cid, str) and row == [[str(tenant), "0"]]:
            ok(f"write: add customer -> id is string, db row tenant={tenant} del_flag=0")
        else:
            fail(f"write: add customer success={r.get('success')} id_type={type(cid).__name__} db={row}")
            return

        # 2. order with products, details, extends (same shape as old JshOrderForm.classifyIntoFormData)
        prod = new.get("/business/product/jshProduct/list", {"pageSize": 1})["result"]["records"][0]
        width, height, num = 600, 1500, 2
        diff = {k: int(prod.get(k) or 0) for k in
                ("aluminumWidthDiff", "aluminumHeightDiff", "glassWidthDiff", "glassHeightDiff")}
        ref_h = -(-height // 3) - 2  # extendType 1 (抽): ceil(h/3)-2, num*3
        detail_base = {"referenceWidth": width, "referenceHeight": ref_h, "num": num * 3, "color": "white",
                       "direction": "1", "extendType": "1", "extendNum": num * 3}

        def order_body(order_id=None, remark="parity add", n=num, code=None):
            item = {"id": "", "productId": prod["id"], "color": "white", "glassColor": "clear",
                    "width": width, "height": height, "num": n, "direction": "1",
                    "extendType": "1", "extendNum": n * 3,
                    "price": "10000", "extendPrice": "500", "totalArea": str(width * height * n),
                    "totalPrice": str(10000 * n + 500 * n * 3),
                    "jshOrderProductExtendList": [{"type": "1", "num": n * 3}],
                    "jshOrderProductDetailList": [
                        dict(detail_base, type=1, width=width + diff["aluminumWidthDiff"],
                             height=ref_h + diff["aluminumHeightDiff"], color="white"),
                        dict(detail_base, type=2, width=width + diff["glassWidthDiff"],
                             height=ref_h + diff["glassHeightDiff"], color="clear")]}
            body = {"orderCode": code, "customerId": cid, "orderTime": time.strftime("%Y-%m-%d %H:%M:%S"),
                    "address": tag, "remark": remark, "totalArea": str(width * height * n),
                    "totalPrice": item["totalPrice"], "jshOrderProductPageList": [item]}
            if order_id:
                body["id"] = order_id
            return body

        r = new._req("POST", "/business/order/jshOrder/add", body=order_body())
        # the backend replaces orderCode with the next jsh_sequence number, so look up by customer
        recs = new.get("/business/order/jshOrder/list", {"customerId": cid})["result"]["records"] or [{}]
        oid, order_code = recs[0].get("id"), recs[0].get("orderCode")
        created["order"] = oid

        def order_rows():
            o = int(oid)
            return [int(db.scalar(f"SELECT COUNT(*) FROM {ndb}.{t} WHERE {c}={o} AND del_flag=0 AND tenant_id={tenant}"))
                    for t, c in (("jsh_order", "id"), ("jsh_order_product", "order_id"),
                                 ("jsh_order_product_detail", "order_id"), ("jsh_order_product_extend", "order_id"))]

        if r.get("success") and isinstance(oid, str) and order_rows() == [1, 1, 2, 1]:
            ok("write: add order -> id string, db rows order/product/detail/extend = 1/1/2/1")
        else:
            fail(f"write: add order success={r.get('success')} msg={str(r.get('message'))[:60]} "
                 f"id_type={type(oid).__name__} rows={order_rows() if oid else None}")
            return
        details = new.get("/business/order/jshOrderProduct/queryJshOrderProductDetailByOrderId",
                          {"id": oid, "pageSize": 100})["result"]
        details = details["records"] if isinstance(details, dict) else details
        db_detail_ids = sorted(x[0] for x in db.rows(f"SELECT id FROM {ndb}.jsh_order_product_detail WHERE order_id={int(oid)}"))
        api_detail_ids = sorted(str(d["id"]) for d in details)
        if api_detail_ids == db_detail_ids and all(isinstance(d["id"], str) for d in details):
            ok(f"write: detail ids (~1e17) returned as strings and equal db ids ({len(db_detail_ids)})")
        else:
            fail("write: detail ids from api differ from db ids")

        # 3. edit (id sent back as string, as the frontend does)
        r = new._req("PUT", "/business/order/jshOrder/edit", body=order_body(oid, remark="parity edit", n=3, code=order_code))
        remark = db.scalar(f"SELECT remark FROM {ndb}.jsh_order WHERE id={int(oid)}")
        pnum = db.scalar(f"SELECT num FROM {ndb}.jsh_order_product WHERE order_id={int(oid)} AND del_flag=0")
        if r.get("success") and remark == "parity edit" and pnum == "3" and order_rows() == [1, 1, 2, 1]:
            ok("write: edit order with string id -> db remark/num updated, children rebuilt 1/1/2/1")
        else:
            fail(f"write: edit success={r.get('success')} remark_ok={remark == 'parity edit'} num={pnum} rows={order_rows()}")
        q = new.get("/business/order/jshOrder/queryById", {"id": oid})
        if q.get("success") and q["result"].get("id") == oid:
            ok("write: queryById with string id returns same string id")
        else:
            fail("write: queryById by string id failed")

        # 4. export: file type and header row vs old backend (same endpoint, same filter)
        exp_params = {"orderTime_begin": f"{args.export_day} 00:00:00", "orderTime_end": f"{args.export_day} 23:59:59"}
        heads = {}
        for name, api in (("old", old), ("new", new)):
            code, ctype, disp, content = api._req("GET", "/business/order/jshOrder/exportXls", exp_params, raw=True)
            kind, head = excel_header(content)
            heads[name] = (kind, head)
            info(f"write: export {name}: http={code} type={kind} bytes={len(content)} disposition_has_xls={'.xls' in disp}")
        if heads["new"][0] in ("xls", "xlsx") and heads["new"] == heads["old"] and any(heads["new"][1]):
            ok(f"write: export file {heads['new'][0]}, header rows identical to old ({[len(r) for r in heads['new'][1]]} cells)")
        else:
            fail(f"write: export header differs: old={heads['old'][0]} new={heads['new'][0]} "
                 f"same_rows={[a == b for a, b in zip(heads['old'][1] or [], heads['new'][1] or [])]}")
        code, ctype, disp, content = new._req("GET", "/business/order/jshOrder/exportXls", {"customerId": cid}, raw=True)
        kind, head = excel_header(content, rows=8)
        if kind and order_code and any(order_code in r for r in head):
            ok("write: export of the new order contains its order code")
        else:
            fail("write: export of the new order missing its row")

        # 5. delete (logical, like 3.0)
        r = new._req("DELETE", "/business/order/jshOrder/delete", {"id": oid})
        if r.get("success") and order_rows() == [0, 0, 0, 0]:
            ok("write: delete order with string id -> order and children del_flag=1")
        else:
            fail(f"write: delete order success={r.get('success')} remaining={order_rows()}")
        r = new._req("DELETE", "/business/customer/jshCustomer/delete", {"id": cid})
        left = db.scalar(f"SELECT COUNT(*) FROM {ndb}.jsh_customer WHERE id={int(cid)} AND del_flag=0")
        if r.get("success") and left == "0":
            ok("write: delete customer -> db del_flag=1")
        else:
            fail(f"write: delete customer success={r.get('success')} remaining={left}")
    finally:
        # physical cleanup of everything this run created
        if created["order"]:
            o = int(created["order"])
            for t, c in (("jsh_order_product_detail", "order_id"), ("jsh_order_product_extend", "order_id"),
                         ("jsh_order_product", "order_id"), ("jsh_order", "id")):
                db.rows(f"DELETE FROM {ndb}.{t} WHERE {c}={o}")
        if created["customer"]:
            c = int(created["customer"])
            for t in ("jsh_order_product_detail", "jsh_order_product_extend", "jsh_order_product"):
                db.rows(f"DELETE FROM {ndb}.{t} WHERE order_id IN (SELECT id FROM {ndb}.jsh_order WHERE customer_id={c})")
            db.rows(f"DELETE FROM {ndb}.jsh_order WHERE customer_id={c}")
            db.rows(f"DELETE FROM {ndb}.jsh_customer_address WHERE customer_id={c}")
            db.rows(f"DELETE FROM {ndb}.jsh_customer WHERE id={c}")
        cid_sql = int(created["customer"] or 0)
        left = db.scalar(f"SELECT (SELECT COUNT(*) FROM {ndb}.jsh_order WHERE customer_id={cid_sql})"
                         f" + (SELECT COUNT(*) FROM {ndb}.jsh_customer WHERE name='{tag}')")
        (ok if left == "0" else fail)(f"write: cleanup removed test rows (left={left})")


# ---------------------------------------------------------------- statistics

SUMMARY_COLS = "DATE_FORMAT(time,'%Y-%m-%d'), customer_id, tenant_id, total_num, total_area, total_price, del_flag"


def stats_check(args, users, db):
    admin = [(u, t) for u, t in users if not t]
    if not admin:
        fail("stats needs a user without tenant (admin) to run the quartz job")
        return
    u = admin[0][0]
    start = (dt.date.today() - dt.timedelta(days=44)).isoformat()
    window = f"time >= '{start}'"
    for name, base, header, dbname in (("old", args.old, "tenant-id", args.old_db),
                                       ("new", args.new, "X-Tenant-Id", args.new_db)):
        job = db.scalar(f"SELECT id FROM {dbname}.sys_quartz_job WHERE job_class_name LIKE '%StatisticOrderJob%' LIMIT 1")
        if name == "new":
            # zero the chosen day on the new DB so identical rows afterwards prove a real recompute
            db.rows(f"UPDATE {dbname}.jsh_order_summary_daily SET total_num=0, total_area=0, total_price=0 "
                    f"WHERE DATE(time) = '{args.stats_date}'")
        # refreshed = some row written at or after the moment we trigger (second precision)
        t0 = db.scalar("SELECT DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s')")
        api = Api(base, header)
        api.login(u, args.password, None)
        r = api.get("/sys/quartzJob/execute", {"id": job})
        deadline = time.time() + 60
        refreshed = False
        while time.time() < deadline and not refreshed:
            time.sleep(2)
            refreshed = db.scalar(f"SELECT COUNT(*) FROM {dbname}.jsh_order_summary_daily "
                                  f"WHERE {window} AND update_time >= '{t0}'") != "0"
        if r.get("success") and refreshed:
            ok(f"stats: {name} job executed (summary rows refreshed)")
        else:
            fail(f"stats: {name} execute success={r.get('success')} msg={str(r.get('message'))[:60]} refreshed={refreshed}")
    for table, cond in (("jsh_order_summary_daily", window),
                        ("jsh_order_summary_monthly", f"time >= '{start[:7]}-01'"),
                        ("jsh_order_summary_daily", f"DATE(time) = '{args.stats_date}'")):
        o = sorted(tuple(r) for r in db.rows(f"SELECT {SUMMARY_COLS} FROM {args.old_db}.{table} WHERE {cond}"))
        n = sorted(tuple(r) for r in db.rows(f"SELECT {SUMMARY_COLS} FROM {args.new_db}.{table} WHERE {cond}"))
        if o == n and o:
            ok(f"stats: {table} [{cond}] identical ({len(o)} rows)")
        else:
            keys = sorted({(r[0], r[1]) for r in set(o) ^ set(n)})[:8]
            fail(f"stats: {table} [{cond}] old={len(o)} new={len(n)} differing (date,customer_id)={keys}")


# ---------------------------------------------------------------- main

def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--old", required=True, help="old backend base, e.g. http://127.0.0.1:8081/portal")
    ap.add_argument("--new", required=True, help="new backend base, e.g. http://127.0.0.1:8080/portal")
    ap.add_argument("--user", action="append", required=True,
                    help="login name, optionally name:tenantId (repeatable). No tenant = admin-style user")
    ap.add_argument("--password", required=True)
    ap.add_argument("--src", default=str(DEFAULT_SRC), help="ERP module java sources (endpoint scan)")
    ap.add_argument("--db-cmd", help='mysql client command, e.g. "docker exec -i erp-verify-mysql mysql -uroot -proot"')
    ap.add_argument("--old-db", default="jeecg_old")
    ap.add_argument("--new-db", default="jeecg_v3")
    ap.add_argument("--date-from")
    ap.add_argument("--date-to")
    ap.add_argument("--month", help="YYYY-MM for the monthly summary filter")
    ap.add_argument("--include-sequence", action="store_true", help="also call /business/sequence/num (advances jsh_sequence)")
    ap.add_argument("--write-chain", action="store_true", help="create/edit/export/delete on the new backend (needs --db-cmd)")
    ap.add_argument("--export-day", default=(dt.date.today() - dt.timedelta(days=12)).isoformat())
    ap.add_argument("--stats", action="store_true",
                    help="execute StatisticOrderJob on both and compare (needs --db-cmd; zeroes --stats-date rows "
                         "in --new-db first, so restore with db/erp/migrate_from_3.0.sql afterwards)")
    ap.add_argument("--stats-date", default=(dt.date.today() - dt.timedelta(days=10)).isoformat())
    args = ap.parse_args()

    users = []
    for spec in args.user:
        name, _, tenant = spec.partition(":")
        users.append((name, tenant or None))
    if (args.write_chain or args.stats) and not args.db_cmd:
        ap.error("--write-chain/--stats need --db-cmd")

    endpoints = scan_get_endpoints(args.src)
    info(f"scanned {len(endpoints)} GET endpoints from {args.src}")
    read_parity(args, users, endpoints)
    db = Db(args.db_cmd) if args.db_cmd else None
    if args.write_chain:
        write_chain(args, users, db)
    if args.stats:
        stats_check(args, users, db)

    if FAILURES:
        print(f"{len(FAILURES)} PARITY CHECK(S) FAILED")
        sys.exit(1)
    print("ALL PARITY CHECKS PASSED")


if __name__ == "__main__":
    main()
