package com.example.polybooster.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_scores")
data class QuizScore(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val score: Int,
    val lang: String,
    val timestamp: Long = System.currentTimeMillis()
)
