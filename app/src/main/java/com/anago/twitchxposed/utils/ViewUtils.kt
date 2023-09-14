package com.anago.twitchxposed.utils

import android.content.Context
import android.view.LayoutInflater

object ViewUtils {
    val Context.layoutInflater: LayoutInflater
        get() = LayoutInflater.from(this)
}