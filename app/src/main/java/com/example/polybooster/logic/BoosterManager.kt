package com.example.polybooster.logic

import android.content.Context
import android.content.SharedPreferences
import com.example.polybooster.data.database.AppDatabase
import com.example.polybooster.data.model.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

class BoosterManager(private val context: Context, private val db: AppDatabase) {

    companion object {
        private const val PREFS_NAME = "booster_prefs"
        private const val KEY_NEXT_BOOSTER_TIME = "next_booster_time"
        private const val KEY_STARS = "stars"
        private const val BOOSTER_INTERVAL_MS = 12 * 60 * 60 * 1000L // 12h
        private const val BOOSTER_SIZE = 5
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getRemainingTimeMillis(): Long {
        val now = System.currentTimeMillis()
        return (prefs.getLong(KEY_NEXT_BOOSTER_TIME, 0L) - now).coerceAtLeast(0)
    }

    fun canOpenBooster(): Boolean = getRemainingTimeMillis() == 0L || getStars() > 0

    fun getStars(): Int = prefs.getInt(KEY_STARS, 0)

    fun addStar() {
        val current = getStars()
        prefs.edit().putInt(KEY_STARS, current + 1).apply()
    }

    fun useStar(): Boolean {
        val current = getStars()
        return if (current > 0) {
            prefs.edit().putInt(KEY_STARS, current - 1).apply()
            true
        } else false
    }

    suspend fun openBooster(useStar: Boolean = false): List<Card> = withContext(Dispatchers.IO) {
        if (!canOpenBooster()) return@withContext emptyList()

        if (getRemainingTimeMillis() > 0 && useStar) {
            if (!useStar()) return@withContext emptyList()
        } else {
            val nextTime = System.currentTimeMillis() + BOOSTER_INTERVAL_MS
            prefs.edit().putLong(KEY_NEXT_BOOSTER_TIME, nextTime).apply()
        }

        val allCards = db.cardDao().getAllCards()
        val selected = allCards.shuffled().take(BOOSTER_SIZE)

        // Marquer les cartes tirées comme débloquées
        selected.forEach {
            val updated = it.copy(unlocked = true)
            db.cardDao().insertCard(updated)
        }

        return@withContext selected
    }
}
