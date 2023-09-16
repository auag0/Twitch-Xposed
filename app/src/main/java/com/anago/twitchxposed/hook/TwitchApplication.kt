package com.anago.twitchxposed.hook

import android.app.Application
import com.anago.twitchxposed.hook.base.BaseHook
import com.anago.twitchxposed.hook.emote.EmoteManager
import com.anago.twitchxposed.pref.PRefs.setupPrefs
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TwitchApplication(private val classLoader: ClassLoader) : BaseHook(classLoader) {
    private val clazz by lazy {
        XposedHelpers.findClass(
            "tv.twitch.android.app.consumer.TwitchApplication",
            classLoader
        )
    }

    companion object {
        private lateinit var app: Application

        fun getApp(): Application {
            return app
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun hook() {
        XposedBridge.hookAllMethods(clazz, "onCreate", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val application = param.thisObject as Application
                app = application
                setupPrefs(application)
            }

            override fun afterHookedMethod(param: MethodHookParam) {
                GlobalScope.launch(Dispatchers.IO) {
                    EmoteManager.fetchAllEmotes()
                }
            }
        })
    }
}