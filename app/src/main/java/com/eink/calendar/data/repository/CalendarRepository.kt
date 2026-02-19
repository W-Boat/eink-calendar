package com.eink.calendar.data.repository

import com.eink.calendar.data.local.CalendarContentProvider
import com.eink.calendar.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 日历数据仓库
 * 统一数据访问接口，支持多个数据源（本地、远程）
 */
@Singleton
class CalendarRepository @Inject constructor(
    private val contentProvider: CalendarContentProvider
) {

    /**
     * 获取所有日历
     */
    fun getAllCalendars(): Flow<List<Calendar>> = flow {
        val calendars = contentProvider.getAllCalendars()
        emit(calendars)
    }

    /**
     * 获取指定日期范围内的所有事件
     */
    fun getEventsInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<CalendarEvent>> = flow {
        val startMillis = startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMillis = endDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val events = contentProvider.getEventsInRange(startMillis, endMillis)
        emit(events)
    }

    /**
     * 获取指定月份数据（用于月视图）
     */
    fun getMonthData(yearMonth: YearMonth): Flow<MonthData> = flow {
        val firstDay = yearMonth.atDay(1)
        val lastDay = yearMonth.atEndOfMonth()
        val events = contentProvider.getEventsInRange(
            firstDay.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            lastDay.atStartOfDay().plusDays(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )

        val today = LocalDate.now()
        val days = mutableListOf<DayCell>()

        // 获取首行的前几天（上个月的日期）
        val firstDayOfWeek = firstDay.dayOfWeek.value % 7  // 0=Sunday, 6=Saturday
        val previousMonth = yearMonth.minusMonths(1)
        val previousMonthLastDay = previousMonth.atEndOfMonth().dayOfMonth
        for (i in (previousMonthLastDay - firstDayOfWeek + 1)..previousMonthLastDay) {
            val date = previousMonth.atDay(i)
            days.add(DayCell(date, emptyList(), false))
        }

        // 当前月份的所有日期
        for (day in 1..yearMonth.atEndOfMonth().dayOfMonth) {
            val date = yearMonth.atDay(day)
            val dayEvents = events.filter { event ->
                val eventDate = event.startTime.toLocalDate()
                eventDate == date
            }
            days.add(DayCell(date, dayEvents, true, date == today))
        }

        // 获取尾行的后几天（下个月的日期）
        val remainingDays = 42 - days.size  // 6行 * 7列 = 42
        val nextMonth = yearMonth.plusMonths(1)
        for (i in 1..remainingDays) {
            val date = nextMonth.atDay(i)
            days.add(DayCell(date, emptyList(), false))
        }

        emit(MonthData(yearMonth.year, yearMonth.monthValue, days))
    }

    /**
     * 获取特定日期的数据（用于日视图）
     */
    fun getDayData(date: LocalDate): Flow<DayData> = flow {
        val events = contentProvider.getEventsInRange(
            date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            date.atStartOfDay().plusDays(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        ).sortedBy { it.startTime }

        emit(DayData(date, events))
    }

    /**
     * 获取周数据（用于周视图）
     */
    fun getWeekData(date: LocalDate): Flow<List<TimeSlot>> = flow {
        val weekStart = date.minusDays(date.dayOfWeek.value.toLong() - 1)
        val weekEnd = weekStart.plusDays(6)

        val events = contentProvider.getEventsInRange(
            weekStart.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            weekEnd.atStartOfDay().plusDays(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )

        val timeSlots = mutableListOf<TimeSlot>()
        var currentTime = weekStart.atStartOfDay()
        val weekEndTime = weekEnd.atStartOfDay().plusDays(1)

        while (currentTime < weekEndTime) {
            val nextTime = currentTime.plusHours(1)
            val slotEvents = events.filter { event ->
                !event.endTime.isBefore(currentTime) && !event.startTime.isAfter(nextTime)
            }
            val slotEvent = slotEvents.firstOrNull()
            timeSlots.add(TimeSlot(currentTime, nextTime, slotEvent, slotEvent == null))
            currentTime = nextTime
        }

        emit(timeSlots)
    }

    /**
     * 获取即将来临的事件
     */
    fun getUpcomingEvents(days: Int = 7): Flow<List<CalendarEvent>> = flow {
        val startDate = LocalDate.now()
        val endDate = startDate.plusDays(days.toLong())
        val events = contentProvider.getEventsInRange(
            startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            endDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        ).sortedBy { it.startTime }
        emit(events)
    }

    /**
     * 获取特定事件的详细信息
     */
    fun getEventDetails(eventId: Long): Flow<CalendarEvent?> = flow {
        val event = contentProvider.getEventById(eventId)
        emit(event)
    }

    /**
     * 获取事件提醒
     */
    fun getEventReminders(eventId: Long): Flow<List<EventReminder>> = flow {
        val reminders = contentProvider.getRemindersForEvent(eventId)
        emit(reminders)
    }
}
