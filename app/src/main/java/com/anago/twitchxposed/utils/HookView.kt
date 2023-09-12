package com.anago.twitchxposed.utils

import android.view.View
import de.robv.android.xposed.XposedHelpers

object HookView {
    fun View?.onClick(): Boolean {
        var result = false
        if (this != null) {
            val mListenerInfo = XposedHelpers.getObjectField(this, "mListenerInfo")
            if (mListenerInfo != null) {
                val mOnClickListener =
                    XposedHelpers.getObjectField(mListenerInfo, "mOnClickListener")
                if (mOnClickListener != null) {
                    XposedHelpers.callMethod(mOnClickListener, "onClick", this)
                    result = true
                }
            }
        }
        return result
    }
}