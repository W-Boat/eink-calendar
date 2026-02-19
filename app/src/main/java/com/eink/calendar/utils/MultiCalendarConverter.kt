package com.eink.calendar.utils

import tyme4j.JiaoqiTable
import tyme4j.Lunar
import tyme4j.Solar
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 多日历转换工具
 * 使用 tyme4j 库提供农历、节气、干支等功能
 */
@Singleton
class MultiCalendarConverter @Inject constructor() {

    /**
     * 获取日期的农历信息
     */
    fun getLunarInfo(date: LocalDate): com.eink.calendar.domain.model.LunarInfo {
        val solar = Solar(date.year, date.monthValue, date.dayOfMonth)
        val lunar = solar.lunar

        val monthDisplay = getLunarMonthDisplay(lunar.month, lunar.isLeap)
        val dayDisplay = getLunarDayDisplay(lunar.day)
        val zodiac = getChineseZodiac(lunar.year)

        return com.eink.calendar.domain.model.LunarInfo(
            year = lunar.year,
            month = lunar.month,
            day = lunar.day,
            monthDisplay = monthDisplay,
            dayDisplay = dayDisplay,
            isLeapMonth = lunar.isLeap,
            chineseZodiac = zodiac,
            description = "农历${monthDisplay}${dayDisplay}"
        )
    }

    /**
     * 获取日期的星座信息
     */
    fun getZodiacInfo(date: LocalDate): com.eink.calendar.domain.model.ZodiacInfo {
        val month = date.monthValue
        val day = date.dayOfMonth

        val (name, englishName, symbol, dateRange, element, planet) = when {
            (month == 3 && day >= 21) || (month == 4 && day <= 19) ->
                Zodiac("白羊座", "Aries", "♈", "3.21-4.19", "火", "火星")
            (month == 4 && day >= 20) || (month == 5 && day <= 20) ->
                Zodiac("金牛座", "Taurus", "♉", "4.20-5.20", "土", "金星")
            (month == 5 && day >= 21) || (month == 6 && day <= 20) ->
                Zodiac("双子座", "Gemini", "♊", "5.21-6.20", "风", "水星")
            (month == 6 && day >= 21) || (month == 7 && day <= 22) ->
                Zodiac("巨蟹座", "Cancer", "♋", "6.21-7.22", "水", "月球")
            (month == 7 && day >= 23) || (month == 8 && day <= 22) ->
                Zodiac("狮子座", "Leo", "♌", "7.23-8.22", "火", "太阳")
            (month == 8 && day >= 23) || (month == 9 && day <= 22) ->
                Zodiac("处女座", "Virgo", "♍", "8.23-9.22", "土", "水星")
            (month == 9 && day >= 23) || (month == 10 && day <= 22) ->
                Zodiac("天秤座", "Libra", "♎", "9.23-10.22", "风", "金星")
            (month == 10 && day >= 23) || (month == 11 && day <= 21) ->
                Zodiac("天蝎座", "Scorpio", "♏", "10.23-11.21", "水", "冥王星")
            (month == 11 && day >= 22) || (month == 12 && day <= 21) ->
                Zodiac("射手座", "Sagittarius", "♐", "11.22-12.21", "火", "木星")
            else -> Zodiac("摩羯座", "Capricorn", "♑", "12.22-1.19", "土", "土星")
        }

        return com.eink.calendar.domain.model.ZodiacInfo(
            name = name,
            englishName = englishName,
            symbol = symbol,
            dateRange = dateRange,
            element = element,
            dominantPlanet = planet
        )
    }

    /**
     * 获取日期的节气信息
     */
    fun getSolarTermInfo(date: LocalDate): com.eink.calendar.domain.model.SolarTermInfo {
        val solar = Solar(date.year, date.monthValue, date.dayOfMonth)
        val jq = solar.jiaoqi

        return com.eink.calendar.domain.model.SolarTermInfo(
            name = jq.name,
            isMinorTerm = jq.type == 0,
            daysBefore = -1,  // 可根据需要计算
            date = if (jq.name.isNotEmpty()) date else null
        )
    }

    /**
     * 获取日期的干支和中国历法信息
     */
    fun getChineseInfo(date: LocalDate): com.eink.calendar.domain.model.ChineseInfo {
        val lunar = Solar(date.year, date.monthValue, date.dayOfMonth).lunar
        val year = lunar.year
        val month = lunar.month
        val day = lunar.day

        val heavenlyStem = getHeavenlyStem(year % 10)
        val earthlyBranch = getEarthlyBranch(year % 12)
        val ganZhi = "$heavenlyStem$earthlyBranch"
        val zodiac = getChineseZodiac(year)

        return com.eink.calendar.domain.model.ChineseInfo(
            heavenlyStem = heavenlyStem,
            earthlyBranch = earthlyBranch,
            ganZhi = ganZhi,
            horoscope = "生肖$zodiac"
        )
    }

    /**
     * 农历月份显示 (正月、二月等)
     */
    private fun getLunarMonthDisplay(month: Int, isLeap: Boolean): String {
        val monthNames = arrayOf(
            "", "正月", "二月", "三月", "四月", "五月", "六月",
            "七月", "八月", "九月", "十月", "冬月", "腊月"
        )
        val prefix = if (isLeap) "闰" else ""
        return prefix + monthNames.getOrNull(month) ?: "月"
    }

    /**
     * 农历日期显示 (初一、初二等)
     */
    private fun getLunarDayDisplay(day: Int): String {
        return when (day) {
            1, 11, 21 -> "${(day / 10).toInt()}十" + if (day % 10 == 1) "初一" else if (day == 11) "十一" else "廿一"
            else -> {
                val tens = day / 10
                val ones = day % 10
                val tensStr = when (tens) {
                    0 -> "初"
                    1 -> "十"
                    2 -> "廿"
                    3 -> "卅"
                    else -> ""
                }
                val onesStr = when (ones) {
                    0 -> "十"
                    1 -> "一"
                    2 -> "二"
                    3 -> "三"
                    4 -> "四"
                    5 -> "五"
                    6 -> "六"
                    7 -> "七"
                    8 -> "八"
                    9 -> "九"
                    else -> ""
                }
                tensStr + onesStr
            }
        }
    }

    /**
     * 天干
     */
    private fun getHeavenlyStem(index: Int): String {
        val stems = arrayOf("甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸")
        return stems.getOrNull(index) ?: ""
    }

    /**
     * 地支
     */
    private fun getEarthlyBranch(index: Int): String {
        val branches = arrayOf(
            "子", "丑", "寅", "卯", "辰", "巳",
            "午", "未", "申", "酉", "戌", "亥"
        )
        return branches.getOrNull(index) ?: ""
    }

    /**
     * 生肖
     */
    private fun getChineseZodiac(year: Int): String {
        val zodiacs = arrayOf(
            "鼠", "牛", "虎", "兔", "龙", "蛇",
            "马", "羊", "猴", "鸡", "狗", "猪"
        )
        return zodiacs.getOrNull((year - 1900) % 12) ?: ""
    }

    /**
     * 星座数据结构
     */
    private data class Zodiac(
        val name: String,
        val englishName: String,
        val symbol: String,
        val dateRange: String,
        val element: String,
        val planet: String
    )
}
