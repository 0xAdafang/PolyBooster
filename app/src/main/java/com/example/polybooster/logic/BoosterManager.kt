// BoosterManager.kt
package com.example.polybooster.booster

import android.content.Context
import androidx.core.content.edit          // KTX
import com.example.polybooster.data.database.AppDatabase
import com.example.polybooster.data.model.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BoosterManager(
    context: Context,
    private val database: AppDatabase
) {
    private val prefs = context.getSharedPreferences("BoosterPrefs", Context.MODE_PRIVATE)
    private val cardDao = database.cardDao()

    companion object {
        private const val KEY_STAR_COUNT = "star_count"
        private const val BOOSTER_COST = 1
    }

    /** Initialise 20 étoiles au tout premier lancement. */
    suspend fun initializeIfFirstLaunch() = withContext(Dispatchers.IO) {
        if (!prefs.contains(KEY_STAR_COUNT)) {
            prefs.edit { putInt(KEY_STAR_COUNT, 20) }
        }
    }

    suspend fun canOpenBooster(): Boolean = withContext(Dispatchers.IO) {
        getStarCount() >= BOOSTER_COST
    }

    /** Tire 5 cartes au hasard parmi celles encore verrouillées et les déverrouille. */
    suspend fun openBooster(): List<Card> = withContext(Dispatchers.IO) {
        if (!canOpenBooster()) throw IllegalStateException("Not enough stars")

        // dépense les 10 étoiles
        prefs.edit { putInt(KEY_STAR_COUNT, getStarCount() - BOOSTER_COST) }

        val cards = cardDao.getRandomLockedCards(5)
        cards.forEach { cardDao.unlockCard(it.id) }
        cards
    }

    /** Ajoute n étoiles (récompense de quizz, daily, etc.). */
    fun addStar() = addStars(1)

    private fun addStars(amount: Int) {
        prefs.edit { putInt(KEY_STAR_COUNT, getStarCount() + amount) }
    }
    fun getStarCount(): Int = prefs.getInt(KEY_STAR_COUNT, 0)

    suspend fun spendStar(): Boolean = withContext(Dispatchers.IO) {
        val current = getStarCount()
        if (current <= 0) return@withContext false
        prefs.edit { putInt(KEY_STAR_COUNT, current - 10) }
        true
    }


}
