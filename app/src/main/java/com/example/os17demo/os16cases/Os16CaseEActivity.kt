package com.example.os17demo.os16cases

class Os16CaseEActivity : Os16CaseBaseActivity() {
    override fun spec(): Os16CaseSpec =
        Os16CaseSpec(
            caseId = "16E",
            titleText = "无障碍公告弃用",
            summaryText = "Android 16 弃用干扰性无障碍公告，建议改为 paneTitle/liveRegion 等语义化方式。",
            codeSnippet =
                """
                // 旧方式（逐步避免）
                // view.announceForAccessibility("已刷新")

                // 新方式示例
                view.accessibilityLiveRegion = View.ACCESSIBILITY_LIVE_REGION_POLITE
                view.accessibilityPaneTitle = "订单结果"
                """.trimIndent(),
        )

    override fun runCheck(): String {
        val paneTitleAvailable = runCatching {
            Class.forName("android.view.View").getMethod("setAccessibilityPaneTitle", CharSequence::class.java)
        }.isSuccess
        return "setAccessibilityPaneTitle 可用: $paneTitleAvailable。建议用语义化无障碍更新替代 announcement。"
    }
}
