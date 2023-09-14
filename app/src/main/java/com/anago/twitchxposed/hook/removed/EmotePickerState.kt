/*
package com.anago.twitchxposed.hook.removed

import com.anago.twitchxposed.hook.base.BaseHook
import com.anago.twitchxposed.utils.xposed.FieldUtils.getStaticField
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

class EmotePickerState(private val classLoader: ClassLoader) : BaseHook(classLoader) {
    private lateinit var classLoader: ClassLoader
    private lateinit var classEmoteUiSet: Class<*>
    private lateinit var classEmoteHeaderUiModel: Class<*>
    private lateinit var classEmoteHeaderNamedUiModel: Class<*>
    private lateinit var classEmotePickerSection: Class<*>
    private lateinit var classEmoteUiModel: Class<*>
    private lateinit var classClickedEmote: Class<*>
    private lateinit var classEmoteModelAssetType: Class<*>
    private lateinit var classEmoteImageDescriptor: Class<*>
    private val classClickedEmoteUnlocked: Class<*> by lazy {
        XposedHelpers.findClass(
            "tv.twitch.android.shared.emotes.emotepicker.models.ClickedEmote\$Unlocked",
            classLoader
        )
    }

    override fun hook() {
        this.classLoader = classLoader
        val clazz = XposedHelpers.findClass(
            "tv.twitch.android.shared.emotes.emotepicker.EmotePickerPresenter\$EmotePickerState",
            classLoader
        )
        classEmoteUiSet = XposedHelpers.findClass(
            "tv.twitch.android.shared.emotes.emotepicker.models.EmoteUiSet",
            classLoader
        )
        classEmoteHeaderUiModel = XposedHelpers.findClass(
            "tv.twitch.android.shared.emotes.emotepicker.models.EmoteHeaderUiModel",
            classLoader
        )
        classEmoteHeaderNamedUiModel = XposedHelpers.findClass(
            "tv.twitch.android.shared.emotes.emotepicker.models.EmoteHeaderUiModel\$EmoteHeaderNamedUiModel",
            classLoader
        )
        classEmotePickerSection = XposedHelpers.findClass(
            "tv.twitch.android.shared.emotes.emotepicker.models.EmotePickerSection",
            classLoader
        )
        classEmoteUiModel = XposedHelpers.findClass(
            "tv.twitch.android.shared.emotes.emotepicker.models.EmoteUiModel",
            classLoader
        )
        classClickedEmote = XposedHelpers.findClass(
            "tv.twitch.android.shared.emotes.emotepicker.models.ClickedEmote",
            classLoader
        )
        classEmoteModelAssetType = XposedHelpers.findClass(
            "tv.twitch.android.models.emotes.EmoteModelAssetType",
            classLoader
        )
        classEmoteImageDescriptor = XposedHelpers.findClass(
            "tv.twitch.android.shared.emotes.emotepicker.models.EmoteImageDescriptor",
            classLoader
        )


        val c = XposedHelpers.findClass(
            "tv.twitch.android.shared.emotes.emotepicker.adapter.EmotePickerAdapterBinder",
            classLoader
        )
        XposedBridge.hookAllMethods(c, "bindItems", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {

                val origEmoteSets = param.args[0] as List<*>

                val constructorEmoteHeaderNamedUiModel =
                    classEmoteHeaderNamedUiModel.getConstructor(
                        String::class.java,
                        String::class.java,
                        String::class.java,
                        Boolean::class.java,
                        classEmotePickerSection,
                        Boolean::class.java
                    )
                val enumALL = classEmotePickerSection.getStaticField<Any>("ALL")
                val emoteHeaderNamedUiModel = constructorEmoteHeaderNamedUiModel.newInstance(
                    "BetterTTV Emotes",
                    "",
                    null,
                    true,
                    enumALL,
                    false
                )

                val constructorClickedEmoteUnlocked =
                    classClickedEmoteUnlocked.declaredConstructors.first()
                val clickedEmoteUnlocked = constructorClickedEmoteUnlocked.newInstance(

                )

                val constructorEmoteUiModel = classEmoteUiModel.getConstructor(
                    String::class.java,
                    classClickedEmote,
                    classEmoteModelAssetType,
                    classEmoteImageDescriptor,
                    Int::class.java,
                    Int::class.java
                )
                val emoteUiModel = constructorEmoteUiModel.newInstance(

                )


                val constructorEmoteUiSet =
                    classEmoteUiSet.getConstructor(
                        classEmoteHeaderUiModel,
                        List::class.java
                    )
                val emoteUiSet =
                    constructorEmoteUiSet.newInstance(emoteHeaderNamedUiModel, emptyList<Any>())

                val newEmoteSets = ArrayList(origEmoteSets).apply {
                    add(emoteUiSet)
                }

                param.args[0] = newEmoteSets
            }
        })
    }
}*/
