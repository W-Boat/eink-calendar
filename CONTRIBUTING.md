# 贡献指南

感谢你对 E-ink Calendar 的关注！我们欢迎所有形式的贡献。

## 🤝 贡献方式

### 报告问题

发现 bug？请创建一个 GitHub Issue：

1. 使用清晰、描述性的标题
2. 详细描述问题现象
3. 提供重现步骤
4. 包含你的环境信息（Android 版本、设备型号等）

### 提交功能建议

有创意？我们很乐意听取：

1. 清晰解释功能的价值
2. 描述你期望的行为
3. 提供其他应用的参考案例（如适用）

### 代码贡献

欢迎 Pull Request！流程如下：

#### 1. Fork 并 Clone

```bash
git clone https://github.com/YOUR_USERNAME/eink-calendar.git
cd eink-calendar
git remote add upstream https://github.com/ORIGINAL_OWNER/eink-calendar.git
```

#### 2. 创建特性分支

```bash
git checkout -b feature/your-feature-name
```

使用有意义的分支名：
- `feature/` - 新功能
- `fix/` - 修复问题
- `refactor/` - 代码重构
- `docs/` - 文档更新
- `test/` - 测试相关

#### 3. 开发和测试

```bash
# 构建项目
./gradlew clean build

# 运行测试
./gradlew test

# 代码检查
./gradlew lint
```

#### 4. 提交更改

```bash
git add .
git commit -m "feat: 描述你的更改"
```

提交信息格式：
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Type**:
- `feat`: 新功能
- `fix`: 修复问题
- `refactor`: 代码重构
- `style`: 代码风格
- `test`: 测试相关
- `docs`: 文档更新
- `chore`: 构建或工具链

**Example**:
```
feat(calendar): 添加周视图功能

实现了基于时间槽的周视图，支持小时级别的事件显示。
包括导航、刷新和事件交互功能。

Closes #123
```

#### 5. 推送并创建 Pull Request

```bash
git push origin feature/your-feature-name
```

在 GitHub 创建 PR，填写完整的描述。

## 📋 代码规范

### Kotlin 风格指南

遵循 [Kotlin 官方编码规范](https://kotlinlang.org/docs/coding-conventions.html)：

```kotlin
// ✅ 正确
fun calculateSum(numbers: List<Int>): Int {
    var sum = 0
    for (number in numbers) {
        sum += number
    }
    return sum
}

// ✅ 使用集合函数
fun calculateSum(numbers: List<Int>): Int = numbers.sum()

// ❌ 避免
fun calculateSum(nums:List<Int>):Int{
    var s=0
    for(n in nums){s+=n}
    return s
}
```

### 命名规范

- **类名**: `PascalCase`
  ```kotlin
  class CalendarEvent { }
  ```

- **函数名**: `camelCase`
  ```kotlin
  fun getCalendarEvents() { }
  ```

- **常量**: `UPPER_SNAKE_CASE`
  ```kotlin
  companion object {
      const val MAX_RETRY_COUNT = 3
  }
  ```

- **变量**: `camelCase`
  ```kotlin
  val eventList = mutableListOf<Event>()
  ```

### 注释规范

为复杂逻辑添加有意义的注释：

```kotlin
/**
 * 计算两个日期之间的天数差
 *
 * @param startDate 开始日期
 * @param endDate 结束日期
 * @return 两个日期间隔的天数
 */
fun daysBetween(startDate: LocalDate, endDate: LocalDate): Long {
    return ChronoUnit.DAYS.between(startDate, endDate)
}
```

### Android 最佳实践

#### ViewModel 和 LiveData

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {

    private val _data = MutableLiveData<String>()
    val data: LiveData<String> = _data.asLiveData()

    fun loadData() {
        viewModelScope.launch {
            _data.value = repository.fetchData()
        }
    }
}
```

#### 依赖注入 (Hilt)

```kotlin
@AndroidEntryPoint
class MyActivity : AppCompatActivity() {
    private val viewModel: MyViewModel by viewModels()
}

@Singleton
class MyRepository @Inject constructor(
    private val apiClient: ApiClient
) { }
```

#### Flow 和 StateFlow

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // 加载数据
                _uiState.value = UiState.Success(data)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message)
            }
        }
    }
}
```

## 🧪 测试要求

编写单元测试覆盖新功能：

```kotlin
class CalendarRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val contentProvider = mockk<CalendarContentProvider>()
    private val repository = CalendarRepository(contentProvider)

    @Test
    fun `getEventsInRange returns correct events`() = runTest {
        // Arrange
        val startDate = LocalDate.of(2024, 1, 1)
        val endDate = LocalDate.of(2024, 1, 31)
        val expectedEvents = listOf(mockk<CalendarEvent>())

        coEvery { contentProvider.getEventsInRange(any(), any()) } returns expectedEvents

        // Act
        repository.getEventsInRange(startDate, endDate).test {
            // Assert
            assertEquals(expectedEvents, awaitItem())
            awaitComplete()
        }
    }
}
```

## 📚 文档

如果你的改动涉及：
- 新的公共 API
- 新的配置选项
- 重要的行为更改

请更新相关文档：
- `README.md` - 如果影响用户
- 代码文档 - KDoc 注释
- `CHANGELOG.md` - 记录变更

## 🎨 墨水屏特定的考虑

贡献涉及 UI 改动时，请考虑：

- **避免动画** - 墨水屏性能差
- **简化颜色** - 坚持黑白配色
- **大触摸区域** - 便于手写笔操作
- **快速响应** - 减少加载时间
- **刷新优化** - 最小化屏幕更新

## 💬 讨论

对于更大的改动或建议，请在 GitHub Discussions 中开启讨论。

## ✨ 行为规范

我们承诺为所有人创建包容的环境。请尊重他人，避免：

- 骚扰或歧视语言
- 个人攻击
- 任何形式的骚扰

违规者可能被禁止参与。

## 📞 需要帮助？

- 📖 查看 [README.md](README.md) 了解项目信息
- 💬 在 GitHub Discussions 提问
- 🐛 查看现有 Issues 寻找灵感

---

感谢你的贡献！🎉
