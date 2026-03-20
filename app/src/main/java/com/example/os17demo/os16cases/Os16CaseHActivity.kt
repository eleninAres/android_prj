package com.example.os17demo.os16cases

import android.widget.TextView

class Os16CaseHActivity : Os16CaseBaseActivity() {
    override fun spec(): Os16CaseSpec =
        Os16CaseSpec(
            caseId = "16H",
            titleText = "themed icon 与 elegantTextHeight",
            summaryText = "系统自动主题化图标，同时 elegantTextHeight 被弃用/停用。",
            codeSnippet =
                """
                // 图标：adaptive icon 增加 monochrome layer
                // res/mipmap-anydpi-v26/ic_launcher.xml 增加 <monochrome> 图层

                // 文本：不要再依赖 elegantTextHeight 控制
                // 直接验证多语言脚本在设计稿下的真实排版
                """.trimIndent(),
        )

    override fun runCheck(): String {
        @Suppress("DEPRECATION")
        val elegant = TextView(this).isElegantTextHeight
        return "当前设备 TextView.isElegantTextHeight=$elegant。建议以真实多语言渲染回归替代旧属性依赖。"
    }
}
