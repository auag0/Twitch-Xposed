package com.anago.twitchxposed.hook

import com.anago.twitchxposed.hook.emote.EmoteManager
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TwitchApplication : BaseHook() {
    override fun hook(classLoader: ClassLoader) {
        val clazz = XposedHelpers.findClass(
            "tv.twitch.android.app.consumer.TwitchApplication",
            classLoader
        )

        XposedBridge.hookAllMethods(clazz, "onCreate", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                CoroutineScope(Dispatchers.IO).launch {
                    EmoteManager.fetchEmotes()
                }
            }
        })
    }
}