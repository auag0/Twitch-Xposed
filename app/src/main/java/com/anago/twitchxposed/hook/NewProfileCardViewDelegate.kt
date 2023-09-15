package com.anago.twitchxposed.hook

import android.content.Context
import android.os.Environment
import android.view.View
import android.widget.Toast
import com.anago.twitchxposed.hook.base.BaseHook
import com.anago.twitchxposed.utils.Logger.logE
import com.anago.twitchxposed.utils.xposed.FieldUtils.getField
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class NewProfileCardViewDelegate(private val classLoader: ClassLoader) : BaseHook(classLoader) {
    private val clazz by lazy {
        XposedHelpers.findClass(
            "tv.twitch.android.feature.profile.profilecard.NewProfileCardViewDelegate",
            classLoader
        )
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun hook() {
        XposedBridge.hookAllConstructors(clazz, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val profileIcon = param.thisObject.getField<View>("profileIcon")
                profileIcon.setOnLongClickListener {
                    val context = param.args[0] as Context
                    val imageUrl = profileIcon.getField<String>("imageUrl")

                    GlobalScope.launch(Dispatchers.IO) {
                        var httpURLConnection: HttpURLConnection? = null
                        try {
                            httpURLConnection = URL(imageUrl).openConnection() as HttpURLConnection
                            httpURLConnection.requestMethod = "GET"
                            httpURLConnection.connect()

                            val downloadDir = File(
                                Environment.getExternalStorageDirectory(),
                                Environment.DIRECTORY_DOWNLOADS
                            )

                            httpURLConnection.inputStream.use { inputStream ->
                                val outFile = File(downloadDir, "profile_image.png")
                                outFile.outputStream().use { outputStream ->
                                    inputStream.copyTo(outputStream)
                                }
                            }

                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Download succeeded", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } catch (e: Exception) {
                            logE(e.message)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            }
                            e.printStackTrace()
                        } finally {
                            httpURLConnection?.disconnect()
                        }
                    }

                    true
                }
            }
        })
    }
}