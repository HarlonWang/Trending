# Trending AI

> 🚧 **当前状态：项目正在积极开发中 (Work in Progress)**

**「 洞察趋势，AI 领航 」**  
*聚合 GitHub Trending 热门仓库，通过 AI 摘要技术，让核心价值触手可及。*

---

### 🌟 项目简介

**Trending AI** 是一款基于 **Kotlin Multiplatform (KMP)** 开发的跨平台移动应用，致力于为开发者提供极致的 GitHub Trending 仓库探索体验。

我们深知在信息爆炸的开源世界中，快速定位高价值项目的重要性。因此，该应用不仅展示 GitHub 今日、本周及本月的 Trending 热门仓库榜单，更深度集成了 **Gemini** 与 **DeepSeek** 等模型，为每个仓库生成精炼的 **AI 摘要速览**。无需深入阅读代码库，即可快速洞察项目核心逻辑。

---

## 📸 界面预览

| Android 预览 | iOS 预览 |
| :---: | :---: |
| <img src="assets/android_screenshot.png" width="300"> | <img src="assets/ios_screenshot.png" width="300"> |

---

## ✨ 核心功能

- 🚀 **GitHub 热门榜单**：支持查看“今日”、“本周”和“本月”的 Trending 热门项目。
- 🤖 **AI 智能摘要**：集成 Gemini 和 DeepSeek 模型，自动生成项目的核心解读。
- 📱 **原生跨平台体验**：采用 Compose Multiplatform 构建，一套代码同时运行在 Android 和 iOS 平台。
- 🎨 **Material 3 设计**：遵循 Google 最新 Material Design 3 规范，提供流畅的视觉交互。

---

### 📥 立即下载

项目目前处于早期预览阶段，您可以点击下载进行体验（更多功能正持续迭代中）：

- **Android**: [![Download](https://img.shields.io/badge/Download-APK-green?style=flat-square&logo=android)](https://github.com/HarlonWang/Trending/releases)
- **iOS**: 🚀 *Coming Soon*

---

## 🛠️ 技术栈

- **核心架构**: [Kotlin Multiplatform (KMP)](https://kotlinlang.org/docs/multiplatform.html)
- **UI 框架**: [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- **网络库**: [Ktor Client](https://ktor.io/)
- **序列化**: [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
- **UI 组件**: Material 3 (Expressive API)

---

## 📂 项目结构与架构

项目采用**分层架构 (Layered Architecture)**，结合 Android 官方最佳实践进行分包，确保逻辑解耦与良好的测试性。

### 核心分层说明

```text
shared/src/commonMain/kotlin/whl/trending/ai/
├── core/                   # 核心基础设施 (Infrastructure)
│   ├── platform/           # 跨平台适配 (expect/actual)
│   ├── theme/              # 全局 UI 规范 (Theme, Color)
│   └── App.kt              # 应用入口与全局路由导航
├── data/                   # 数据层 (Data Layer)
│   ├── model/              # 数据实体 (DTO / Entity)
│   ├── remote/             # 远程数据源 (Ktor API 实现)
│   ├── local/              # 本地持久化 (Preferences / Settings)
│   └── repository/         # 存储库 (Repository - 业务逻辑入口)
└── ui/                     # 表现层 (Presentation Layer)
    ├── main/               # 首页趋势列表 (Screen & ViewModel)
    ├── settings/           # 设置模块
    └── component/          # 全局公共 UI 组件
```

### 协作规范与约定

为了保持代码库的整洁与可维护性，后续开发请遵循以下约定：

1.  **数据流向**：
    *   **UI (Screen)** ↔️ **ViewModel** ↔️ **Repository** ↔️ **API/Local Source**。
    *   严禁在 ViewModel 中直接持有 API 实例，必须通过 `Repository` 进行数据抽象。
2.  **状态管理**：
    *   使用 `androidx.lifecycle.ViewModel` 管理页面级状态。
    *   UI 状态统一建模为 `data class UiState`，并通过 `StateFlow` 暴露给 Compose 组件。
3.  **UI 组件化**：
    *   大型页面（如 `MainScreen`）必须拆分为私有小型 Composable 函数。
    *   通用组件放入 `ui/component` 目录下。
4.  **跨平台处理**：
    *   平台相关逻辑优先考虑在 `core/platform` 下定义 `expect` 方法。
5.  **单元测试**：
    *   新的 ViewModel 逻辑必须在 `commonTest` 对应路径下提供测试用例（如 `MainViewModelTest`）。
    *   测试应覆盖初始加载、错误处理以及并发请求场景。
