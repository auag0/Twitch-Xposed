package com.anago.twitchxposed.hook

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import androidx.core.text.set
import com.anago.twitchxposed.database.AppDatabase.Companion.getDB
import com.anago.twitchxposed.database.entity.UserChatMessage
import com.anago.twitchxposed.hook.base.BaseHook
import com.anago.twitchxposed.hook.emote.EmoteManager
import com.anago.twitchxposed.utils.Logger.logE
import com.anago.twitchxposed.utils.xposed.FieldUtils.getStaticField
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MessageRecyclerItem(private val classLoader: ClassLoader) : BaseHook(classLoader) {
    private val clazz by lazy {
        XposedHelpers.findClass(
            "tv.twitch.android.shared.chat.adapter.item.MessageRecyclerItem",
            classLoader
        )
    }
    private val classMediaSpan by lazy {
        XposedHelpers.findClass(
            "tv.twitch.android.shared.ui.elements.span.MediaSpan\$Type",
            classLoader
        )
    }
    private val classUrlDrawable by lazy {
        XposedHelpers.findClass(
            "tv.twitch.android.shared.ui.elements.span.UrlDrawable",
            classLoader
        )
    }
    private val classCenteredImageSpan by lazy {
        XposedHelpers.findClass(
            "tv.twitch.android.shared.ui.elements.span.CenteredImageSpan",
            classLoader
        )
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun hook() {
        XposedBridge.hookAllConstructors(clazz, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val context = param.args[0] as Context
                param.args[6] = addEmotes(context, param.args[6] as Spanned)
            }

            override fun afterHookedMethod(param: MethodHookParam) {
                if (param.args.size != 16) {
                    return
                }
                val messageId = param.args[1] as String
                val userId = param.args[2] as Int
                val username = param.args[3] as String
                val displayName = param.args[4] as String
                val messageTimestamp = param.args[5] as Int
                val message = param.args[13] as String

                GlobalScope.launch(Dispatchers.IO) {
                    getDB().userChatMessageDao().insertMessage(
                        UserChatMessage(
                            messageId = messageId,
                            userId = userId,
                            userName = username,
                            displayName = displayName,
                            message = message,
                            timestamp = messageTimestamp
                        )
                    )
                }
            }
        })
    }

    private fun addEmotes(context: Context, spanned: Spanned): Spanned {
        val userNameEnd = spanned.indexOf(": ")
        if (userNameEnd == -1) {
            return spanned
        }

        try {
            val emotes = EmoteManager.getLoadedEmotes()
            val builder = SpannableStringBuilder(spanned)
            val regex = Regex("(?<=^|\\s)([A-z:!]+)(?=\\s|$)")
            val matches = regex.findAll(spanned, userNameEnd)

            matches.forEach { matchResult ->
                val value = matchResult.value.trim()
                val emote = emotes.firstOrNull { it.code.equals(value, true) }

                if (emote != null) {
                    val start = matchResult.range.first
                    val end = matchResult.range.last + 1
                    builder[start, end] =
                        createCenteredImageSpan(emote.getUrl(context), Color.TRANSPARENT)
                }
            }

            return builder
        } catch (e: Exception) {
            logE("failed add emote: ${e.message}")
            e.printStackTrace()
        }

        return spanned
    }

    private fun createMediaSpan(): Any {
        return classMediaSpan.getStaticField("Emote")
    }

    private fun createUrlDrawable(
        url: String
    ): Any {
        val constructor = classUrlDrawable.getConstructor(String::class.java, classMediaSpan)
        val type = createMediaSpan()
        return constructor.newInstance(url, type)
    }

    private fun createCenteredImageSpan(
        url: String,
        bgColor: Int
    ): Any {
        val constructor =
            classCenteredImageSpan.getConstructor(Drawable::class.java, Integer::class.java)
        val urlDrawable = createUrlDrawable(url)
        return constructor.newInstance(urlDrawable, bgColor)
    }
}