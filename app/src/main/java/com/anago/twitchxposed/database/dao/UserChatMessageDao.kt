package com.anago.twitchxposed.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anago.twitchxposed.database.entity.UserChatMessage

@Dao
interface UserChatMessageDao {
    @Query("SELECT * FROM userchatmessage WHERE userId = :userId ORDER by timestamp desc")
    fun getAllMessagesByUserId(userId: Int): List<UserChatMessage>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessage(message: UserChatMessage)

    @Query("SELECT COUNT(*) FROM userchatmessage")
    fun getAllMessageCount(): Int

    @Query("DELETE FROM userchatmessage")
    suspend fun deleteAllMessageLogs()
}