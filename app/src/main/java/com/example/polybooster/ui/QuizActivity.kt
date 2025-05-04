package com.example.polybooster.ui

import android.media.MediaPlayer
import android.media.AudioManager
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
import android.speech.tts.TextToSpeech
import java.util.Locale

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
    private lateinit var questionIcon: ImageView
    private lateinit var textToSpeech: TextToSpeech

    // État du quiz
    private var questions    = listOf<QuizManager.QuizQuestion>()
    private var currentIndex = 0
    private val userAnswers  = mutableMapOf<Int, String>()
    private var selectedLang : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // Configuration du volume
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val recommendedVolume = (maxVolume * 0.75).toInt()

        // Pour augmenter le volume s'il est trop bas
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (currentVolume < recommendedVolume) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, recommendedVolume, 0)
        }

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.topAppBarQuiz)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Accueil"

        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

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
        questionIcon = findViewById(R.id.questionIcon)

        // Spinner langues avec icônes
        val languages = listOf(
            SpinnerLanguageItem("EN", R.drawable.ic_flag_uk),
            SpinnerLanguageItem("ES", R.drawable.ic_flag_es),
            SpinnerLanguageItem("MIX", R.drawable.languages)
        )

        val languageAdapter = LanguageAdapter(this, languages)
        langSelector.adapter = languageAdapter

        langSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = languages[position]
                selectedLang = if (selected.name == "MIX") null else selected.name
                selectedLangLabel.text = "Langue sélectionnée : ${selected.name}"

                // Animation douce
                val fadeIn = AnimationUtils.loadAnimation(this@QuizActivity, android.R.anim.fade_in)
                selectedLangLabel.startAnimation(fadeIn)

                loadQuiz()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.FRANCE
            }
        }

        nextButton.setOnClickListener {
            val q = questions[currentIndex]
            val userAnswer = answerInput.text.toString().trim()
            userAnswers[q.id] = userAnswer

            // Vérifie si la réponse est correcte
            val isCorrect = quizManager.checkAnswer(q, userAnswer)

            // Choisit le son à jouer
            val sound = if (isCorrect) R.raw.correct_sound else R.raw.wrong_sound

            // Joue le son PUIS montre la question suivante
            val player = MediaPlayer.create(this, sound)
            player.setOnCompletionListener {
                player.release()
                currentIndex++
                if (currentIndex < questions.size) {
                    showQuestion(true)
                } else {
                    showResult()
                }
            }
            player.start()
        }

        restartButton.setOnClickListener {
            restartButton.visibility = View.GONE
            resultText.text = ""
            loadQuiz()
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
            nextButton.visibility = View.VISIBLE
        }
    }

    private fun showQuestion(withAnim: Boolean) {
        val q = questions[currentIndex]

        // Conversion des codes de langue (EN, ES) en noms complets
        val langFull = when (q.answerLang) {
            "EN" -> "Anglais"
            "ES" -> "Espagnol"
            else -> q.answerLang
        }

        // Affichage de la question
        questionText.text = getString(R.string.quiz_prompt, q.promptText, langFull)
        progressText.text = getString(R.string.quiz_progress, currentIndex + 1, questions.size)
        answerInput.setText("")
        answerInput.requestFocus()
        textToSpeech.speak(q.promptText, TextToSpeech.QUEUE_FLUSH, null, null)

        // Charger l'icône de la question (par nom depuis drawable)
        val resId = resources.getIdentifier(q.iconName, "drawable", packageName)
        if (resId != 0) {
            questionIcon.setImageResource(resId)
        } else {
            questionIcon.setImageResource(R.drawable.ic_launcher_background)
        }

        // Animation d’apparition
        if (withAnim) {
            val fadeZoom = AnimationUtils.loadAnimation(this, android.R.anim.fade_in).apply {
                duration = 400
            }
            listOf(questionIcon, questionText, answerInput, progressText).forEach {
                it.startAnimation(fadeZoom)
            }
        }
    }

    private fun showResult() {
        val score = quizManager.evaluateQuiz(userAnswers, questions)
        resultText.text = if (score >= 7)
            getString(R.string.quiz_score_star, score)
        else
            getString(R.string.quiz_score, score)

        // Son de joie si une étoile est gagnée
        if (score >= 7) {
            val mediaPlayer = MediaPlayer.create(this, R.raw.star_won)
            mediaPlayer?.apply {
                setOnCompletionListener { release() }
                start()
            }
        }

        resultText.startAnimation(
            AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        )

        progressText.text = ""
        nextButton.visibility = View.GONE
        nextButton.isEnabled  = false
        restartButton.visibility = View.VISIBLE

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

    override fun onDestroy() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }

}
