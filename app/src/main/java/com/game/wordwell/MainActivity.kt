package com.game.wordwell

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up button click listeners
        findViewById<MaterialButton>(R.id.wordleButton).setOnClickListener {
            startActivity(Intent(this, WordleActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.wordScrambleButton).setOnClickListener {
            // TODO: Launch Word Scramble game
            Toast.makeText(this, "Word Scramble coming soon!", Toast.LENGTH_SHORT).show()
        }

        findViewById<MaterialButton>(R.id.wordSearchButton).setOnClickListener {
            // TODO: Launch Word Search game
            Toast.makeText(this, "Word Search coming soon!", Toast.LENGTH_SHORT).show()
        }

        findViewById<FloatingActionButton>(R.id.settingsButton).setOnClickListener {
            // TODO: Launch Settings activity
            Toast.makeText(this, "Settings coming soon!", Toast.LENGTH_SHORT).show()
        }
    }
}