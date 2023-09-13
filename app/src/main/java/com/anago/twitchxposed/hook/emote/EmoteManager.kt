package com.anago.twitchxposed.hook.emote

import android.content.Context
import com.anago.twitchxposed.utils.Logger.logD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

object EmoteManager {
    private const val BTTV_API = "https://api.betterttv.net/3/cached/emotes/global"

    private val loadedEmotes: ArrayList<Emote> = ArrayList()

    fun getLoadedEmotes(): List<Emote> {
        return loadedEmotes
    }

    suspend fun fetchEmotes() {
        withContext(Dispatchers.IO) {
            var httpURLConnection: HttpURLConnection? = null
            try {
                loadedEmotes.clear()
                httpURLConnection = URL(BTTV_API).openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "GET"
                httpURLConnection.setRequestProperty("Cache-Control", "max-age=900")
                httpURLConnection.connect()

                if (httpURLConnection.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = httpURLConnection.inputStream
                    val jsonText = inputStream.bufferedReader().use { it.readText() }
                    val jsonArray = JSONArray(jsonText)
                    for (i in 0 until jsonArray.length()) {
                        val emoteJson = jsonArray.getJSONObject(i)
                        val emote = Emote(
                            id = emoteJson.getString("id"),
                            code = emoteJson.getString("code")
                        )
                        loadedEmotes.add(emote)
                    }
                    logD("${loadedEmotes.size} founded emotes")
                } else {
                    val errorStream = httpURLConnection.errorStream
                    val errorMessage = errorStream.bufferedReader().use { it.readText() }
                    logD("failed fetch emotes: code=${httpURLConnection.responseCode} message=${errorMessage}")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                httpURLConnection?.disconnect()
            }
        }
    }

    data class Emote(
        val id: String,
        val code: String
    ) {
        fun getUrl(context: Context): String {
            val size = if (context.resources.displayMetrics.density > 2.0f) "3x" else "2x"
            return "https://cdn.betterttv.net/emote/${id}/${size}"
        }
    }
}