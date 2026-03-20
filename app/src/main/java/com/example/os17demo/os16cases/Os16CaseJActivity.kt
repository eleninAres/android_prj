package com.example.os17demo.os16cases

class Os16CaseJActivity : Os16CaseBaseActivity() {
    override fun spec(): Os16CaseSpec =
        Os16CaseSpec(
            caseId = "16J",
            titleText = "蓝牙绑定丢失与配套设备变更",
            summaryText = "包括发现超时通知行为调整、绑定丢失/加密变更新 intent，以及 removeBond 公共 API。",
            codeSnippet =
                """
                // 监听绑定状态变化
                registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED))

                // target 16 可评估使用 CDM removeBond API
                // companionDeviceManager.removeBond(associationId)
                """.trimIndent(),
        )

    override fun runCheck(): String {
        val removeBondAvailable = runCatching {
            Class.forName("android.companion.CompanionDeviceManager")
                .methods.any { it.name == "removeBond" }
        }.getOrDefault(false)
        return "CompanionDeviceManager.removeBond 可用: $removeBondAvailable。" +
            "建议绑定状态以系统广播和系统对话框结果为准。"
    }
}
