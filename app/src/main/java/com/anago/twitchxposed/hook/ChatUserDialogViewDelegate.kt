package com.anago.twitchxposed.hook

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.anago.twitchxposed.MainHook.Companion.modResource
import com.anago.twitchxposed.R
import com.anago.twitchxposed.hook.base.BaseHook
import com.anago.twitchxposed.utils.DateUtils.calculateDaysDiff
import com.anago.twitchxposed.utils.DateUtils.toFormattedDate
import com.anago.twitchxposed.utils.StringUtils.textCopy
import com.anago.twitchxposed.utils.UrlUtils.openWebPage
import com.anago.twitchxposed.utils.ViewUtils.layoutInflater
import com.anago.twitchxposed.utils.xposed.FieldUtils.getField
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

class ChatUserDialogViewDelegate(private val classLoader: ClassLoader) : BaseHook(classLoader) {
    private val clazz by lazy {
        XposedHelpers.findClass(
            "tv.twitch.android.shared.chat.chatuserdialog.ChatUserDialogViewDelegate",
            classLoader
        )
    }

    override fun hook() {
        XposedBridge.hookAllMethods(clazz, "bind", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val context = param.thisObject.getField<Context>("context")
                val chatUser = param.args[2]
                val usernameText = param.thisObject.getField<TextView>("usernameText")

                val origText = usernameText.text
                val detailText = "[ Detail ]"
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

    private fun createDetailItem(
        context: Context,
        linearLayout: LinearLayout,
        iconResId: Int,
        titleText: String,
        descriptionText: String?,
        onClick: () -> Unit
    ): View? {
        val layoutInflater = context.layoutInflater
        val layoutId = modResource.getLayout(R.layout.chat_user_detail_item)
        return layoutInflater.inflate(layoutId, linearLayout, false).apply {
            findViewById<ImageView>(R.id.icon).apply {
                setImageDrawable(ResourcesCompat.getDrawable(modResource, iconResId, null))
            }
            findViewById<TextView>(R.id.title).apply {
                text = titleText
            }
            findViewById<TextView>(R.id.description).apply {
                if (descriptionText.isNullOrBlank()) {
                    visibility = View.GONE
                } else {
                    text = descriptionText
                }
            }
            setOnClickListener { onClick() }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showUserDetailDialog(context: Context, chatUser: Any) {
        val createDateMillis: Long = chatUser.getField("createDateMillis")
        val userId: Int = chatUser.getField("userId")
        val displayName: String = chatUser.getField("displayName")
        val username: String = chatUser.getField("username")
        val formattedDate = createDateMillis.toFormattedDate("yyyy-MM-dd")
        val daysAgo = "${calculateDaysDiff(createDateMillis, System.currentTimeMillis())} days ago"

        val linearLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            addView(createDetailItem(
                context,
                this,
                R.drawable.ic_cake,
                "Account Creation Date",
                "$formattedDate $daysAgo"
            ) {
                textCopy(context, formattedDate)
            })

            addView(createDetailItem(
                context, this, R.drawable.ic_fingerprint, "User Id", userId.toString()
            ) {
                textCopy(context, userId.toString())
            })

            addView(createDetailItem(
                context, this, R.drawable.ic_open_in_new, "twitchlogger.pl", null
            ) {
                openWebPage(context, "https://twitchlogger.pl/tracker/${username}")
            })
        }

        AlertDialog.Builder(context).apply {
            setTitle("$username(${displayName})")
            setView(linearLayout)
        }.show()
    }
}