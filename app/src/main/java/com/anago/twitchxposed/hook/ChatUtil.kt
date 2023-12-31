package com.anago.twitchxposed.hook

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import com.anago.twitchxposed.hook.base.BaseHook
import com.anago.twitchxposed.pref.PRefs
import com.anago.twitchxposed.utils.xposed.MethodUtils.invokeOriginalMethod
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

class ChatUtil(private val classLoader: ClassLoader) : BaseHook(classLoader) {
    private val clazz by lazy {
        XposedHelpers.findClass(
            "tv.twitch.android.shared.chat.util.ChatUtil\$Companion",
            classLoader
        )
    }

    override fun hook() {
        XposedBridge.hookAllMethods(
            clazz,
            "createDeletedSpanFromChatMessageSpan",
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam): Any {
                    if (!PRefs.enablePreventMessages) {
                        return param.invokeOriginalMethod()!!
                    }
                    val origMessage = param.args[1] as Spanned
                    val deletedText = "(DELETED)"
                    val userNameEndIndex = origMessage.lastIndexOf(": ")
                    val newMessage = SpannableStringBuilder(origMessage).append(deletedText).apply {
                        // メッセージ部分の色の変更
                        /*                        setSpan(
                                                    ForegroundColorSpan(Color.RED),
                                                    userNameEndIndex,
                                                    origMessage.length,
                                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                                )*/
                        // (DELETED) の文字の色とサイズの変更
                        setSpan(
                            ForegroundColorSpan(Color.RED),
                            origMessage.length,
                            origMessage.length + deletedText.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        setSpan(
                            RelativeSizeSpan(0.8f),
                            origMessage.length,
                            origMessage.length + deletedText.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    return newMessage
                }
            })
    }
}