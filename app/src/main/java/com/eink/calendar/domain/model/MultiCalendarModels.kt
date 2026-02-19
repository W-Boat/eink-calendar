package com.eink.calendar.domain.model

import java.time.LocalDate

/**
 * 多种日历系统的日期信息
 * 包含公历、农历、星座、干支、节气等
 */
data class MultiCalendarDate(
    val gregorianDate: LocalDate,
    val lunarInfo: LunarInfo,
    val zodiacInfo: ZodiacInfo,
    val solarTermInfo: SolarTermInfo,
    val chineseInfo: ChineseInfo
)

/**
 * 农历信息
 */
data class LunarInfo(
    val year: Int,              // 农历年
    val month: Int,             // 农历月 (1-12, 闰月为负数)
    val day: Int,               // 农历日
    val monthDisplay: String,   // 显示用的月份 (正月、二月等)
    val dayDisplay: String,     // 显示用的日期 (初一、初二等)
    val isLeapMonth: Boolean,   // 是否闰月
    val chineseZodiac: String,  // 生肖 (鼠、牛、虎等)
    val description: String     // 完整描述 (如"农历正月初一")
)

/**
 * 星座信息
 */
data class ZodiacInfo(
    val name: String,           // 星座名称 (白羊座、金牛座等)
    val englishName: String,    // 英文名 (Aries, Taurus等)
    val symbol: String,         // 符号 (♈、♉等)
    val dateRange: String,      // 日期范围 (如 "3.21-4.19")
    val element: String,        // 元素 (火、土、风、水)
    val dominantPlanet: String  // 主宰行星
)

/**
 * 节气信息
 */
data class SolarTermInfo(
    val name: String,           // 节气名称 (立春、雨水等)
    val isMinorTerm: Boolean,   // 是否为节气(false为气)
    val daysBefore: Int,        // 距离下一个节气的天数
    val date: LocalDate?        // 本节气的日期 (如果今天是节气)
)

/**
 * 中国传统历法信息
 */
data class ChineseInfo(
    val heavenlyStem: String,   // 天干 (甲、乙、丙等)
    val earthlyBranch: String,  // 地支 (子、丑、寅等)
    val ganZhi: String,         // 干支 (甲子、乙丑等)
    val horoscope: String,      // 生肖属性和运势描述
    val fortuneFestival: List<String> = emptyList()  // 吉祥节日 (春节、中秋等)
)

/**
 * 节日信息
 */
data class FestivalInfo(
    val name: String,           // 节日名称
    val type: FestivalType,     // 节日类型
    val date: LocalDate?,       // 日期 (公历)
    val lunarDate: String?,     // 农历日期
    val description: String = ""// 节日描述
)

enum class FestivalType {
    LUNAR_NEW_YEAR,      // 春节
    LANTERN_FESTIVAL,    // 元宵节
    TOMB_SWEEPING_DAY,   // 清明节
    DRAGON_BOAT_FESTIVAL,// 端午节
    MID_AUTUMN_FESTIVAL, // 中秋节
    DOUBLE_NINTH_FESTIVAL, // 重阳节
    WINTER_SOLSTICE,     // 冬至
    QINGMING,            // 清明
    OFFICIAL_HOLIDAY,    // 法定假日
    TRADITIONAL_FESTIVAL,// 传统节日
    WESTERN_FESTIVAL     // 西方节日
}

/**
 * 日期显示配置
 */
data class CalendarDisplayConfig(
    val showLunar: Boolean = true,        // 显示农历
    val showZodiac: Boolean = true,       // 显示星座
    val showSolarTerm: Boolean = true,    // 显示节气
    val showChineseGanZhi: Boolean = true,// 显示干支
    val showFestivals: Boolean = true,    // 显示节日
    val showWeather: Boolean = false      // 显示天气
)
