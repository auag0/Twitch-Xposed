package com.anago.twitchxposed

import android.content.res.XModuleResources
import com.anago.twitchxposed.hook.EmoteCardViewDelegate
import com.anago.twitchxposed.hook.ChatUserDialogViewDelegate
import com.anago.twitchxposed.hook.ChatUtil
import com.anago.twitchxposed.hook.CommunityPointsButtonViewDelegate
import com.anago.twitchxposed.hook.MessageRecyclerItem
import com.anago.twitchxposed.hook.NewProfileCardViewDelegate
import com.anago.twitchxposed.hook.SettingsMenuViewDelegate
import com.anago.twitchxposed.hook.TwitchApplication
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LoadPackage


class MainHook : IXposedHookLoadPackage, IXposedHookZygoteInit, IXposedHookInitPackageResources {
    private lateinit var MODULE_PATH: String

    companion object {
        lateinit var modResource: XModuleResources
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        MODULE_PATH = startupParam.modulePath
    }

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        modResource = XModuleResources.createInstance(MODULE_PATH, resparam.res)
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "tv.twitch.android.app") {
            return
        }

        startHook(lpparam.classLoader)
    }

    private fun startHook(classLoader: ClassLoader) {
        TwitchApplication(classLoader).hook()
        CommunityPointsButtonViewDelegate(classLoader).hook()
        ChatUtil(classLoader).hook()
        MessageRecyclerItem(classLoader).hook()
        SettingsMenuViewDelegate(classLoader).hook()
        ChatUserDialogViewDelegate(classLoader).hook()
        NewProfileCardViewDelegate(classLoader).hook()
        EmoteCardViewDelegate(classLoader).hook()
    }
}