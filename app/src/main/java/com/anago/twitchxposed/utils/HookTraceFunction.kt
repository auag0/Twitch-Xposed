package com.anago.twitchxposed.utils

object HookTraceFunction {
    fun printTrace() {
        try {
            throw Exception("Twitch-Xposed")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}