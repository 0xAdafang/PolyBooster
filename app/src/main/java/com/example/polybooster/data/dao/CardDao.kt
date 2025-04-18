package com.example.polybooster.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.polybooster.data.model.Card



@Dao
interface CardDao {

    @Query("SELECT * FROM cards")
    suspend fun getAllCards(): List<Card>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: Card)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cards: List<Card>)

    @Delete
    suspend fun deleteCard(card: Card)

    @Query("SELECT * FROM cards WHERE unlocked = 0 ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomLockedCards(limit: Int): List<Card>

    @Query("UPDATE cards SET unlocked = 1 WHERE id = :cardId")
    suspend fun unlockCard(cardId: Int)

    @Query("""
    SELECT * FROM cards
    WHERE category = :category
    ORDER BY fr
""")
    suspend fun getCardsByCategory(category: String): List<Card>
}