package com.game.wordwell.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guessing_game_5_letters")
data class WordEntity(
    @PrimaryKey val word: String,
    @ColumnInfo(name = "last_used_timestamp")
    var lastUsedTimestamp: Long? = null
) 