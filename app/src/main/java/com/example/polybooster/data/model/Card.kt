package com.example.polybooster.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fr: String,
    val en: String,
    val es: String,
    val category: String,
    val unlocked: Boolean = false,
    val portfolio: String? = null
)
