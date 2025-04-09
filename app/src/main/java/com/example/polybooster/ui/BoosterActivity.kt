package com.example.polybooster.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polybooster.R
import com.example.polybooster.data.database.AppDatabase
import com.example.polybooster.logic.BoosterManager
import kotlinx.coroutines.*

class BoosterActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var boosterManager: BoosterManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CardAdapter
    private lateinit var openButton: Button
    private lateinit var timerText: TextView
    private lateinit var useStarButton: Button
    private lateinit var starCountText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booster)

        db = AppDatabase.getDatabase(this)
        boosterManager = BoosterManager(this, db)

        recyclerView = findViewById(R.id.boosterRecyclerView)
        adapter = CardAdapter(listOf())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        openButton = findViewById(R.id.openBoosterButton)
        timerText = findViewById(R.id.timerTextView)
        useStarButton = findViewById(R.id.useStarButton)
        starCountText = findViewById(R.id.starCountTextView)

        updateUI()

        openButton.setOnClickListener {
            openBooster(false)
        }

        useStarButton.setOnClickListener {
            openBooster(true)
        }
    }

    private fun updateUI() {
        val millis = boosterManager.getRemainingTimeMillis()
        if (millis == 0L) {
            timerText.text = "Booster disponible !"
        } else {
            val hours = (millis / (1000 * 60 * 60))
            val minutes = (millis / (1000 * 60)) % 60
            timerText.text = "Prochain booster dans ${hours}h ${minutes}min"
        }

        val stars = boosterManager.getStars()
        starCountText.text = "Étoiles : $stars"
    }

    private fun openBooster(useStar: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            val boosterCards = boosterManager.openBooster(useStar)
            withContext(Dispatchers.Main) {
                adapter.updateCards(boosterCards)
                updateUI()
            }
        }
    }
}
