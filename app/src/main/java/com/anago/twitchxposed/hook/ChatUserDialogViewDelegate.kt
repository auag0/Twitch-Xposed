package com.anago.twitchxposed.hook

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.anago.twitchxposed.utils.xposed.Field.getField
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

class ChatUserDialogViewDelegate : BaseHook() {
    override fun hook(classLoader: ClassLoader) {
        val clazz = XposedHelpers.findClass(
            "tv.twitch.android.shared.chat.chatuserdialog.ChatUserDialogViewDelegate",
            classLoader
        )

        XposedBridge.hookAllMethods(clazz, "bind", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val context = param.thisObject.getField<Context>("context")
                val chatUser = param.args[2]
                val usernameText = param.thisObject.getField<TextView>("usernameText")

                val origText = usernameText.text
                val detailText = "( Detail )"
                val newText = "$origText$detailText"

                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        showUserDetailDialog(context, chatUser)
                    }
                }

                val bgSpan = BackgroundColorSpan(Color.YELLOW)

                val spannableString = SpannableString(newText).apply {
                    val startIndex = origText.length
                    val endIndex = newText.length
                    setSpan(
                        clickableSpan,
                        startIndex,
                        endIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    setSpan(
                        bgSpan,
                        startIndex,
                        endIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                usernameText.text = spannableString
                usernameText.movementMethod = LinkMovementMethod.getInstance()
            }
        })
    }

    private fun showUserDetailDialog(context: Context, chatUser: Any) {
        /*        val createDateMillis: Long = chatUser.getField("createDateMillis")
                val userId: Int = chatUser.getField("userId")
                val formattedDate = createDateMillis.toFormattedDate("yyyy-MM-dd")*/

        Toast.makeText(context, "coming soon.", Toast.LENGTH_LONG).show()
    }
}