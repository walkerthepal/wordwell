package com.game.wordwell.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWord(word: WordEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllWords(words: List<WordEntity>)

    @Update
    suspend fun updateWord(word: WordEntity)

    @Query("SELECT * FROM guessing_game_5_letters WHERE word = :wordText LIMIT 1")
    suspend fun getWord(wordText: String): WordEntity?

    @Query("SELECT * FROM guessing_game_5_letters WHERE last_used_timestamp IS NULL OR last_used_timestamp < :thresholdTimestamp ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWordNotUsedRecently(thresholdTimestamp: Long): WordEntity?

    @Query("SELECT * FROM guessing_game_5_letters ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWord(): WordEntity?

    @Query("SELECT COUNT(*) FROM guessing_game_5_letters")
    suspend fun getWordCount(): Int
} 