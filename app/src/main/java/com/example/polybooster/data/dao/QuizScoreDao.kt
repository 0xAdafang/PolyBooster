package com.example.polybooster.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.polybooster.data.model.QuizScore

@Dao
interface QuizScoreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(score: QuizScore)

    /** Liste tous les scores, du plus récent au plus ancien */
    @Query("SELECT * FROM quiz_scores ORDER BY timestamp DESC")
    suspend fun getAllScores(): List<QuizScore>

    @Query("DELETE FROM quiz_scores")
    suspend fun deleteAll()
}