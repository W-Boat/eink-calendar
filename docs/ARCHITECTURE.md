# 架构和设计文档

## 项目架构概览

E-ink Calendar 采用 **Clean Architecture** 和 **MVVM** 设计模式，确保代码的可维护性、可测试性和可扩展性。

```
┌─────────────────────────────────────────────────┐
│          Presentation Layer (UI)                 │
│  Activities, Fragments, ViewModels, Adapters    │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│           Domain Layer (Business)                │
│  Models, UseCases, Interfaces, Repositories     │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│           Data Layer (Storage & API)             │
│  DataSources, DAOs, NetworkClients, Mappers     │
└─────────────────────────────────────────────────┘
```

## 分层设计详解

### 1. Presentation Layer (表现层)

**职责**: UI 显示、用户交互处理、状态管理

**主要组件**:
- **Activities** - 应用入口和主容器
- **Fragments** - 各种视图页面
- **ViewModels** - UI 状态和业务逻辑协调
- **Adapters** - 列表/网格数据适配

**关键类**:
```
ui/
├── MainActivity                 - 主活动
├── month/
│   ├── MonthFragment           - 月视图
│   ├── MonthViewModel          - 月视图逻辑
│   └── MonthCalendarAdapter    - 日期适配器
├── day/
│   ├── DayFragment             - 日视图
│   └── DayViewModel            - 日视图逻辑
├── agenda/
│   ├── AgendaFragment          - 议程视图
│   └── AgendaViewModel         - 议程逻辑
└── MainViewModel               - 全局状态
```

### 2. Domain Layer (业务逻辑层)

**职责**: 核心业务逻辑、数据模型定义、接口抽象

**主要内容**:
- **Models** - 纯 Kotlin 数据类
- **Repositories** - 数据访问接口
- **UseCases** - 业务用例（可选）

**关键类**:
```
domain/
├── model/
│   ├── Calendar               - 日历信息
│   ├── CalendarEvent          - 事件模型
│   ├── DayData                - 日数据
│   ├── MonthData              - 月数据
│   ├── WeatherData            - 天气数据
│   ├── AppSettings            - 应用设置
│   └── ...其他模型
└── repository/
    └── CalendarRepository      - 日历数据仓库接口
```

### 3. Data Layer (数据访问层)

**职责**: 数据存储、检索、同步、API 通信

**主要内容**:
- **ContentProviders** - 系统内容提供者访问
- **Repositories** - 数据仓库实现
- **DataSources** - 本地/远程数据源
- **Mappers** - 数据模型转换

**关键类**:
```
data/
├── local/
│   ├── CalendarContentProvider  - 系统日历访问
│   ├── CalendarDAO              - 本地数据访问
│   └── LocalDataSource          - 本地数据源
├── remote/
│   ├── WeatherAPI               - 天气 API
│   └── CalendarSyncAPI          - 同步 API
└── repository/
    └── CalendarRepository        - 仓库实现
```

## 核心流程图

### 1. 应用启动流程

```
MainActivity.onCreate()
    ↓
Initialize Hilt DI
    ↓
Set up Navigation
    ↓
Load Initial Calendar Data
    ↓
Display MonthFragment
    ↓
Observe ViewModel Data
    ↓
Update UI
```

### 2. 月视图数据加载流程

```
用户打开应用 / 切换月份
    ↓
MonthFragment 中的 MonthViewModel
    ↓
调用 calendarRepository.getMonthData()
    ↓
CalendarRepository 查询系统日历
    ↓
CalendarContentProvider.getEventsInRange()
    ↓
系统 CalendarProvider 返回事件
    ↓
转换为 MonthData 对象
    ↓
StateFlow 发出数据
    ↓
Fragment 观察到数据变化
    ↓
更新 RecyclerView
    ↓
用户看到日历显示
```

### 3. 事件点击流程

```
用户点击某一天
    ↓
MonthCalendarAdapter.onDateClick()
    ↓
MonthViewModel.selectDate(date)
    ↓
更新 selectedDate StateFlow
    ↓
Fragment 观察到变化
    ↓
导航到 DayFragment
    ↓
DayViewModel 加载该天的事件
    ↓
显示日视图
```

## 数据模型关系

```
Calendar (1) ─── (N) CalendarEvent
    │
    ├─ id: Long
    ├─ displayName: String
    ├─ color: Int
    └─ ...

CalendarEvent (1) ─── (N) EventReminder
    │
    ├─ id: Long
    ├─ calendarId: Long (FK to Calendar)
    ├─ title: String
    ├─ startTime: LocalDateTime
    ├─ endTime: LocalDateTime
    ├─ rrule: String (重复规则)
    └─ ...

EventReminder
    │
    ├─ id: Long
    ├─ eventId: Long (FK to Event)
    ├─ minutes: Int
    └─ method: ReminderMethod

DayCell (用于月视图的单个日期)
    │
    ├─ date: LocalDate
    ├─ events: List<CalendarEvent>
    ├─ isCurrentMonth: Boolean
    └─ isToday: Boolean

MonthData (整个月的数据)
    │
    ├─ year: Int
    ├─ month: Int
    └─ days: List<DayCell> (42 个元素 = 6行×7列)

DayData (单天的数据)
    │
    ├─ date: LocalDate
    ├─ events: List<CalendarEvent>
    └─ weatherData: WeatherData?

TimeSlot (用于周/日视图的时间块)
    │
    ├─ startTime: LocalDateTime
    ├─ endTime: LocalDateTime
    ├─ event: CalendarEvent?
    └─ isAvailable: Boolean
```

