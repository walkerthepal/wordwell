package com.game.wordwell

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class WordleSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wordle_selection)

        findViewById<MaterialButton>(R.id.btnFiveLetter).setOnClickListener {
            startGame(5)
        }

        findViewById<MaterialButton>(R.id.btnSevenLetter).setOnClickListener {
            startGame(7)
        }
    }

    private fun startGame(wordLength: Int) {
        val intent = Intent(this, WordleActivity::class.java).apply {
            putExtra(WordleActivity.EXTRA_WORD_LENGTH, wordLength)
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }
} 