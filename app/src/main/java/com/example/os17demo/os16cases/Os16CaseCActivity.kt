package com.example.os17demo.os16cases

class Os16CaseCActivity : Os16CaseBaseActivity() {
    override fun spec(): Os16CaseSpec =
        Os16CaseSpec(
            caseId = "16C",
            titleText = "ART 内部变更与非 SDK 访问",
            summaryText = "依赖 ART 内部结构或非 SDK 接口的代码在 Android 16 风险更高。",
            codeSnippet =
                """
                val vmPolicy = StrictMode.VmPolicy.Builder()
                    .detectNonSdkApiUsage()
                    .penaltyLog()
                    .build()
                StrictMode.setVmPolicy(vmPolicy)

                // 启动后观察 logcat，移除所有非 SDK 访问
                """.trimIndent(),
        )

    override fun runCheck(): String {
        val detectMethodAvailable = runCatching {
            Class.forName("android.os.StrictMode\$VmPolicy\$Builder")
                .getMethod("detectNonSdkApiUsage")
        }.isSuccess
        return "detectNonSdkApiUsage 可用: $detectMethodAvailable。建议在 debug 构建默认开启。"
    }
}
