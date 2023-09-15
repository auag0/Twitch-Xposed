package com.anago.twitchxposed.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object NetworkUtils {
    suspend fun downloadFile(
        fileUrl: String,
        outFile: File,
        onDownloaded: () -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            var httpURLConnection: HttpURLConnection? = null
            try {
                httpURLConnection = URL(fileUrl).openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "GET"
                httpURLConnection.connect()

                httpURLConnection.inputStream.use { inputStream ->
                    outFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                withContext(Dispatchers.Main) {
                    onDownloaded()
                }
            } catch (e: Exception) {
                Logger.logE(e.message)
                withContext(Dispatchers.Main) {
                    onFailed(e)
                }
                e.printStackTrace()
            } finally {
                httpURLConnection?.disconnect()
            }
        }
    }
}