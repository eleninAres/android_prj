package com.example.os17demo

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
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
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

        title = "Android 17 适配示例"

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
                "这个 Demo 把 Android 17 的关键行为变更做成按钮示例。" +
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
        addDemoButton(container, "3) BAL 强化（后台启动 Activity）") { demoBal() }
        addDemoButton(container, "4) Loopback 权限 USE_LOOPBACK_INTERFACE") { demoLoopbackPermission() }
        addDemoButton(container, "5) 默认启用 CT（证书透明度）") { demoCtDefault() }
        addDemoButton(container, "6) 原生 DCL 更严格（System.load）") { demoSaferNativeDcl() }
        addDemoButton(container, "7) 大屏约束被忽略（sw>=600dp）") { demoLargeScreenBehavior() }
        addDemoButton(container, "8) URI 显式授权示例（面向 Android 18 提前适配）") { demoUriGrant() }
        addDemoButton(container, "9) Keystore key 数量限制") { demoKeystoreLimitHandling() }
        addDemoButton(container, "10) IME 旋转后可见性恢复") { demoImeVisibility() }
        addDemoButton(container, "11) Pointer Capture 触控板相对事件") { demoPointerCapture() }
        addDemoButton(container, "12) 后台音频强化") { demoBackgroundAudioHardening() }
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
        appendLine("Demo 已启动。先点 1~6 体验 targetSdk=17 重点变更。")
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
