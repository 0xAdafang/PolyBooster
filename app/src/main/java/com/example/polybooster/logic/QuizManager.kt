package com.example.polybooster.logic

import com.example.polybooster.data.database.AppDatabase
import com.example.polybooster.data.model.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.polybooster.booster.BoosterManager

class QuizManager(private val db: AppDatabase, private val boosterManager: BoosterManager) {

    data class QuizQuestion(
        val id: Int,
        val promptLang: String,
        val answerLang: String,
        val promptText: String,
        val correctAnswer: String,
        val iconName: String
    )

    suspend fun generateQuiz(targetLang: String? = null): List<QuizQuestion> = withContext(Dispatchers.IO) {
        val allUnlocked = db.cardDao().getAllCards().filter { it.unlocked }
        if (allUnlocked.size < 10) return@withContext emptyList()

        return@withContext allUnlocked.shuffled().take(10).map { card ->
            when (targetLang ?: listOf("EN", "ES").random()) {
                "EN" -> QuizQuestion(
                    id = card.id,
                    promptLang = "FR",
                    answerLang = "EN",
                    promptText = card.fr,
                    correctAnswer = card.en,
                    iconName = card.iconName ?: "ic_launcher_background"
                )
                else -> QuizQuestion(
                    id = card.id,
                    promptLang = "FR",
                    answerLang = "ES",
                    promptText = card.fr,
                    correctAnswer = card.es,
                    iconName = card.iconName ?: "ic_launcher_background"
                )
            }
        }
    }
    fun checkAnswer(question: QuizQuestion, userAnswer: String): Boolean {
        return question.correctAnswer.trim().equals(userAnswer.trim(), ignoreCase = true)
    }

    fun evaluateQuiz(
        userAnswers: Map<Int, String>,
        questions: List<QuizQuestion>
    ): Int {
        val correct = questions.count { q ->
            userAnswers[q.id]?.trim()
                ?.equals(q.correctAnswer.trim(), ignoreCase = true) == true
        }


        if (correct >= 7) boosterManager.addStar()

        return correct
    }

}
