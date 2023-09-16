package com.anago.twitchxposed.hook

import android.content.Context
import android.view.View
import com.anago.twitchxposed.hook.base.BaseHook
import com.anago.twitchxposed.hook.download.NetworkImageWidgetDownload.downloadImageDialog
import com.anago.twitchxposed.utils.xposed.FieldUtils.getField
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

class NewProfileCardViewDelegate(private val classLoader: ClassLoader) : BaseHook(classLoader) {
    private val clazz by lazy {
        XposedHelpers.findClass(
            "tv.twitch.android.feature.profile.profilecard.NewProfileCardViewDelegate",
            classLoader
        )
    }

    override fun hook() {
        XposedBridge.hookAllConstructors(clazz, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val context = param.args[0] as Context
                val profileIcon = param.thisObject.getField<View>("profileIcon")
                val banner = param.thisObject.getField<View>("banner")

                profileIcon.setOnLongClickListener(
                    onLongClickListener(
                        context,
                        profileIcon,
                        "profile_image"
                    )
                )
                banner.setOnLongClickListener(
                    onLongClickListener(
                        context,
                        banner,
                        "banner_image"
                    )
                )
            }
        })
    }

    private fun onLongClickListener(
        context: Context,
        networkImageWidget: View,
        fileName: String
    ): View.OnLongClickListener {
        return View.OnLongClickListener {
            downloadImageDialog(context, fileName, networkImageWidget)
            true
        }
    }
}