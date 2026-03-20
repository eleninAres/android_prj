package com.example.os17demo.os16cases

class Os16CaseBActivity : Os16CaseBaseActivity() {
    override fun spec(): Os16CaseSpec =
        Os16CaseSpec(
            caseId = "16B",
            titleText = "有序广播优先级不再全局",
            summaryText = "Android 16 起不要再依赖跨进程全局优先级来保证业务执行顺序。",
            codeSnippet =
                """
                // 旧模式（不建议继续依赖）
                intentFilter.priority = 1000

                // 新建议：关键链路使用显式组件/前台服务/应用内事件总线
                val intent = Intent(context, SyncReceiver::class.java)
                context.sendBroadcast(intent)
                """.trimIndent(),
        )

    override fun runCheck(): String =
        "检查项：是否存在“优先级高就一定先执行”的业务假设。若有，改为显式触发链路。"
}
