package com.example.os17demo.os16cases

import android.provider.MediaStore

class Os16CaseIActivity : Os16CaseBaseActivity() {
    override fun spec(): Os16CaseSpec =
        Os16CaseSpec(
            caseId = "16I",
            titleText = "Intent/MediaStore/GPU 安全变化",
            summaryText = "Android 16 安全强化涉及 intent 解析、MediaStore 版本和 Mali GPU ioctl 过滤。",
            codeSnippet =
                """
                // 接收方对跨应用 Intent 做白名单校验
                val actionAllowed = intent.action in setOf(ACTION_VIEW_ORDER)
                val uriAllowed = intent.data?.scheme == "content"
                if (!actionAllowed || !uriAllowed) {
                    finish()
                    return
                }

                // 不要把 MediaStore#getVersion() 当跨应用稳定指纹
                """.trimIndent(),
        )

    override fun runCheck(): String {
        val mediaVersion = runCatching { MediaStore.getVersion(this) }.getOrNull()
        return "MediaStore.getVersion()=${mediaVersion ?: "unavailable"}。请确认未将其用于设备/应用指纹推断。"
    }
}
