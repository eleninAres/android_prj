package com.example.os17demo.os16cases

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback

class Os16CaseFActivity : Os16CaseBaseActivity() {
    private var predictiveBackRegistered = false
    private var lastInterceptAt = 0L
    private var onBackInvokedCallback: OnBackInvokedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBackCallbackForDemo()
    }

    override fun spec(): Os16CaseSpec =
        Os16CaseSpec(
            caseId = "16F",
            titleText = "预测性返回迁移",
            summaryText =
                "该页面已真实接入返回回调：Android 13+ 使用 OnBackInvokedCallback，" +
                    "低版本使用 OnBackPressedDispatcher。手势返回时会先提示，再次返回退出页面。",
            codeSnippet =
                """
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    onBackInvokedDispatcher.registerOnBackInvokedCallback(
                        OnBackInvokedDispatcher.PRIORITY_DEFAULT
                    ) { navigateUp() }
                }

                // 临时兜底（过渡期）：
                // android:enableOnBackInvokedCallback="false"
                """.trimIndent(),
        )

    override fun runCheck(): String {
        val available = runCatching { Class.forName("android.window.OnBackInvokedDispatcher") }.isSuccess
        return "OnBackInvokedDispatcher 可用: $available，callback 已注册: $predictiveBackRegistered。"
    }

    override fun onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedCallback?.let { callback ->
                onBackInvokedDispatcher.unregisterOnBackInvokedCallback(callback)
            }
        }
        onBackInvokedCallback = null
        super.onDestroy()
    }

    private fun registerBackCallbackForDemo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val callback = OnBackInvokedCallback {
                handleBackIntercept("OnBackInvokedCallback")
            }
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                callback,
            )
            onBackInvokedCallback = callback
            predictiveBackRegistered = true
        } else {
            onBackPressedDispatcher.addCallback(
                this,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        handleBackIntercept("OnBackPressedDispatcher")
                    }
                },
            )
            predictiveBackRegistered = true
        }
    }

    private fun handleBackIntercept(source: String) {
        val now = System.currentTimeMillis()
        if (now - lastInterceptAt < 2000) {
            finish()
            return
        }
        lastInterceptAt = now
        Toast.makeText(this, "$source 已接管返回，再返回一次退出", Toast.LENGTH_SHORT).show()
    }
}
