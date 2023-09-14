package com.anago.twitchxposed.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Date {
    /** yyyy-MM-dd HH:mm:ss **/
    fun Long.toFormattedDate(dateFormat: String): String {
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
        return simpleDateFormat.format(Date(this))
    }
}