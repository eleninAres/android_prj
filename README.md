# android_prj

这是一个用于演示 **Android 17 行为变更适配** 的最小 Android App Demo。

## 目录

- `app/src/main/java/com/example/os17demo/MainActivity.kt`
  - 每个按钮对应一个变更点示例。
- `app/src/main/AndroidManifest.xml`
  - 包含 `USE_LOOPBACK_INTERFACE`、`networkSecurityConfig` 等配置示例。
- `app/src/main/res/xml/network_security_config.xml`
  - `usesCleartextTraffic` 迁移到 Network Security Config 的示例。

## 如何运行

1. 用 Android Studio 打开项目根目录。
2. 安装 Android 17 对应 SDK（并根据本地环境调整 `compileSdk/targetSdk`）。
3. 同步 Gradle 并运行 `app` 模块。

> 说明：这是行为变更适配示例工程，重点在“怎么改”，不是完整业务 App。

## 已覆盖的 Android 17 变更点（按钮示例）

1. MessageQueue 无锁实现（反射 `mMessages` 风险）
2. `static final` 字段不可修改
3. BAL 强化（后台启动 Activity 限制）
4. Loopback 权限 `USE_LOOPBACK_INTERFACE`
5. 默认启用 CT（证书透明度）
6. 更安全的原生 DCL（`System.load` 文件只读）
7. 大屏设备忽略方向/比例等限制（`sw>=600dp`）
8. 显式 URI 授权（提前适配未来变更）
9. Keystore key 数量限制处理
10. 旋转后 IME 可见性恢复
11. Pointer capture 触控板相对事件
12. 后台音频强化（AudioFocus 失败兜底）
