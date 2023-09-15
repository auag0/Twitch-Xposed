package com.anago.twitchxposed.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anago.twitchxposed.database.entity.Emote

@Dao
interface CachedEmoteDao {
    @Query("SELECT * FROM emote")
    fun getAllCachedEmote(): List<Emote>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEmotes(emotes: List<Emote>)

    @Query("DELETE FROM emote")
    fun deleteAllEmotes()
}