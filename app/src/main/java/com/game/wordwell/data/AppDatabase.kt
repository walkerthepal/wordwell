package com.game.wordwell.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.game.wordwell.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

@Database(entities = [WordEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wordwell_database"
                )
                .addCallback(WordDatabaseCallback(context, scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class WordDatabaseCallback(
        private val context: Context,
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(context, database.wordDao())
                }
            }
        }

        private suspend fun populateDatabase(context: Context, wordDao: WordDao) {
            if (wordDao.getWordCount() == 0) {
                val words = mutableListOf<WordEntity>()
                try {
                    val inputStream = context.resources.openRawResource(R.raw.wordle_words)
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        line?.trim()?.let {
                            if (it.length == 5) {
                                words.add(WordEntity(word = it.uppercase()))
                            }
                        }
                    }
                    reader.close()
                    wordDao.insertAllWords(words)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
} 