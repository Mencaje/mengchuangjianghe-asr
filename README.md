# mengchuangjianghe-asr

萌创匠盒语音转文字模块（ASR），为输入法提供高效语音输入能力，开源共建，支持离线/在线语音识别。

**不依赖 Android**，纯 JVM 库，任意项目添加一行依赖即可拉取使用。

## 添加依赖（一行）

### Gradle (Kotlin DSL)

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}
dependencies {
    implementation("com.github.zxcvvvvvbnm:mengchuangjianghe-asr:1.0.0")
}
```

### Gradle (Groovy)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    implementation 'com.github.zxcvvvvvbnm:mengchuangjianghe-asr:1.0.0'
}
```

## 使用方式

```kotlin
import com.mengchuangjianghe.asr.AsrFactory
import com.mengchuangjianghe.asr.AsrResult

// 获取默认引擎（当前为演示引擎，无需配置即可跑通）
val engine = AsrFactory.createDefault()

// 或演示引擎（集成测试推荐）
val demoEngine = AsrFactory.createDemo()

// 识别：传入 16bit 单声道 PCM 与采样率（如 16000）
val pcmData: ByteArray = ... // 从麦克风或文件读取
val result: AsrResult = engine.recognize(pcmData, 16000)
if (result.isSuccess()) {
    println(result.text)       // 识别文本
    println(result.confidence) // 置信度
}

// 使用 HTTP 引擎（需配置 ASR 接口地址与可选 API Key）
val httpEngine = AsrFactory.createHttp(
    apiUrl = "https://your-asr-api.com/recognize",
    apiKey = "your-api-key"
)
val httpResult = httpEngine.recognize(pcmData, 16000)
```

## 接口说明

- **AsrEngine**：语音转文字引擎接口  
  - `recognize(pcmData: ByteArray, sampleRate: Int): AsrResult`  
  - `isAvailable(): Boolean`  
  - `release()`  
- **AsrResult**：识别结果，含 `text`、`confidence`、`isFinal`  
- **AsrFactory**：`createDemo()`、`createHttp(...)`、`createDefault()`

## 本地构建

```bash
./gradlew build
```

## 版本与发布

版本在 `build.gradle.kts` 中指定；通过 GitHub 打 tag（如 `1.0.0`）后，JitPack 会自动构建，依赖即可使用 `com.github.zxcvvvvvbnm:mengchuangjianghe-asr:1.0.0`。

## License

Apache-2.0
