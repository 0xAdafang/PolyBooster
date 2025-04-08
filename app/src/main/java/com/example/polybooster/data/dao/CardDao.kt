package com.example.polybooster.data.dao

import androidx.room.*
import com.example.polybooster.data.model.Card

@Dao
interface CardDao {
    @Query("SELECT * FROM Card")
    fun getAllCards(): List<Card>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCard(card: Card)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(cards: List<Card>)

    @Query("SELECT * FROM Card WHERE unlocked = 1 LIMIT 7")
    fun getBoosterCards(): List<Card>

    @Query("SELECT * FROM Card WHERE portfolio = :portfolioName")
    fun getCardsByPortfolio(portfolioName: String): List<Card>

    @Query("SELECT * FROM Card WHERE unlocked = 1 AND lang = :language")
    fun getUnlockedCardsByLanguage(language: String): List<Card>
}