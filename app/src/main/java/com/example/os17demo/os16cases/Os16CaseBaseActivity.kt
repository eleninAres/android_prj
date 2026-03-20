package com.example.os17demo.os16cases

import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Os16CaseSpec(
    val caseId: String,
    val titleText: String,
    val summaryText: String,
    val codeSnippet: String,
)

abstract class Os16CaseBaseActivity : AppCompatActivity() {
    private lateinit var resultView: TextView

    abstract fun spec(): Os16CaseSpec

    abstract fun runCheck(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val caseSpec = spec()
        title = "${caseSpec.caseId} ${caseSpec.titleText}"

        val root = ScrollView(this)
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }
        root.addView(container)

        val summary = TextView(this).apply {
            text = caseSpec.summaryText
            textSize = 15f
        }
        container.addView(summary)

        val snippetTitle = TextView(this).apply {
            text = "示例代码片段"
            textSize = 16f
        }
        container.addView(snippetTitle)

        val snippet = TextView(this).apply {
            text = caseSpec.codeSnippet
            typeface = Typeface.MONOSPACE
            textSize = 13f
            setTextIsSelectable(true)
        }
        container.addView(snippet)

        val runButton = Button(this).apply {
            text = "运行检查"
            setOnClickListener {
                val message = runCheck()
                resultView.append("[${now()}] $message\n\n")
            }
        }
        container.addView(runButton)

        resultView = TextView(this).apply {
            text = "运行结果输出区\n\n"
            setTextIsSelectable(true)
        }
        container.addView(resultView)

        setContentView(root)
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun now(): String = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
}
