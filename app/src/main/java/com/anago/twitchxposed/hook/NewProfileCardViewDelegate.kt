package com.anago.twitchxposed.hook

import android.content.Context
import android.os.Environment
import android.view.View
import android.widget.Toast
import com.anago.twitchxposed.hook.base.BaseHook
import com.anago.twitchxposed.utils.NetworkUtils.downloadFile
import com.anago.twitchxposed.utils.xposed.FieldUtils.getField
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class NewProfileCardViewDelegate(private val classLoader: ClassLoader) : BaseHook(classLoader) {
    private val clazz by lazy {
        XposedHelpers.findClass(
            "tv.twitch.android.feature.profile.profilecard.NewProfileCardViewDelegate",
            classLoader
        )
    }

    override fun hook() {
        XposedBridge.hookAllConstructors(clazz, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val context = param.args[0] as Context
                val profileIcon = param.thisObject.getField<View>("profileIcon")
                val banner = param.thisObject.getField<View>("banner")

                profileIcon.setOnLongClickListener(
                    onLongClickListener(
                        context,
                        profileIcon,
                        "profile_image.png"
                    )
                )
                banner.setOnLongClickListener(
                    onLongClickListener(
                        context,
                        banner,
                        "banner_image.png"
                    )
                )
            }
        })
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun onLongClickListener(
        context: Context,
        networkImageWidget: View,
        fileName: String
    ): View.OnLongClickListener {
        return View.OnLongClickListener {
            val imageUrl = networkImageWidget.getField<String>("imageUrl")

            val downloadDir = File(
                Environment.getExternalStorageDirectory(),
                Environment.DIRECTORY_DOWNLOADS
            )
            val outFile = File(downloadDir, fileName)

            GlobalScope.launch(Dispatchers.IO) {
                downloadFile(
                    imageUrl,
                    outFile,
                    onDownloaded = {
                        Toast.makeText(context, "download succeeded", Toast.LENGTH_SHORT)
                            .show()
                    },
                    onFailed = { e ->
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    }
                )
            }
            true
        }
    }
}