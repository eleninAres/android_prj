# android_prj

这是一个用于演示 **Android 16 / 17 行为变更适配** 的最小 Android App Demo。

## 目录

- `app/src/main/java/com/example/os17demo/MainActivity.kt`
  - 每个按钮对应一个变更点示例。
- `app/src/main/AndroidManifest.xml`
  - 包含 `USE_LOOPBACK_INTERFACE`、`networkSecurityConfig` 等配置示例。
- `app/src/main/res/xml/network_security_config.xml`
  - `usesCleartextTraffic` 迁移到 Network Security Config 的示例。

## 如何运行

1. 用 Android Studio 打开项目根目录。
2. 安装 Android 16/17 对应 SDK（并根据本地环境调整 `compileSdk/targetSdk`）。
3. 同步 Gradle 并运行 `app` 模块。

> 说明：这是行为变更适配示例工程，重点在“怎么改”，不是完整业务 App。

## 已覆盖的 Android 17 变更点（按钮示例）

1. MessageQueue 无锁实现（反射 `mMessages` 风险）
2. `static final` 字段不可修改
3. 复杂 IME 实体键盘输入的无障碍支持（API 可用性与适配建议）
4. BAL 强化（后台启动 Activity 限制）
5. Loopback 权限 `USE_LOOPBACK_INTERFACE`
6. 默认启用 CT（证书透明度）
7. 更安全的原生 DCL（`System.load` 文件只读）
8. 大屏设备忽略方向/比例等限制（`sw>=600dp`）
9. usesCleartextTraffic 弃用计划（迁移到 Network Security Config）
10. 显式 URI 授权（提前适配未来变更）
11. Keystore key 数量限制处理
12. 旋转后 IME 可见性恢复
13. Pointer capture 触控板相对事件
14. 后台音频强化（AudioFocus 失败兜底）

## 与官方文档覆盖对照

### behavior-changes-all
- [x] usesCleartextTraffic 弃用计划
- [x] 限制隐式 URI 授权
- [x] 每个应用的密钥库限制
- [x] 旋转后恢复默认 IME 可见性
- [x] 指针捕获期间触控板默认相对事件
- [x] 后台音频强化

### behavior-changes-17（targetSdk=17）
- [x] MessageQueue 的新无锁实现
- [x] 静态 final 字段现在不可修改
- [x] 复杂 IME 实体键盘输入的无障碍支持
- [x] 活动安全性（BAL 强化）
- [x] 本地主机保护
- [x] 默认启用 CT
- [x] 更安全的原生 DCL
- [x] 大屏忽略方向/尺寸/宽高比限制

## Android 16 变更 Demo（新增）

### 已新增按钮示例（16A ~ 16M）

- 16A~16M 已拆分为 **独立 Activity**
- 每个 Activity 页面都包含：
  - 变更说明
  - 可复制的“示例代码片段”
  - “运行检查”按钮与结果输出区

对应代码目录：
- `app/src/main/java/com/example/os17demo/os16cases/`
  - `Os16CaseBaseActivity.kt`
  - `Os16CaseAActivity.kt` ~ `Os16CaseMActivity.kt`

### behavior-changes-all（Android 16）
- [x] JobScheduler 配额优化（16A）
- [x] 已放弃空作业 stop reason（16A）
- [x] 有序广播优先级范围不再全局（16B）
- [x] ART 内部变更（16C）
- [x] 16KB 页面大小兼容模式（16D）
- [x] 弃用干扰性无障碍公告（16E）
- [x] 三按钮导航预测性返回（16F）
- [x] 自动带主题应用图标（16H）
- [x] 虚拟设备所有者替换项（16M）
- [x] Intent 重定向攻击防护增强（16I）
- [x] 配套设备发现超时通知行为变更（16J）
- [x] 债券/绑定丢失处理改进（16J）

### behavior-changes-16（targetSdk 对应 Android 16 / API 36）
- [x] edge-to-edge 选择停用功能停用（16G）
- [x] 预测性返回迁移或临时停用（16F）
- [x] elegantTextHeight 弃用停用（16H）
- [x] scheduleAtFixedRate 行为优化（16A）
- [x] 自适应布局（16G）
- [x] 健康与健身权限细粒度化（16K）
- [x] 蓝牙绑定丢失/加密变更新 intent（16J）
- [x] CompanionDeviceManager.removeBond API（16J）
- [x] MediaStore 版本锁定（16I）
- [x] 更安全的 intent（16I）
- [x] GPU 系统调用过滤（16I）
- [x] 本地网络权限（16K）
- [x] 应用拥有的照片（16L）
