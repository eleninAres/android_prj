package com.example.os17demo.os16cases

class Os16CaseAActivity : Os16CaseBaseActivity() {
    override fun spec(): Os16CaseSpec =
        Os16CaseSpec(
            caseId = "16A",
            titleText = "Job/调度行为变更",
            summaryText =
                "Android 16 调整了 JobScheduler/WorkManager 相关执行配额，" +
                    "并且 scheduleAtFixedRate 的补偿执行次数也发生变化。",
            codeSnippet =
                """
                // 建议：记录停止原因并做幂等重试
                val stopReason = workInfo.stopReason
                if (stopReason == WorkInfo.STOP_REASON_TIMEOUT) {
                    enqueueRetry()
                }

                // 建议：用户主动触发的数据传输，优先改为用户发起型传输作业
                // 以避免在更严格配额下被频繁中断
                """.trimIndent(),
        )

    override fun runCheck(): String =
        "检查项：\n" +
            "1) 作业是否处理 stopReason 并幂等重试；\n" +
            "2) 用户发起传输是否改为更合适的作业类型；\n" +
            "3) scheduleAtFixedRate 是否仍依赖“补偿全部遗漏执行”。"
}
