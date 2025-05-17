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

    @Query("SELECT * FROM guessing_game_words WHERE word = :wordText AND word_length = :length LIMIT 1")
    suspend fun getWord(wordText: String, length: Int): WordEntity?

    @Query("SELECT * FROM guessing_game_words WHERE word_length = :length AND (last_used_timestamp IS NULL OR last_used_timestamp < :thresholdTimestamp) ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWordNotUsedRecently(length: Int, thresholdTimestamp: Long): WordEntity?

    @Query("SELECT * FROM guessing_game_words WHERE word_length = :length ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWord(length: Int): WordEntity?

    @Query("SELECT COUNT(*) FROM guessing_game_words WHERE word_length = :length")
    suspend fun getWordCount(length: Int): Int
} 