package com.anago.twitchxposed.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.anago.twitchxposed.database.dao.UserChatMessageDao
import com.anago.twitchxposed.database.entity.UserChatMessage

@Database(entities = [UserChatMessage::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userChatMessageDao(): UserChatMessageDao

    companion object {
        private lateinit var database: AppDatabase

        fun getDB(): AppDatabase {
            return database
        }

        fun setupDatabase(application: Application) {
            if (::database.isInitialized) {
                return
            }
            database = Room.databaseBuilder(
                application,
                AppDatabase::class.java, "mod"
            ).build()
        }
    }
}