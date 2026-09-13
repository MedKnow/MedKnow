---
title: MedKnow
language_tabs:
  - shell: Shell
  - http: HTTP
  - javascript: JavaScript
  - ruby: Ruby
  - python: Python
  - php: PHP
  - java: Java
  - go: Go
toc_footers: []
includes: []
search: true
code_clipboard: true
highlight_theme: darkula
headingLevel: 2
generator: "@tarslib/widdershins v4.0.30"

---

# MedKnow

Base URLs:

* <a href="http://localhost:8090">开发环境: http://localhost:8090</a>

# Authentication

# 用户模块

## POST 发送短信验证码

POST /api/v1/auth/send-code

用户输入手机号，后端调用短信网关发送 6 位验证码。同一手机号 60 秒内只能发送一次。

> Body 请求参数

```json
{
  "phone": "13800138000"
}
```

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|body|body|object| 是 ||none|
|» phone|body|string| 是 | 手机号|用户的手机号码|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "message": "验证码已发送",
  "data": {
    "expireSeconds": 300
  }
}
```

> 400 Response

```json
{
  "code": 1003,
  "message": "手机号格式不正确",
  "data": null
}
```

> 502 Response

```json
{
  "code": 5001,
  "message": "短信发送失败，请稍后重试",
  "data": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|none|Inline|
|502|[Bad Gateway](https://tools.ietf.org/html/rfc7231#section-6.6.3)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|状态码|接口返回的状态码|
|» message|string|true|none|提示信息|接口返回的提示信息|
|» data|object|true|none|数据|接口返回的数据对象|
|»» expireSeconds|integer|true|none|过期秒数|数据的过期时间，单位秒|

状态码 **400**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||none|
|» message|string|true|none||none|
|» data|null|true|none||none|

状态码 **502**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||none|
|» message|string|true|none||none|
|» data|null|true|none||none|

## POST 手机号验证码登录

POST /api/v1/auth/login

用户输入手机号和短信验证码完成登录，返回 JWT Token

> Body 请求参数

```json
{
  "phone": "13800138000",
  "code": "123456"
}
```

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|body|body|object| 是 ||none|
|» phone|body|string| 是 | 手机号码|用户手机号码|
|» code|body|string| 是 | 验证码|短信验证码|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "userName": "张大爷",
    "status": "NORMAL"
  }
}
```

> 400 Response

```json
{
  "code": 1002,
  "message": "验证码已过期，请重新获取",
  "data": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|状态码|接口返回的状态码，200表示成功|
|» message|string|true|none|消息|接口返回的消息内容|
|» data|object|true|none|数据|接口返回的数据对象|
|»» token|string|true|none|令牌|用户登录令牌|
|»» userId|integer|true|none|用户ID|用户唯一标识|
|»» userName|string|true|none|用户名|用户登录名称|
|»» status|string|true|none|状态|用户当前状态|

状态码 **400**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|状态码|接口返回的状态码，0表示成功，其他表示错误|
|» message|string|true|none|消息|接口返回的提示信息|
|» data|null|true|none|数据|接口返回的数据，此处为null|

## GET 获取个人信息

GET /api/v1/user/profile

获取当前登录用户的基本信息，包括昵称、头像、年龄、性别、职业等。

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|Authorization|header|string| 是 ||格式：Bearer <token>，登录时返回的 token|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "userId": 1,
    "userName": "张大爷",
    "phone": "138****8000",
    "avatar": "https://cdn.yaozhidao.com/avatars/default.png",
    "age": 65,
    "gender": "MALE",
    "occupation": "退休",
    "allergies": "青霉素",
    "chronicDiseases": "高血压",
    "status": "NORMAL",
    "createdAt": "2026-07-13 10:30:00"
  }
}
```

> 401 Response

```json
{
  "code": 4010,
  "message": "Token 已过期，请重新登录",
  "data": null
}
```

> 500 Response

```json
{
  "code": 5000,
  "message": "服务器繁忙，请稍后重试",
  "data": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

*API 响应*

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|业务状态码，0 表示成功|状态码|
|» message|string|true|none|提示信息|提示信息|
|» data|object|true|none|用户数据|用户数据|
|»» userId|integer|true|none|用户 ID，登录时返回的那个|用户ID|
|»» userName|string|true|none|用户名|用户名|
|»» phone|string|true|none|手机号，脱敏显示（中间 4 位用 * 代替）|手机号|
|»» avatar|string|true|none|头像URL|头像URL|
|»» age|integer|true|none|年龄|年龄|
|»» gender|string|true|none|性别：MALE 男 / FEMALE 女 / OTHER 其他|性别|
|»» occupation|string|true|none|职业|职业|
|»» allergies|string|false|none|过敏史|过敏史|
|»» chronicDiseases|string|false|none|慢性疾病|慢性疾病|
|»» status|string|true|none|账户状态：NORMAL 正常 / PENDING 待完善 / RESTRICTED 受限|状态|
|»» createdAt|string|true|none|注册时间，格式 yyyy-MM-dd HH:mm:ss|创建时间|

状态码 **401**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|状态码|返回的状态码，通常用于表示请求结果|
|» message|string|true|none|消息|返回的提示信息，用于说明请求处理情况|
|» data|null|true|none|数据|返回的数据内容，此处为null表示无数据返回|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|状态码|接口响应状态码|
|» message|string|true|none|消息|接口响应消息|
|» data|null|true|none|数据|接口响应数据|

## PUT 更新个人信息

PUT /api/v1/user/profile

用户修改自己的昵称、年龄、性别、职业、过敏史、慢性病史等信息。

> Body 请求参数

```json
{
  "userName": "张大爷",
  "age": 66,
  "gender": "MALE",
  "occupation": "退休",
  "allergies": "青霉素",
  "chronicDiseases": "高血压, 糖尿病"
}
```

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|Authorization|header|string| 是 ||Bearer <token>，标识当前登录用户|
|body|body|object| 是 ||none|
|» userName|body|string| 否 | 用户名|用户昵称，2-20 个字符|
|» age|body|integer| 否 | 年龄|年龄，0-120|
|» gender|body|string| 否 | 性别|MALE / FEMALE / OTHER|
|» occupation|body|string| 否 | 职业|职业|
|» allergies|body|string| 否 | 过敏史|过敏史，多个过敏源用逗号分隔|
|» chronicDiseases|body|string| 否 | 慢性疾病|慢性病史，多个用逗号分隔|

#### 枚举值

|属性|值|
|---|---|
|» gender|MALE|
|» gender|FEMALE|
|» gender|OTHER|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "message": "更新成功",
  "data": null
}
```

> 400 Response

```json
{
  "code": 4000,
  "message": "参数校验失败",
  "data": null
}
```

> 401 Response

```json
{
  "code": 4010,
  "message": "Token 已过期，请重新登录",
  "data": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|none|Inline|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|状态码|请求状态码，200表示成功|
|» message|string|true|none|提示消息|返回的提示信息|
|» data|null|true|none|返回数据|返回的数据内容，可能为空|

状态码 **400**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|状态码|接口响应状态码，200表示成功|
|» message|string|true|none|提示信息|接口返回的提示信息，描述操作结果|
|» data|null|true|none|数据|接口返回的数据，当前为null|

状态码 **401**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|状态码|接口调用状态码，例如：200 表示成功，其他值表示异常|
|» message|string|true|none|消息|返回的消息内容，例如：'操作成功' 或具体的错误信息|
|» data|null|true|none|数据|返回的业务数据，当前为null表示无数据返回|

## GET 获取用户偏好设置

GET /api/v1/user/settings

获取当前用户的个性化设置，包括主题色、头像、提醒开关等。App 启动时调用，用于恢复用户的自定义配置。

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|Authorization|header|string| 是 ||Bearer <token>，标识当前登录用户|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "themeColor": "PURPLE",
    "avatar": "https://cdn.yaozhidao.com/avatars/default.png",
    "notificationEnabled": true,
    "smsReminderEnabled": false
  }
}
```

> 401 Response

```json
{
  "code": 4010,
  "message": "Token 已过期，请重新登录",
  "data": null
}
```

> 500 Response

```json
{
  "code": 5000,
  "message": "服务器繁忙，请稍后重试",
  "data": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||业务状态码，0 表示成功|
|» message|string|true|none||提示信息|
|» data|object|true|none||用户偏好设置|
|»» themeColor|string|true|none||主题色：PURPLE 默认紫 / GREEN 护眼绿 / BLUE 深邃蓝 / ORANGE 暖阳橙 / GRAY 极简灰|
|»» avatar|string|true|none||头像 URL，如果没设置过返回默认头像地址|
|»» notificationEnabled|boolean|true|none||App 推送开关：true 开启 / false 关闭|
|»» smsReminderEnabled|boolean|true|none||短信提醒开关：true 开启 / false 关闭|

状态码 **401**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||none|
|» message|string|true|none||none|
|» data|null|true|none||none|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||none|
|» message|string|true|none||none|
|» data|null|true|none||none|

## POST 上传头像

POST /api/v1/user/avatar

> Body 请求参数

```yaml
file: ""

```

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|Authorization|header|string| 是 ||认证令牌|
|body|body|object| 是 ||none|
|» file|body|string(binary)| 是 ||头像文件。图片文件，格式限 jpg、jpeg、png，大小 ≤2MB|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "message": "上传成功",
  "data": {
    "avatarUrl": "string"
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|none|Inline|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|业务状态码|none|
|» message|string|true|none|提示信息|none|
|» data|object|true|none|数据体|none|
|»» avatarUrl|string|true|none|头像地址|可访问的完整路径，如 https://cdn.example.com/avatar/user_1.jpg|

状态码 **400**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|40007：文件大小超限；40008：格式不支持|
|» message|string|true|none|错误描述|如“图片大小不能超过2MB”或“仅支持 JPG/PNG 格式”|

#### 枚举值

|属性|值|
|---|---|
|code|40007|
|code|40008|

状态码 **401**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

# 用药计划

## POST 创建用药计划

POST /api/v1/medication-plans

用户输入诊断或医嘱信息，生成结构化用药计划，以“草稿”状态保存。包含药品清单、提醒方式及智能解析冲突结果。

> Body 请求参数

```json
{
    "drugs": [
        {
            "drugName": "阿莫西林",
            "dosage": "1片",
            "frequency": "每日3次",
            "takeTime": "07:00,12:00,18:00",
            "takeMethod": "AFTER_MEAL",
            "dietaryRestrictions": "忌酒"
        },
        {
            "drugName": "布洛芬",
            "dosage": "1粒",
            "frequency": "每日2次",
            "takeTime": "08:00,20:00",
            "takeMethod": "AFTER_MEAL",
            "dietaryRestrictions": "忌空腹服用"
        }
    ],
    "startDate": "2026-07-14",
    "endDate": "2026-07-28",
    "notes": "忌酒，多喝水",
    "diagnosis": "上呼吸道感染伴发热",
    "reminderMethods": [
        "ALARM",
        "PUSH"
    ]
}
```

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|body|body|object| 是 ||none|
|» drugs|body|[object]| 是 ||none|
|»» drugName|body|string| 是 | 药品名称|后端需额外拦截 SQL 关键字|
|»» dosage|body|string| 是 | 单次剂量|后端校验不超过说明书最大剂量|
|»» frequency|body|string| 是 | 服用频次|如“每日3次”|
|»» takeTime|body|string| 是 | 服药时间|06:00~23:00，多个用逗号分隔|
|»» takeMethod|body|string| 是 | 服用方式|none|
|»» dietaryRestrictions|body|string| 否 | 忌口提示|例如“忌酒”|
|» startDate|body|string| 是 | 开始日期|yyyy-MM-dd，≥当天|
|» endDate|body|string| 是 | 结束日期|yyyy-MM-dd，> startDate|
|» notes|body|string| 否 | 备注|none|
|» diagnosis|body|string| 否 | 诊断/医嘱|用户输入的诊断信息|
|» reminderMethods|body|[string]| 是 | 提醒方式|至少选一种|

#### 枚举值

|属性|值|
|---|---|
|»» takeMethod|BEFORE_MEAL|
|»» takeMethod|AFTER_MEAL|
|»» takeMethod|EMPTY_STOMACH|
|»» takeMethod|BEFORE_SLEEP|
|» reminderMethods|ALARM|
|» reminderMethods|PUSH|
|» reminderMethods|SMS|

> 返回示例

> 201 Response

```json
{
    "code": 201,
    "message": "创建成功",
    "data": {
        "planId": 1,
        "status": "DRAFT",
        "startDate": "2026-07-14",
        "endDate": "2026-07-28",
        "notes": "忌酒",
        "diagnosis": "上呼吸道感染",
        "reminderMethods": [
            "ALARM",
            "PUSH"
        ],
        "drugs": [
            {
                "drugId": 1,
                "drugName": "阿莫西林",
                "verified": true,
                "dosageRisk": false
            }
        ],
        "conflicts": []
    }
}
```

> 400 Response

```json
{
    "code": 40001,
    "message": "药品名称不能为空"
}
{
    "code": 40002,
    "message": "结束日期不能早于开始日期"
}
{
    "code": 40003,
    "message": "阿莫西林单次剂量超出说明书最大建议值"
}
```

> 403 Response

```json
{
    "code": 40301,
    "message": "请先完成注册或完善信息"
}
```

> 500 Response

```json
{
    "code": 50000,
    "message": "系统繁忙，请稍后再试"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|none|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|none|Inline|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **201**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||固定值 201|
|» message|string|true|none||固定值 "创建成功"|
|» data|object|true|none|创建结果|none|
|»» planId|integer|true|none|计划ID|新创建的唯一标识|
|»» status|string|true|none|计划状态|初始为草稿|
|»» startDate|string|true|none|开始日期|none|
|»» endDate|string|true|none|结束日期|none|
|»» notes|string¦null|false|none|备注|none|
|»» diagnosis|string¦null|false|none|诊断|none|
|»» reminderMethods|[string]|true|none|提醒方式|用户选择的提醒渠道|
|»» drugs|[object]|true|none|药品列表|返回服务端校验结果|
|»»» drugId|integer|true|none|药品ID|计划内药品唯一标识|
|»»» drugName|string|true|none|药品名称|none|
|»»» verified|boolean|true|none|是否匹配药品库|false 表示药品名未匹配标准库|
|»»» dosageRisk|boolean|true|none|剂量风险标记|true 表示超出说明书剂量|
|»» conflicts|[object]|true|none|智能解析冲突|none|
|»»» drugs|[string]|true|none|冲突药品|涉及的药品名称|
|»»» type|string|true|none|冲突类型|none|
|»»» message|string|true|none|冲突描述|详细提示|

#### 枚举值

|属性|值|
|---|---|
|status|DRAFT|
|type|INTERVAL|
|type|CONTRAINDICATION|

状态码 **400**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|错误码：40001 通用校验；40002 日期错误；40003 剂量超限|
|» message|string|true|none|错误描述|例如“药品名称不能为空”|

#### 枚举值

|属性|值|
|---|---|
|code|40001|
|code|40002|
|code|40003|

状态码 **403**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|未注册或信息不完善|
|» message|string|true|none|错误描述|“请先完成注册或完善信息”|

#### 枚举值

|属性|值|
|---|---|
|code|40301|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|系统未知错误|
|» message|string|true|none|错误描述|“系统繁忙，请稍后再试”|

## GET 获取用药计划列表

GET /api/v1/medication-plans

获取当前用户所有用药计划摘要列表，支持按状态筛选。

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|status|query|string| 否 ||筛选条件，不传则全部|
|page|query|integer| 否 ||none|
|size|query|integer| 否 ||none|

#### 枚举值

|属性|值|
|---|---|
|status|DRAFT|
|status|ACTIVE|
|status|PAUSED|
|status|COMPLETED|
|status|EXPIRED|

> 返回示例

> 200 Response

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "total": 5,
        "pageNum": 1,
        "pageSize": 10,
        "plans": [
            {
                "planId": 1,
                "status": "ACTIVE",
                "drugCount": 2,
                "startDate": "2026-07-14",
                "endDate": "2026-07-28",
                "adherenceRate": 85,
                "notes": "忌酒",
                "mainDrugName": "阿莫西林等2种",
                "reminderMethods": [
                    "ALARM",
                    "PUSH"
                ]
            }
        ]
    }
}
```

> 400 Response

```json
{
    "code": 40004,
    "message": "无效的状态筛选值"
}
```

> 403 Response

```json
{
    "code": 40300,
    "message": "请先登录"
}
```

> 500 Response

```json
{
    "code": 50000,
    "message": "系统繁忙，请稍后再试"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|none|Inline|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|状态码|none|
|» message|string|true|none|提示信息|none|
|» data|object|true|none|数据体|none|
|»» total|integer|true|none|总记录数|none|
|»» pageNum|integer|true|none|当前页码|none|
|»» pageSize|integer|true|none|每页大小|none|
|»» plans|[object]|true|none|计划列表|none|
|»»» planId|integer|true|none|计划ID|none|
|»»» status|string|true|none|计划状态|none|
|»»» drugCount|integer|true|none|药品数量|none|
|»»» startDate|string(date)|true|none|开始日期|none|
|»»» endDate|string(date)|true|none|结束日期|none|
|»»» adherenceRate|integer|true|none|依从率|百分比|
|»»» notes|string|false|none|备注|可能为 null|
|»»» mainDrugName|string|true|none|主要药品|如“阿莫西林等2种”|
|»»» reminderMethods|[string]|true|none|提醒方式|缩略展示|

状态码 **400**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **403**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|系统未知错误|
|» message|string|true|none|错误描述|系统繁忙，请稍后再试|

## GET 获取计划详情

GET /api/v1/medication-plans/{planId}

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|planId|path|integer| 是 ||路径参数|

> 返回示例

> 200 Response

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "planId": 1,
        "status": "ACTIVE",
        "startDate": "2026-07-14",
        "endDate": "2026-07-28",
        "notes": "忌酒",
        "adherenceRate": 85,
        "diagnosis": "上呼吸道感染",
        "reminderMethods": [
            "ALARM",
            "PUSH"
        ],
        "drugs": [
            {
                "drugId": 1,
                "drugName": "阿莫西林",
                "dosage": "1片",
                "frequency": "每日3次",
                "takeTime": "07:00,12:00,18:00",
                "takeMethod": "AFTER_MEAL",
                "dietaryRestrictions": "忌酒",
                "verified": true,
                "dosageRisk": false
            }
        ],
        "conflicts": [],
        "todayReminders": [
            {
                "reminderId": 101,
                "drugName": "阿莫西林",
                "scheduledTime": "2026-07-14 07:00",
                "status": "TRIGGERED"
            }
        ]
    }
}
```

> 403 Response

```json
{
    "code": 40302,
    "message": "无权访问该计划"
}
```

> 404 Response

```json
{
    "code": 40401,
    "message": "用药计划不存在"
}
```

> 500 Response

```json
{
    "code": 50000,
    "message": "系统繁忙，请稍后再试"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|none|Inline|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|状态码|none|
|» message|string|true|none|提示信息|none|
|» data|object|true|none|数据体|none|
|»» planId|integer|true|none|计划ID|none|
|»» status|string|true|none|计划状态|none|
|»» startDate|string|true|none|开始日期|none|
|»» endDate|string|true|none|结束日期|none|
|»» notes|string¦null|false|none|备注|none|
|»» diagnosis|string¦null|false|none|诊断|none|
|»» adherenceRate|integer|true|none|依从率|none|
|»» reminderMethods|[string]|true|none|提醒方式|none|
|»» drugs|[object]|true|none|药品清单|详细约束信息|
|»»» drugId|integer|true|none|药品ID|none|
|»»» drugName|string|true|none|药品名称|none|
|»»» dosage|string|true|none|剂量|none|
|»»» frequency|string|true|none|频次|none|
|»»» takeTime|string|true|none|服药时间|可能多个逗号分隔|
|»»» takeMethod|string|true|none|服用方式|none|
|»»» dietaryRestrictions|string¦null|false|none|忌口|none|
|»»» verified|boolean|true|none|验证标识|none|
|»»» dosageRisk|boolean|true|none|风险标识|none|
|»» conflicts|[string]|true|none|冲突列表|结构同创建接口|
|»» todayReminders|[object]|true|none|今日提醒|当天所有提醒|
|»»» reminderId|integer|true|none|提醒ID|none|
|»»» drugName|string|true|none|药品名|none|
|»»» scheduledTime|string|true|none|计划提醒时间|none|
|»»» status|string|true|none|提醒状态|none|

#### 枚举值

|属性|值|
|---|---|
|status|PENDING|
|status|TRIGGERED|
|status|TAKEN|
|status|IGNORED|
|status|EXPIRED|

状态码 **403**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **404**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|系统未知错误|
|» message|string|true|none|错误描述|系统繁忙，请稍后再试|

## PUT 更新用药计划

PUT /api/v1/medication-plans/{planId}

> Body 请求参数

```json
{
    "drugs": [
        {
            "drugName": "阿莫西林",
            "dosage": "1片",
            "frequency": "每日3次",
            "takeTime": "07:00,12:00,18:00",
            "takeMethod": "AFTER_MEAL",
            "dietaryRestrictions": "忌酒"
        },
        {
            "drugName": "布洛芬",
            "dosage": "1粒",
            "frequency": "每日2次",
            "takeTime": "08:00,20:00",
            "takeMethod": "AFTER_MEAL",
            "dietaryRestrictions": "忌空腹服用"
        }
    ],
    "startDate": "2026-07-14",
    "endDate": "2026-07-28",
    "notes": "忌酒，多喝水",
    "diagnosis": "上呼吸道感染伴发热",
    "reminderMethods": [
        "ALARM",
        "PUSH"
    ]
}
```

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|planId|path|integer| 是 ||none|
|body|body|object| 是 ||none|
|» drugs|body|[object]| 是 | 药品清单|本次计划包含的所有药品|
|»» drugName|body|string| 是 | 药品名称|用户输入的药品名|
|»» dosage|body|string| 是 | 单次剂量|数字+单位|
|»» frequency|body|string| 是 | 服用频次|如“每日3次”|
|»» takeTime|body|string| 是 | 服药时间|06:00~23:00，多个用逗号分隔|
|»» takeMethod|body|string| 是 | 服用方式|饭前/饭后/空腹/睡前|
|»» dietaryRestrictions|body|string| 否 | 忌口提示|例如“忌酒”|
|» startDate|body|string(date)| 是 | 开始日期|计划生效日期|
|» endDate|body|string(date)| 是 | 结束日期|计划截止日期|
|» notes|body|string| 否 | 备注|全局备注|
|» diagnosis|body|string| 否 | 诊断/医嘱|用户输入的诊断信息|
|» reminderMethods|body|[string]| 是 | 提醒方式|闹钟/推送/短信|

#### 枚举值

|属性|值|
|---|---|
|»» takeMethod|BEFORE_MEAL|
|»» takeMethod|AFTER_MEAL|
|»» takeMethod|EMPTY_STOMACH|
|»» takeMethod|BEFORE_SLEEP|
|» reminderMethods|ALARM|
|» reminderMethods|PUSH|
|» reminderMethods|SMS|

> 返回示例

> 200 Response

```json
{
    "code": 200,
    "message": "更新成功",
    "data": null
}
```

> 400 Response

```json
{
    "code": 40001,
    "message": "药品名称不能为空"
}
{
    "code": 40002,
    "message": "结束日期不能早于开始日期"
}
{
    "code": 40003,
    "message": "阿莫西林单次剂量超出说明书最大建议值"
}
```

> 404 Response

```json
{
    "code": 40401,
    "message": "用药计划不存在"
}
```

> 409 Response

```json
{
    "code": 40901,
    "message": "仅草稿或暂停状态的计划可更新"
}
```

> 500 Response

```json
{
    "code": 50000,
    "message": "系统繁忙，请稍后再试"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|none|Inline|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|none|Inline|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|状态码|none|
|» message|string|true|none|提示信息|none|
|» data|null|true|none|数据体|固定为null|

状态码 **400**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|例如“药品名称不能为空”|

#### 枚举值

|属性|值|
|---|---|
|code|40001|
|code|40002|
|code|40003|

状态码 **404**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **409**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|系统未知错误|
|» message|string|true|none|错误描述|“系统繁忙，请稍后再试”|

## POST 激活用药计划

POST /api/v1/medication-plans/{planId}/activate

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|planId|path|integer| 是 ||none|

> 返回示例

> 200 Response

```json
{
    "code": 200,
    "message": "已生效",
    "data": {
        "planId": 1,
        "status": "ACTIVE",
        "activatedAt": "2026-07-14T11:00:00Z"
    }
}
```

> 400 Response

```json
{
    "code": 40010,
    "message": "请至少选择一种提醒方式"
}
```

> 404 Response

```json
{
    "code": 40401,
    "message": "用药计划不存在"
}
```

> 409 Response

```json
{
    "code": 40902,
    "message": "当前状态不可激活"
}
```

> 500 Response

```json
{
    "code": 50000,
    "message": "系统繁忙，请稍后再试"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|none|Inline|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|none|Inline|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|状态码|none|
|» message|string|true|none|提示信息|none|
|» data|object|true|none|数据体|none|
|»» planId|integer|true|none|计划ID|none|
|»» status|string|true|none|计划状态|none|
|»» activatedAt|string(date-time)|true|none|激活时间|格式 yyyy-MM-ddTHH:mm:ssZ|

状态码 **400**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **404**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **409**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|系统未知错误|
|» message|string|true|none|错误描述|系统繁忙，请稍后再试|

## POST 暂停用药计划

POST /api/v1/medication-plans/{planId}/pause

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|planId|path|integer| 是 ||none|

> 返回示例

> 200 Response

```json
{
    "code": 200,
    "message": "已暂停",
    "data": {
        "planId": 1,
        "status": "PAUSED",
        "pausedAt": "2026-07-20T08:30:00Z"
    }
}
```

> 404 Response

```json
{
    "code": 40401,
    "message": "用药计划不存在"
}
```

> 409 Response

```json
{
    "code": 40903,
    "message": "仅生效中的计划可以暂停"
}
```

> 500 Response

```json
{
    "code": 50000,
    "message": "系统繁忙，请稍后再试"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|none|Inline|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|状态码|none|
|» message|string|true|none|提示信息|none|
|» data|object|true|none|数据体|none|
|»» planId|integer|true|none|计划ID|none|
|»» status|string|true|none|计划状态|none|
|»» pausedAt|string(date-time)|true|none|暂停时间|none|

状态码 **404**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **409**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|系统未知错误|
|» message|string|true|none|错误描述|系统繁忙，请稍后再试|

## POST 恢复用药计划

POST /api/v1/medication-plans/{planId}/resume

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|planId|path|integer| 是 ||none|

> 返回示例

> 200 Response

```json
{
    "code": 200,
    "message": "已恢复",
    "data": {
        "planId": 1,
        "status": "ACTIVE",
        "resumedAt": "2026-07-22T09:00:00Z"
    }
}
```

> 404 Response

```json
{
    "code": 40401,
    "message": "用药计划不存在"
}
```

> 409 Response

```json
{
    "code": 40904,
    "message": "仅暂停中的计划可以恢复"
}
```

> 500 Response

```json
{
    "code": 50000,
    "message": "系统繁忙，请稍后再试"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|none|Inline|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|状态码|none|
|» message|string|true|none|提示信息|none|
|» data|object|true|none|数据体|none|
|»» planId|integer|true|none|计划ID|none|
|»» status|string|true|none|计划状态|none|
|»» resumedAt|string(date-time)|true|none|恢复时间|none|

状态码 **404**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **409**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|系统未知错误|
|» message|string|true|none|错误描述|系统繁忙，请稍后再试|

# 服药打卡

## POST 服药打卡

POST /api/v1/checkins

> Body 请求参数

```json
{
    "reminderId": 1,
    "actualTime": "2026-07-14 07:05",
    "isLate": true
}
```

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|Authorization|header|string| 是 ||认证令牌|
|body|body|object| 是 ||none|
|» reminderId|body|integer| 是 | 提醒ID|要打卡的提醒记录ID|
|» actualTime|body|string| 是 | 实际服药时间|用户真实服药的时间点|
|» isLate|body|boolean| 是 | 是否延迟打卡|超过提醒时间 30 分钟后打卡为 true|

> 返回示例

> 200 Response

```json
{
    "code": 0,
    "message": "打卡成功",
    "data": null
}
```

> 400 Response

```json
{
    "code": 40001,
    "message": "参数错误：reminderId 不能为空"
}
```

> 404 Response

```json
{
    "code": 40402,
    "message": "提醒记录不存在"
}
```

> 409 Response

```json
{
    "code": 40901,
    "message": "该提醒已打卡"
}
```

> 500 Response

```json
{
    "code": 50000,
    "message": "系统繁忙，请稍后再试"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|none|Inline|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|none|Inline|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|业务状态码|成功|
|» message|string|true|none|提示信息|none|
|» data|null|true|none|数据体|固定为 null|

状态码 **400**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|例如缺少必填字段、格式错误|
|» message|string|true|none|错误描述|none|

状态码 **404**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **409**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

## GET 获取今日待打卡列表

GET /api/v1/checkins/today

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|Authorization|header|string| 是 ||认证令牌|

> 返回示例

> 200 Response

```json
{
    "code": 0,
    "message": "获取成功",
    "data": {
        "reminders": [
            {
                "reminderId": 1,
                "planId": 1,
                "drugName": "阿莫西林",
                "scheduledTime": "07:00",
                "status": "PENDING"
            },
            {
                "reminderId": 2,
                "planId": 1,
                "drugName": "阿莫西林",
                "scheduledTime": "12:00",
                "status": "TRIGGERED"
            }
        ]
    }
}
```

> 401 Response

```json
{
    "code": 40100,
    "message": "请先登录"
}
```

> 500 Response

```json
{
    "code": 50000,
    "message": "系统繁忙，请稍后再试"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|业务状态码|none|
|» message|string|true|none|提示信息|none|
|» data|object|true|none|数据体|none|
|»» reminders|[object]|true|none|今日提醒列表|none|
|»»» reminderId|integer|true|none|提醒ID|none|
|»»» planId|integer|true|none|所属计划ID|none|
|»»» drugName|string|true|none|药品名称|none|
|»»» scheduledTime|string|true|none|计划服药时间|仅当天时间点|
|»»» status|string|true|none|提醒状态|当前提醒的状态|

#### 枚举值

|属性|值|
|---|---|
|status|PENDING|
|status|TRIGGERED|
|status|TAKEN|
|status|IGNORED|
|status|EXPIRED|

状态码 **401**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

## GET 获取依从率统计

GET /api/v1/checkins/stats

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|period|query|string| 否 ||统计周期|
|Authorization|header|string| 是 ||认证令牌|

#### 枚举值

|属性|值|
|---|---|
|period|WEEK|
|period|MONTH|

> 返回示例

> 200 Response

```json
{
    "code": 0,
    "message": "获取成功",
    "data": {
        "period": "WEEK",
        "totalDoses": 21,
        "confirmedDoses": 18,
        "missedDoses": 3,
        "adherenceRate": 85.7
    }
}

{
    "code": 0,
    "message": "获取成功",
    "data": {
        "period": "MONTH",
        "totalDoses": 84,
        "confirmedDoses": 70,
        "missedDoses": 14,
        "adherenceRate": 83.3
    }
}
```

> 400 Response

```json
{
    "code": 40003,
    "message": "无效的统计周期，仅支持 WEEK/MONTH"
}
```

> 401 Response

```json
{
    "code": 40100,
    "message": "请先登录"
}
```

> 500 Response

```json
{
    "code": 50000,
    "message": "系统繁忙，请稍后再试"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|none|Inline|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|业务状态码|none|
|» message|string|true|none|提示信息|none|
|» data|object|true|none|数据体|none|
|»» period|string|true|none|统计周期|none|
|»» totalDoses|integer|true|none|计划总次数|该周期内应服药总次数|
|»» confirmedDoses|integer|true|none|已确认次数|用户实际打卡次数|
|»» missedDoses|integer|true|none|漏服次数|超时未处理的提醒数|
|»» adherenceRate|number|true|none|依从率|百分比，保留一位小数|

#### 枚举值

|属性|值|
|---|---|
|period|WEEK|
|period|MONTH|

状态码 **400**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **401**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

# 药品搜索

## GET 药品模糊搜索

GET /api/v1/drugs/search

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|keyword|query|string| 是 ||药品名称，支持中英文模糊匹配|
|page|query|integer| 否 ||页码|
|size|query|integer| 否 ||每页条数|

> 返回示例

> 200 Response

```json
{
    "code": 0,
    "message": "搜索成功",
    "data": {
        "total": 2,
        "page": 1,
        "size": 20,
        "results": [
            {
                "drugId": 1,
                "drugName": "阿莫西林胶囊",
                "genericName": "阿莫西林",
                "category": "抗生素",
                "summary": "适用于敏感菌引起的感染"
            },
            {
                "drugId": 2,
                "drugName": "阿莫西林克拉维酸钾片",
                "genericName": "阿莫西林克拉维酸钾",
                "category": "抗生素",
                "summary": "复方制剂，用于产酶耐药菌感染"
            }
        ]
    }
}
```

> 400 Response

```json
{
    "code": 40001,
    "message": "搜索关键词不能为空"
}

{
    "code": 40002,
    "message": "搜索内容包含不规范字符"
}
```

> 500 Response

```json
{
    "code": 50000,
    "message": "系统繁忙，请稍后再试"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|业务状态码|none|
|» message|string|true|none|提示信息|none|
|» data|object|true|none|数据体|none|
|»» total|integer|true|none|总记录数|none|
|»» page|integer|true|none|当前页码|none|
|»» size|integer|true|none|每页条数|none|
|»» results|[object]|true|none|搜索结果列表|none|
|»»» drugId|integer|true|none|药品ID|none|
|»»» drugName|string|true|none|药品名称|如“阿莫西林胶囊”|
|»»» genericName|string|true|none|通用名|如“阿莫西林”|
|»»» category|string|false|none|药品类别|如“抗生素”|
|»»» summary|string|false|none|功效摘要|简要描述|

状态码 **400**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|例如关键词为空、含特殊字符|
|» message|string|true|none|错误描述|如“搜索关键词不能为空”或“搜索内容包含不规范字符”|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

## GET 药品详情

GET /api/v1/drugs/{drugId}

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|drugId|path|integer| 是 ||药品ID|

> 返回示例

> 200 Response

```json
{
    "code": 0,
    "message": "获取成功",
    "data": {
        "drugId": 1,
        "drugName": "阿莫西林胶囊",
        "genericName": "阿莫西林",
        "category": "抗生素",
        "indications": "适用于敏感菌（不产β内酰胺酶菌株）引起的下列感染：呼吸道感染、泌尿生殖道感染、皮肤软组织感染等。",
        "dosage": "成人一次0.5g，每6～8小时1次；小儿一日剂量按体重20～40mg/kg，每8小时1次。",
        "sideEffects": "恶心、呕吐、腹泻及假膜性肠炎等胃肠道反应；皮疹、药物热和哮喘等过敏反应。",
        "contraindications": "对青霉素类药物过敏者禁用；传染性单核细胞增多症患者禁用。",
        "precautions": "肾功能不全者需根据肌酐清除率调整剂量；有哮喘、枯草热等过敏性疾病史者慎用。",
        "storage": "密封，在阴凉干燥处保存（不超过20℃）。"
    }
}
```

> 404 Response

```json
{
    "code": 40403,
    "message": "药品不存在"
}
```

> 500 Response

```json
{
    "code": 50000,
    "message": "系统繁忙，请稍后再试"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|业务状态码|none|
|» message|string|true|none|提示信息|none|
|» data|object|true|none|数据体|none|
|»» drugId|integer|true|none|药品ID|none|
|»» drugName|string|true|none|药品名称|none|
|»» genericName|string|true|none|通用名|none|
|»» category|string|false|none|药品类别|如“抗生素”|
|»» indications|string|false|none|适应症|none|
|»» dosage|string|false|none|用法用量|none|
|»» sideEffects|string|false|none|不良反应|none|
|»» contraindications|string|false|none|禁忌|none|
|»» precautions|string|false|none|注意事项|none|
|»» storage|string|false|none|存储条件|none|

状态码 **404**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

## POST 收藏药品到药箱

POST /api/v1/drugs/{drugId}/collect

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|drugId|path|integer| 是 ||药品ID|

> 返回示例

> 200 Response

```json
{
    "code": 0,
    "message": "已收藏",
    "data": null
}
```

> 404 Response

```json
{
    "code": 40403,
    "message": "药品不存在"
}
```

> 409 Response

```json
{
    "code": 40905,
    "message": "该药品已在药箱中"
}
```

> 500 Response

```json
{
    "code": 50000,
    "message": "系统繁忙，请稍后再试"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|none|Inline|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|业务状态码|none|
|» message|string|true|none|提示信息|none|
|» data|null|true|none|数据体|none|

状态码 **404**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **409**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

# 就诊导航

## GET 推荐医院列表

GET /api/v1/hospitals/recommend

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|longitude|query|number| 是 ||经度。用户当前定位经度|
|latitude|query|number| 是 ||纬度。用户当前定位纬度|
|department|query|string| 否 ||筛选科室。如“内科”、“急诊科”，不传则综合推荐|
|distance|query|integer| 否 ||搜索半径（公里）。超出范围的不返回|
|page|query|integer| 否 ||页码|
|size|query|integer| 否 ||每页条数。最大 20 避免接口过重|

> 返回示例

> 200 Response

```json
{
    "code": 0,
    "message": "推荐成功",
    "data": {
        "total": 3,
        "page": 1,
        "size": 10,
        "list": [
            {
                "hospitalId": 1,
                "name": "广州市第一人民医院",
                "address": "广州市越秀区盘福路1号",
                "distance": 2.8,
                "rating": 4.5,
                "mainDepartments": [
                    "内科",
                    "急诊科",
                    "心血管内科"
                ],
                "estimatedTime": 15,
                "phone": "020-81048888"
            },
            {
                "hospitalId": 2,
                "name": "广东省人民医院",
                "address": "广州市越秀区中山二路106号",
                "distance": 3.5,
                "rating": 4.7,
                "mainDepartments": [
                    "内科",
                    "呼吸内科",
                    "内分泌科"
                ],
                "estimatedTime": 20,
                "phone": "020-83827812"
            }
        ]
    }
}
```

> 400 Response

```json
{
    "code": 40005,
    "message": "定位信息不完整，请授权位置权限"
}
```

> 500 Response

```json
{
    "code": 50000,
    "message": "系统繁忙，请稍后再试"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|业务状态码|none|
|» message|string|true|none|提示信息|none|
|» data|object|true|none|数据体|none|
|»» total|integer|true|none|总记录数|none|
|»» page|integer|true|none|当前页码|none|
|»» size|integer|true|none|每页条数|none|
|»» list|[object]|true|none|推荐医院列表|none|
|»»» hospitalId|integer|true|none|医院ID|none|
|»»» name|string|true|none|医院名称|全称|
|»»» address|string|true|none|地址|none|
|»»» distance|number|true|none|距离（公里）|保留一位小数，如 3.2|
|»»» rating|number|true|none|综合评分|保留一位小数|
|»»» mainDepartments|[string]|true|none|强项科室|科室名称列表|
|»»» estimatedTime|integer|true|none|预计到达时间（分钟）|驾车预计耗时|
|»»» phone|string|false|none|联系电话|可能为空|

状态码 **400**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|40005 经纬度缺失或格式错误；40006 距离参数无效|
|» message|string|true|none|错误描述|如“定位信息不完整，请授权位置权限”|

#### 枚举值

|属性|值|
|---|---|
|code|40005|
|code|40006|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

## GET 医院详情

GET /api/v1/hospitals/{hospitalsId}

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|hospitalsId|path|integer| 是 ||医院ID|

> 返回示例

> 200 Response

```json
{
    "code": 0,
    "message": "获取成功",
    "data": {
        "hospitalId": 1,
        "name": "广州市第一人民医院",
        "address": "广州市越秀区盘福路1号",
        "phone": "020-81048888",
        "rating": 4.5,
        "introduction": "广州市第一人民医院始建于1899年，是广州市属最大的综合性三级甲等医院。",
        "departments": [
            {
                "departmentName": "内科",
                "description": "内科是本院的重点科室，擅长心血管、呼吸及消化系统疾病诊治。"
            },
            {
                "departmentName": "急诊科",
                "description": "提供24小时急诊服务，配备先进的抢救设备。"
            }
        ]
    }
}
```

> 404 Response

```json
{
    "code": 40404,
    "message": "医院不存在"
}
```

> 500 Response

```json
{
    "code": 50000,
    "message": "系统繁忙，请稍后再试"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|业务状态码|none|
|» message|string|true|none|提示信息|none|
|» data|object|true|none|数据体|none|
|»» hospitalId|integer|true|none|医院ID|none|
|»» name|string|true|none|医院名称|none|
|»» address|string|true|none|地址|none|
|»» phone|string|false|none|联系电话|none|
|»» rating|number|true|none|综合评分|none|
|»» introduction|string|false|none|医院简介|none|
|»» departments|[object]|true|none|科室列表|none|
|»»» departmentName|string|true|none|科室名称|none|
|»»» description|string|false|none|科室简介|none|

状态码 **404**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

## GET 搜索医院

GET /api/v1/hospitals/search

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|keyword|query|string| 是 ||搜索关键词。医院名称或地址|
|page|query|integer| 否 ||页码|
|size|query|integer| 否 ||每页条数|

> 返回示例

> 200 Response

```json
{
    "code": 0,
    "message": "搜索成功",
    "data": {
        "total": 2,
        "page": 1,
        "size": 10,
        "list": [
            {
                "hospitalId": 1,
                "name": "广州市第一人民医院",
                "address": "广州市越秀区盘福路1号",
                "phone": "020-81048888",
                "rating": 4.5,
                "mainDepartments": [
                    "内科",
                    "急诊科",
                    "心血管内科"
                ]
            },
            {
                "hospitalId": 3,
                "name": "广州市番禺区人民医院",
                "address": "广州市番禺区市桥街桥东路93号",
                "phone": "020-84826611",
                "rating": 4.2,
                "mainDepartments": [
                    "内科",
                    "骨科",
                    "妇产科"
                ]
            }
        ]
    }
}
```

> 400 Response

```json
{
    "code": 40001,
    "message": "搜索关键词不能为空"
}

{
    "code": 40002,
    "message": "搜索内容包含不规范字符"
}
```

> 500 Response

```json
{
    "code": 50000,
    "message": "系统繁忙，请稍后再试"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|业务状态码|none|
|» message|string|true|none|提示信息|none|
|» data|object|true|none|数据体|none|
|»» total|integer|true|none|总记录数|none|
|»» page|integer|true|none|当前页码|none|
|»» size|integer|true|none|每页条数|none|
|»» list|[object]|true|none|医院列表|none|
|»»» hospitalId|integer|true|none|医院ID|none|
|»»» name|string|true|none|医院名称|none|
|»»» address|string|true|none|地址|none|
|»»» phone|string|false|none|联系电话|none|
|»»» rating|number|true|none|综合评分|none|
|»»» mainDepartments|[string]|true|none|主要科室|none|

状态码 **400**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|关键词为空或不合规|
|» message|string|true|none|错误描述|搜索关键词不能为空|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

#  科普文章

## GET 推荐文章列表

GET /api/v1/articles/recommend

根据用户画像（年龄、性别、职业、用药记录）返回个性化科普文章列表。未登录用户返回默认热门文章。

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|Authorization|header|string| 否 ||Bearer <token>，未登录用户也能看文章，所以选填。但登录后推荐更精准|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "articles": [
      {
        "articleId": 1,
        "title": "高血压患者冬季用药指南",
        "summary": "冬季气温降低，高血压患者需要注意什么？本文详细介绍了冬季用药的注意事项...",
        "coverImage": "https://cdn.yaozhidao.com/articles/cover-1.png",
        "category": "西药",
        "author": "张医生",
        "publishTime": "2026-07-10 08:00:00",
        "readCount": 1523,
        "likeCount": 89,
        "isLiked": false,
        "isCollected": false
      },
      {
        "articleId": 2,
        "title": "中药调理脾胃的常见方法",
        "summary": "脾胃不和是很多中老年人的常见问题，本文整理了中药调理脾胃的几种常见方法...",
        "coverImage": "https://cdn.yaozhidao.com/articles/cover-2.png",
        "category": "中药",
        "author": "李药师",
        "publishTime": "2026-07-09 10:30:00",
        "readCount": 987,
        "likeCount": 45,
        "isLiked": true,
        "isCollected": false
      }
    ],
    "total": 2
  }
}
```

> 500 Response

```json
{
  "code": 5000,
  "message": "服务器繁忙，请稍后重试",
  "data": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||业务状态码，0 表示成功|
|» message|string|true|none||提示信息|
|» data|object|true|none||文章数据|
|»» articles|[object]|true|none||文章列表，按推荐匹配度降序排列|
|»»» articleId|integer|true|none||文章 ID|
|»»» title|string|true|none||文章标题|
|»»» summary|string|true|none||文章摘要，前 60 个字符|
|»»» coverImage|string|true|none||封面图片 URL|
|»»» category|string|true|none||分类：中药 / 西药 / 养生 / 疾病 / 饮食|
|»»» author|string|true|none||作者或来源|
|»»» publishTime|string|true|none||发布时间，格式 yyyy-MM-dd HH:mm:ss|
|»»» readCount|integer|true|none||阅读量|
|»»» likeCount|integer|true|none||点赞数|
|»»» isLiked|boolean|true|none||当前用户是否已点赞（未登录用户固定为 false）|
|»»» isCollected|boolean|true|none||当前用户是否已收藏（未登录用户固定为 false）|
|»» total|integer|true|none||总文章数|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||none|
|» message|string|true|none||none|
|» data|null|true|none||none|

## GET 文章列表（按分类筛选）

GET /api/v1/articles

按分类获取科普文章列表，支持分页。未登录用户也可调用。

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|category|query|string| 否 ||分类筛选：中药 / 西药 / 养生 / 疾病 / 饮食，不传则返回全部|
|page|query|integer| 否 ||页码，默认 1|
|size|query|integer| 否 ||每页数量，默认 10，最大 20|
|Authorization|header|string| 否 ||Bearer <token>，未登录用户也能看文章|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "articles": [
      {
        "articleId": 3,
        "title": "如何正确服用中药汤剂",
        "summary": "中药汤剂的服用方法直接影响药效，本文介绍了正确的煎煮和服用方法...",
        "coverImage": "https://cdn.yaozhidao.com/articles/cover-3.png",
        "category": "中药",
        "author": "王药师",
        "publishTime": "2026-07-08 14:00:00",
        "readCount": 2341,
        "likeCount": 156,
        "isLiked": false,
        "isCollected": false
      }
    ],
    "total": 1,
    "page": 1,
    "size": 10,
    "totalPages": 1
  }
}
```

> 500 Response

```json
{
  "code": 5000,
  "message": "服务器繁忙，请稍后重试",
  "data": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||业务状态码，0 表示成功|
|» message|string|true|none||提示信息|
|» data|object|true|none||文章数据|
|»» articles|[object]|true|none||文章列表，按发布时间降序排列|
|»»» articleId|integer|false|none||none|
|»»» title|string|false|none||none|
|»»» summary|string|false|none||none|
|»»» coverImage|string|false|none||none|
|»»» category|string|false|none||none|
|»»» author|string|false|none||none|
|»»» publishTime|string|false|none||none|
|»»» readCount|integer|false|none||none|
|»»» likeCount|integer|false|none||none|
|»»» isLiked|boolean|false|none||none|
|»»» isCollected|boolean|false|none||none|
|»» total|integer|true|none||该分类下的总文章数|
|»» page|integer|true|none||当前页码|
|»» size|integer|true|none||每页数量|
|»» totalPages|integer|true|none||总页数|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||none|
|» message|string|true|none||none|
|» data|null|true|none||none|

## GET 文章详情

GET /api/v1/articles/{articleId}

获取科普文章的完整内容，包括正文、作者信息、互动数据等。

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|articleId|path|integer| 是 ||文章 ID|
|Authorization|header|string| 否 ||Bearer <token>，未登录用户也能看文章详情，但无法互动|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "articleId": 1,
    "title": "高血压患者冬季用药指南",
    "content": "冬季气温降低，高血压患者需要注意什么？\n\n一、按时服药\n冬季血压容易波动，患者应严格按照医嘱服药，不可自行增减剂量...\n\n二、注意保暖\n...",
    "coverImage": "https://cdn.yaozhidao.com/articles/cover-1.png",
    "category": "西药",
    "author": "张医生",
    "authorTitle": "三甲医院心血管科主治医师",
    "publishTime": "2026-07-10 08:00:00",
    "readCount": 1523,
    "likeCount": 89,
    "collectCount": 45,
    "shareCount": 12,
    "isLiked": false,
    "isCollected": false,
    "tags": ["高血压", "冬季", "用药安全"],
    "relatedArticles": [
      {
        "articleId": 4,
        "title": "高血压患者夏季注意事项",
        "coverImage": "https://cdn.yaozhidao.com/articles/cover-4.png"
      }
    ]
  }
}
```

> 404 Response

```json
{
  "code": 3001,
  "message": "文章不存在",
  "data": null
}
```

> 500 Response

```json
{
  "code": 5000,
  "message": "服务器繁忙，请稍后重试",
  "data": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||业务状态码，0 表示成功|
|» message|string|true|none||提示信息|
|» data|object|true|none||文章完整数据|
|»» articleId|integer|true|none||文章 ID|
|»» title|string|true|none||文章标题|
|»» content|string|true|none||文章正文，支持 Markdown 或纯文本|
|»» coverImage|string|true|none||封面图片 URL|
|»» category|string|true|none||分类：`中药` / `西药` / `养生` / `疾病` / `饮食`|
|»» author|string|true|none||作者|
|»» authorTitle|string|true|none||作者职称/简介|
|»» publishTime|string|true|none||发布时间|
|»» readCount|integer|true|none||阅读量|
|»» likeCount|integer|true|none||点赞数|
|»» collectCount|integer|true|none||收藏数|
|»» shareCount|integer|true|none||分享数|
|»» isLiked|boolean|true|none||当前用户是否已点赞|
|»» isCollected|boolean|true|none||当前用户是否已收藏|
|»» tags|[string]|true|none||文章标签|
|»» relatedArticles|[object]|true|none||相关文章推荐（最多 3 篇）|
|»»» articleId|integer|false|none||相关文章 ID|
|»»» title|string|false|none||相关文章标题|
|»»» coverImage|string|false|none||相关文章封面图片 URL|

状态码 **404**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||none|
|» message|string|true|none||none|
|» data|null|true|none||none|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||none|
|» message|string|true|none||none|
|» data|null|true|none||none|

## POST 收藏文章

POST /api/v1/articles/{articleId}/collect

用户收藏或取消收藏一篇科普文章。已收藏时调用则取消收藏。

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|articleId|path|integer| 是 ||文章 ID|
|Authorization|header|string| 否 ||Bearer <token>，必须登录才能收藏|

> 返回示例

> 401 Response

```json
{
  "code": 4011,
  "message": "未登录或 Token 为空",
  "data": null
}
```

> 404 Response

```json
{
  "code": 3001,
  "message": "文章不存在",
  "data": null
}
```

> 500 Response

```json
{
  "code": 5000,
  "message": "服务器繁忙，请稍后重试",
  "data": null
}
```

> 200 Response

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "isCollected": true
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|none|Inline|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||业务状态码，0 表示成功|
|» message|string|true|none||已收藏 或 已取消收藏|
|» data|object|true|none||none|
|»» isCollected|boolean|true|none||操作后的收藏状态：true 已收藏 / false 未收藏|

状态码 **401**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||none|
|» message|string|true|none||none|
|» data|null|true|none||none|

状态码 **404**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||none|
|» message|string|true|none||none|
|» data|null|true|none||none|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||none|
|» message|string|true|none||none|
|» data|null|true|none||none|

## POST 点赞文章

POST /api/v1/articles/{articleId}/like

用户点赞或取消点赞一篇科普文章。已点赞时调用则取消点赞。

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|articleId|path|integer| 是 ||文章 ID|
|Authorization|header|string| 否 ||Bearer token，必须登录才能点赞|

> 返回示例

> 401 Response

```json
{
  "code": 4011,
  "message": "未登录或 Token 为空",
  "data": null
}
```

> 404 Response

```json
{
  "code": 3001,
  "message": "文章不存在",
  "data": null
}
```

> 500 Response

```json
{
  "code": 5000,
  "message": "服务器繁忙，请稍后重试",
  "data": null
}
```

> 200 Response

```json
{
  "code": 0,
  "message": "string",
  "data": {
    "isLiked": true,
    "likeCount": 0
  }
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|none|Inline|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||业务状态码，0 表示成功|
|» message|string|true|none||提示信息，如：点赞成功 或 已取消点赞|
|» data|object|true|none||点赞状态数据|
|»» isLiked|boolean|true|none||操作后的点赞状态：true 已点赞 / false 未点赞|
|»» likeCount|integer|true|none||操作后的总点赞数，前端直接更新显示|

状态码 **401**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||none|
|» message|string|true|none||none|
|» data|null|true|none||none|

状态码 **404**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||none|
|» message|string|true|none||none|
|» data|null|true|none||none|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||none|
|» message|string|true|none||none|
|» data|null|true|none||none|

## GET 分享文章

GET /api/v1/articles/{articleId}/share

用户分享文章到微信、QQ 或复制链接。后端记录分享行为，用于优化推荐算法。

> Body 请求参数

```json
{
  "target": "WECHAT"
}
```

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|articleId|path|integer| 是 ||文章 ID，从 URL 路径中获取|
|Authorization|header|string| 否 ||Bearer <token>，未登录用户也可以分享，所以选填|
|body|body|object| 是 ||none|
|» target|body|string| 是 ||分享目标平台：WECHAT 微信 / QQ QQ / COPY_LINK 复制链接|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "message": "分享成功",
  "data": null
}
```

> 400 Response

```json
{
  "code": 4000,
  "message": "不支持的分享平台",
  "data": null
}
```

> 404 Response

```json
{
  "code": 3001,
  "message": "文章不存在",
  "data": null
}
```

> 500 Response

```json
{
  "code": 5000,
  "message": "服务器繁忙，请稍后重试",
  "data": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|none|Inline|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||业务状态码，0 表示成功|
|» message|string|true|none||提示信息|
|» data|null|true|none||无返回数据|

状态码 **400**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||none|
|» message|string|true|none||none|
|» data|null|true|none||none|

状态码 **404**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||none|
|» message|string|true|none||none|
|» data|null|true|none||none|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none||none|
|» message|string|true|none||none|
|» data|null|true|none||none|

# 通用

## GET 健康检查

GET /api/v1/health

检查后端服务是否正常运行。不需要登录，不需要任何参数。返回服务状态和当前时间。

> 返回示例

> 200 Response

```json
{
  "status": "ok",
  "timestamp": "2026-07-14 10:00:00"
}
```

> 500 Response

```json
{
  "status": "error",
  "timestamp": "2026-07-14 10:00:00"
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» status|string|true|none||服务状态，固定为 ok（正常）。如果服务异常，这个接口根本不会返回 200|
|» timestamp|string|true|none||当前服务器时间，格式 yyyy-MM-dd HH:mm:ss。用于前端与服务器时间同步|

#### 枚举值

|属性|值|
|---|---|
|status|ok|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» status|string|true|none||none|
|» timestamp|string|true|none||none|

## POST 用户反馈

POST /api/v1/feedback

> Body 请求参数

```json
{
  "content": "string",
  "contact": "string",
  "images": [
    "string"
  ]
}
```

### 请求参数

|名称|位置|类型|必选|中文名|说明|
|---|---|---|---|---|---|
|body|body|object| 是 ||none|
|» content|body|string| 是 | 反馈内容|用户输入的建议或问题描述|
|» contact|body|string| 否 | 联系方式|选填，用于回复用户|
|» images|body|[string]| 否 | 截图链接|选填，需先上传图片后传入 URL|

> 返回示例

> 200 Response

```json
{
  "code": 0,
  "message": "提交成功",
  "data": null
}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|none|Inline|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|none|Inline|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|none|Inline|

### 返回数据结构

状态码 **200**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|业务状态码|none|
|» message|string|true|none|提示信息|none|
|» data|null|true|none|数据体|none|

状态码 **400**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|40009：反馈内容为空或超长；40010：截图数量超限|
|» message|string|true|none|错误描述|如“反馈内容不能为空”或“最多上传3张截图”|

#### 枚举值

|属性|值|
|---|---|
|code|40009|
|code|40010|

状态码 **401**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

状态码 **500**

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|» code|integer|true|none|错误码|none|
|» message|string|true|none|错误描述|none|

# 数据模型

<h2 id="tocS_DrugInfo">DrugInfo</h2>

<a id="schemadruginfo"></a>
<a id="schema_DrugInfo"></a>
<a id="tocSdruginfo"></a>
<a id="tocsdruginfo"></a>

```json
[
  {
    "drugId": "string",
    "name": "string",
    "genericName": "string",
    "summary": "string",
    "matchScore": 0,
    "category": "string"
  }
]

```

DrugInfo

### 属性

|名称|类型|必选|约束|中文名|说明|
|---|---|---|---|---|---|
|DrugInfo|[object]|false|none|DrugInfo|none|
|drugId|string|true|none||none|
|name|string|true|none||none|
|genericName|string|true|none||none|
|summary|string|true|none||none|
|matchScore|integer|true|none||none|
|category|string|false|none||none|

