package com.game.wordwell.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guessing_game_words")
data class WordEntity(
    @PrimaryKey val word: String,
    @ColumnInfo(name = "word_length")
    val wordLength: Int,
    @ColumnInfo(name = "last_used_timestamp")
    var lastUsedTimestamp: Long? = null
) 