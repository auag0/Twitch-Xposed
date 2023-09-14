package com.anago.twitchxposed.hook

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.anago.twitchxposed.hook.base.BaseHook
import com.anago.twitchxposed.utils.xposed.FieldUtils.getField
import com.anago.twitchxposed.utils.xposed.ViewUtils.getLayoutId
import com.anago.twitchxposed.utils.xposed.ViewUtils.getViewId
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

class SettingsMenuViewDelegate(private val classLoader: ClassLoader) : BaseHook(classLoader) {
    override fun hook() {
        val clazz = XposedHelpers.findClass(
            "tv.twitch.android.feature.settings.menu.SettingsMenuViewDelegate",
            classLoader
        )

        XposedBridge.hookAllMethods(clazz, "render", object : XC_MethodHook() {
            @SuppressLint("DiscouragedApi", "SetTextI18n")
            override fun afterHookedMethod(param: MethodHookParam) {
                super.afterHookedMethod(param)

                val accountSettingsContainer = param.thisObject.getField<ViewGroup>(
                    "accountSettingsContainer"
                )

                if (accountSettingsContainer.findViewWithTag<View?>("added") != null) {
                    return
                }

                val context: Context = accountSettingsContainer.context
                val layoutInflater = LayoutInflater.from(context)

                val menuGroupView = layoutInflater.inflate(
                    getLayoutId(context, "settings_menu_group"),
                    accountSettingsContainer,
                    false
                ) as ViewGroup
                menuGroupView.tag = "added"

                val menuItemView = layoutInflater.inflate(
                    getLayoutId(context, "settings_menu_item"),
                    menuGroupView,
                    false
                )

                menuItemView.findViewById<TextView>(getViewId(context, "menu_item_title")).apply {
                    text = "Xposed Mod Settings"
                }

                menuItemView.findViewById<ImageView>(getViewId(context, "icon")).apply {
                    setImageResource(android.R.mipmap.sym_def_app_icon)
                }

                menuGroupView.addView(menuItemView)
                accountSettingsContainer.addView(menuGroupView)

                menuGroupView.setOnClickListener {
                    Toast.makeText(context, "coming soon...", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}