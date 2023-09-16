package com.anago.twitchxposed.hook.download

import android.app.AlertDialog
import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.anago.twitchxposed.utils.NetworkUtils
import com.anago.twitchxposed.utils.StringUtils
import com.anago.twitchxposed.utils.xposed.FieldUtils.getField
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

object NetworkImageWidgetDownload {
    fun downloadImageDialog(context: Context, fileName: String, networkImageWidget: Any) {
        val imageUrl = networkImageWidget.getField<String>("imageUrl")

        AlertDialog.Builder(context).apply {
            val items = arrayOf("copy url", "download")
            setItems(items) { _, which ->
                when (which) {
                    0 -> StringUtils.textCopy(context, imageUrl)
                    1 -> downloadImage(context, fileName, imageUrl)
                }
            }
        }.show()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun downloadImage(context: Context, fileName: String, imageUrl: String) {
        val downloadDir = File(
            Environment.getExternalStorageDirectory(),
            Environment.DIRECTORY_DOWNLOADS
        )
        var fileExt = imageUrl.substringAfterLast(".")
        if (fileExt != "png" && fileExt != "jpeg" && fileExt != "jpg") {
            fileExt = "png"
        }
        val outFile = File(downloadDir, "$fileName.$fileExt")

        GlobalScope.launch(Dispatchers.IO) {
            NetworkUtils.downloadFile(
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