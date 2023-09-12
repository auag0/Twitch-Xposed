package com.anago.twitchxposed.hook.removed

import com.anago.twitchxposed.hook.BaseHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers


class ChatConnectionController : BaseHook() {
    override fun hook(classLoader: ClassLoader) {
        val clazz = XposedHelpers.findClass(
            "tv.twitch.android.shared.chat.observables.ChatConnectionController",
            classLoader
        )

        XposedBridge.hookAllMethods(
            clazz,
            "handleChannelMessagesCleared",
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam) {
                    param.result = null
                }
            })

        XposedBridge.hookAllMethods(
            clazz,
            "handleChannelUserMessagesCleared",
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam) {
                    param.result = null
                }
            })

        XposedBridge.hookAllMethods(
            clazz,
            "handleChannelMessageCleared",
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam) {
                    param.result = null
                }
            })
    }
}