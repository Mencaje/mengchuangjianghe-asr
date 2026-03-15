# mengchuangjianghe-asr

萌创匠盒语音转文字模块（ASR），为输入法提供高效语音输入能力，开源共建。支持**现说现转**、识别结果**修辞规整**，并封装**百度短语音识别**等真实算法。

**不依赖 Android**，纯 JVM 库，任意项目添加一行依赖即可拉取使用。

## 添加依赖（一行）

### Gradle (Kotlin DSL)

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}
dependencies {
    implementation("com.github.zxcvvvvvbnm:mengchuangjianghe-asr:1.1.0")
}
```

### Gradle (Groovy)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    implementation 'com.github.zxcvvvvvbnm:mengchuangjianghe-asr:1.1.0'
}
```

## 使用方式

```kotlin
import com.mengchuangjianghe.asr.AsrFactory
import com.mengchuangjianghe.asr.AsrResult
import com.mengchuangjianghe.asr.TextRefiner

// 获取默认引擎（当前为演示引擎，无需配置即可跑通）
val engine = AsrFactory.createDefault()

// 识别：传入 16bit 单声道 PCM 与采样率（如 16000）
val pcmData: ByteArray = ... // 从麦克风或文件读取
val result: AsrResult = engine.recognize(pcmData, 16000)
if (result.isSuccess()) {
    val text = TextRefiner.refine(result.text)  // 修辞规整后再上屏
    println(text)
}

// 真实算法：百度短语音识别（需在百度开放平台申请应用并换取 token）
val baiduEngine = AsrFactory.createBaidu(token = "your_access_token")
val baiduResult = baiduEngine.recognize(pcmData, 16000)

// 或使用 HTTP 引擎对接任意 ASR 接口
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
- **TextRefiner**：修辞规整，`refine(recognized: String): String`，去空格、句末标点等，使结果更精准可读  
- **AsrFactory**：`createDemo()`、`createHttp(...)`、`createBaidu(token)`、`createDefault()`

## 本地构建

```bash
./gradlew build
```

## 版本与发布

版本在 `build.gradle.kts` 中指定；通过 GitHub 打 tag（如 `1.1.0`）后，JitPack 会自动构建，依赖即可使用 `com.github.zxcvvvvvbnm:mengchuangjianghe-asr:1.1.0`。

## License

Apache-2.0
