package com.example.os17demo.os16cases

class Os16CaseMActivity : Os16CaseBaseActivity() {
    override fun spec(): Os16CaseSpec =
        Os16CaseSpec(
            caseId = "16M",
            titleText = "虚拟设备所有者替换项",
            summaryText = "VDO 投屏/多显示场景存在系统行为替换，建议避免 display/方向硬编码。",
            codeSnippet =
                """
                // 多显示适配建议
                val display = context.display
                val displayId = display?.displayId ?: -1
                Log.i("Display", "displayId=${'$'}displayId")

                // 根据窗口上下文和 displayId 动态计算布局，而非固定方向假设
                """.trimIndent(),
        )

    override fun runCheck(): String {
        val displayId = display?.displayId ?: -1
        val swDp = resources.configuration.smallestScreenWidthDp
        return "当前 displayId=$displayId, smallestScreenWidthDp=$swDp。请确认多显示输入与布局按 display 上下文计算。"
    }
}
