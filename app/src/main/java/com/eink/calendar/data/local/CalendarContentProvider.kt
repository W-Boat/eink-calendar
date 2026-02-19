package com.eink.calendar.data.local

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import com.eink.calendar.domain.model.Calendar
import com.eink.calendar.domain.model.CalendarEvent
import com.eink.calendar.domain.model.EventReminder
import com.eink.calendar.domain.model.ReminderMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 系统日历内容提供者访问层
 * 使用 Android CalendarContract 读写日历数据
 */
@Singleton
class CalendarContentProvider @Inject constructor(
    private val contentResolver: ContentResolver
) {

    // ==================== 日历查询 ====================

    suspend fun getAllCalendars(): List<Calendar> = withContext(Dispatchers.IO) {
        val calendars = mutableListOf<Calendar>()
        val cursor = contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            CALENDAR_PROJECTION,
            null, null,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
        ) ?: return@withContext emptyList()

        cursor.use {
            while (it.moveToNext()) {
                calendars.add(it.toCalendar())
            }
        }
        calendars
    }

    // ==================== 事件查询 ====================

    suspend fun getEventsInRange(
        startMillis: Long,
        endMillis: Long,
        calendarIds: List<Long>? = null
    ): List<CalendarEvent> = withContext(Dispatchers.IO) {
        val events = mutableListOf<CalendarEvent>()

        val uriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(uriBuilder, startMillis)
        ContentUris.appendId(uriBuilder, endMillis)
        val uri = uriBuilder.build()

        val selection = if (calendarIds != null && calendarIds.isNotEmpty()) {
            "${CalendarContract.Instances.CALENDAR_ID} IN (${calendarIds.joinToString(",")})"
        } else null

        val cursor = contentResolver.query(
            uri,
            INSTANCE_PROJECTION,
            selection,
            null,
            CalendarContract.Instances.BEGIN
        ) ?: return@withContext emptyList()

        cursor.use {
            while (it.moveToNext()) {
                events.add(it.toCalendarEvent())
            }
        }
        events
    }

    suspend fun getEventById(eventId: Long): CalendarEvent? = withContext(Dispatchers.IO) {
        val cursor = contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            EVENT_PROJECTION,
            "${CalendarContract.Events._ID} = ?",
            arrayOf(eventId.toString()),
            null
        ) ?: return@withContext null

        cursor.use {
            if (it.moveToFirst()) it.toCalendarEventFull()
            else null
        }
    }

    suspend fun getRemindersForEvent(eventId: Long): List<EventReminder> = withContext(Dispatchers.IO) {
        val reminders = mutableListOf<EventReminder>()
        val cursor = contentResolver.query(
            CalendarContract.Reminders.CONTENT_URI,
            REMINDER_PROJECTION,
            "${CalendarContract.Reminders.EVENT_ID} = ?",
            arrayOf(eventId.toString()),
            null
        ) ?: return@withContext emptyList()

        cursor.use {
            while (it.moveToNext()) {
                reminders.add(it.toEventReminder())
            }
        }
        reminders
    }

    // ==================== Cursor 扩展 ====================

    private fun Cursor.toCalendar() = Calendar(
        id = getLong(getColumnIndexOrThrow(CalendarContract.Calendars._ID)),
        displayName = getString(getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)) ?: "",
        description = getString(getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_DESCRIPTION)),
        accountName = getString(getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_NAME)),
        accountType = getString(getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_TYPE)),
        color = getInt(getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_COLOR)),
        visible = getInt(getColumnIndexOrThrow(CalendarContract.Calendars.VISIBLE)) == 1,
        syncEvents = getInt(getColumnIndexOrThrow(CalendarContract.Calendars.SYNC_EVENTS)) == 1
    )

    private fun Cursor.toCalendarEvent() = CalendarEvent(
        id = getLong(getColumnIndexOrThrow(CalendarContract.Instances.EVENT_ID)),
        calendarId = getLong(getColumnIndexOrThrow(CalendarContract.Instances.CALENDAR_ID)),
        title = getString(getColumnIndexOrThrow(CalendarContract.Instances.TITLE)) ?: "(无标题)",
        description = getString(getColumnIndexOrThrow(CalendarContract.Instances.DESCRIPTION)),
        startTime = getLong(getColumnIndexOrThrow(CalendarContract.Instances.BEGIN)).toLocalDateTime(),
        endTime = getLong(getColumnIndexOrThrow(CalendarContract.Instances.END)).toLocalDateTime(),
        location = getString(getColumnIndexOrThrow(CalendarContract.Instances.EVENT_LOCATION)),
        isAllDay = getInt(getColumnIndexOrThrow(CalendarContract.Instances.ALL_DAY)) == 1,
        color = getInt(getColumnIndexOrThrow(CalendarContract.Instances.CALENDAR_COLOR))
    )

    private fun Cursor.toCalendarEventFull() = CalendarEvent(
        id = getLong(getColumnIndexOrThrow(CalendarContract.Events._ID)),
        calendarId = getLong(getColumnIndexOrThrow(CalendarContract.Events.CALENDAR_ID)),
        title = getString(getColumnIndexOrThrow(CalendarContract.Events.TITLE)) ?: "(无标题)",
        description = getString(getColumnIndexOrThrow(CalendarContract.Events.DESCRIPTION)),
        startTime = getLong(getColumnIndexOrThrow(CalendarContract.Events.DTSTART)).toLocalDateTime(),
        endTime = getLong(getColumnIndexOrThrow(CalendarContract.Events.DTEND)).toLocalDateTime(),
        location = getString(getColumnIndexOrThrow(CalendarContract.Events.EVENT_LOCATION)),
        isAllDay = getInt(getColumnIndexOrThrow(CalendarContract.Events.ALL_DAY)) == 1,
        repeatRule = getString(getColumnIndexOrThrow(CalendarContract.Events.RRULE))
    )

    private fun Cursor.toEventReminder() = EventReminder(
        id = getLong(getColumnIndexOrThrow(CalendarContract.Reminders._ID)),
        eventId = getLong(getColumnIndexOrThrow(CalendarContract.Reminders.EVENT_ID)),
        minutes = getInt(getColumnIndexOrThrow(CalendarContract.Reminders.MINUTES)),
        method = when (getInt(getColumnIndexOrThrow(CalendarContract.Reminders.METHOD))) {
            CalendarContract.Reminders.METHOD_EMAIL -> ReminderMethod.EMAIL
            CalendarContract.Reminders.METHOD_ALARM -> ReminderMethod.ALARM
            else -> ReminderMethod.NOTIFICATION
        }
    )

    private fun Long.toLocalDateTime(): LocalDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())

    // ==================== 列定义 ====================

    companion object {
        private val CALENDAR_PROJECTION = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.CALENDAR_DESCRIPTION,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.ACCOUNT_TYPE,
            CalendarContract.Calendars.CALENDAR_COLOR,
            CalendarContract.Calendars.VISIBLE,
            CalendarContract.Calendars.SYNC_EVENTS
        )

        private val INSTANCE_PROJECTION = arrayOf(
            CalendarContract.Instances.EVENT_ID,
            CalendarContract.Instances.CALENDAR_ID,
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.DESCRIPTION,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
            CalendarContract.Instances.EVENT_LOCATION,
            CalendarContract.Instances.ALL_DAY,
            CalendarContract.Instances.CALENDAR_COLOR,
            CalendarContract.Instances.RRULE
        )

        private val EVENT_PROJECTION = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.ALL_DAY,
            CalendarContract.Events.RRULE
        )

        private val REMINDER_PROJECTION = arrayOf(
            CalendarContract.Reminders._ID,
            CalendarContract.Reminders.EVENT_ID,
            CalendarContract.Reminders.MINUTES,
            CalendarContract.Reminders.METHOD
        )
    }
}
