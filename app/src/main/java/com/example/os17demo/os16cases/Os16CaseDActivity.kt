package com.example.os17demo.os16cases

import android.system.Os
import android.system.OsConstants

class Os16CaseDActivity : Os16CaseBaseActivity() {
    override fun spec(): Os16CaseSpec =
        Os16CaseSpec(
            caseId = "16D",
            titleText = "16KB 页面大小兼容模式",
            summaryText = "Android 16 引入 16KB page size 兼容模式，native 代码需验证页大小假设。",
            codeSnippet =
                """
                val pageSize = Os.sysconf(OsConstants._SC_PAGESIZE)
                Log.i("PageSize", "page size = ${'$'}pageSize")

                // Native 侧需验证 so 对齐与内存分配是否兼容 16KB 页大小
                """.trimIndent(),
        )

    override fun runCheck(): String {
        val pageSize = runCatching { Os.sysconf(OsConstants._SC_PAGESIZE) }.getOrNull()
        return "当前系统页大小: ${pageSize ?: "unknown"} bytes。请确认 native 模块不只假设 4096。"
    }
}
