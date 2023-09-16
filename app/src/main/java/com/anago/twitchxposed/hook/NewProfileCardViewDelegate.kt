package com.anago.twitchxposed.hook

import android.app.AlertDialog
import android.content.Context
import android.os.Environment
import android.view.View
import android.widget.Toast
import com.anago.twitchxposed.hook.base.BaseHook
import com.anago.twitchxposed.utils.NetworkUtils.downloadFile
import com.anago.twitchxposed.utils.StringUtils.textCopy
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
                        "profile_image"
                    )
                )
                banner.setOnLongClickListener(
                    onLongClickListener(
                        context,
                        banner,
                        "banner_image"
                    )
                )
            }
        })
    }

    private fun onLongClickListener(
        context: Context,
        networkImageWidget: View,
        fileName: String
    ): View.OnLongClickListener {
        return View.OnLongClickListener {
            val imageUrl = networkImageWidget.getField<String>("imageUrl")

            AlertDialog.Builder(context).apply {
                val items = arrayOf("copy url", "download")
                setItems(items) { _, which ->
                    when (which) {
                        0 -> textCopy(context, imageUrl)
                        1 -> downloadImage(context, fileName, imageUrl)
                    }
                }
            }.show()

            true
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun downloadImage(context: Context, fileName: String, imageUrl: String) {
        val downloadDir = File(
            Environment.getExternalStorageDirectory(),
            Environment.DIRECTORY_DOWNLOADS
        )
        val fileExt = imageUrl.substringAfterLast(".")
        val outFile = File(downloadDir, "$fileName.$fileExt")

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
    }
}