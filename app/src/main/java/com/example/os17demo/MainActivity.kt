package com.example.os17demo

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.NetworkSecurityPolicy
import android.system.Os
import android.system.OsConstants
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.crypto.KeyGenerator

class MainActivity : AppCompatActivity() {
    private lateinit var outputView: TextView
    private lateinit var imeDemoInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "Android 16/17 适配示例"

        val root = ScrollView(this)
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }
        root.addView(
            container,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ),
        )

        val intro = TextView(this).apply {
            text =
                "这个 Demo 把 Android 16/17 的关键行为变更做成按钮示例。" +
                    "点击后会在下方输出适配建议、示例代码或运行结果。"
        }
        container.addView(intro)

        imeDemoInput = EditText(this).apply {
            hint = "IME 示例输入框（旋转后可重新显式拉起）"
        }
        container.addView(
            imeDemoInput,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ),
        )

        addDemoButton(container, "1) MessageQueue 无锁实现") { demoMessageQueue() }
        addDemoButton(container, "2) static final 不可修改") { demoStaticFinal() }
        addDemoButton(container, "3) 复杂 IME 实体键盘输入的无障碍支持") { demoA11yImePhysicalKeyboard() }
        addDemoButton(container, "4) BAL 强化（后台启动 Activity）") { demoBal() }
        addDemoButton(container, "5) Loopback 权限 USE_LOOPBACK_INTERFACE") { demoLoopbackPermission() }
        addDemoButton(container, "6) 默认启用 CT（证书透明度）") { demoCtDefault() }
        addDemoButton(container, "7) 原生 DCL 更严格（System.load）") { demoSaferNativeDcl() }
        addDemoButton(container, "8) 大屏约束被忽略（sw>=600dp）") { demoLargeScreenBehavior() }
        addDemoButton(container, "9) usesCleartextTraffic 弃用计划") { demoUsesCleartextDeprecation() }
        addDemoButton(container, "10) URI 显式授权示例（面向 Android 18 提前适配）") { demoUriGrant() }
        addDemoButton(container, "11) Keystore key 数量限制") { demoKeystoreLimitHandling() }
        addDemoButton(container, "12) IME 旋转后可见性恢复") { demoImeVisibility() }
        addDemoButton(container, "13) Pointer Capture 触控板相对事件") { demoPointerCapture() }
        addDemoButton(container, "14) 后台音频强化") { demoBackgroundAudioHardening() }

        addSectionTitle(container, "Android 16 变更示例")
        addDemoButton(container, "16A) Job/调度行为变更（配额+空作业+固定频率）") { demo16JobAndSchedulerChanges() }
        addDemoButton(container, "16B) 有序广播优先级范围不再全局") { demo16OrderedBroadcastPriority() }
        addDemoButton(container, "16C) ART 内部变更与非 SDK 访问治理") { demo16ArtChanges() }
        addDemoButton(container, "16D) 16KB 页面大小兼容模式") { demo16PageSizeCompatMode() }
        addDemoButton(container, "16E) 无障碍公告弃用（announceForAccessibility）") { demo16DisruptiveA11y() }
        addDemoButton(container, "16F) 预测性返回/三按钮返回迁移") { demo16PredictiveBackMigration() }
        addDemoButton(container, "16G) 无边框与大屏自适应布局") { demo16EdgeToEdgeAndAdaptiveLayout() }
        addDemoButton(container, "16H) themed icon 与 elegantTextHeight 变更") { demo16ThemedIconAndElegantText() }
        addDemoButton(container, "16I) Intent 安全、MediaStore 版本锁定、GPU 过滤") { demo16SecurityIntentsMediaGpu() }
        addDemoButton(container, "16J) 蓝牙绑定丢失、超时与 removeBond API") { demo16BluetoothAndCompanionChanges() }
        addDemoButton(container, "16K) 健康权限与本地网络权限") { demo16HealthAndLocalNetworkPermissions() }
        addDemoButton(container, "16L) 应用拥有的照片（受限媒体访问）") { demo16OwnedPhotos() }
        addDemoButton(container, "16M) 虚拟设备所有者替换项") { demo16VirtualDeviceOwnerOverrides() }
        addDemoButton(container, "清空输出") { outputView.text = "" }

        outputView = TextView(this).apply {
            setTextIsSelectable(true)
        }
        container.addView(
            outputView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ),
        )

        setContentView(root)
        appendLine("Demo 已启动。上半部分是 Android 17，下半部分是 Android 16 变更示例。")
    }

    private fun addDemoButton(container: LinearLayout, title: String, action: () -> Unit) {
        val button = Button(this).apply {
            text = title
            setOnClickListener { action() }
        }
        container.addView(
            button,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ),
        )
    }

    private fun addSectionTitle(container: LinearLayout, text: String) {
        val section = TextView(this).apply {
            this.text = "----- $text -----"
            textSize = 16f
        }
        container.addView(section)
    }

    private fun demoMessageQueue() {
        val queue = Looper.getMainLooper().queue
        val reflectionResult = runCatching {
            val field = queue.javaClass.getDeclaredField("mMessages")
            field.isAccessible = true
            field.get(queue)
        }
        appendLine(
            buildString {
                append("MessageQueue 私有字段反射结果: ${reflectionResult.getOrNull()}\n")
                append("在 Android 17 的无锁实现中，mMessages 可能恒为 null，不能再用于判断队列状态。\n")
                append("适配建议：删除反射依赖；测试框架升级到 Espresso >= 3.7.0、Robolectric >= 4.17。")
            },
        )
    }

    private fun demoStaticFinal() {
        val result = runCatching {
            val field = FinalFieldHolder::class.java.getDeclaredField("IMMUTABLE")
            field.isAccessible = true
            field.set(null, "MUTATED")
            "反射修改成功（旧行为，Android 17 目标下不应依赖）"
        }.getOrElse { throwable ->
            "修改失败: ${throwable::class.java.simpleName} - ${throwable.message}"
        }
        appendLine(
            buildString {
                append("尝试反射修改 static final 字段结果：$result\n")
                append("Android 17 target 下应视为非法，JNI 修改 static final 也会导致崩溃。")
            },
        )
    }

    private fun demoBal() {
        val options = ActivityOptions.makeBasic()
        val result = runCatching {
            val modeField =
                ActivityOptions::class.java.getField(
                    "MODE_BACKGROUND_ACTIVITY_START_ALLOW_IF_VISIBLE",
                )
            val setter =
                ActivityOptions::class.java.getMethod(
                    "setPendingIntentBackgroundActivityStartMode",
                    Int::class.javaPrimitiveType,
                )
            setter.invoke(options, modeField.getInt(null))
            "已通过反射调用细粒度 BAL 模式（ALLOW_IF_VISIBLE）"
        }.getOrElse { throwable ->
            "当前平台或 SDK 不支持该 API: ${throwable::class.java.simpleName}"
        }

        appendLine(
            buildString {
                append("$result\n")
                append("适配要点：弃用旧 MODE_BACKGROUND_ACTIVITY_START_ALLOWED；")
                append("PendingIntent/IntentSender 拉起页面时改为更小授权面。")
            },
        )
    }

    private fun demoA11yImePhysicalKeyboard() {
        val textAttributeClass = runCatching {
            Class.forName("android.view.inputmethod.TextAttribute")
        }.getOrNull()
        val builderMethod = runCatching {
            Class.forName("android.view.inputmethod.TextAttribute\$Builder")
                .getMethod("setTextSuggestionSelected", Boolean::class.javaPrimitiveType)
        }.getOrNull()
        val textChangeTypesMethod = runCatching {
            Class.forName("android.view.accessibility.AccessibilityEvent")
                .getMethod("setTextChangeTypes", Int::class.javaPrimitiveType)
        }.getOrNull()

        appendLine(
            buildString {
                append("TextAttribute 类可用: ${textAttributeClass != null}\n")
                append("TextAttribute.Builder.setTextSuggestionSelected 可用: ${builderMethod != null}\n")
                append("AccessibilityEvent.setTextChangeTypes 可用: ${textChangeTypesMethod != null}\n")
                append("适配建议：输入法应用与无障碍服务应读取/上报文本变更类型；")
                append("普通应用优先使用标准 TextView/EditText 组件以自动获得兼容性。")
            },
        )
    }

    private fun demoLoopbackPermission() {
        val permission = "android.permission.USE_LOOPBACK_INTERFACE"
        val granted = packageManager.checkPermission(permission, packageName) == PackageManager.PERMISSION_GRANTED
        appendLine(
            buildString {
                append("当前应用 $permission 授权状态: $granted\n")
                append("Android 17 target 下，跨应用 127.0.0.1/::1 通信需要通信双方都声明该权限。")
            },
        )
    }

    private fun demoCtDefault() {
        appendLine(
            "Android 17 target 下默认启用 CT（证书透明度）。\n" +
                "落地建议：检查线上域名证书链 CT 合规，重点关注自建证书/代理链路。",
        )
    }

    private fun demoSaferNativeDcl() {
        val workDir = File(codeCacheDir, "native_dcl").apply { mkdirs() }
        val soFile = File(workDir, "libdemo.so")
        if (!soFile.exists()) {
            soFile.writeBytes(byteArrayOf(0x7F, 0x45, 0x4C, 0x46))
        }
        soFile.setWritable(true, true)
        val writable = soFile.canWrite()
        val loadWhileWritable = runCatching {
            System.load(soFile.absolutePath)
            "加载成功（仅演示，不应依赖）"
        }.getOrElse { "${it::class.java.simpleName}: ${it.message}" }

        soFile.setReadOnly()
        val readOnly = !soFile.canWrite()

        appendLine(
            buildString {
                append("native 文件可写状态: $writable, 加载结果: $loadWhileWritable\n")
                append("调用 setReadOnly() 后只读状态: $readOnly\n")
                append("Android 17 target 下，System.load 的文件应保证只读。")
            },
        )
    }

    private fun demoLargeScreenBehavior() {
        val swDp = resources.configuration.smallestScreenWidthDp
        appendLine(
            buildString {
                append("当前设备 smallestScreenWidthDp = $swDp\n")
                append("当 sw>=600dp 且 target Android 17 时，方向/尺寸/宽高比限制可能被系统忽略。\n")
                append("请改为响应式布局，不依赖锁定方向与比例。")
            },
        )
    }

    private fun demoUsesCleartextDeprecation() {
        val policy = NetworkSecurityPolicy.getInstance()
        val appPermitted = policy.isCleartextTrafficPermitted
        val examplePermitted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            policy.isCleartextTrafficPermitted("example.com")
        } else {
            appPermitted
        }
        appendLine(
            buildString {
                append("全局明文流量允许: $appPermitted\n")
                append("example.com 明文流量允许: $examplePermitted\n")
                append("适配建议：逐步弃用 usesCleartextTraffic，改用 network_security_config 按域名精细配置。")
            },
        )
    }

    private fun demoUriGrant() {
        val uri = Uri.parse("content://$packageName.demo.provider/sample.jpg")
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val targets =
            packageManager.queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY,
            )
        targets.forEach { info ->
            grantUriPermission(
                info.activityInfo.packageName,
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
        }
        appendLine(
            "已演示显式 grantUriPermission。建议现在就改显式授权，避免未来版本移除隐式授权后的兼容问题。",
        )
    }

    private fun demoKeystoreLimitHandling() {
        val alias = "os17_demo_key_${System.currentTimeMillis()}"
        val result = runCatching {
            val keyGenerator =
                KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore",
                )
            keyGenerator.init(
                KeyGenParameterSpec.Builder(
                    alias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build(),
            )
            keyGenerator.generateKey()
            "成功创建 key alias=$alias"
        }.getOrElse { throwable ->
            val numericCode = runCatching {
                throwable.javaClass.getMethod("getNumericErrorCode").invoke(throwable)
            }.getOrNull()
            "创建失败: ${throwable::class.java.simpleName}, numericErrorCode=$numericCode"
        }
        appendLine(
            "$result\nAndroid 17 起非系统应用有 keystore key 数量上限，需做好异常兜底与回收策略。",
        )
    }

    private fun demoImeVisibility() {
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE or
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE,
        )
        imeDemoInput.requestFocus()
        val imm = getSystemService(InputMethodManager::class.java)
        imm.showSoftInput(imeDemoInput, InputMethodManager.SHOW_IMPLICIT)
        appendLine("已显式请求显示软键盘。适配建议：配置变化后在 onCreate/onConfigurationChanged 主动恢复 IME。")
    }

    private fun demoPointerCapture() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            appendLine("API < 26 不支持 pointer capture。")
            return
        }
        val result = runCatching {
            val method = View::class.java.getMethod(
                "requestPointerCapture",
                Int::class.javaPrimitiveType,
            )
            val mode = View::class.java.getField("POINTER_CAPTURE_MODE_RELATIVE").getInt(null)
            method.invoke(window.decorView, mode)
            "调用 requestPointerCapture(int) 成功，使用相对事件模式。"
        }.getOrElse {
            window.decorView.requestPointerCapture()
            "仅调用旧版 requestPointerCapture()。"
        }
        appendLine("$result\nAndroid 17 默认会把触控板手势转换为相对事件。")
    }

    private fun demoBackgroundAudioHardening() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            appendLine("API < 26 跳过 AudioFocusRequest 示例。")
            return
        }

        val audioManager = getSystemService(AudioManager::class.java)
        val request =
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                )
                .setOnAudioFocusChangeListener { }
                .build()
        val focusResult = audioManager.requestAudioFocus(request)
        audioManager.abandonAudioFocusRequest(request)

        appendLine(
            "AudioFocus 请求结果: $focusResult\n" +
                "Android 17 对后台音频调用更严格，后台请求可能失败或静默不生效，需前台可见态触发。",
        )
    }

    private fun demo16JobAndSchedulerChanges() {
        appendLine(
            buildString {
                append("Android 16 调度相关变更:\n")
                append("1) JobScheduler 配额更严格，前台服务并行执行作业也会计入配额。\n")
                append("2) 空作业被放弃时，会出现明确 stop reason，需在 WorkInfo.getStopReason()/JobParameters.getStopReason() 里兜底。\n")
                append("3) target 16 后 scheduleAtFixedRate 补偿执行最多只会立即补 1 次。\n")
                append("示例修改：把用户主动触发的上传下载从普通 Job 切到 user-initiated data transfer job，")
                append("并在 stop reason=timeout/abandoned 时做幂等重试。")
            },
        )
    }

    private fun demo16OrderedBroadcastPriority() {
        appendLine(
            "Android 16 起有序广播优先级不再是跨进程全局排序。\n" +
                "示例修改：不要依赖“高优先级抢占”业务链路，改为显式组件/前台服务/应用内事件总线。",
        )
    }

    private fun demo16ArtChanges() {
        val strictModeMethod = runCatching {
            Class.forName("android.os.StrictMode\$VmPolicy\$Builder")
                .getMethod("detectNonSdkApiUsage")
        }.isSuccess
        appendLine(
            buildString {
                append("ART 内部变更：依赖 ART/非 SDK 内部结构的代码风险升高。\n")
                append("detectNonSdkApiUsage 可用: $strictModeMethod\n")
                append("示例修改：启用 StrictMode 非 SDK 检测 + 升级三方 SDK，移除反射调用内部 API。")
            },
        )
    }

    private fun demo16PageSizeCompatMode() {
        val pageSize = runCatching { Os.sysconf(OsConstants._SC_PAGESIZE) }.getOrNull()
        appendLine(
            buildString {
                append("当前系统页大小: ${pageSize ?: "unknown"} bytes\n")
                append("Android 16 引入 16KB 页大小兼容模式。\n")
                append("示例修改：检查 native so 是否按 16KB 对齐构建，避免仅假设 4KB 页大小。")
            },
        )
    }

    private fun demo16DisruptiveA11y() {
        appendLine(
            "Android 16 废弃干扰性无障碍公告（announceForAccessibility/TYPE_ANNOUNCEMENT）。\n" +
                "示例修改：关键 UI 变化改用 setAccessibilityPaneTitle / LiveRegion / 语义化节点更新。",
        )
    }

    private fun demo16PredictiveBackMigration() {
        val onBackInvokedAvailable = runCatching {
            Class.forName("android.window.OnBackInvokedDispatcher")
        }.isSuccess
        appendLine(
            buildString {
                append("OnBackInvokedDispatcher 可用: $onBackInvokedAvailable\n")
                append("Android 16 target 下系统默认启用预测性返回，onBackPressed/KEYCODE_BACK 依赖会失效。\n")
                append("示例修改：迁移到 OnBackInvokedCallback/AndroidX 返回导航；")
                append("仅作为临时过渡时在 manifest 设置 enableOnBackInvokedCallback=false。")
            },
        )
    }

    private fun demo16EdgeToEdgeAndAdaptiveLayout() {
        val swDp = resources.configuration.smallestScreenWidthDp
        val attrId = resources.getIdentifier("windowOptOutEdgeToEdgeEnforcement", "attr", "android")
        appendLine(
            buildString {
                append("smallestScreenWidthDp=$swDp\n")
                append("windowOptOutEdgeToEdgeEnforcement attr id=$attrId\n")
                append("Android 16 target 下 edge-to-edge 选择停用即将失效，且大屏不再尊重方向/宽高比限制。\n")
                append("示例修改：统一使用 WindowInsets + 响应式布局（折叠屏/平板/桌面窗口）。")
            },
        )
    }

    private fun demo16ThemedIconAndElegantText() {
        @Suppress("DEPRECATION")
        val elegantNow = imeDemoInput.isElegantTextHeight
        appendLine(
            buildString {
                append("当前 TextView elegantTextHeight=$elegantNow\n")
                append("Android 16 弃用并停用 elegantTextHeight 控制路径；请验证多语言字体渲染。\n")
                append("此外系统会自动主题化应用图标，建议提供 monochrome layer 以获得稳定视觉。")
            },
        )
    }

    private fun demo16SecurityIntentsMediaGpu() {
        val mediaVersion = runCatching { MediaStore.getVersion(this) }.getOrNull()
        appendLine(
            buildString {
                append("MediaStore.getVersion()=${mediaVersion ?: "unavailable"}\n")
                append("Android 16 安全相关：\n")
                append("- 更强 Intent 重定向防护（默认强化）\n")
                append("- 更安全的 intent（接收方可在清单选择更严格解析）\n")
                append("- MediaStore 版本变为按应用唯一，避免指纹识别\n")
                append("- Mali GPU 某些 ioctl 在正式版被过滤\n")
                append("示例修改：为跨应用 intent 全量做 action/data/category 校验，避免隐式匹配假设。")
            },
        )
    }

    private fun demo16BluetoothAndCompanionChanges() {
        val keyMissingIntent = "android.bluetooth.device.action.KEY_MISSING"
        val encryptionChangeIntent = "android.bluetooth.device.action.ENCRYPTION_CHANGE"
        val removeBondMethod = runCatching {
            Class.forName("android.companion.CompanionDeviceManager")
                .methods
                .any { it.name == "removeBond" }
        }.getOrDefault(false)
        appendLine(
            buildString {
                append("ACTION_KEY_MISSING=$keyMissingIntent\n")
                append("ACTION_ENCRYPTION_CHANGE=$encryptionChangeIntent\n")
                append("CompanionDeviceManager.removeBond 可用: $removeBondMethod\n")
                append("Android 16 对发现超时与绑定丢失处理策略已调整，建议应用以系统对话框结果为准，")
                append("并监听 ACTION_BOND_STATE_CHANGED 做状态同步。")
            },
        )
    }

    private fun demo16HealthAndLocalNetworkPermissions() {
        val bodySensorsGranted =
            packageManager.checkPermission(Manifest.permission.BODY_SENSORS, packageName) == PackageManager.PERMISSION_GRANTED
        val localNetworkPermission = "android.permission.LOCAL_NETWORK"
        val localNetworkGranted =
            packageManager.checkPermission(localNetworkPermission, packageName) == PackageManager.PERMISSION_GRANTED
        appendLine(
            buildString {
                append("BODY_SENSORS 已授权: $bodySensorsGranted\n")
                append("LOCAL_NETWORK 已授权: $localNetworkGranted\n")
                append("Android 16 target 下健康权限逐步细粒度化（android.permission.health.*）；")
                append("本地网络访问进入运行时授权路线，需处理拒绝/撤销分支。")
            },
        )
    }

    private fun demo16OwnedPhotos() {
        val selectedMediaPerm = "android.permission.READ_MEDIA_VISUAL_USER_SELECTED"
        val granted =
            packageManager.checkPermission(selectedMediaPerm, packageName) == PackageManager.PERMISSION_GRANTED
        appendLine(
            buildString {
                append("READ_MEDIA_VISUAL_USER_SELECTED 已授权: $granted\n")
                append("Android 16 target 下，用户选择“仅限部分照片/视频”时，应用拥有的媒体会在选择器中预选。\n")
                append("示例修改：每次进入媒体流程都基于最新授权集合读取，不缓存长期可见性。")
            },
        )
    }

    private fun demo16VirtualDeviceOwnerOverrides() {
        appendLine(
            "Android 16 在虚拟设备所有者投屏场景引入额外系统行为替换。\n" +
                "示例修改：多显示设备场景避免硬编码输入/方向假设，按 displayId 和窗口上下文动态适配。",
        )
    }

    private fun appendLine(message: String) {
        val line = "[${now()}] $message\n\n"
        outputView.append(line)
        Log.i(TAG, message)
    }

    private fun now(): String = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    companion object {
        private const val TAG = "Os17Demo"
    }
}
