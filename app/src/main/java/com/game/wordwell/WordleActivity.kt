package com.game.wordwell

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.BufferedReader
import java.util.*

class WordleActivity : AppCompatActivity() {
    private lateinit var gameGrid: GridLayout
    private lateinit var hiddenInput: EditText
    private var currentRow = 0
    private var currentCol = 0
    private val wordLength = 5
    private val maxAttempts = 6
    private lateinit var targetWord: String
    private val gameBoard = Array(maxAttempts) { Array(wordLength) { "" } }
    private val wordList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wordle)

        gameGrid = findViewById(R.id.gameGrid)
        hiddenInput = findViewById(R.id.hiddenInput)

        loadWordList()
        selectRandomWord()
        setupGameGrid()
        setupInputHandling()
        
        // Show keyboard when activity starts
        showKeyboard(hiddenInput)

        // Make the game area clickable to show keyboard
        gameGrid.setOnClickListener {
            showKeyboard(hiddenInput)
        }
    }

    private fun setupInputHandling() {
        // Handle text input
        hiddenInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.isNotEmpty()) {
                    val lastChar = s.last()
                    if (lastChar.isLetter()) {
                        handleLetter(lastChar.uppercaseChar())
                    }
                    // Clear the input after processing
                    hiddenInput.text.clear()
                }
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })

        // Handle Enter and Backspace
        hiddenInput.setOnEditorActionListener { _, actionId, event ->
            when {
                actionId == EditorInfo.IME_ACTION_DONE -> {
                    handleEnter()
                    true
                }
                event?.keyCode == KeyEvent.KEYCODE_DEL -> {
                    handleBackspace()
                    true
                }
                else -> false
            }
        }
    }

    private fun showKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun loadWordList() {
        resources.openRawResource(R.raw.wordle_words).use { inputStream ->
            BufferedReader(inputStream.reader()).useLines { lines ->
                wordList.addAll(lines)
            }
        }
    }

    private fun selectRandomWord() {
        targetWord = wordList.random().uppercase()
    }

    private fun setupGameGrid() {
        // Create 6x5 grid of cells
        for (row in 0 until maxAttempts) {
            for (col in 0 until wordLength) {
                val cell = TextView(this).apply {
                    id = View.generateViewId()
                    setBackgroundResource(R.drawable.cell_background)
                    textSize = 24f
                    gravity = android.view.Gravity.CENTER
                    width = resources.getDimensionPixelSize(R.dimen.cell_size)
                    height = resources.getDimensionPixelSize(R.dimen.cell_size)
                    setTextColor(ContextCompat.getColor(context, android.R.color.black))
                }
                gameGrid.addView(cell)
            }
        }
    }

    private fun handleLetter(letter: Char) {
        if (currentCol < wordLength) {
            gameBoard[currentRow][currentCol] = letter.toString()
            updateCell(currentRow, currentCol, letter.toString())
            animateCell(currentRow, currentCol)
            currentCol++
        }
    }

    private fun handleBackspace() {
        if (currentCol > 0) {
            currentCol--
            gameBoard[currentRow][currentCol] = ""
            updateCell(currentRow, currentCol, "")
        }
    }

    private fun handleEnter() {
        if (currentCol == wordLength) {
            val guess = gameBoard[currentRow].joinToString("")
            if (!wordList.contains(guess.lowercase())) {
                showInvalidWordDialog()
                return
            }

            val result = checkGuess(guess)
            updateRowColors(currentRow, result)
            
            if (guess == targetWord) {
                showWinDialog()
                return
            }

            if (currentRow == maxAttempts - 1) {
                showGameOverDialog()
                return
            }

            currentRow++
            currentCol = 0
        }
    }

    private fun showInvalidWordDialog() {
        AlertDialog.Builder(this)
            .setTitle("Invalid Word")
            .setMessage("This word is not in our dictionary.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showWinDialog() {
        AlertDialog.Builder(this)
            .setTitle("Congratulations!")
            .setMessage("You've won! The word was $targetWord")
            .setPositiveButton("Play Again") { _, _ -> restartGame() }
            .setCancelable(false)
            .show()
    }

    private fun showGameOverDialog() {
        AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage("The word was $targetWord")
            .setPositiveButton("Play Again") { _, _ -> restartGame() }
            .setCancelable(false)
            .show()
    }

    private fun restartGame() {
        selectRandomWord()
        currentRow = 0
        currentCol = 0
        for (row in 0 until maxAttempts) {
            for (col in 0 until wordLength) {
                gameBoard[row][col] = ""
                updateCell(row, col, "")
                val cell = gameGrid.getChildAt(row * wordLength + col) as TextView
                cell.setBackgroundResource(R.drawable.cell_background)
            }
        }
    }

    private fun animateCell(row: Int, col: Int) {
        val cell = gameGrid.getChildAt(row * wordLength + col) as TextView
        ObjectAnimator.ofFloat(cell, "scaleX", 0f, 1f).apply {
            duration = 100
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
        ObjectAnimator.ofFloat(cell, "scaleY", 0f, 1f).apply {
            duration = 100
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun checkGuess(guess: String): List<Int> {
        val result = MutableList(wordLength) { 0 } // 0: wrong, 1: wrong position, 2: correct
        val targetChars = targetWord.toCharArray()
        val guessChars = guess.toCharArray()

        // First pass: mark correct positions
        for (i in guessChars.indices) {
            if (guessChars[i] == targetChars[i]) {
                result[i] = 2
                targetChars[i] = ' ' // Mark as used
            }
        }

        // Second pass: mark wrong positions
        for (i in guessChars.indices) {
            if (result[i] == 0) {
                val index = targetChars.indexOf(guessChars[i])
                if (index != -1) {
                    result[i] = 1
                    targetChars[index] = ' ' // Mark as used
                }
            }
        }

        return result
    }

    private fun updateCell(row: Int, col: Int, text: String) {
        val cell = gameGrid.getChildAt(row * wordLength + col) as TextView
        cell.text = text
    }

    private fun updateRowColors(row: Int, result: List<Int>) {
        for (col in 0 until wordLength) {
            val cell = gameGrid.getChildAt(row * wordLength + col) as TextView
            val colorRes = when (result[col]) {
                2 -> R.color.correct_position
                1 -> R.color.wrong_position
                else -> R.color.wrong_letter
            }
            cell.setBackgroundColor(ContextCompat.getColor(this, colorRes))
        }
    }
} 