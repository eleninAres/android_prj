package com.example.os17demo.os16cases

class Os16CaseFActivity : Os16CaseBaseActivity() {
    override fun spec(): Os16CaseSpec =
        Os16CaseSpec(
            caseId = "16F",
            titleText = "预测性返回迁移",
            summaryText = "target Android 16 后需迁移到 OnBackInvokedCallback/AndroidX 返回 API。",
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
        return "OnBackInvokedDispatcher 可用: $available。请确认代码不再依赖 onBackPressed/KEYCODE_BACK 拦截。"
    }
}
