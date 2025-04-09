package com.example.polybooster.logic

import com.example.polybooster.data.database.AppDatabase
import com.example.polybooster.data.model.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuizManager(private val db: AppDatabase, private val boosterManager: BoosterManager) {

    data class QuizQuestion(
        val id: Int,
        val promptLang: String,
        val answerLang: String,
        val promptText: String,
        val correctAnswer: String
    )

    suspend fun generateQuiz(): List<QuizQuestion> = withContext(Dispatchers.IO) {
        val allUnlocked = db.cardDao().getAllCards().filter { it.unlocked }
        if (allUnlocked.size < 10) return@withContext emptyList()

        return@withContext allUnlocked.shuffled().take(10).map { card ->
            if (card.lang == "EN") {
                QuizQuestion(
                    id = card.id,
                    promptLang = "FR",
                    answerLang = "EN",
                    promptText = card.front,
                    correctAnswer = card.back
                )
            } else {
                QuizQuestion(
                    id = card.id,
                    promptLang = "ES",
                    answerLang = "FR",
                    promptText = card.front,
                    correctAnswer = card.back
                )
            }
        }
    }

    fun evaluateQuiz(userAnswers: Map<Int, String>, questions: List<QuizQuestion>): Int {
        val correctCount = questions.count { q ->
            userAnswers[q.id]?.trim()?.equals(q.correctAnswer.trim(), ignoreCase = true) == true
        }
        if (correctCount == 10) boosterManager.addStar()
        return correctCount
    }
}
