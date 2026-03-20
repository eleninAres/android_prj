package com.example.os17demo.os16cases

import android.content.pm.PackageManager

class Os16CaseLActivity : Os16CaseBaseActivity() {
    override fun spec(): Os16CaseSpec =
        Os16CaseSpec(
            caseId = "16L",
            titleText = "应用拥有的照片",
            summaryText = "用户选择“部分照片/视频”授权后，应用拥有媒体会在选择器预选，需动态尊重授权范围。",
            codeSnippet =
                """
                val granted = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                ) == PackageManager.PERMISSION_GRANTED

                // 每次进入媒体流程都按最新授权集合查询，不缓存长期可见性
                """.trimIndent(),
        )

    override fun runCheck(): String {
        val granted =
            packageManager.checkPermission(
                "android.permission.READ_MEDIA_VISUAL_USER_SELECTED",
                packageName,
            ) == PackageManager.PERMISSION_GRANTED
        return "READ_MEDIA_VISUAL_USER_SELECTED=$granted。请验证“用户取消预选项”后的实时权限收敛。"
    }
}
