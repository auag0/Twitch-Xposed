package com.anago.twitchxposed.hook.removed

import com.anago.twitchxposed.hook.BaseHook
import com.anago.twitchxposed.utils.Logger.logD
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

class ChatChannelListener : BaseHook() {
    override fun hook(classLoader: ClassLoader) {
        val clazz = XposedHelpers.findClass(
            "tv.twitch.android.sdk.ChatController\$ChatChannelListener",
            classLoader
        )

        XposedBridge.hookAllMethods(
            clazz,
            "chatChannelMessagesCleared",
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam) {
                    param.result = null
                    logD("clearChannelMessages Bypass")
                }
            })

        XposedBridge.hookAllMethods(
            clazz,
            "chatChannelUserMessagesCleared",
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam) {
                    param.result = null
                    logD("clearUserMessages Bypass")
                }
            })

        XposedBridge.hookAllMethods(
            clazz,
            "chatChannelMessageDeleted",
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam) {
                    param.result = null
                    logD("deleteChannelMessage Bypass")
                }
            })
    }
}