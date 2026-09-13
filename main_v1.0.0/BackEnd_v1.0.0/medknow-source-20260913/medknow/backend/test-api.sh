#!/bin/bash
# ============================================================
# 「药知道」MedKnow 后端全量接口回归测试（30 个接口 + 主要错误分支）
# 用法: 先启动服务（mvn spring-boot:run），再运行 ./test-api.sh
# 注意: 脚本开头会清空验证码表绕过 60 秒重发限制（仅限开发测试环境）
# ============================================================
BASE=http://localhost:8090/api/v1
PASS=0; FAIL=0
MYSQL="D:/MySQL/MySQL Server 8.0/bin/mysql.exe"

check() { # check <名称> <期望包含> <实际输出>  （-F 固定字符串匹配，避免正则特殊字符）
  if echo "$3" | grep -qF "$2"; then
    PASS=$((PASS+1)); echo "✅ $1"
  else
    FAIL=$((FAIL+1)); echo "❌ $1  期望[$2] 实际[$3]"
  fi
}

# 清空验证码表（绕过 60 秒重发限制）+ 清理计划/收藏数据（保证测试可重复执行），仅开发测试用
"$MYSQL" --default-character-set=utf8mb4 -u medknow -pmedknow123 medknow -e "DELETE FROM sms_code; DELETE FROM reminder; DELETE FROM plan_drug; DELETE FROM medication_plan; DELETE FROM user_drug_box WHERE user_id=1;" 2>/dev/null

# git-bash 的 MSYS 会把命令行中文参数转成 GBK 导致 curl 发送非法 UTF-8，
# 因此所有中文 query 参数写入临时文件，用 --data-urlencode "name@file" 方式传递。
# 注意：Windows curl 读不了 MSYS 虚拟的 /tmp 路径，必须用 cygpath 转成 Windows 路径
# （不能依赖 $TEMP 环境变量，cmd 继承环境下其值可能带反斜杠或未定义）
TMPWIN=$(cygpath -m /tmp)
printf '%s' '阿莫西林' > "$TMPWIN/kw_drug"
printf '%s' '骨科'     > "$TMPWIN/kw_dept"
printf '%s' '越秀'     > "$TMPWIN/kw_addr"
printf '%s' '中药'     > "$TMPWIN/kw_cat"
printf '%s' '神秘'     > "$TMPWIN/kw_badcat"

sendcode() { # sendcode <手机号> → 输出 devCode
  curl -s -X POST $BASE/auth/send-code -H 'Content-Type: application/json' -d "{\"phone\":\"$1\"}" \
    | grep -o '"devCode":"[0-9]*"' | cut -d'"' -f4
}

# ---------- 29. 健康检查（独立格式） ----------
OUT=$(curl -s $BASE/health); check "29 健康检查" '"status":"ok"' "$OUT"

# ---------- 1. 发送验证码（60秒限制 + devCode） ----------
OUT=$(curl -s -X POST $BASE/auth/send-code -H 'Content-Type: application/json' -d '{"phone":"13800138000"}')
check "1a 发送验证码" '"code":0' "$OUT"
DEVCODE=$(echo "$OUT" | grep -o '"devCode":"[0-9]*"' | cut -d'"' -f4)
check "1b 开发模式返回验证码" "$DEVCODE" "$DEVCODE"
OUT=$(curl -s -X POST $BASE/auth/send-code -H 'Content-Type: application/json' -d '{"phone":"13800138000"}')
check "1c 60秒重发限制(1004)" '1004' "$OUT"
OUT=$(curl -s -X POST $BASE/auth/send-code -H 'Content-Type: application/json' -d '{"phone":"123"}')
check "1d 手机号格式错误(1003)" '1003' "$OUT"

