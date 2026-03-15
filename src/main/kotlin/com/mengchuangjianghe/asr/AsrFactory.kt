package com.mengchuangjianghe.asr

/**
 * 创建 ASR 引擎的入口，方便“添加一行依赖”后一行代码获取引擎。
 */
object AsrFactory {

    /**
     * 获取演示引擎（无需配置，集成测试可用）
     */
    @JvmStatic
    fun createDemo(): AsrEngine = DemoAsrEngine()

    /**
     * 获取 HTTP 引擎（需配置接口地址与可选 API Key）
     */
    @JvmStatic
    fun createHttp(
        apiUrl: String,
        apiKey: String? = null,
        format: String = "pcm",
        connectTimeoutMs: Int = 10000,
        readTimeoutMs: Int = 30000
    ): AsrEngine = HttpAsrEngine(apiUrl, apiKey, format, connectTimeoutMs, readTimeoutMs)

    /**
     * 百度短语音识别（需在百度开放平台申请应用并换取 token）
     */
    @JvmStatic
    fun createBaidu(
        token: String,
        connectTimeoutMs: Int = 10000,
        readTimeoutMs: Int = 30000
    ): AsrEngine = BaiduAsrEngine(token, connectTimeoutMs, readTimeoutMs)

    /**
     * 默认引擎：当前为演示引擎；正式环境可改为 createBaidu(token) 或 createHttp(...)
     */
    @JvmStatic
    fun createDefault(): AsrEngine = createDemo()
}
