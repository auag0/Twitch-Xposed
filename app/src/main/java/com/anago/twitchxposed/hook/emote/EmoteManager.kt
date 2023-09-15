package com.anago.twitchxposed.hook.emote

import com.anago.twitchxposed.database.AppDatabase.Companion.getDB
import com.anago.twitchxposed.database.entity.Emote
import com.anago.twitchxposed.pref.PRefs.lastEmoteCacheTime
import com.anago.twitchxposed.utils.Logger.logD
import com.anago.twitchxposed.utils.Logger.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

object EmoteManager {
    private val loadedEmotes: ArrayList<Emote> = ArrayList()

    fun getLoadedEmotes(): List<Emote> {
        return loadedEmotes
    }

    suspend fun fetchAllEmotes() {
        loadedEmotes.clear()

        if (System.currentTimeMillis() - lastEmoteCacheTime > TimeUnit.MINUTES.toMillis(15)) {
            logD("fetch emotes from network")
            val bttvEmotes = fetchBTTVEmotes()
            val sevenTVEmotes = fetchSevenTVEmotes()
            val ffzEmotes = fetchFFZEmotes()

            loadedEmotes.addAll(bttvEmotes)
            loadedEmotes.addAll(sevenTVEmotes)
            loadedEmotes.addAll(ffzEmotes)

            getDB().cachedEmoteDao().deleteAllEmotes()
            getDB().cachedEmoteDao().insertEmotes(loadedEmotes)
            lastEmoteCacheTime = System.currentTimeMillis()
        } else {
            logD("fetch emotes from database")
            val emotes = getDB().cachedEmoteDao().getAllCachedEmote()
            loadedEmotes.addAll(emotes)
        }

        logD("${loadedEmotes.size} loaded all emotes")
    }

    private suspend fun fetchBTTVEmotes(): List<Emote> {
        val response = fetch("https://api.betterttv.net/3/cached/emotes/global")
        return parseEmotes(response) { item ->
            val id = item.getString("id")
            Emote(
                code = item.getString("code"),
                url = "https://cdn.betterttv.net/emote/${id}/2x"
            )
        }
    }

    private suspend fun fetchSevenTVEmotes(): List<Emote> {
        val response = fetch("https://api.7tv.app/v2/emotes/global")
        return parseEmotes(response) { item ->
            val id = item.getString("id")
            Emote(
                code = item.getString("name"),
                url = "https://cdn.7tv.app/emote/${id}/2x.webp"
            )
        }
    }

    private suspend fun fetchFFZEmotes(): List<Emote> {
        val response = fetch("https://api.betterttv.net/3/cached/frankerfacez/emotes/global")
        return parseEmotes(response) { item ->
            val id = item.getString("id")
            Emote(
                code = item.getString("code"),
                url = "https://cdn.betterttv.net/frankerfacez_emote/${id}/2"
            )
        }
    }

    private fun parseEmotes(response: String?, parse: (JSONObject) -> Emote): List<Emote> {
        val emotes = ArrayList<Emote>()
        if (!response.isNullOrBlank()) {
            val jsonArray = JSONArray(response)
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val emote = parse(item)
                emotes.add(emote)
            }
        }
        return emotes
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