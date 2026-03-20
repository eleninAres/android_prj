package com.example.os17demo.os16cases

import android.Manifest
import android.content.pm.PackageManager

class Os16CaseKActivity : Os16CaseBaseActivity() {
    override fun spec(): Os16CaseSpec =
        Os16CaseSpec(
            caseId = "16K",
            titleText = "健康权限与本地网络权限",
            summaryText = "健康权限细粒度化，本地网络访问逐步走向运行时授权。",
            codeSnippet =
                """
                val granted = ContextCompat.checkSelfPermission(
                    context, "android.permission.LOCAL_NETWORK"
                ) == PackageManager.PERMISSION_GRANTED

                if (!granted) {
                    requestPermissions(arrayOf("android.permission.LOCAL_NETWORK"), 1001)
                }
                """.trimIndent(),
        )

    override fun runCheck(): String {
        val bodySensorsGranted =
            packageManager.checkPermission(Manifest.permission.BODY_SENSORS, packageName) == PackageManager.PERMISSION_GRANTED
        val localNetworkGranted =
            packageManager.checkPermission("android.permission.LOCAL_NETWORK", packageName) ==
                PackageManager.PERMISSION_GRANTED
        return "BODY_SENSORS=$bodySensorsGranted, LOCAL_NETWORK=$localNetworkGranted。请补拒绝与撤销场景。"
    }
}
