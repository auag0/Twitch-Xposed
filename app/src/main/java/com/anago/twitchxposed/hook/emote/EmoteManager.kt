package com.anago.twitchxposed.hook.emote

import com.anago.twitchxposed.hook.emote.model.Emote
import com.anago.twitchxposed.utils.Logger.logD
import com.anago.twitchxposed.utils.Logger.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

object EmoteManager {
    private val loadedEmotes: ArrayList<Emote> = ArrayList()

    fun getLoadedEmotes(): List<Emote> {
        return loadedEmotes
    }

    suspend fun fetchAllEmotes() {
        loadedEmotes.clear()
        fetchBTTVEmotes()
        fetchSevenTVEmotes()
        fetchFFZEmotes()
        logD("${loadedEmotes.size} loaded emotes")
    }

    private suspend fun fetchBTTVEmotes() {
        val response = fetch("https://api.betterttv.net/3/cached/emotes/global")
        val jsonArray = JSONArray(response)
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val id = item.getString("id")
            val emote = Emote(
                code = item.getString("code"),
                url = "https://cdn.betterttv.net/emote/${id}/2x"
            )
            loadedEmotes.add(emote)
        }
    }

    private suspend fun fetchSevenTVEmotes() {
        val response = fetch("https://api.7tv.app/v2/emotes/global")
        val jsonArray = JSONArray(response)
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val id = item.getString("id")
            val emote = Emote(
                code = item.getString("name"),
                url = "https://cdn.7tv.app/emote/${id}/2x.webp"
            )
            loadedEmotes.add(emote)
        }
    }

    private suspend fun fetchFFZEmotes() {
        val response = fetch("https://api.betterttv.net/3/cached/frankerfacez/emotes/global")
        val jsonArray = JSONArray(response)
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val id = item.getString("id")
            val emote = Emote(
                code = item.getString("code"),
                url = "https://cdn.betterttv.net/frankerfacez_emote/${id}/2"
            )
            loadedEmotes.add(emote)
        }
    }

    private suspend fun fetch(url: String): String? = withContext(Dispatchers.IO) {
        var response: String? = null
        var httpURLConnection: HttpURLConnection? = null

        try {
            httpURLConnection = (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("Cache-Control", "max-age=900")
            }
            httpURLConnection.connect()

            response = httpURLConnection.inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            logE(e.message)
            e.printStackTrace()
        } finally {
            httpURLConnection?.disconnect()
        }

        return@withContext response
    }
}