# ---------- 2. 登录 ----------
OUT=$(curl -s -X POST $BASE/auth/login -H 'Content-Type: application/json' -d "{\"phone\":\"13800138000\",\"code\":\"$DEVCODE\"}")
check "2a 验证码登录" '"token"' "$OUT"
TOKEN=$(echo "$OUT" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
AUTH="Authorization: Bearer $TOKEN"
OUT=$(curl -s -X POST $BASE/auth/login -H 'Content-Type: application/json' -d '{"phone":"13800138000","code":"000000"}')
check "2b 验证码错误(1001)" '1001' "$OUT"

# ---------- 3. 个人信息（脱敏） ----------
OUT=$(curl -s $BASE/user/profile -H "$AUTH")
check "3a 获取个人信息(脱敏)" '138****8000' "$OUT"
OUT=$(curl -s $BASE/user/profile)
check "3b 未登录访问(4011)" '4011' "$OUT"

# ---------- 4. 更新个人信息（补全→NORMAL） ----------
OUT=$(curl -s -X PUT $BASE/user/profile -H "$AUTH" -H 'Content-Type: application/json' --data-binary @- <<'JSON'
{"userName":"张大爷","age":66,"gender":"MALE","occupation":"退休","allergies":"青霉素","chronicDiseases":"高血压,糖尿病"}
JSON
)
check "4 更新个人信息" '"code":0' "$OUT"

# ---------- 5. 偏好设置 ----------
OUT=$(curl -s $BASE/user/settings -H "$AUTH")
check "5 获取偏好设置" 'PURPLE' "$OUT"

# ---------- 6. 头像上传 ----------
echo '/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/wAALCAABAAEBAREA/8QAFAABAAAAAAAAAAAAAAAAAAAACf/EABQQAQAAAAAAAAAAAAAAAAAAAAD/2gAIAQEAAD8AKp//2Q==' | base64 -d > /tmp/test.jpg
OUT=$(curl -s -X POST $BASE/user/avatar -H "$AUTH" -F "file=@/tmp/test.jpg")
check "6a 上传头像" 'avatarUrl' "$OUT"
cp /tmp/test.jpg /tmp/test.gif
OUT=$(curl -s -X POST $BASE/user/avatar -H "$AUTH" -F "file=@/tmp/test.gif")
check "6b 格式不支持(40008)" '40008' "$OUT"
dd if=/dev/zero of=/tmp/big.jpg bs=1M count=3 2>/dev/null
OUT=$(curl -s -X POST $BASE/user/avatar -H "$AUTH" -F "file=@/tmp/big.jpg")
check "6c 文件超限(40007)" '40007' "$OUT"

# ---------- 17. 药品搜索（公开） ----------
OUT=$(curl -s -G "$BASE/drugs/search" --data-urlencode "keyword@$TMPWIN/kw_drug" --data-urlencode "page=1" --data-urlencode "size=10")
check "17a 药品模糊搜索" '阿莫西林胶囊' "$OUT"
OUT=$(curl -s -G "$BASE/drugs/search" --data-urlencode "keyword=1=1")
check "17b SQL关键字拦截(40002)" '40002' "$OUT"

# ---------- 18. 药品详情（公开） ----------
OUT=$(curl -s $BASE/drugs/1)
check "18a 药品详情" '阿莫西林胶囊' "$OUT"
OUT=$(curl -s $BASE/drugs/999)
check "18b 药品不存在(40403)" '40403' "$OUT"

# ---------- 19. 收藏药箱 ----------
OUT=$(curl -s -X POST $BASE/drugs/1/collect -H "$AUTH")
check "19a 收藏药品" '"code":0' "$OUT"
OUT=$(curl -s -X POST $BASE/drugs/1/collect -H "$AUTH")
check "19b 重复收藏(40905)" '40905' "$OUT"

# ---------- 20. 医院推荐 ----------
OUT=$(curl -s -G "$BASE/hospitals/recommend" --data-urlencode "longitude=113.2632" --data-urlencode "latitude=23.1290")
check "20a 推荐医院" '广州市第一人民医院' "$OUT"
OUT=$(curl -s -G "$BASE/hospitals/recommend" --data-urlencode "longitude=113.2632" --data-urlencode "latitude=23.1290" --data-urlencode "department@$TMPWIN/kw_dept")
check "20b 科室筛选(骨科权重生效)" '红十字会医院' "$OUT"
OUT=$(curl -s "$BASE/hospitals/recommend")
check "20c 缺少定位(40005)" '40005' "$OUT"

# ---------- 21. 医院详情 ----------
OUT=$(curl -s $BASE/hospitals/1)
check "21a 医院详情" '越秀区盘福路1号' "$OUT"
OUT=$(curl -s $BASE/hospitals/999)
check "21b 医院不存在(40404)" '40404' "$OUT"

# ---------- 22. 搜索医院 ----------
OUT=$(curl -s -G "$BASE/hospitals/search" --data-urlencode "keyword@$TMPWIN/kw_addr")
check "22 搜索医院" '广东省人民医院' "$OUT"

# ---------- 23. 文章列表（公开） ----------
OUT=$(curl -s -G "$BASE/articles" --data-urlencode "category@$TMPWIN/kw_cat" --data-urlencode "page=1" --data-urlencode "size=10")
check "23a 文章分类列表" '中药' "$OUT"
OUT=$(curl -s -G "$BASE/articles" --data-urlencode "category@$TMPWIN/kw_badcat")
check "23b 无效分类(40001)" '40001' "$OUT"

# ---------- 24. 文章推荐（未登录→热门） ----------
OUT=$(curl -s $BASE/articles/recommend)
check "24 未登录推荐文章" 'articles' "$OUT"

# ---------- 25. 文章详情（阅读量+1） ----------
OUT=$(curl -s $BASE/articles/1)
check "25a 文章详情" '高血压患者冬季用药指南' "$OUT"
OUT=$(curl -s $BASE/articles/999)
check "25b 文章不存在(3001)" '3001' "$OUT"

# ---------- 26. 收藏切换 ----------
OUT=$(curl -s -X POST $BASE/articles/1/collect -H "$AUTH")
check "26a 收藏文章" '"isCollected":true' "$OUT"
OUT=$(curl -s -X POST $BASE/articles/1/collect -H "$AUTH")
check "26b 取消收藏" '"isCollected":false' "$OUT"

# ---------- 27. 点赞切换 ----------
OUT=$(curl -s -X POST $BASE/articles/1/like -H "$AUTH")
check "27a 点赞文章" '"isLiked":true' "$OUT"
OUT=$(curl -s -X POST $BASE/articles/1/like)
check "27b 未登录点赞(4011)" '4011' "$OUT"
# 27c 取消点赞（切换式对称验证 + 状态归零，保证测试可重复执行）
OUT=$(curl -s -X POST $BASE/articles/1/like -H "$AUTH")
check "27c 取消点赞" '"isLiked":false' "$OUT"

# ---------- 28. 分享 ----------
OUT=$(curl -s -X GET $BASE/articles/1/share -H "$AUTH" -H 'Content-Type: application/json' -d '{"target":"WECHAT"}')
check "28a 分享文章" '"code":0' "$OUT"
OUT=$(curl -s -X GET $BASE/articles/1/share -H "$AUTH" -H 'Content-Type: application/json' -d '{"target":"TWITTER"}')
check "28b 非法平台(4000)" '4000' "$OUT"

# ---------- P1. PENDING 用户建计划（40301） ----------
CODE2=$(sendcode 13900000002)
OUT=$(curl -s -X POST $BASE/auth/login -H 'Content-Type: application/json' -d "{\"phone\":\"13900000002\",\"code\":\"$CODE2\"}")
TOKEN2=$(echo "$OUT" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
AUTH2="Authorization: Bearer $TOKEN2"
OUT=$(curl -s -X POST $BASE/medication-plans -H "$AUTH2" -H 'Content-Type: application/json' --data-binary @- <<'JSON'
{"drugs":[{"drugName":"阿莫西林","dosage":"1片","frequency":"每日3次","takeTime":"07:00","takeMethod":"AFTER_MEAL"}],"startDate":"2026-08-06","endDate":"2026-08-20","reminderMethods":["ALARM"]}
JSON
)
check "P1 信息不完善建计划(40301)" '40301' "$OUT"

# ---------- 7. 创建用药计划（含冲突检测） ----------
OUT=$(curl -s -X POST $BASE/medication-plans -H "$AUTH" -H 'Content-Type: application/json' --data-binary @- <<'JSON'
{"drugs":[{"drugName":"阿莫西林","dosage":"1片","frequency":"每日3次","takeTime":"07:00,12:00,18:00","takeMethod":"AFTER_MEAL","dietaryRestrictions":"忌酒"},{"drugName":"布洛芬","dosage":"1粒","frequency":"每日2次","takeTime":"08:00,20:00","takeMethod":"AFTER_MEAL"}],"startDate":"2026-08-06","endDate":"2026-08-20","notes":"忌酒，多喝水","diagnosis":"上呼吸道感染伴发热","reminderMethods":["ALARM","PUSH"]}
JSON
)
check "7a 创建计划(INTERVAL冲突)" 'INTERVAL' "$OUT"
PLAN=$(echo "$OUT" | grep -o '"planId":[0-9]*' | head -1 | cut -d':' -f2)
OUT=$(curl -s -X POST $BASE/medication-plans -H "$AUTH" -H 'Content-Type: application/json' --data-binary @- <<'JSON'
{"drugs":[{"drugName":"藿香正气水","dosage":"10ml","frequency":"每日2次","takeTime":"08:00,20:00","takeMethod":"AFTER_MEAL"},{"drugName":"头孢呋辛酯片","dosage":"0.5g","frequency":"每日2次","takeTime":"08:00,20:00","takeMethod":"AFTER_MEAL"}],"startDate":"2026-08-06","endDate":"2026-08-20","reminderMethods":["ALARM"]}
JSON
)
check "7b 配伍禁忌(CONTRAINDICATION)" 'CONTRAINDICATION' "$OUT"
OUT=$(curl -s -X POST $BASE/medication-plans -H "$AUTH" -H 'Content-Type: application/json' --data-binary @- <<'JSON'
{"drugs":[{"drugName":"阿莫西林","dosage":"5g","frequency":"每日3次","takeTime":"07:00","takeMethod":"AFTER_MEAL"}],"startDate":"2026-08-06","endDate":"2026-08-20","reminderMethods":["ALARM"]}
JSON
)
check "7c 剂量超限标记(dosageRisk)" '"dosageRisk":true' "$OUT"
OUT=$(curl -s -X POST $BASE/medication-plans -H "$AUTH" -H 'Content-Type: application/json' --data-binary @- <<'JSON'
{"drugs":[{"drugName":"阿莫西林","dosage":"1片","frequency":"每日3次","takeTime":"07:00","takeMethod":"AFTER_MEAL"}],"startDate":"2026-08-20","endDate":"2026-08-06","reminderMethods":["ALARM"]}
JSON
)
check "7d 日期逻辑错误(40002)" '40002' "$OUT"

# ---------- 8. 计划列表 ----------
OUT=$(curl -s -G "$BASE/medication-plans" --data-urlencode "status=DRAFT" --data-urlencode "page=1" --data-urlencode "size=10" -H "$AUTH")
check "8a 计划列表" 'mainDrugName' "$OUT"
OUT=$(curl -s -G "$BASE/medication-plans" --data-urlencode "status=XXX" -H "$AUTH")
check "8b 无效状态(40004)" '40004' "$OUT"

# ---------- 9. 计划详情 ----------
OUT=$(curl -s $BASE/medication-plans/$PLAN -H "$AUTH")
check "9 计划详情" 'todayReminders' "$OUT"

# ---------- 10. 更新计划（草稿可更新） ----------
OUT=$(curl -s -X PUT $BASE/medication-plans/$PLAN -H "$AUTH" -H 'Content-Type: application/json' --data-binary @- <<'JSON'
{"drugs":[{"drugName":"阿莫西林","dosage":"1片","frequency":"每日3次","takeTime":"07:00,12:00,18:00","takeMethod":"AFTER_MEAL"}],"startDate":"2026-08-06","endDate":"2026-08-20","reminderMethods":["ALARM"]}
JSON
)
check "10 更新计划" '"code":0' "$OUT"

# ---------- 11. 激活（批量生成提醒） ----------
OUT=$(curl -s -X POST $BASE/medication-plans/$PLAN/activate -H "$AUTH")
check "11a 激活计划" '"status":"ACTIVE"' "$OUT"
OUT=$(curl -s -X POST $BASE/medication-plans/$PLAN/activate -H "$AUTH")
check "11b 重复激活(40902)" '40902' "$OUT"
OUT=$(curl -s -X PUT $BASE/medication-plans/$PLAN -H "$AUTH" -H 'Content-Type: application/json' --data-binary @- <<'JSON'
{"drugs":[{"drugName":"阿莫西林","dosage":"1片","frequency":"每日3次","takeTime":"07:00","takeMethod":"AFTER_MEAL"}],"startDate":"2026-08-06","endDate":"2026-08-20","reminderMethods":["ALARM"]}
JSON
)
check "11c 生效中不可更新(40901)" '40901' "$OUT"

# ---------- 12/13. 暂停/恢复 ----------
OUT=$(curl -s -X POST $BASE/medication-plans/$PLAN/pause -H "$AUTH")
check "12a 暂停计划" '"status":"PAUSED"' "$OUT"
OUT=$(curl -s -X POST $BASE/medication-plans/$PLAN/pause -H "$AUTH")
check "12b 重复暂停(40903)" '40903' "$OUT"
OUT=$(curl -s -X POST $BASE/medication-plans/$PLAN/resume -H "$AUTH")
check "13 恢复计划" '"status":"ACTIVE"' "$OUT"

# ---------- 15. 今日提醒 ----------
OUT=$(curl -s $BASE/checkins/today -H "$AUTH")
check "15 今日提醒列表" 'reminders' "$OUT"
RID=$(echo "$OUT" | grep -o '"reminderId":[0-9]*' | head -1 | cut -d':' -f2)

# ---------- 14. 打卡 ----------
OUT=$(curl -s -X POST $BASE/checkins -H "$AUTH" -H 'Content-Type: application/json' -d "{\"reminderId\":$RID,\"actualTime\":\"2026-08-06 07:05\",\"isLate\":true}")
check "14a 服药打卡" '"code":0' "$OUT"
OUT=$(curl -s -X POST $BASE/checkins -H "$AUTH" -H 'Content-Type: application/json' -d "{\"reminderId\":$RID,\"actualTime\":\"2026-08-06 07:05\",\"isLate\":false}")
check "14b 重复打卡(40901)" '40901' "$OUT"
OUT=$(curl -s -X POST $BASE/checkins -H "$AUTH" -H 'Content-Type: application/json' -d '{"reminderId":99999,"actualTime":"2026-08-06 07:05","isLate":false}')
check "14c 提醒不存在(40402)" '40402' "$OUT"

# ---------- 16. 依从率统计 ----------
OUT=$(curl -s -G "$BASE/checkins/stats" --data-urlencode "period=WEEK" -H "$AUTH")
check "16a 周依从率" 'adherenceRate' "$OUT"
OUT=$(curl -s -G "$BASE/checkins/stats" --data-urlencode "period=YEAR" -H "$AUTH")
check "16b 无效周期(40003)" '40003' "$OUT"

# ---------- 30. 用户反馈 ----------
OUT=$(curl -s -X POST $BASE/feedback -H 'Content-Type: application/json' --data-binary @- <<'JSON'
{"content":"建议增加夜间模式","contact":"13800138000"}
JSON
)
check "30a 提交反馈" '"code":0' "$OUT"
OUT=$(curl -s -X POST $BASE/feedback -H 'Content-Type: application/json' -d '{"content":""}')
check "30b 空内容(40009)" '40009' "$OUT"

# ---------- 汇总 ----------
echo ""
echo "==================================="
echo "通过 $PASS 项 / 失败 $FAIL 项"
echo "==================================="
[ $FAIL -eq 0 ] && echo "🎉 全部通过！" || echo "⚠️ 有失败项，请检查"
