package com.anago.twitchxposed.utils.xposed

import de.robv.android.xposed.XposedHelpers

object Field {
    @Suppress("UNCHECKED_CAST")
    fun <T> Any.getField(fieldName: String): T {
        try {
            return XposedHelpers.getObjectField(this, fieldName) as T
        } catch (e: Exception) {
            throw ClassCastException()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> Class<*>.getStaticField(fieldName: String): T {
        try {
            return XposedHelpers.getStaticObjectField(this, fieldName) as T
        } catch (e: Exception) {
            throw ClassCastException()
        }
    }
}