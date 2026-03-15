package com.mengchuangjianghe.asr

import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.Base64

/**
 * 百度短语音识别 REST API 封装（普通话）。
 * 需在百度开放平台创建应用获取 API Key / Secret，换 token 后传入。
 * 文档：https://ai.baidu.com/ai-doc/SPEECH/Vk38lxily
 */
class BaiduAsrEngine(
    private val token: String,
    private val connectTimeoutMs: Int = 10000,
    private val readTimeoutMs: Int = 30000
) : AsrEngine {

    private val apiUrl = "https://vop.baidu.com/server_api"

    private var available = token.isNotBlank()

    override fun isAvailable(): Boolean = available

    override fun recognize(pcmData: ByteArray, sampleRate: Int): AsrResult {
        if (!available || pcmData.isEmpty()) {
            return AsrResult(if (pcmData.isEmpty()) "" else "未配置 token 或不可用", 0f, true)
        }
        return doRecognize(pcmData, sampleRate)
    }

    private fun doRecognize(pcmData: ByteArray, sampleRate: Int): AsrResult {
        var conn: HttpURLConnection? = null
        try {
            val url = URL(apiUrl)
            conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.connectTimeout = connectTimeoutMs
            conn.readTimeout = readTimeoutMs
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Accept", "application/json")
            val speechBase64 = Base64.getEncoder().encodeToString(pcmData)
            val body = """
                {
                    "format": "pcm",
                    "rate": $sampleRate,
                    "channel": 1,
                    "token": "$token",
                    "cuid": "mengchuangjianghe",
                    "len": ${pcmData.size},
                    "speech": "$speechBase64"
                }
            """.trimIndent()
            conn.outputStream.use { os: OutputStream ->
                os.write(body.toByteArray(StandardCharsets.UTF_8))
                os.flush()
            }
            val code = conn.responseCode
            if (code != 200) {
                val err = conn.errorStream?.reader(StandardCharsets.UTF_8)?.readText() ?: "HTTP $code"
                return AsrResult("识别请求失败: $err", 0f, true)
            }
            val responseBody = conn.inputStream.reader(StandardCharsets.UTF_8).readText()
            return parseBaiduResponse(responseBody)
        } catch (e: Exception) {
            return AsrResult("识别异常: ${e.message}", 0f, true)
        } finally {
            conn?.disconnect()
        }
    }

    private fun parseBaiduResponse(body: String): AsrResult {
        val t = body.trim()
        if (t.isEmpty()) return AsrResult("", 0f, true)
        // 百度返回 err_no: 0 表示成功，result 为识别结果数组
        if (t.contains("\"err_no\":0") || t.contains("\"err_no\": 0")) {
            var text = ""
            val resultStart = t.indexOf("\"result\"")
            if (resultStart >= 0) {
                val arrStart = t.indexOf('[', resultStart)
                if (arrStart >= 0) {
                    val firstQuote = t.indexOf('"', arrStart)
                    if (firstQuote >= 0) {
                        val endQuote = t.indexOf('"', firstQuote + 1)
                        if (endQuote > firstQuote) {
                            text = t.substring(firstQuote + 1, endQuote)
                        }
                    }
                }
            }
            return AsrResult(TextRefiner.refine(text), 0.95f, true)
        }
        val errMsg = when {
            t.contains("\"err_msg\"") -> {
                val i = t.indexOf("\"err_msg\"") + 9
                val start = t.indexOf('"', i) + 1
                val end = t.indexOf('"', start)
                if (end > start) t.substring(start, end) else "未知错误"
            }
            else -> "识别失败"
        }
        return AsrResult(errMsg, 0f, true)
    }

    override fun release() {
        available = false
    }
}
