# MedKnow「药知道」后端服务

用药管理 App 的后端服务：Spring Boot 4.1 + Java 21 + MyBatis + MySQL 8，提供 API 文档（MedKnow.md）约定的全部 30 个接口。

## 技术栈

| 组件 | 版本 | 说明 |
|---|---|---|
| Spring Boot | 4.1.0 | 注意：Boot 4 的 web starter 已改名 `spring-boot-starter-webmvc` |
| Java | 21 (LTS) | 本机 JDK 位于 `C:\Users\ZHOUYIQUAN\.jdks\ms-21.0.9` |
| MyBatis | mybatis-spring-boot-starter 4.1.0 | 原生 MyBatis，手写 XML SQL |
| MySQL | 8.0（本机服务 MySQL80） | 库名 `medknow`，账号 `medknow/medknow123` |
| JWT | jjwt 0.12.7 | 登录鉴权，有效期 7 天 |
| JSON | Jackson 2（静态工具）+ Boot 4 内置 Jackson 3 | 两者并存，互不冲突 |

## 目录结构

```
medknow-backend/
├── db/schema.sql          # 13 张表 DDL
├── db/data.sql            # 种子数据（1用户/15药/9院/10文）
├── test-api.sh            # 全量接口回归测试（61 项断言）
├── upload/avatars/        # 头像上传目录
└── src/main/
    ├── java/com/yaozhidao/
    │   ├── common/        # 统一响应/错误码/全局异常/校验
    │   ├── config/        # WebConfig/定时任务/配置项
    │   ├── security/      # JWT 工具/拦截器/用户上下文
    │   ├── entity/ mapper/ dto/ controller/ service/
    └── resources/
        ├── application.yml / application-dev.yml
        └── mapper/*.xml   # 手写 SQL
```

## 启动步骤

1. **初始化数据库**（只需一次，已执行过可跳过）：
   ```bash
   mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS medknow DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
   mysql -u root -p -e "CREATE USER IF NOT EXISTS 'medknow'@'localhost' IDENTIFIED BY 'medknow123'; GRANT ALL PRIVILEGES ON medknow.* TO 'medknow'@'localhost'; FLUSH PRIVILEGES;"
   mysql -u medknow -pmedknow123 medknow < db/schema.sql
   mysql -u medknow -pmedknow123 medknow < db/data.sql
   ```
2. **启动服务**：IDEA 打开 `medknow-backend`，运行 `MedknowBackendApplication`；或命令行 `mvn spring-boot:run`（需先配好 JAVA_HOME 指向 `.jdks\ms-21.0.9`）
3. **验证**：`curl http://localhost:8090/api/v1/health` 返回 `{"status":"ok","timestamp":"..."}`

## 测试

```bash
./test-api.sh    # 61 项断言：30 个接口 + 主要错误分支，可重复执行
```

## 种子数据

- 测试用户：张大爷 `13800138000`（已完善资料 NORMAL，可直接登录）
- 15 种常见药（含说明书剂量上限，供剂量校验）、9 家广州医院（含科室）、10 篇科普文章（五分类）

## 登录联调速查（开发模式）

验证码不接真实短信：打印在服务端控制台，且接口响应直接返回 `data.devCode`。流程：

```bash
# 1. 发验证码（60 秒限一次）
curl -X POST localhost:8090/api/v1/auth/send-code -H 'Content-Type: application/json' -d '{"phone":"13800138000"}'
# → data.devCode 即验证码

# 2. 登录拿 token
curl -X POST localhost:8090/api/v1/auth/login -H 'Content-Type: application/json' -d '{"phone":"13800138000","code":"<devCode>"}'

# 3. 之后的请求带请求头
Authorization: Bearer <token>
```

## 📌 契约裁决表（Android 端联调必读）

API 文档（MedKnow.md）存在少量不一致，以下为**最终裁决**，联调以本表为准：

| 项目 | 文档原文 | 最终裁决 | 原因 |
|---|---|---|---|
| 成功响应 code | 混写 0 / 200 / 201 | **统一 0** | 客户端只需判断 code==0 |
| 创建类接口 HTTP 状态码 | 201 | **201**（code 仍为 0） | 保留文档语义 |
| 401 错误码 | 40100 / 4011 混用 | **4010**=Token过期，**4011**=未登录 | 统一两码 |
| 500 错误码 | 5000 / 50000 混用 | **50000** | 统一 |
| 文章不存在 | 3001（HTTP 未定） | **3001 + HTTP 404** | 语义正确 |
| 40901 复用 | 状态不可更新 / 已打卡 | **两者都用 40901**，message 区分 | 文档如此 |
| 日期时间格式 | 不统一 | 一律 **yyyy-MM-dd HH:mm:ss**（LocalDateTime 已加 @JsonFormat） | 防 Jackson 3 默认 ISO 格式坑 |
| 依从率统计周期 | PRD 说自然周 | 开发版用**滚动 7 天/30 天**窗口 | 简化实现，README 注明 |
| 提醒状态 IGNORED | 状态机存在 | 保留状态值，暂无接口触发（Android 端"跳过"功能预留） | 30 个接口无跳过动作 |
| 补打卡 | 仅限 48 小时内 | EXPIRED 也可打卡（isLate=true） | 简化 |

## 认证分级

| 级别 | 接口 | 说明 |
|---|---|---|
| 必须登录 | user/**、medication-plans/**、checkins/**、drugs/{id}/collect、articles/{id}/collect、articles/{id}/like | 无 token → 4011 |
| 可选登录 | articles/**（除互动） | 有 token 推荐更精准，无 token 可用 |
| 公开 | auth/**、health、feedback、drugs/search、drugs/{id}、hospitals/** | 无需 token |

## 错误码速查

| HTTP | code | 含义 |
|---|---|---|
| 400 | 1001/1002/1003/1004 | 验证码错误/过期/手机号格式/发送频繁 |
| 400 | 4000~40010 | 参数类（40001 通用/40002 输入不规范/40003 值无效/40005 定位/40007 文件大小/40008 格式） |
| 401 | 4010/4011 | Token 过期/未登录 |
| 403 | 40300~40302 | 权限（40301 信息不完善） |
| 404 | 40401~40404、3001 | 计划/提醒/药品/医院/文章不存在 |
| 409 | 40901~40905 | 状态冲突（已打卡/不可激活/暂停/恢复/已在药箱） |
| 500 | 50000 | 服务器错误 |

## 常用账号

| 手机号 | 状态 | 说明 |
|---|---|---|
| 13800138000 | NORMAL | 种子用户张大爷，全功能可用 |
| 13900000001/2 | PENDING | 新注册用户（测试信息不完善拦截） |
