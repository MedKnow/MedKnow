# Android 端

本目录预留给「药知道」的安卓客户端（Kotlin + Jetpack Compose，最低 API 35）。

接口契约以 [`../docs/MedKnow.md`](../docs/MedKnow.md) 为准，后端实现见 [`../backend`](../backend)。

## 联调提示

- 模拟器访问宿主机后端用 `http://10.0.2.2:8090`（后端默认端口 8090，不是 8080）
- 明文 HTTP 需要在 `network_security_config.xml` 里放行
- 完整联调步骤见 [`../backend/安卓端联调说明.md`](../backend/安卓端联调说明.md)

> 代码尚未提交到这里。搭档 clone 仓库后，直接把现有 Android 工程放进本目录即可。
