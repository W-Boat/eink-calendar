# 墨水屏日历应用 - 快速开始指南

## 📱 项目概览

这是一个为电子墨水屏设备（如 Amazon Kindle、掌阅等）专门优化的 Android 日历应用。

**核心优势**：
- 🎨 为墨水屏优化的简洁黑白设计
- ⚡ 智能刷新模式，自动清除残影
- 📅 完整的日历功能（月/周/日/议程视图）
- 🔋 极低电耗设计，续航时间长
- 🔄 后台同步和自动更新

## 🔧 环境配置

### 必需工具

1. **Android Studio** (推荐最新版本)
   - 下载：https://developer.android.com/studio

2. **JDK 17+**
   - Android Studio 通常内置

3. **Git**
   - 用于克隆项目

### 项目设置

```bash
# 1. 克隆项目
git clone https://github.com/YOUR_USERNAME/eink-calendar.git
cd eink-calendar

# 2. 使用 Android Studio 打开
# File > Open > 选择项目目录

# 3. Gradle 会自动同步依赖（耐心等待）

# 4. 同步完成后就可以编译运行了
```

## 🏗️ 项目结构

```
eink-calendar/
├── app/                          # 主应用模块
│   ├── src/main/
│   │   ├── java/com/eink/calendar/
│   │   │   ├── data/             # 数据层
│   │   │   │   ├── local/       # 本地数据源
│   │   │   │   └── repository/  # 数据仓库
│   │   │   ├── domain/           # 业务逻辑层
│   │   │   │   └── model/       # 数据模型
│   │   │   ├── ui/               # 表现层
│   │   │   │   ├── month/       # 月视图
│   │   │   │   ├── day/         # 日视图
│   │   │   │   ├── agenda/      # 议程视图
│   │   │   │   └── MainActivity # 主活动
│   │   │   └── utils/            # 工具类
│   │   └── res/                  # 资源文件
│   │       ├── layout/           # 布局文件
│   │       ├── values/           # 颜色、字符串等
│   │       └── menu/             # 菜单定义
│   └── build.gradle.kts          # 构建配置
├── .github/
│   └── workflows/                # CI/CD 工作流
├── README.md                     # 项目主文档
├── CONTRIBUTING.md               # 贡献指南
└── LICENSE                       # 许可证
```

## 🚀 构建和运行

### 使用 Android Studio

1. **同步 Gradle**
   - `File` → `Sync Now`

2. **选择目标设备/模拟器**
   - 虚拟设备：`Device Manager` → 创建模拟器
   - 真机：连接 USB 并启用开发者模式

3. **运行应用**
   - `Run` → `Run 'app'`（或按 `Shift+F10`）

### 使用命令行

```bash
# 构建 Debug 版本
./gradlew assembleDebug

# 安装到设备
./gradlew installDebug

# 一键构建和运行
./gradlew runDebug

# 构建 Release 版本
./gradlew assembleRelease
```

## 📚 核心概念

### MVVM 架构

应用使用 Model-View-ViewModel (MVVM) 架构：

```
┌──────────────┐
│   Fragment   │ (View)
└──────┬───────┘
       │ 观察
       ↓
┌──────────────┐
│  ViewModel   │ (ViewModel)
└──────┬───────┘
       │ 请求
       ↓
┌──────────────┐
│ Repository   │ (Model)
└──────┬───────┘
       │ 读写
       ↓
┌──────────────┐
│  LocalData   │ (Data Source)
└──────────────┘
```

### 数据流

```
用户交互 → Fragment → ViewModel → Repository → ContentProvider → 系统日历

系统日历 → ContentProvider → Repository → StateFlow → Fragment → UI 更新
```

## 💡 核心功能实现

### 1. 读取系统日历

```kotlin
// 获取所有日历
val calendars = calendarRepository.getAllCalendars()

// 获取特定时间范围的事件
val events = calendarRepository.getEventsInRange(startDate, endDate)
```

### 2. 月视图显示

