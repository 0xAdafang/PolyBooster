package com.example.polybooster.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.polybooster.R
import com.example.polybooster.data.database.AppDatabase
import com.example.polybooster.logic.BoosterManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.*

class StatsActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var boosterManager: BoosterManager

    private lateinit var unlockedText: TextView
    private lateinit var starsText: TextView
    private lateinit var totalText: TextView
    private lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        db = AppDatabase.getDatabase(this)
        boosterManager = BoosterManager(this, db)

        unlockedText = findViewById(R.id.unlockedTextView)
        starsText = findViewById(R.id.starTextView)
        totalText = findViewById(R.id.totalTextView)
        pieChart = findViewById(R.id.pieChart)

        loadStats()
    }

    private fun loadStats() {
        CoroutineScope(Dispatchers.IO).launch {
            val cards = db.cardDao().getAllCards()
            val unlocked = cards.count { it.unlocked }
            val total = cards.size
            val stars = boosterManager.getStars()

            withContext(Dispatchers.Main) {
                unlockedText.text = "Cartes débloquées : $unlocked"
                totalText.text = "Cartes totales : $total"
                starsText.text = "Étoiles disponibles : $stars"

                val entries = listOf(
                    PieEntry(unlocked.toFloat(), "Débloquées"),
                    PieEntry((total - unlocked).toFloat(), "Restantes")
                )
                val dataSet = PieDataSet(entries, "Progression")
                dataSet.setDrawValues(true)
                val data = PieData(dataSet)
                pieChart.data = data
                pieChart.description.isEnabled = false
                pieChart.animateY(1000)
                pieChart.invalidate()
            }
        }
    }
}
