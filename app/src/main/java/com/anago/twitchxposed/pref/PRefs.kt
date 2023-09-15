package com.anago.twitchxposed.pref

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object PRefs {
    private lateinit var sharedPreferences: SharedPreferences

    fun setupPrefs(app: Application) {
        if (::sharedPreferences.isInitialized) {
            return
        }
        sharedPreferences = app.getSharedPreferences("${app.packageName}_preferences", MODE_PRIVATE)
    }

    var enableAutoClaimPoints by Pref("bool_auto_claim_point", true)
    var enablePreventMessages by Pref("bool_prevent_messages", true)
    var enableMessageLogs by Pref("bool_message_logs", true)

    var lastEmoteCacheTime by Pref("last_emote_cache_time", 0L)
    class Pref<T>(private val key: String, private val defValue: T) : ReadWriteProperty<Any, T> {
        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Any, property: KProperty<*>): T {
            return (sharedPreferences.all[key] as? T?) ?: defValue
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            sharedPreferences.edit {
                when (value) {
                    is String -> putString(key, value as String)
                    is Boolean -> putBoolean(key, value as Boolean)
                    is Float -> putFloat(key, value as Float)
                    is Int -> putInt(key, value as Int)
                    is Long -> putLong(key, value as Long)
                    else -> throw IllegalArgumentException("Unsupported type")
                }
            }
        }
    }
}