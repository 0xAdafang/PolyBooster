package com.example.polybooster.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.polybooster.R
import com.example.polybooster.booster.BoosterManager
import com.example.polybooster.data.database.AppDatabase
import com.example.polybooster.data.model.QuizScore
import com.example.polybooster.logic.QuizManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var quizManager: QuizManager
    private lateinit var boosterManager: BoosterManager

    private lateinit var questionText: TextView
    private lateinit var answerInput: EditText
    private lateinit var nextButton: Button
    private lateinit var resultText: TextView
    private lateinit var langSelector: Spinner
    private lateinit var selectedLangLabel: TextView
    private lateinit var progressText: TextView
    private lateinit var restartButton: Button
    private lateinit var homeButton: Button

    private var questions = listOf<QuizManager.QuizQuestion>()
    private var currentIndex = 0
    private val userAnswers = mutableMapOf<Int, String>()
    private var selectedLang: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        db = AppDatabase.getDatabase(this)
        boosterManager = BoosterManager(this, this.db)
        quizManager = QuizManager(db, boosterManager)

        questionText = findViewById(R.id.questionText)
        answerInput = findViewById(R.id.answerInput)
        nextButton = findViewById(R.id.nextButton)
        resultText = findViewById(R.id.resultText)
        langSelector = findViewById(R.id.languageSpinner)
        selectedLangLabel = findViewById(R.id.selectedLangLabel)
        progressText = findViewById(R.id.progressText)

        restartButton = Button(this).apply {
            text = getString(R.string.quiz_restart)
            visibility = View.GONE
        }

        homeButton = Button(this).apply {
            text = getString(R.string.quiz_home)
            visibility = View.GONE
        }

        val container = findViewById<LinearLayout>(android.R.id.content)
        container.addView(restartButton)
        container.addView(homeButton)

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.quiz_languages,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        langSelector.adapter = adapter

        langSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedLang = when (position) {
                    0 -> "EN"
                    1 -> "ES"
                    else -> null
                }
                val langName = when (selectedLang) {
                    "EN" -> getString(R.string.lang_en)
                    "ES" -> getString(R.string.lang_es)
                    else -> getString(R.string.lang_mix)
                }
                selectedLangLabel.text = getString(R.string.lang_selected, langName)
                loadQuiz()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        nextButton.setOnClickListener {
            val current = questions[currentIndex]
            userAnswers[current.id] = answerInput.text.toString()
            currentIndex++
            if (currentIndex < questions.size) {
                showQuestion(true)
            } else {
                showResult()
            }
        }

        restartButton.setOnClickListener {
            restartButton.visibility = View.GONE
            homeButton.visibility = View.GONE
            resultText.text = ""
            loadQuiz()
        }

        homeButton.setOnClickListener {
            startActivity(Intent(this@QuizActivity, MainActivity::class.java))
            finish()
        }
    }

    private fun loadQuiz() {
        CoroutineScope(Dispatchers.IO).launch {
            questions = quizManager.generateQuiz(selectedLang)
            withContext(Dispatchers.Main) {
                if (questions.isEmpty()) {
                    questionText.text = getString(R.string.quiz_not_enough_cards)
                    nextButton.isEnabled = false
                    progressText.text = ""
                } else {
                    currentIndex = 0
                    userAnswers.clear()
                    showQuestion(false)
                    nextButton.isEnabled = true
                }
            }
        }
    }

    private fun showQuestion(withAnimation: Boolean) {
        val q = questions[currentIndex]
        questionText.text = getString(
            R.string.quiz_prompt,
            q.promptLang,
            q.answerLang,
            q.promptText
        )
        progressText.text = getString(R.string.quiz_progress, currentIndex + 1, questions.size)
        answerInput.setText("")

        if (withAnimation) {
            val animOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_left)
            val animIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
            questionText.startAnimation(animOut)
            questionText.startAnimation(animIn)
            answerInput.startAnimation(animIn)
            progressText.startAnimation(animIn)
        }
    }

    private fun showResult() {
        val score = quizManager.evaluateQuiz(userAnswers, questions)
        resultText.text = if (score == 10) {
            getString(R.string.quiz_win)
        } else {
            getString(R.string.quiz_score, score)
        }
        resultText.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in))
        progressText.text = ""
        nextButton.isEnabled = false
        restartButton.visibility = View.VISIBLE
        homeButton.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            db.quizScoreDao().insert(
                QuizScore(score = score, lang = selectedLang ?: "MIX")
            )
        }
    }
}
