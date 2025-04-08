package com.example.polybooster.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Card(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val front: String,           // français
    val back: String,            // traduction
    val lang: String,            // "EN" ou "ES"
    val portfolio: String,       // ex: "aliments", "vêtements"
    val unlocked: Boolean = false
)