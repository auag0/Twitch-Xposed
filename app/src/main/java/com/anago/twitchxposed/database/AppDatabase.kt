package com.anago.twitchxposed.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.anago.twitchxposed.database.dao.CachedEmoteDao
import com.anago.twitchxposed.database.dao.UserChatMessageDao
import com.anago.twitchxposed.database.entity.Emote
import com.anago.twitchxposed.database.entity.UserChatMessage
import com.anago.twitchxposed.hook.TwitchApplication.Companion.getApp
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(entities = [UserChatMessage::class, Emote::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userChatMessageDao(): UserChatMessageDao
    abstract fun cachedEmoteDao(): CachedEmoteDao

    companion object {
        private var database: AppDatabase? = null

        @OptIn(InternalCoroutinesApi::class)
        fun getDB(context: Context = getApp()): AppDatabase {
            if (database == null) {
                synchronized(this) {
                    database = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java, "mod"
                    ).build()
                }
            }
            return database!!
        }
    }
}