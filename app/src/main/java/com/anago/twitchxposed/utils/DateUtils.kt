package com.anago.twitchxposed.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {
    /** yyyy-MM-dd HH:mm:ss **/
    fun Long.millisToFormattedDate(dateFormat: String): String {
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
        return simpleDateFormat.format(Date(this))
    }

    fun Int.secToFormattedDate(dateFormat: String): String {
        return (this * 1000L).millisToFormattedDate(dateFormat)
    }

    fun calculateDaysDiff(fromDateMillis: Long, toDateMillis: Long): Int {
        val millisDiff = toDateMillis - fromDateMillis
        return TimeUnit.MILLISECONDS.toDays(millisDiff).toInt()
    }
}