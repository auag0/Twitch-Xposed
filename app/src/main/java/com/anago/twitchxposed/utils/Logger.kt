package com.anago.twitchxposed.utils

import android.util.Log

object Logger {
    private const val TAG = "Twitch-Xposed"

    fun logD(msg: Any?) {
        Log.d(TAG, msg.toString())
    }

    fun logE(msg: Any?) {
        Log.e(TAG, msg.toString())
    }
}