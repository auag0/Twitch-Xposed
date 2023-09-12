package com.anago.twitchxposed.hook

import android.view.ViewGroup
import com.anago.twitchxposed.utils.Logger.logD
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers


class CommunityPointsButtonViewDelegate : BaseHook() {
    override fun hook(classLoader: ClassLoader) {
        val clazz = XposedHelpers.findClass(
            "tv.twitch.android.shared.community.points.viewdelegate.CommunityPointsButtonViewDelegate",
            classLoader
        )

        XposedBridge.hookAllMethods(clazz, "showClaimAvailable", object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) {
                XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args)
                val buttonLayout =
                    XposedHelpers.getObjectField(param.thisObject, "buttonLayout") as? ViewGroup?
                        ?: throw Exception("buttonLayout not found")
                buttonLayout.callOnClick()
                logD("Claim Clicked")
            }
        })
    }
}