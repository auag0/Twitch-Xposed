package com.anago.twitchxposed.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService

object StringUtils {
    fun textCopy(context: Context, text: String) {
        val clipboardManager = getSystemService(context, ClipboardManager::class.java)
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(ClipData.newPlainText("", text))
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}