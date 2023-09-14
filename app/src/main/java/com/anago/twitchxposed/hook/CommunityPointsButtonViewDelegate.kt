package com.anago.twitchxposed.hook

import android.view.ViewGroup
import com.anago.twitchxposed.hook.base.BaseHook
import com.anago.twitchxposed.utils.Logger.logD
import com.anago.twitchxposed.utils.xposed.FieldUtils.getField
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers


class CommunityPointsButtonViewDelegate(private val classLoader: ClassLoader) :
    BaseHook(classLoader) {
    private val clazz by lazy {
        XposedHelpers.findClass(
            "tv.twitch.android.shared.community.points.viewdelegate.CommunityPointsButtonViewDelegate",
            classLoader
        )
    }

    override fun hook() {
        XposedBridge.hookAllMethods(clazz, "showClaimAvailable", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val buttonLayout = param.thisObject.getField<ViewGroup>("buttonLayout")
                buttonLayout.callOnClick()
                logD("Claim Clicked")
            }
        })
    }
}