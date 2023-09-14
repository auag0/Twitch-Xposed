package com.anago.twitchxposed.hook

import android.view.ViewGroup
import com.anago.twitchxposed.utils.Logger.logD
import com.anago.twitchxposed.utils.xposed.Field.getField
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers


class CommunityPointsButtonViewDelegate : BaseHook() {
    override fun hook(classLoader: ClassLoader) {
        val clazz = XposedHelpers.findClass(
            "tv.twitch.android.shared.community.points.viewdelegate.CommunityPointsButtonViewDelegate",
            classLoader
        )

        XposedBridge.hookAllMethods(clazz, "showClaimAvailable", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val buttonLayout = param.thisObject.getField<ViewGroup>("buttonLayout")
                buttonLayout.callOnClick()
                logD("Claim Clicked")
            }
        })
    }
}