package com.anago.twitchxposed.utils.xposed

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge

object MethodUtils {
    fun XC_MethodHook.MethodHookParam.invokeOriginalMethod(): Any? {
        return XposedBridge.invokeOriginalMethod(this.method, this.thisObject, this.args)
    }
}