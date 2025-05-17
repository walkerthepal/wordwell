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

@Database(entities = [WordEntity::class], version = 2, exportSchema = false)
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
                .fallbackToDestructiveMigration()
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
            if (wordDao.getWordCount(5) == 0) {
                val words = mutableListOf<WordEntity>()
                
                try {
                    val inputStream5 = context.resources.openRawResource(R.raw.wordle_words_5)
                    val reader5 = BufferedReader(InputStreamReader(inputStream5))
                    var line: String?
                    while (reader5.readLine().also { line = it } != null) {
                        line?.trim()?.let {
                            if (it.length == 5) {
                                words.add(WordEntity(word = it.uppercase(), wordLength = 5))
                            }
                        }
                    }
                    reader5.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                try {
                    val inputStream7 = context.resources.openRawResource(R.raw.wordle_words_7)
                    val reader7 = BufferedReader(InputStreamReader(inputStream7))
                    var line: String?
                    while (reader7.readLine().also { line = it } != null) {
                        line?.trim()?.let {
                            if (it.length == 7) {
                                words.add(WordEntity(word = it.uppercase(), wordLength = 7))
                            }
                        }
                    }
                    reader7.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                wordDao.insertAllWords(words)
            }
        }
    }
} 