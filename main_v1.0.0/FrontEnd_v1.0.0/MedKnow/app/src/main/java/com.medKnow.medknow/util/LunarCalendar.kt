package com.medKnow.medknow.util

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * 纯 Kotlin 农历转换工具（无需第三方依赖）
 * 支持 1900-01-31 ~ 2100-12-31 范围内的公历转农历
 */
object LunarCalendar {

    // 1900~2100 年农历数据
    private val lunarInfo = intArrayOf(
        0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,
        0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977,
        0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970,
        0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950,
        0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557,
        0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5b0, 0x14573, 0x052b0, 0x0a9a8, 0x0e950, 0x06aa0,
        0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0,
        0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b6a0, 0x195a6,
        0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570,
        0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0,
        0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5,
        0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930,
        0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530,
        0x05aa0, 0x076a3, 0x096d0, 0x04afb, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45,
        0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0,
        0x14b63, 0x09370, 0x049f8, 0x04970, 0x064b0, 0x168a6, 0x0ea50, 0x06b20, 0x1a6c4, 0x0aae0,
        0x0a2e0, 0x0d2e3, 0x0c960, 0x0d557, 0x0d4a0, 0x0da50, 0x05d55, 0x056a0, 0x0a6d0, 0x055d4,
        0x052d0, 0x0a9b8, 0x0a950, 0x0b4a0, 0x0b6a6, 0x0ad50, 0x055a0, 0x0aba4, 0x0a5b0, 0x052b0,
        0x0b273, 0x06930, 0x07337, 0x06aa0, 0x0ad50, 0x14b55, 0x04b60, 0x0a570, 0x054e4, 0x0d160,
        0x0e968, 0x0d520, 0x0daa0, 0x16aa6, 0x056d0, 0x04ae0, 0x0a9d4, 0x0a2d0, 0x0d150, 0x0f252,
        0x0d520
    )

    private val monthNames = arrayOf("正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "冬", "腊")

    // 农历日期名称
    private val dayNames = arrayOf(
        "初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十",
        "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
        "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十"
    )

    fun today(): String = of(LocalDate.now())

    fun of(date: LocalDate): String {
        val (year, month, day, _) = toLunar(date)
        return "农历${monthNames[month - 1]}月${dayNames[day - 1]}"
    }

    private data class Result(val year: Int, val month: Int, val day: Int, val isLeap: Boolean)

    private fun toLunar(date: LocalDate): Result {
        val base = LocalDate.of(1900, 1, 31)
        val offset = ChronoUnit.DAYS.between(base, date).toInt()

        var lunarYear = 1900
        while (lunarYear < 2100 && offset >= yearDays(lunarYear)) {
            offset.let { }
            lunarYear++
        }
        // 重新计算：offset 需减去之前所有整年的天数
        var days = ChronoUnit.DAYS.between(base, date).toInt()
        var y = 1900
        while (y < 2100 && days >= yearDays(y)) {
            days -= yearDays(y)
            y++
        }
        lunarYear = y
        var temp = days

        val leap = leapMonth(lunarYear)
        var month = 1
        var isLeap = false

        while (month <= 12) {
            var mDays: Int
            if (isLeap) {
                mDays = if (leap == month) leapDays(lunarYear) else monthDays(lunarYear, month)
            } else {
                mDays = monthDays(lunarYear, month)
            }
            if (temp < mDays) break
            temp -= mDays
            if (!isLeap && leap == month) {
                isLeap = true
            } else {
                isLeap = false
                month++
            }
        }

        return Result(lunarYear, month, temp + 1, isLeap)
    }

    // 某年闰月月份（0 = 无闰月）
    private fun leapMonth(year: Int): Int = lunarInfo[year - 1900] and 0xf

    // 某年闰月天数
    private fun leapDays(year: Int): Int =
        if (leapMonth(year) != 0) {
            if (lunarInfo[year - 1900] and 0x10000 != 0) 30 else 29
        } else 0

    // 某年某月天数
    private fun monthDays(year: Int, month: Int): Int =
        if (lunarInfo[year - 1900] and (0x10000 shr month) != 0) 30 else 29

    // 某年总天数
    private fun yearDays(year: Int): Int {
        var sum = 348
        var i = 0x8000
        while (i > 0x8) {
            if (lunarInfo[year - 1900] and i != 0) sum += 1
            i = i shr 1
        }
        return sum + leapDays(year)
    }
}