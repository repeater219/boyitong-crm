#!/bin/bash
# 博易通CRM 部署冒烟测试脚本
# 用法: ./smoke-test.sh [BASE_URL]
# 默认: https://boyitong-crm-production.up.railway.app

set -e

BASE_URL="${1:-https://boyitong-crm-production.up.railway.app}"
PASS=0
FAIL=0

green() { echo -e "\033[32m✓ $1\033[0m"; }
red() { echo -e "\033[31m✗ $1\033[0m"; }
check() {
  if [ "$1" -eq 0 ]; then
    green "$2"
    PASS=$((PASS + 1))
  else
    red "$2 — $3"
    FAIL=$((FAIL + 1))
  fi
}

echo "========================================"
echo "  博易通CRM 冒烟测试"
echo "  目标: $BASE_URL"
echo "========================================"
echo ""

# ─── 1. 首页静态资源 ──────────────────────────────────
echo "--- 静态资源 ---"

HTTP=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL")
check $([ "$HTTP" = "200" ]) "首页 (/) 返回 200" "got $HTTP"

HTTP=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/index.html")
check $([ "$HTTP" = "200" ]) "index.html 返回 200" "got $HTTP"

HTTP=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/assets/")
check $([ "$HTTP" != "404" ]) "assets/ 可访问" "got $HTTP"

# ─── 2. 登录 ───────────────────────────────────────────
echo ""
echo "--- 认证 ---"

LOGIN=$(curl -s "$BASE_URL/api/auth/login" \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin123"}')
TOKEN=$(echo "$LOGIN" | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['token'])" 2>/dev/null || echo "")
check $([ -n "$TOKEN" ]) "登录成功，获取到 token" "token为空"

# 错误密码应该返回 400/401
ERR_HTTP=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/auth/login" \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"wrong"}')
check $([ "$ERR_HTTP" != "200" ]) "错误密码登录返回非200 (实际=$ERR_HTTP)" "got $ERR_HTTP"

# 无 token 访问 API 应被拒绝
HTTP=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/customers?page=0&size=5")
check $([ "$HTTP" = "403" ]) "无token访问API被拒绝 (实际=$HTTP)" "got $HTTP"

# ─── 3. 核心 API ──────────────────────────────────────
echo ""
echo "--- API 功能 ---"

AUTH="Authorization: Bearer $TOKEN"

# 客户列表
DATA=$(curl -s "$BASE_URL/api/customers?page=0&size=5" -H "$AUTH")
COUNT=$(echo "$DATA" | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['totalElements'])" 2>/dev/null || echo "0")
check $([ "$COUNT" -gt 0 ]) "客户列表返回数据 (共${COUNT}条)" "count=$COUNT"

# 统计数据
DATA=$(curl -s "$BASE_URL/api/stats" -H "$AUTH")
TOTAL=$(echo "$DATA" | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['totalCustomers'])" 2>/dev/null || echo "0")
check $([ "$TOTAL" -gt 0 ]) "统计接口返回数据 (客户${TOTAL}个)" "total=$TOTAL"

# 商机
DATA=$(curl -s "$BASE_URL/api/crm/opportunities" -H "$AUTH")
OPP_COUNT=$(echo "$DATA" | python3 -c "import sys,json; d=json.load(sys.stdin)['data']; print(len(d) if isinstance(d,list) else 0)" 2>/dev/null || echo "0")
check $([ "$OPP_COUNT" -gt 0 ]) "商机接口返回数据 (${OPP_COUNT}条)" "count=$OPP_COUNT"

# 合同
DATA=$(curl -s "$BASE_URL/api/crm/contracts" -H "$AUTH")
CT_COUNT=$(echo "$DATA" | python3 -c "import sys,json; d=json.load(sys.stdin)['data']; print(len(d) if isinstance(d,list) else 0)" 2>/dev/null || echo "0")
check $([ "$CT_COUNT" -gt 0 ]) "合同接口返回数据 (${CT_COUNT}条)" "count=$CT_COUNT"

# 产品
DATA=$(curl -s "$BASE_URL/api/crm/products" -H "$AUTH")
PROD_COUNT=$(echo "$DATA" | python3 -c "import sys,json; d=json.load(sys.stdin)['data']; print(len(d) if isinstance(d,list) else 0)" 2>/dev/null || echo "0")
check $([ "$PROD_COUNT" -gt 0 ]) "产品接口返回数据 (${PROD_COUNT}条)" "count=$PROD_COUNT"

# 通知
HTTP=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/notifications?unreadOnly=false" -H "$AUTH")
check $([ "$HTTP" = "200" ]) "通知接口返回 200" "got $HTTP"

# ─── 4. CRM 新建 ──────────────────────────────────────
echo ""
echo "--- CRM 操作 ---"

# 新建产品
CREATE=$(curl -s -X POST "$BASE_URL/api/crm/products" -H "$AUTH" -H 'Content-Type: application/json' \
  -d '{"name":"测试产品","category":"测试","unit":"个","price":99.99}')
PROD_ID=$(echo "$CREATE" | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['id'])" 2>/dev/null || echo "")
check $([ -n "$PROD_ID" ]) "新建产品成功 (id=$PROD_ID)" "id为空"

# 删除测试产品
HTTP=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE "$BASE_URL/api/crm/products/$PROD_ID" -H "$AUTH")
check $([ "$HTTP" = "200" ]) "删除产品成功" "got $HTTP"

