package com.anago.twitchxposed.utils.xposed

import android.annotation.SuppressLint
import android.content.Context

object ViewUtils {
    @SuppressLint("DiscouragedApi")
    fun getLayoutId(
        context: Context,
        name: String,
        packageName: String = context.packageName
    ): Int {
        return context.resources.getIdentifier(name, "layout", packageName)
    }

    @SuppressLint("DiscouragedApi")
    fun getViewId(
        context: Context,
        name: String,
        packageName: String = context.packageName
    ): Int {
        return context.resources.getIdentifier(name, "id", packageName)
    }
}