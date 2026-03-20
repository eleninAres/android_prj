package com.example.os17demo.os16cases

class Os16CaseGActivity : Os16CaseBaseActivity() {
    override fun spec(): Os16CaseSpec =
        Os16CaseSpec(
            caseId = "16G",
            titleText = "无边框与自适应布局",
            summaryText = "edge-to-edge 选择停用能力逐步失效，且大屏上方向/比例限制会被忽略。",
            codeSnippet =
                """
                WindowCompat.setDecorFitsSystemWindows(window, false)
                ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
                    val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
                    insets
                }
                """.trimIndent(),
        )

    override fun runCheck(): String {
        val swDp = resources.configuration.smallestScreenWidthDp
        val attrId = resources.getIdentifier("windowOptOutEdgeToEdgeEnforcement", "attr", "android")
        return "smallestScreenWidthDp=$swDp, opt-out attrId=$attrId。建议统一改为 insets + 响应式布局。"
    }
}
