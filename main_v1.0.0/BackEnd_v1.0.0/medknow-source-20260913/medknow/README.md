# 药知道 MedKnow

面向中老年慢病患者的用药管理 App。核心功能：用药计划与提醒、打卡记录、药品查询、用药冲突提示、文章科普、附近医院。

## 目录结构

```
medknow/
├── backend/     # 后端服务（Spring Boot 4.1 + Java 21 + MyBatis + MySQL 8）
├── android/     # 安卓客户端（Kotlin + Jetpack Compose，最低 API 35）
└── docs/        # 接口文档与需求文档
    ├── MedKnow.md   # API 接口契约（30 个接口，后端已全部实现）
    └── PRD.txt      # 产品需求文档
```

## 团队分工

| 成员 | 负责 |
|---|---|
| 周奕全 | 后端（`backend/`） |
| 搭档 | 安卓端（`android/`） |

**接口契约以 `docs/MedKnow.md` 为准**，两端都以它为准开发，改动接口时同步更新该文档。

## 快速开始（后端）

环境要求：JDK 21、Maven 3.9+、MySQL 8。

```bash
cd backend

# 1. 初始化数据库（只需一次）
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS medknow DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p -e "CREATE USER IF NOT EXISTS 'medknow'@'localhost' IDENTIFIED BY 'medknow123'; GRANT ALL PRIVILEGES ON medknow.* TO 'medknow'@'localhost'; FLUSH PRIVILEGES;"
mysql -u medknow -pmedknow123 medknow < db/schema.sql
mysql -u medknow -pmedknow123 medknow < db/data.sql

# 2. 启动（默认端口 8090）
mvn spring-boot:run
```

跑接口回归测试（61 项断言，需后端已启动）：

```bash
cd backend && ./test-api.sh
```

更详细的说明见 [`backend/README.md`](backend/README.md)。

## 配置说明

`backend/src/main/resources/application.yml` 里所有配置项**都带默认值，clone 下来可直接跑**。需要覆盖时用环境变量：

| 环境变量 | 默认值 | 说明 |
|---|---|---|
| `SERVER_PORT` | `8090` | 服务端口 |
| `DB_URL` | 本机 `medknow` 库 | JDBC 连接串 |
| `DB_USERNAME` | `medknow` | 数据库账号 |
| `DB_PASSWORD` | `medknow123` | 数据库密码 |
| `JWT_SECRET` | 开发用密钥 | 生产必须覆盖（≥32 字符） |
| `UPLOAD_DIR` | `./upload/avatars` | 头像上传目录 |

也可以复制 `backend/src/main/resources/application-local.yml.example` 为同目录下的 `application-local.yml` 写个人配置（该文件已被 `.gitignore` 忽略，不会提交）。

## 开发约定

- 后端开发模式默认开启：短信验证码**明文打印到控制台并直接在响应里返回**（`dev-code-return`），联调时不用真发短信。上线前必须关掉。
- 安卓模拟器访问宿主机后端用 `http://10.0.2.2:8090`。
- 两端联调细节见 [`backend/安卓端联调说明.md`](backend/安卓端联调说明.md)。
