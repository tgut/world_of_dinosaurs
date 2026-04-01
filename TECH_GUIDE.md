# World of Dinosaurs Extended — 技术栈学习指南

本文档解释本项目使用的所有技术，帮助你理解每个组件的作用和学习路径。

---

## 1. Kotlin（编程语言）

**是什么**: Android 官方推荐的开发语言，替代 Java。
**为什么用**: 代码更简洁、更安全（空安全）、支持协程（异步编程）。
**学习资源**:
- [Kotlin 官方中文教程](https://book.kotlincn.net/)
- [Android 开发者 Kotlin 入门](https://developer.android.com/kotlin)

```kotlin
// Java 写法
String name = "T-Rex";
if (name != null) {
    System.out.println(name.length());
}

// Kotlin 写法 —— 更简洁
val name: String = "T-Rex"
println(name.length)
```

---

## 2. Jetpack Compose（UI 框架）

**是什么**: Android 新一代 UI 工具包，用代码描述界面（声明式 UI）。
**对比**: 传统方式用 XML 画界面，Compose 直接用 Kotlin 函数画界面。
**为什么用**: 代码更少、实时预览、状态驱动自动刷新 UI。

```kotlin
// 传统 XML 方式：需要写 XML 布局文件 + Java/Kotlin 代码绑定
// activity_main.xml + MainActivity.kt

// Compose 方式：一个函数就是一个界面组件
@Composable
fun DinosaurCard(name: String, era: String) {
    Card {
        Column {
            Text(text = name, style = MaterialTheme.typography.titleLarge)
            Text(text = era, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
```

**学习资源**:
- [Jetpack Compose 官方教程](https://developer.android.com/develop/ui/compose/tutorial)
- [Compose 示例项目](https://github.com/android/compose-samples)

---

## 3. Material 3 / Material Design 3（设计系统）

**是什么**: Google 的设计语言，定义了按钮、卡片、颜色、字体等 UI 组件的样式规范。
**为什么用**: 提供现成的美观 UI 组件，支持动态主题色、深色模式。
**在项目中**: 我们用 Material 3 的 `Card`、`Button`、`TopAppBar`、`NavigationBar` 等组件。

**学习资源**:
- [Material 3 官网](https://m3.material.io/)

---

## 4. MVVM 架构模式

**是什么**: Model-View-ViewModel 的缩写，一种代码组织方式。

```
┌─────────────┐    ┌──────────────┐    ┌─────────────┐
│   View      │ ←→ │  ViewModel   │ ←→ │   Model     │
│  (界面/UI)  │    │ (业务逻辑)    │    │ (数据)      │
│  Compose    │    │ 处理用户操作   │    │ API/数据库   │
│  Screens    │    │ 准备显示数据   │    │ JSON文件    │
└─────────────┘    └──────────────┘    └─────────────┘
```

- **View (视图)**: 用户看到的界面，比如恐龙列表页面
- **ViewModel (视图模型)**: 处理业务逻辑，比如"加载恐龙数据"、"搜索恐龙"
- **Model (模型)**: 数据本身，比如恐龙的名称、体重、图片

**好处**: 界面和数据分离，方便测试和维护。

---

## 5. Hilt（依赖注入框架）

**是什么**: Google 推荐的依赖注入（DI）框架，帮你自动创建和管理对象。
**通俗理解**: 不用手动 `new` 对象，Hilt 自动帮你准备好需要的东西。

```kotlin
// 没有 Hilt：手动创建一堆对象
val database = Room.databaseBuilder(...).build()
val dao = database.favoriteDao()
val repository = FavoriteRepository(dao)
val viewModel = FavoritesViewModel(repository)

// 有了 Hilt：自动注入，你只需要声明需要什么
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: FavoriteRepository  // Hilt 自动提供
) : ViewModel()
```

**学习资源**:
- [Hilt 官方教程](https://developer.android.com/training/dependency-injection/hilt-android)

---

## 6. Room（本地数据库）

**是什么**: Android 官方的 SQLite 数据库封装，用注解简化数据库操作。
**在项目中**: 存储用户收藏的恐龙列表。

```kotlin
// 定义数据表
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val dinosaurId: String,  // 恐龙ID作为主键
    val addedAt: Long                     // 收藏时间
)

// 定义操作（增删查改）
@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    fun getAll(): Flow<List<FavoriteEntity>>  // 查询所有收藏

    @Insert
    suspend fun add(favorite: FavoriteEntity)  // 添加收藏

    @Query("DELETE FROM favorites WHERE dinosaurId = :id")
    suspend fun remove(id: String)             // 取消收藏
}
```

**学习资源**:
- [Room 官方教程](https://developer.android.com/training/data-storage/room)

---

## 7. Retrofit + Moshi（网络请求）

**是什么**:
- **Retrofit**: 网络请求库，用来调用 API 获取数据
- **Moshi**: JSON 解析库，把 JSON 字符串转成 Kotlin 对象

```kotlin
// 定义 API 接口
interface DinoApiService {
    @GET("dinosaurs")
    suspend fun getDinosaurs(): List<DinosaurDto>
}

// Moshi 自动把 JSON 转成对象
// {"name": "T-Rex", "era": "Cretaceous"} → DinosaurDto(name="T-Rex", era="Cretaceous")
```

**在项目中**: 主要用本地 JSON 文件，Moshi 用来解析这些 JSON。

---

## 8. Coil（图片加载）

**是什么**: Kotlin 优先的图片加载库，支持网络图片、本地图片。
**对比**: 类似 Glide/Picasso，但更轻量、原生支持 Compose。

```kotlin
// 一行代码加载网络图片
AsyncImage(
    model = "https://example.com/trex.jpg",
    contentDescription = "霸王龙",
    modifier = Modifier.size(200.dp)
)
```

---

## 9. Navigation Compose（页面导航）

**是什么**: 管理 App 内页面跳转的组件。
**通俗理解**: 定义"从哪个页面可以跳到哪个页面"，类似网页的路由。

```kotlin
// 定义路由
NavHost(startDestination = "home") {
    composable("home") { HomeScreen() }          // 首页
    composable("detail/{id}") { DetailScreen() }  // 详情页，带参数
    composable("favorites") { FavoritesScreen() }  // 收藏页
}

// 跳转
navController.navigate("detail/trex")  // 跳到霸王龙详情页
```

---

## 10. DataStore（轻量存储）

**是什么**: 替代 SharedPreferences 的新一代键值对存储方案。
**在项目中**: 存储用户设置（语言选择、主题选择）。

```kotlin
// 保存语言设置
suspend fun setLanguage(lang: String) {
    dataStore.edit { settings ->
        settings[LANGUAGE_KEY] = lang  // "zh" 或 "en"
    }
}

// 读取语言设置
val language: Flow<String> = dataStore.data.map { settings ->
    settings[LANGUAGE_KEY] ?: "en"  // 默认英文
}
```

---

## 11. SceneView（3D/AR 渲染）

**是什么**: 基于 Google Filament 引擎的 3D 渲染库，同时支持 ARCore（增强现实）。
**在项目中**:
- **3D 模式**: 在屏幕上显示恐龙 3D 模型，用户可以旋转、缩放
- **AR 模式**: 用手机摄像头，把恐龙"放"到现实环境中

```kotlin
// 加载并显示 3D 模型
SceneView {
    modelLoader.loadModel("models/trex.glb")
    // 用户可以手指拖拽旋转、双指缩放
}
```

**3D 模型格式**: `.glb`（二进制 glTF），这是一种通用 3D 模型格式
**模型来源**: [Sketchfab](https://sketchfab.com/) 上有很多免费的恐龙 3D 模型

---

## 12. Flow / Coroutines（异步编程）

**是什么**:
- **Coroutines (协程)**: Kotlin 的异步编程方案，避免阻塞主线程
- **Flow (流)**: 异步数据流，数据变化时自动通知 UI 更新

**通俗理解**:
- 协程 = "在后台做耗时操作（如网络请求），完成后回到主线程更新 UI"
- Flow = "数据变了，UI 自动刷新"

```kotlin
// 协程：后台加载数据
viewModelScope.launch {
    val dinosaurs = repository.getDinosaurs()  // 后台执行，不卡 UI
    _uiState.value = HomeUiState(dinosaurs = dinosaurs)  // 回到主线程更新
}

// Flow：数据变化时自动更新
repository.getFavorites()  // 返回 Flow<List<Dinosaur>>
    .collect { favorites ->
        // 每当收藏列表变化，这里自动执行
        _uiState.value = FavoritesUiState(favorites = favorites)
    }
```

---

## 13. KSP（Kotlin Symbol Processing）

**是什么**: 编译时代码生成工具。
**为什么需要**: Hilt、Room、Moshi 都需要在编译时自动生成一些辅助代码。KSP 就是干这个的。
**你不需要直接写 KSP 代码**，只需要在 `build.gradle.kts` 中配置即可。

---

## 14. Clean Architecture（整洁架构）

**是什么**: 一种代码组织原则，把代码分成三层：

```
┌──────────────────────────────────────────┐
│  UI Layer (ui/)                          │  ← 界面相关
│  Screens, ViewModels, Composables        │
├──────────────────────────────────────────┤
│  Domain Layer (domain/)                  │  ← 核心业务逻辑
│  Models, Repository Interfaces, UseCases │
├──────────────────────────────────────────┤
│  Data Layer (data/)                      │  ← 数据获取
│  API, Database, JSON, Repository Impl    │
└──────────────────────────────────────────┘
```

**规则**: 上层依赖下层，下层不知道上层的存在。
**好处**: 换数据源（比如从本地 JSON 换成网络 API）不影响 UI 代码。

---

## 项目目录结构速览

```
app/src/main/java/com/example/world_of_dinosaurs_extented/
├── DinoApp.kt              # App 入口
├── MainActivity.kt         # 唯一的 Activity
├── navigation/             # 页面路由
├── di/                     # Hilt 依赖注入配置
├── domain/                 # 业务层
│   ├── model/              #   数据模型（Dinosaur, Quiz 等）
│   ├── repository/         #   数据仓库接口
│   └── usecase/            #   用例（具体业务操作）
├── data/                   # 数据层
│   ├── asset/              #   本地 JSON 数据加载
│   ├── local/              #   Room 数据库
│   ├── remote/             #   网络 API
│   └── repository/         #   数据仓库实现
└── ui/                     # 界面层
    ├── theme/              #   颜色、字体、主题
    ├── common/             #   公共 UI 组件
    ├── home/               #   首页（恐龙列表）
    ├── detail/             #   恐龙详情页
    ├── favorites/          #   收藏页
    ├── quiz/               #   知识问答
    ├── model3d/            #   3D 模型 / AR
    └── settings/           #   设置页（语言/主题切换）
```

---

## 推荐学习顺序

1. **Kotlin 基础** → 变量、函数、类、空安全、集合操作
2. **Jetpack Compose** → 基本组件（Text, Button, Image, Column, Row, LazyColumn）
3. **MVVM + ViewModel** → 理解数据流方向
4. **Navigation** → 页面跳转
5. **Room** → 本地数据存储
6. **Hilt** → 依赖注入（可以后面再深入）
7. **Coroutines + Flow** → 异步操作（边用边学）

---

## 15. OSMDroid（离线地图）

**是什么**: 基于 OpenStreetMap 的 Android 地图库，完全免费，无需 API Key。
**在项目中**: 发现地图功能，展示恐龙化石发现地点。

```kotlin
// OSMDroid 配置
val config = Configuration.getInstance()
config.userAgentValue = context.packageName
config.osmdroidBasePath = context.getExternalFilesDir(null)
config.osmdroidTileCache = File(context.getExternalFilesDir(null), "osmdroid/tiles")

// 创建地图视图
MapView(context).apply {
    setTileSource(TileSourceFactory.MAPNIK)  // 标准地图
    setMultiTouchControls(true)
    controller.setZoom(3.0)
    controller.setCenter(GeoPoint(20.0, 0.0))
}
```

**学习资源**:
- [OSMDroid Wiki](https://github.com/osmdroid/osmdroid/wiki)

---

## 16. OpenAI 兼容 Chat API（AI 聊天）

**是什么**: 使用 OpenAI 兼容格式调用各种大模型 API（DeepSeek / Qwen / Gemini / 自定义）。
**在项目中**: AI 恐龙问答功能，支持文字和语音输入。

```kotlin
// 所有提供商使用统一的 OpenAI 格式
@POST("chat/completions")
suspend fun chatCompletion(
    @Header("Authorization") authorization: String,
    @Body request: ChatCompletionRequest
): ChatCompletionResponse

// ChatCompletionRequest
data class ChatCompletionRequest(
    val model: String,           // "deepseek-chat", "qwen-turbo", "gemini-2.5-flash-lite"
    val messages: List<ChatMessageDto>,
    val temperature: Double = 0.7,
    val max_tokens: Int = 1024
)
```

**支持的提供商**:
| 提供商 | Base URL | 默认模型 |
|--------|----------|----------|
| DeepSeek | `https://api.deepseek.com/` | `deepseek-chat` |
| Qwen | `https://dashscope.aliyuncs.com/compatible-mode/v1/` | `qwen-turbo` |
| Gemini | `https://generativelanguage.googleapis.com/v1beta/openai/` | `gemini-2.5-flash-lite` |
| 自定义 | 用户填写 | 用户填写 |

---

## 17. TextToSpeech（语音合成 / TTS）

**是什么**: Android 内置的语音合成引擎，可以将文本朗读出来。
**在项目中**: 详情页全文朗读、AI 聊天回复朗读，支持中英双语。

```kotlin
@Singleton
class TtsManager @Inject constructor(@ApplicationContext context: Context) {
    private var tts: TextToSpeech? = null

    fun speak(text: String, language: String, speed: Float, pitch: Float) {
        val locale = if (language == "zh") Locale.CHINESE else Locale.ENGLISH
        tts?.language = locale
        tts?.setSpeechRate(speed)
        tts?.setPitch(pitch)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }
}
```

**特点**: 无需网络、无额外依赖、设备端运行。

---

## 18. SpeechRecognizer（语音识别）

**是什么**: Android 内置的语音识别服务，将语音转为文字。
**在项目中**: AI 聊天页面的语音输入功能。

```kotlin
val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
    putExtra(RecognizerIntent.EXTRA_LANGUAGE, if (lang == "zh") "zh-CN" else "en-US")
}
speechRecognizer?.startListening(intent)
```

**权限**: 需要 `RECORD_AUDIO` 运行时权限。

---

## 项目目录结构速览

```
app/src/main/java/com/example/world_of_dinosaurs_extented/
├── DinoApp.kt              # App 入口
├── MainActivity.kt         # 唯一的 Activity
├── navigation/             # 页面路由
├── di/                     # Hilt 依赖注入配置
├── domain/                 # 业务层
│   ├── model/              #   数据模型（Dinosaur, Quiz, DinosaurMapMarker 等）
│   ├── repository/         #   数据仓库接口（ChatRepository, DinoRecognitionRepository 等）
│   └── usecase/            #   用例（具体业务操作）
├── data/                   # 数据层
│   ├── asset/              #   本地 JSON 数据加载
│   ├── local/              #   Room 数据库
│   ├── remote/             #   网络 API（Vision API, Chat API）
│   │   ├── api/            #     Retrofit 接口
│   │   └── dto/            #     数据传输对象
│   ├── map/                #   发现地图坐标查找表
│   ├── model3d/            #   3D 模型配置和缓存
│   ├── tts/                #   TTS 语音合成管理
│   ├── repository/         #   数据仓库实现
│   └── SettingsManager.kt  #   用户设置（DataStore）
└── ui/                     # 界面层
    ├── theme/              #   颜色、字体、主题
    ├── common/             #   公共 UI 组件
    ├── home/               #   首页（恐龙列表）
    ├── detail/             #   恐龙详情页（含朗读/翻译）
    ├── favorites/          #   收藏页
    ├── quiz/               #   知识问答
    ├── timeline/           #   时间线
    ├── qrscan/             #   二维码扫描
    ├── recognition/        #   图像识别
    ├── model3d/            #   3D 模型 / AR
    ├── map/                #   发现地图
    ├── chat/               #   AI 聊天（文字/语音/TTS）
    └── settings/           #   设置页（语言/主题/AI提供商/语音配置/API Key 指引）
```

---

## 功能开发阶段

| Phase | 功能 | 状态 |
|-------|------|------|
| 1-11 | 核心 App (首页/详情/收藏/问答/设置/双语) | ✅ 已完成 |
| 12 | 时间线 (三叠纪/侏罗纪/白垩纪) | ✅ 已完成 |
| 13 | 二维码扫描 + 扫描历史 | ✅ 已完成 |
| 14 | 扫描复习测验 | ✅ 已完成 |
| 15 | 扩展到 230 种恐龙 (PBDB + Wikipedia) | ✅ 已完成 |
| 16 | 图像识别 (Vision API) + 3D/AR (SceneView) | ✅ 已完成 |
| 17 | 发现地图 (OSMDroid + globe.gl 3D) + 更多 3D 模型 | ✅ 已完成 |
| 18 | AI 恐龙问答 (多提供商 + 语音输入) | ✅ 已完成 |
| 19 | TTS 朗读 + 画词翻译 + 地图 Bug 修复 | ✅ 已完成 |
| 20 | 时间线美化 + API Key 申请指引 + 小米语音修复 | ✅ 已完成 |
| 21 | 隐私政策页 + 首次启动同意弹窗 + Release 签名配置 | ✅ 已完成 |
| 22 | 穿山甲广告接入 (Banner 详情页 + 激励视频解锁解析) | ✅ 已完成 |

---

## 推荐学习顺序

1. **Kotlin 基础** → 变量、函数、类、空安全、集合操作
2. **Jetpack Compose** → 基本组件（Text, Button, Image, Column, Row, LazyColumn）
3. **MVVM + ViewModel** → 理解数据流方向
4. **Navigation** → 页面跳转
5. **Room** → 本地数据存储
6. **Hilt** → 依赖注入（可以后面再深入）
7. **Coroutines + Flow** → 异步操作（边用边学）

## 推荐视频/课程

- [Google 官方 Android 基础课程 (Compose)](https://developer.android.com/courses/android-basics-compose/course)
- B站搜索 "Jetpack Compose 入门" 有很多中文教程
- [Philipp Lackner YouTube 频道](https://www.youtube.com/@PhilippLackner) — 英文，但讲解清晰
