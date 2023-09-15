package com.anago.twitchxposed.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Emote(
    @PrimaryKey val code: String,
    @ColumnInfo("url") val url: String
)