## 依赖注入 (Hilt)

### 配置文件: `di/AppModule.kt`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideContentResolver(
        @ApplicationContext context: Context
    ): ContentResolver

    @Singleton
    @Provides
    fun provideCalendarContentProvider(
        contentResolver: ContentResolver
    ): CalendarContentProvider

    @Singleton
    @Provides
    fun provideCalendarRepository(
        contentProvider: CalendarContentProvider
    ): CalendarRepository
}
```

### 注入方式

```kotlin
// Activity
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
}

// Fragment
@AndroidEntryPoint
class MonthFragment : Fragment() {
    private val viewModel: MonthViewModel by viewModels()
}

// ViewModel
@HiltViewModel
class MonthViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository
) : ViewModel()
```

## 权限模型

### 声明的权限

```xml
<uses-permission android:name="android.permission.READ_CALENDAR" />
<uses-permission android:name="android.permission.WRITE_CALENDAR" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

### 运行时权限 (Android 6.0+)

```kotlin
private fun requestCalendarPermissions() {
    requestPermissions(
        arrayOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        ),
        PERMISSION_REQUEST_CODE
    )
}

override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    if (requestCode == PERMISSION_REQUEST_CODE) {
        if (grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 权限已授予，加载日历
            loadCalendarData()
        }
    }
}
```

## 状态管理

### StateFlow 用法

```kotlin
@HiltViewModel
class MonthViewModel @Inject constructor(
    private val repository: CalendarRepository
) : ViewModel() {

    // 私有可变状态
    private val _monthData = MutableStateFlow<MonthData?>(null)
    // 公开不可变状态流
    val monthData: StateFlow<MonthData?> = _monthData.asStateFlow()

    // 观察状态
    fun observeData() {
        viewModelScope.launch {
            repository.getMonthData(yearMonth).collect { data ->
                _monthData.value = data
            }
        }
    }
}

// Fragment 中观察
viewLifecycleOwner.lifecycleScope.launch {
    viewModel.monthData.collect { monthData ->
        // 更新 UI
    }
}
```

## 协程和异步处理

### 使用 viewModelScope

```kotlin
fun loadData() {
    viewModelScope.launch {  // Main 线程
        try {
            val data = withContext(Dispatchers.IO) {  // 切换到 IO 线程
                repository.fetchData()
            }
            _data.value = data  // 切回 Main 线程更新 UI
        } catch (e: Exception) {
            _error.value = e.message
        }
    }
}
```

### Flow 操作

```kotlin
// 在 Repository 中使用 Flow
fun getData(): Flow<Data> = flow {
    val data = contentProvider.query()
    emit(data)
}

// 在 ViewModel 中消费
init {
    viewModelScope.launch {
        repository.getData().collect { data ->
            _data.value = data
        }
    }
}
```

## 测试架构

### 单元测试

```kotlin
class CalendarRepositoryTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `getEventsInRange returns correct events`() = runTest {
        // Arrange
        val expectedEvents = listOf(mockk<CalendarEvent>())
        coEvery { contentProvider.getEventsInRange(any(), any()) }
            returns expectedEvents

        // Act
        val result = repository.getEventsInRange(startDate, endDate)

        // Assert
        result.test {
            assertEquals(expectedEvents, awaitItem())
            awaitComplete()
        }
    }
}
```

### UI 测试

```kotlin
@RunWith(AndroidJUnit4::class)
class MonthFragmentTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun `monthFragment displays calendar`() {
        onView(withId(R.id.calendar_recycler_view))
            .check(matches(isDisplayed()))
    }
}
```

## 性能优化

### 1. 数据加载优化

- **延迟加载**: 仅在需要时加载数据
- **缓存**: 缓存已加载的月份数据
- **分页**: 长列表使用分页加载

### 2. 内存优化

- **对象重用**: 避免频繁创建对象
- **弱引用**: 缓存中使用弱引用
- **及时释放**: 及时移除不需要的数据

### 3. UI 优化

- **ViewHolder 重用**: RecyclerView 自动处理
- **异步绑定**: 不在主线程进行重操作
- **避免布局嵌套**: 扁平化 XML 结构

### 4. 墨水屏特化优化

- **智能刷新**: 8 次部分刷新后完全刷新
- **禁用动画**: 减少不必要的屏幕更新
- **批量同步**: 减少网络请求频率

---

这个架构设计确保了应用的：
✅ 高可维护性 - 清晰的分层和职责划分
✅ 高可测试性 - 每层都可独立测试
✅ 高可扩展性 - 易于添加新功能
✅ 高性能 - 针对墨水屏设备优化
