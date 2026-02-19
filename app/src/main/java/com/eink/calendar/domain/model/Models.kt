package com.eink.calendar.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 日历事件数据模型
 */
data class CalendarEvent(
    val id: Long,
    val calendarId: Long,
    val title: String,
    val description: String? = null,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val location: String? = null,
    val isAllDay: Boolean = false,
    val color: Int? = null,
    val reminderMinutes: Int? = null,
    val repeatRule: String? = null  // RRULE格式
)

/**
 * 日历信息
 */
data class Calendar(
    val id: Long,
    val displayName: String,
    val description: String? = null,
    val accountName: String? = null,
    val accountType: String? = null,
    val color: Int,
    val visible: Boolean = true,
    val isPrimary: Boolean = false,
    val syncEvents: Boolean = true
)

/**
 * 事件提醒
 */
data class EventReminder(
    val id: Long,
    val eventId: Long,
    val minutes: Int,
    val method: ReminderMethod = ReminderMethod.NOTIFICATION
)

enum class ReminderMethod {
    NOTIFICATION,
    EMAIL,
    ALARM
}

/**
 * 单日数据（日视图）
 */
data class DayData(
    val date: LocalDate,
    val events: List<CalendarEvent>,
    val weatherData: WeatherData? = null
)

/**
 * 天气数据
 */
data class WeatherData(
    val temperature: Float,
    val description: String,
    val humidity: Int,
    val windSpeed: Float,
    val icon: String  // 天气图标代码
)

/**
 * 时间槽（用于日/周视图的时间块）
 */
data class TimeSlot(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val event: CalendarEvent? = null,
    val isAvailable: Boolean = true
)

/**
 * 月份数据（用于月视图）
 */
data class MonthData(
    val year: Int,
    val month: Int,
    val days: List<DayCell>
)

data class DayCell(
    val date: LocalDate,
    val events: List<CalendarEvent>,
    val isCurrentMonth: Boolean,
    val isToday: Boolean = false,
    val eventCount: Int = events.size
)

/**
 * 同步状态
 */
data class SyncState(
    val isSyncing: Boolean = false,
    val lastSyncTime: LocalDateTime? = null,
    val error: String? = null,
    val progress: Int = 0  // 0-100
)

/**
 * 应用设置
 */
data class AppSettings(
    val enableNotifications: Boolean = true,
    val enableWeather: Boolean = false,
    val weatherLocation: String? = null,
    val theme: Theme = Theme.LIGHT,
    val fontSize: FontSize = FontSize.NORMAL,
    val syncInterval: Int = 30,  // 分钟
    val refreshMode: RefreshMode = RefreshMode.SMART,
    val enableFastRefresh: Boolean = true
)

enum class Theme {
    LIGHT,
    DARK
}

enum class FontSize {
    SMALL,
    NORMAL,
    LARGE
}

enum class RefreshMode {
    FULL,      // 完全刷新
    PARTIAL,   // 部分刷新
    SMART      // 智能刷新
}
