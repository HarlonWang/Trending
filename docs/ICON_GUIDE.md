# Trending App 图标制作与适配指南

本文档记录了本应用标准图标的生成流程。为符合 Material 3 设计规范并确保多平台（Android/iOS）的完美适配，我们采用“官方矢量化资源 + 自动化切图工具”的方案，无需复杂的专业设计软件。

## 第一步：获取与定制基础矢量图标 (SVG)

我们使用 Google 官方的 Material Symbols 库来提取核心的 `Trending` 图标，并将其调整为更加现代、圆润的风格。

1. **访问图标库**：打开 [Google Fonts - Material Symbols](https://fonts.google.com/icons)。
2. **搜索基础图形**：在搜索框中输入关键词 `trending up`，找到标准的折线向上箭头。
3. **参数定制 (捏脸)**：在右侧的定制面板中进行以下调整：
    * **Fill (填充)**：开启 (设为 1)，使图标从线条变为实心图形。
    * **Weight (粗细)**：调大 (建议 700 左右)，让整体视觉更具分量感和现代感。
    * **Corner radius (圆角)**：拉到最大，消除锋利的直角，使其完全圆润。
4. **下载源文件**：确认效果后，点击下载 **SVG** 格式文件，作为我们的核心前景素材。

## 第二步：Android 自适应图标生成

在 Android Studio 中，我们可以直接利用下载的 SVG 快速生成符合 Android 规范的自适应图标 (Adaptive Icons)。

1. 打开 Android Studio 项目，右键点击 `res` 目录 -> **New** -> **Image Asset**。
2. **配置前景 (Foreground Layer)**：
    * `Asset Type` 选择 **Image**。
    * `Path` 选择刚刚下载的 `trending up` SVG 文件。
    * 在下方将前景颜色调整为**纯白色** (`#FFFFFF`)。
3. **配置背景 (Background Layer)**：
    * `Asset Type` 选择 **Color**。
    * 填入应用的 MD3 主品牌色：**`#6750A4`**。
4. **生成与完成**：点击 Next -> Finish，Android Studio 会自动在 `mipmap` 文件夹下生成最高 512x512 分辨率的自适应图标。

## 第三步：iOS 高清大图与多尺寸生成

苹果 App Store 强制要求提交 1024x1024 像素的无透明度直角方图。我们使用免费网页工具 IconKitchen 将前面准备的素材组合并输出。

1. **访问切图工具**：打开浏览器访问 [IconKitchen](https://icon.kitchen/)。
2. **设置背景 (Background)**：
    * 在左侧菜单点击 **Background**。
    * 选择 `Color` 并填入我们的品牌色：**`#6750A4`**。
3. **设置前景 (Foreground)**：
    * 在左侧菜单点击 **Foreground**。
    * 选择 `Image/SVG`，并上传之前下载的 `trending up` SVG 文件。
4. **细节微调**：观察中间的实时预览，通过调节左侧的 `Padding`（内边距）滑块，调整白色箭头在紫色背景中的占比，使其视觉居中且比例协调。
5. **下载与提取**：
    * 点击右上角的 **Download** 下载压缩包。
    * 解压后，进入 `ios` -> `AppIcon.appiconset` 目录。
    * 其中的 **`AppStore.png`** (1024x1024 分辨率) 即为可直接用于 iOS App Store 提审的最终高清母图，其余文件也可直接用于 iOS 项目的图片资产中。