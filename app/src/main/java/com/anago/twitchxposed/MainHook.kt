package com.anago.twitchxposed

import com.anago.twitchxposed.hook.ChatUserDialogViewDelegate
import com.anago.twitchxposed.hook.ChatUtil
import com.anago.twitchxposed.hook.CommunityPointsButtonViewDelegate
import com.anago.twitchxposed.hook.MessageRecyclerItem
import com.anago.twitchxposed.hook.SettingsMenuViewDelegate
import com.anago.twitchxposed.hook.TwitchApplication
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage


class MainHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "tv.twitch.android.app") {
            return
        }

        startHook(lpparam.classLoader)
    }

    private fun startHook(classLoader: ClassLoader) {
        TwitchApplication().hook(classLoader)
        CommunityPointsButtonViewDelegate().hook(classLoader)
        ChatUtil().hook(classLoader)
        MessageRecyclerItem().hook(classLoader)
        SettingsMenuViewDelegate().hook(classLoader)
        ChatUserDialogViewDelegate().hook(classLoader)
    }
}