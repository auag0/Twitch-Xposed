package com.anago.twitchxposed.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserChatMessage(
    @PrimaryKey val messageId: String,
    @ColumnInfo("userId") val userId: Int,
    @ColumnInfo("userName") val userName: String,
    @ColumnInfo("displayName") val displayName: String,
    @ColumnInfo("message") val message: String,
    @ColumnInfo("timestamp") val timestamp: Int
)