# ─── 5. 密码修改 ──────────────────────────────────────
echo ""
echo "--- 密码安全 ---"

# 错误原密码应该返回 400
HTTP=$(curl -s -o /dev/null -w "%{http_code}" -X PUT "$BASE_URL/api/profile/password" \
  -H "$AUTH" -H 'Content-Type: application/json' \
  -d '{"oldPassword":"wrong","newPassword":"newpass123"}')
MSG=$(curl -s -X PUT "$BASE_URL/api/profile/password" \
  -H "$AUTH" -H 'Content-Type: application/json' \
  -d '{"oldPassword":"wrong","newPassword":"newpass123"}' | \
  python3 -c "import sys,json; print(json.load(sys.stdin).get('message',''))" 2>/dev/null || echo "")
check $([ "$HTTP" = "400" ]) "错误原密码被拒绝 (HTTP $HTTP, msg: $MSG)" "got $HTTP"

# ─── 6. 客户导出 ──────────────────────────────────────
echo ""
echo "--- 导出 ---"

EXPORT_HTTP=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/export/customers?city=柳州" -H "$AUTH")
CONTENT_TYPE=$(curl -s -D - "$BASE_URL/api/export/customers?city=柳州" -H "$AUTH" 2>/dev/null | grep -i content-type | head -1)
check $([ "$EXPORT_HTTP" = "200" ]) "客户导出Excel返回200" "got $EXPORT_HTTP"

# ─── 7. 响应时间 ──────────────────────────────────────
echo ""
echo "--- 响应性能 ---"

TIME=$(curl -s -o /dev/null -w "%{time_total}" "$BASE_URL/api/customers?page=0&size=20" -H "$AUTH")
check $(echo "$TIME < 3" | bc -l 2>/dev/null || echo "1" ) "客户列表响应 < 3s (实际=${TIME}s)" "timeout"

TIME=$(curl -s -o /dev/null -w "%{time_total}" "$BASE_URL/api/stats" -H "$AUTH")
check $(echo "$TIME < 3" | bc -l 2>/dev/null || echo "1" ) "统计接口响应 < 3s (实际=${TIME}s)" "timeout"

# ─── 结果汇总 ─────────────────────────────────────
echo ""
echo "========================================"
if [ "$FAIL" -eq 0 ]; then
  echo -e "\033[32m全部通过! $PASS/$((PASS+FAIL)) 测试通过\033[0m"
else
  echo -e "\033[31m$FAIL 个失败, $PASS/$((PASS+FAIL)) 通过\033[0m"
fi
echo "========================================"