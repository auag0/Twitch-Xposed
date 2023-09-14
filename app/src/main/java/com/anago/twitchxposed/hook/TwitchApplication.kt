package com.anago.twitchxposed.hook

import android.app.Application
import com.anago.twitchxposed.hook.base.BaseHook
import com.anago.twitchxposed.hook.emote.EmoteManager
import com.anago.twitchxposed.pref.PRefs.setupPrefs
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TwitchApplication(private val classLoader: ClassLoader) : BaseHook(classLoader) {
    private val clazz by lazy {
        XposedHelpers.findClass(
            "tv.twitch.android.app.consumer.TwitchApplication",
            classLoader
        )
    }

    override fun hook() {
        XposedBridge.hookAllMethods(clazz, "onCreate", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val application = param.thisObject as Application
                setupPrefs(application)
            }
            override fun afterHookedMethod(param: MethodHookParam) {
                CoroutineScope(Dispatchers.IO).launch {
                    EmoteManager.fetchEmotes()
                }
            }
        })
    }
}