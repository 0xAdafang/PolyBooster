package com.example.polybooster.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.polybooster.R
import com.example.polybooster.data.database.AppDatabase
import com.example.polybooster.logic.BoosterManager
import com.example.polybooster.logic.QuizManager
import kotlinx.coroutines.*

class QuizActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var quizManager: QuizManager
    private lateinit var boosterManager: BoosterManager

    private lateinit var questionText: TextView
    private lateinit var answerInput: EditText
    private lateinit var nextButton: Button
    private lateinit var resultText: TextView

    private var questions = listOf<QuizManager.QuizQuestion>()
    private var currentIndex = 0
    private val userAnswers = mutableMapOf<Int, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        db = AppDatabase.getDatabase(this)
        boosterManager = BoosterManager(this, db)
        quizManager = QuizManager(db, boosterManager)

        questionText = findViewById(R.id.questionText)
        answerInput = findViewById(R.id.answerInput)
        nextButton = findViewById(R.id.nextButton)
        resultText = findViewById(R.id.resultText)

        nextButton.setOnClickListener {
            val current = questions[currentIndex]
            userAnswers[current.id] = answerInput.text.toString()
            currentIndex++

            if (currentIndex < questions.size) {
                showQuestion()
            } else {
                showResult()
            }
        }

        loadQuiz()
    }

    private fun loadQuiz() {
        CoroutineScope(Dispatchers.IO).launch {
            questions = quizManager.generateQuiz()
            withContext(Dispatchers.Main) {
                if (questions.isEmpty()) {
                    questionText.text = "Pas assez de cartes débloquées pour le quiz."
                    nextButton.isEnabled = false
                } else {
                    showQuestion()
                }
            }
        }
    }

    private fun showQuestion() {
        val q = questions[currentIndex]
        questionText.text = "Traduis ce mot (${q.promptLang} → ${q.answerLang}) : ${q.promptText}"
        answerInput.setText("")
    }

    private fun showResult() {
        val score = quizManager.evaluateQuiz(userAnswers, questions)
        resultText.text = if (score == 10) {
            "Bravo ! 10/10. ⭐️ obtenue !"
        } else {
            "Score : $score / 10"
        }
        nextButton.isEnabled = false
    }
}
