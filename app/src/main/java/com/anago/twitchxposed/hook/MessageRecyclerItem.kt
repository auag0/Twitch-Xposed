package com.anago.twitchxposed.hook

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import androidx.core.text.set
import com.anago.twitchxposed.hook.emote.EmoteManager
import com.anago.twitchxposed.utils.Logger.logE
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

class MessageRecyclerItem : BaseHook() {

    private lateinit var classMediaSpan: Class<*>
    private lateinit var classUrlDrawable: Class<*>
    private lateinit var classCenteredImageSpan: Class<*>
    override fun hook(classLoader: ClassLoader) {
        val clazz = XposedHelpers.findClass(
            "tv.twitch.android.shared.chat.adapter.item.MessageRecyclerItem",
            classLoader
        )
        classMediaSpan = XposedHelpers.findClass(
            "tv.twitch.android.shared.ui.elements.span.MediaSpan\$Type",
            classLoader
        )
        classUrlDrawable = XposedHelpers.findClass(
            "tv.twitch.android.shared.ui.elements.span.UrlDrawable",
            classLoader
        )
        classCenteredImageSpan = XposedHelpers.findClass(
            "tv.twitch.android.shared.ui.elements.span.CenteredImageSpan",
            classLoader
        )

        XposedBridge.hookAllConstructors(clazz, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val context = param.args[0] as Context
                param.args[6] = addEmotes(context, param.args[6] as Spanned)
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
        return XposedHelpers.getStaticObjectField(classMediaSpan, "Emote")
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