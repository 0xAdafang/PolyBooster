package com.example.polybooster.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.polybooster.data.model.Card
import com.example.polybooster.data.dao.CardDao

@Database(entities = [Card::class], version = 2) // passe à version 2
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "polybooster.db"
                )
                    .fallbackToDestructiveMigration() // supprime les anciennes données
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}