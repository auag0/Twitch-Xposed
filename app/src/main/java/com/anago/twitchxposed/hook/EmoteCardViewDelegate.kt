package com.anago.twitchxposed.hook

import android.content.Context
import android.view.View
import com.anago.twitchxposed.hook.base.BaseHook
import com.anago.twitchxposed.hook.download.NetworkImageWidgetDownload.downloadImageDialog
import com.anago.twitchxposed.utils.xposed.FieldUtils.getField
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

class EmoteCardViewDelegate(private val classLoader: ClassLoader) : BaseHook(classLoader) {
    private val clazz by lazy {
        XposedHelpers.findClass(
            "tv.twitch.android.shared.chat.emotecard.EmoteCardViewDelegate",
            classLoader
        )
    }

    override fun hook() {
        XposedBridge.hookAllConstructors(clazz, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val context = param.args[0] as Context
                val emoteImage = param.thisObject.getField<View>("emoteImage")

                emoteImage.setOnClickListener {
                    downloadImageDialog(context, "emote", it)
                }
            }
        })
    }
}