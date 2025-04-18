package com.example.polybooster.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.polybooster.R
import com.example.polybooster.booster.BoosterManager
import com.example.polybooster.data.database.AppDatabase
import com.example.polybooster.data.model.QuizScore
import com.example.polybooster.logic.QuizManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var quizManager: QuizManager
    private lateinit var boosterManager: BoosterManager

    // UI
    private lateinit var questionText: TextView
    private lateinit var answerInput : EditText
    private lateinit var nextButton  : Button
    private lateinit var resultText  : TextView
    private lateinit var langSelector: Spinner
    private lateinit var selectedLangLabel: TextView
    private lateinit var progressText: TextView
    private lateinit var restartButton: Button
    private lateinit var homeButton   : Button
    private lateinit var quitButton: Button

    // État du quiz
    private var questions    = listOf<QuizManager.QuizQuestion>()
    private var currentIndex = 0
    private val userAnswers  = mutableMapOf<Int, String>()
    private var selectedLang : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        quitButton = findViewById(R.id.quitButton)

        quitButton.setOnClickListener { finish() }

        db             = AppDatabase.getDatabase(this)
        boosterManager = BoosterManager(this, db)
        quizManager    = QuizManager(db, boosterManager)


        questionText       = findViewById(R.id.questionText)
        answerInput        = findViewById(R.id.answerInput)
        nextButton         = findViewById(R.id.nextButton)
        resultText         = findViewById(R.id.resultText)
        langSelector       = findViewById(R.id.languageSpinner)
        selectedLangLabel  = findViewById(R.id.selectedLangLabel)
        progressText       = findViewById(R.id.progressText)
        restartButton      = findViewById(R.id.restartButton)
        homeButton         = findViewById(R.id.homeButton)

        // spinner langues
        ArrayAdapter.createFromResource(
            this,
            R.array.quiz_languages,
            android.R.layout.simple_spinner_item
        ).also { ad ->
            ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            langSelector.adapter = ad
        }

        langSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>, v: View?, pos: Int, id: Long) {
                selectedLang = when (pos) { 0 -> "EN"; 1 -> "ES"; else -> null }
                val langName = when (selectedLang) {
                    "EN" -> getString(R.string.lang_en)
                    "ES" -> getString(R.string.lang_es)
                    else -> getString(R.string.lang_mix)
                }
                selectedLangLabel.text = getString(R.string.lang_selected, langName)
                loadQuiz()
            }
            override fun onNothingSelected(p: AdapterView<*>) {}
        }

        nextButton.setOnClickListener {
            val q = questions[currentIndex]
            userAnswers[q.id] = answerInput.text.toString()
            currentIndex++
            if (currentIndex < questions.size) showQuestion(true) else showResult()
        }

        restartButton.setOnClickListener {
            restartButton.visibility = View.GONE
            homeButton.visibility    = View.GONE
            resultText.text = ""
            loadQuiz()
        }

        homeButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    // Quiz

    private fun loadQuiz() = lifecycleScope.launch {
        questions = withContext(Dispatchers.IO) {
            quizManager.generateQuiz(selectedLang)
        }
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

    private fun showQuestion(withAnim: Boolean) {
        val q = questions[currentIndex]
        questionText.text = getString(
            R.string.quiz_prompt, q.promptLang, q.answerLang, q.promptText
        )
        progressText.text = getString(R.string.quiz_progress, currentIndex + 1, questions.size)
        answerInput.setText("")
        answerInput.requestFocus()

        if (withAnim) {
            val anim = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
            listOf(questionText, answerInput, progressText).forEach { it.startAnimation(anim) }
        }
    }

    private fun showResult() {
        val score = quizManager.evaluateQuiz(userAnswers, questions)
        resultText.text = if (score >= 7)
            getString(R.string.quiz_score_star, score)
        else
            getString(R.string.quiz_score, score)



        resultText.startAnimation(
            AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        )

        progressText.text = ""
        nextButton.visibility = View.GONE
        nextButton.isEnabled  = false
        restartButton.visibility = View.VISIBLE
        homeButton.visibility    = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            db.quizScoreDao().insert(
                QuizScore(score = score, lang = selectedLang ?: "MIX")
            )
        }
    }

    // Chevron
    override fun onOptionsItemSelected(item: android.view.MenuItem) =
        if (item.itemId == android.R.id.home) { finish(); true }
        else super.onOptionsItemSelected(item)
}