```kotlin
// ViewModel 中加载月份数据
fun loadMonthData() {
    viewModelScope.launch {
        calendarRepository.getMonthData(yearMonth).collect { monthData ->
            _monthData.value = monthData
        }
    }
}
```

### 3. 墨水屏优化

```kotlin
// 设置刷新模式
einkDisplayOptimizer.setRefreshMode("SMART")

// 禁用动画
einkDisplayOptimizer.disableAnimationsForView(view)
```

## 🧪 开发工作流

### 添加新功能（例如：天气显示）

1. **定义数据模型** (`domain/model/`)
   ```kotlin
   data class WeatherData(
       val temperature: Float,
       val description: String
   )
   ```

2. **实现数据访问** (`data/repository/`)
   ```kotlin
   suspend fun getWeather(location: String): WeatherData
   ```

3. **创建 ViewModel** (`ui/weather/`)
   ```kotlin
   @HiltViewModel
   class WeatherViewModel(
       private val repository: WeatherRepository
   ) : ViewModel()
   ```

4. **设计 UI** (`ui/weather/fragment_weather.xml`)
   ```xml
   <TextView android:text="@{viewModel.temperature}" />
   ```

5. **创建 Fragment** (`ui/weather/WeatherFragment.kt`)
   ```kotlin
   @AndroidEntryPoint
   class WeatherFragment : Fragment()
   ```

### 运行测试

```bash
# 运行所有单元测试
./gradlew test

# 运行特定测试类
./gradlew test --tests CalendarRepositoryTest

# 运行 UI 测试
./gradlew connectedAndroidTest
```

## 🔍 调试技巧

### 查看日志

```bash
# 实时日志
./gradlew runDebug --info

# 过滤日志
./gradlew runDebug 2>&1 | grep "E-ink\|Calendar"
```

### Android Studio 调试

1. 在代码中设置断点
2. 点击 `Debug` 按钮（或 `Shift+F9`）
3. 使用调试工具栏暂停/继续执行

### 查看数据库内容

```bash
# 进入 ADB shell
adb shell

# 访问应用数据目录
cd /data/data/com.eink.calendar

# 查看 SharedPreferences
sqlite3 shared_prefs/preferences.xml
```

## 🎨 墨水屏适配检查清单

在提交修改前，确保：

- [ ] 没有使用动画或过渡
- [ ] 只使用黑白两色（或灰度）
- [ ] 大字体易读性良好
- [ ] 触摸区域足够大（最少 48dp × 48dp）
- [ ] 没有频繁刷新（防止伤屏）
- [ ] 在实际墨水屏设备上测试过（如可能）

## 📊 性能目标

| 指标 | 目标 | 检查方式 |
|------|------|---------|
| 应用启动 | < 2 秒 | 使用 Android Profiler |
| 月视图加载 | < 1.5 秒 | 模拟器或真机 |
| 内存使用 | < 50 MB | 内存监控器 |
| 电池消耗 | < 5%/天 | 实际使用统计 |

## 🐛 常见问题

**Q: "Gradle sync failed"**
- A: 检查网络连接，或尝试 `File > Invalidate Caches > Restart`

**Q: 模拟器启动缓慢**
- A: 使用 Android Studio 的轻量级模拟器，或在真机上测试

**Q: "CalendarProvider 权限错误"**
- A: 在 AndroidManifest.xml 中添加 `READ_CALENDAR` 权限

**Q: 收不到日历事件**
- A: 检查 Android 版本是否 >= 26，日历权限是否授予

## 📖 相关文档

- [项目 README](README.md) - 完整项目信息
- [贡献指南](CONTRIBUTING.md) - 代码贡献规范
- [Android 开发文档](https://developer.android.com/) - 官方文档
- [Kotlin 编码规范](https://kotlinlang.org/docs/coding-conventions.html)

## 🚀 下一步

1. ✅ 克隆项目并打开
2. ✅ 构建并运行应用
3. ✅ 浏览源代码理解架构
4. ✅ 尝试修改代码并重新构建
5. ✅ 提交你的第一个 Pull Request！

---

需要帮助？在 GitHub Issues 中提问或阅读 CONTRIBUTING.md！
