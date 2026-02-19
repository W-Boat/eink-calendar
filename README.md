# E-ink Calendar

为电子墨水屏设备（如 Kindle、掌阅等）优化的精美日历时钟应用

![License](https://img.shields.io/badge/license-MIT-green)
![Android](https://img.shields.io/badge/Android-26%2B-blue)
![Build Status](https://github.com/YOUR_USERNAME/eink-calendar/actions/workflows/android-build.yml/badge.svg)

## 主要特性

### 多视图支持
- **月视图** - 整月日历概览，高效查看整月日程
- **周视图** - 按小时划分的周计划，详细查看时间安排
- **日视图** - 一天的完整日程，包括时间、位置、描述
- **议程视图** - 简化的事件列表，快速查看即将来临的事件

### 墨水屏优化
- **智能刷新模式** - 自动在 8 次部分刷新后执行完全刷新，清除残影
- **无动画设计** - 禁用不必要的过渡和动画，节省电量和刷新次数
- **黑白配色** - 简洁的黑白设计，完美适配墨水屏显示
- **大字体选项** - 为墨水屏用户提供舒适的阅读体验

### 日历功能
- **系统日历集成** - 直接读取 Android 系统日历数据
- **多日历支持** - 同时显示多个日历，支持不同颜色区分
- **事件详情** - 显示事件标题、时间、位置、描述
- **事件提醒** - 支持通知、邮件、闹铃等多种提醒方式
- **重复事件** - 支持日/周/月/年重复规则

### 同步功能
- **自动同步** - 可配置的同步间隔（默认 30 分钟）
- **后台同步** - 使用 WorkManager 进行后台数据同步
- **智能同步** - 仅同步变化的数据，节省流量和电量

### 设置项
- 通知开关
- 同步间隔设置
- 主题选择（浅色/深色）
- 字体大小调整
- 刷新模式设置

## 系统需求

- **最低 Android 版本**: Android 8.0 (API 26)
- **目标 Android 版本**: Android 15 (API 35)
- **Java 版本**: 17+
- **构建工具**: Gradle 8.2.0+

## 项目架构

### 分层设计 (Clean Architecture)

```
┌─────────────────────────────────┐
│      Presentation Layer (UI)    │
│  Activities, Fragments, Views   │
└──────────────┬──────────────────┘
               │
┌──────────────▼──────────────────┐
│      Domain Layer (Business)    │
│   Models, UseCases, Interfaces  │
└──────────────┬──────────────────┘
               │
┌──────────────▼──────────────────┐
│       Data Layer (Storage)      │
│ Repository, DataSources, APIs   │
└─────────────────────────────────┘
```

### 核心模块

| 模块 | 职责 | 主要类 |
|------|------|--------|
| **data** | 数据访问和存储 | `CalendarContentProvider`, `CalendarRepository` |
| **domain** | 业务逻辑和数据模型 | `Calendar`, `CalendarEvent`, `DayData` |
| **ui** | 用户界面和交互 | `MainActivity`, `MonthFragment`, `DayFragment` |
| **utils** | 工具和优化器 | `EinkDisplayOptimizer`, `DateUtils` |

### 使用的架构组件

- **MVVM** - Model-View-ViewModel 架构模式
- **Repository Pattern** - 统一数据访问接口
- **Hilt Dependency Injection** - 依赖注入框架
- **Flow & StateFlow** - 响应式数据流
- **Android CalendarContract** - 系统日历数据提供者

## 快速开始

### 克隆项目

```bash
git clone https://github.com/YOUR_USERNAME/eink-calendar.git
cd eink-calendar
```

### 使用 Android Studio 打开

1. 打开 Android Studio
2. 选择 `File > Open`
3. 选择项目目录
4. Gradle 将自动同步依赖

### 构建项目

#### Debug 版本（调试用）
```bash
./gradlew assembleDebug
```

#### Release 版本（发布用）
```bash
./gradlew assembleRelease
```

### 运行应用

```bash
./gradlew installDebug
./gradlew runDebug
```

### 运行测试

```bash
./gradlew test
```

## 依赖项

### 核心库
- **androidx.core:core-ktx** - Kotlin 扩展库
- **androidx.appcompat:appcompat** - 兼容性库
- **com.google.android.material** - Material Design 组件

### 架构框架
- **androidx.lifecycle** - ViewModel, LiveData, Flow
- **androidx.navigation** - 导航框架
- **com.google.dagger:hilt-android** - 依赖注入

### 数据管理
- **androidx.room:room** - 本地数据库 (可选)
- **androidx.datastore** - 偏好设置存储

### 异步和并发
- **org.jetbrains.kotlinx:kotlinx-coroutines** - 协程
- **androidx.work:work-runtime-ktx** - 后台任务

### 网络和 API
- **com.squareup.retrofit2:retrofit** - HTTP 客户端 (天气 API)
- **com.squareup.okhttp3:okhttp** - HTTP 库

## 墨水屏优化详解

### 刷新模式

E-ink 屏幕有三种刷新模式：

1. **完全刷新 (Full Refresh)**
   - 更新整个屏幕像素
   - 清除所有残影，显示最清晰
   - 耗时较长（通常 1-2 秒）
   - 用途：初始化、整页内容改变

2. **部分刷新 (Partial Refresh)**
   - 仅更新需要改变的像素
   - 速度快（通常 200-500ms）
   - 可能产生残影
   - 用途：内容更新、导航

3. **快速刷新 (Fast Refresh)**
   - 使用 3-4 灰度级别
   - 速度最快（通常 100-200ms）
   - 显示质量一般
   - 用途：时间更新、简单内容

### 最佳实践

应用实现了 **智能刷新模式 (Smart Refresh)**：
- 执行部分刷新快速更新内容
- 每 8 次部分刷新后自动执行一次完全刷新
- 用户可手动触发完全刷新
- 防止长时间部分刷新导致的残影堆积

### 电源优化

- 禁用不必要的动画和过渡
- 定时同步而非实时同步
- 后台任务批处理
- 支持睡眠模式

## 配置指南

### 同步间隔设置

在 `AppSettings` 中修改同步间隔（分钟）：

```kotlin
val settings = AppSettings(
    syncInterval = 30  // 30分钟同步一次
)
```

### 刷新模式设置

```kotlin
// 在主界面设置刷新模式
mainViewModel.setRefreshMode("SMART")  // FULL, PARTIAL, SMART
```

### 权限配置

应用需要以下权限（已在 `AndroidManifest.xml` 中声明）：

- `READ_CALENDAR` - 读取日历事件
- `WRITE_CALENDAR` - 修改日历事件
- `INTERNET` - 网络通信
- `POST_NOTIFICATIONS` - 发送通知

## 开发指南

### 代码规范

- **Kotlin 最佳实践** - 使用官方 Kotlin 编码规范
- **命名约定** - 类用 PascalCase，变量用 camelCase
- **注释** - 为复杂逻辑添加有意义的注释
- **测试** - 为关键功能编写单元测试

### 添加新功能

1. **数据模型** - 在 `domain/model` 中定义
2. **Repository** - 在 `data/repository` 中实现数据访问
3. **ViewModel** - 在 `ui` 中创建对应的 ViewModel
4. **UI** - 创建 Fragment 和 Layout

### 提交规范

使用 Conventional Commits：

```
feat: 添加新功能
fix: 修复问题
refactor: 代码重构
docs: 文档更新
test: 测试相关
chore: 构建系统或依赖更新
```

## CI/CD 流程

### GitHub Actions 工作流

项目配置了自动化的构建流程（`.github/workflows/android-build.yml`）：

1. **代码检查** - 运行 lint 检查代码质量
2. **单元测试** - 执行所有单元测试
3. **构建 Debug APK** - 生成调试版本
4. **构建 Release APK** - 生成发布版本（需要签名密钥）
5. **上传工件** - 保存 APK 和报告
6. **创建发布** - 标签提交时自动创建 Release

### 本地构建

```bash
# 完整构建流程
./gradlew clean build

# 仅构建 APK
./gradlew assembleDebug    # Debug 版本
./gradlew assembleRelease  # Release 版本

# 运行测试
./gradlew test

# 代码检查
./gradlew lint
```

## 故障排除

### 常见问题

**Q: 编译错误 "Failed to resolve android.provider.CalendarContract"**
- A: 确保 `minSdkVersion >= 14`（已在项目中设置为 26）

**Q: Hilt 注入失败**
- A: 确保 Activity 继承 `AppCompatActivity` 并添加 `@AndroidEntryPoint`

**Q: 墨水屏不显示更新**
- A: 检查刷新模式设置，尝试手动触发完全刷新

## 性能指标

目标性能指标（针对墨水屏优化）：

- **应用启动时间** < 2 秒
- **月视图加载** < 1.5 秒
- **日视图加载** < 1 秒
- **内存占用** < 50 MB
- **电池消耗** < 5% 每天（含同步）

## 许可证

本项目使用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 贡献者

欢迎提交 Issue 和 Pull Request！

## 技术支持

- **文档**: 查看 [docs](docs/) 目录
- **问题**: 在 GitHub Issues 中提交
- **讨论**: 在 GitHub Discussions 中讨论

## 参考项目

本项目参考了以下开源项目的最佳实践：

- [Etar Calendar](https://github.com/Etar-Group/Etar-Calendar) - Android 日历应用
- [EinkBro](https://github.com/plateaukao/einkbro) - 墨水屏浏览器优化
- [Material Calendar View](https://github.com/Applandeo/Material-Calendar-View) - 日历控件
- [Inkycal](https://github.com/aceinnolab/Inkycal) - 模块化仪表板

## 联系方式

- GitHub: [YOUR_USERNAME](https://github.com/YOUR_USERNAME)
- Email: your.email@example.com

---

Made for E-ink Device Users
