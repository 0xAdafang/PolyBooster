package com.example.polybooster.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polybooster.R
import com.example.polybooster.booster.BoosterManager
import com.example.polybooster.data.database.AppDatabase
import com.example.polybooster.data.model.QuizScore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class StatsActivity : AppCompatActivity() {

    // BDD / manager
    private lateinit var db: AppDatabase
    private lateinit var boosterManager: BoosterManager

    // UI
    private lateinit var unlockedText: TextView
    private lateinit var totalText   : TextView
    private lateinit var starText    : TextView
    private lateinit var globalStats : TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter     : ScoreAdapter
    private lateinit var backButton  : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        // Bouton retour
        backButton = findViewById(R.id.buttonBack)
        backButton.setOnClickListener { finish() }

        // BDD & manager
        db             = AppDatabase.getDatabase(this)
        boosterManager = BoosterManager(this, db)

        // findViewById
        unlockedText = findViewById(R.id.unlockedTextView)
        totalText    = findViewById(R.id.totalTextView)
        starText     = findViewById(R.id.starTextView)
        globalStats  = findViewById(R.id.globalStats)
        recyclerView = findViewById(R.id.statsRecyclerView)

        // RecyclerView
        adapter = ScoreAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter       = adapter

        loadStats()
    }

    /** Charge les cartes, étoiles et scores */
    private fun loadStats() = lifecycleScope.launch(Dispatchers.IO) {

        /* ---- Cartes & étoiles ---- */
        val cards          = db.cardDao().getAllCards()
        val unlockedCount  = cards.count { it.unlocked }
        val totalCount     = cards.size
        val starsAvailable = boosterManager.getStarCount()

        /* ---- Scores ---- */
        val scores   = db.quizScoreDao().getAllScores()
        val average  = scores.map { it.score }.average()
        val best     = scores.maxByOrNull { it.score }?.score ?: "-"
        val count    = scores.size
        val langMap  = scores.groupingBy { it.lang }.eachCount()

        withContext(Dispatchers.Main) {
            // compteurs cartes / étoiles
            unlockedText.text = getString(R.string.unlocked_cards, unlockedCount)
            totalText.text    = getString(R.string.total_cards,    totalCount)
            starText.text     = getString(R.string.available_stars, starsAvailable)

            // bloc stats globales
            globalStats.text = getString(
                R.string.stat_block,
                count,
                if (average.isFinite()) "%.2f".format(average) else "-",
                best,
                langMap["EN"] ?: 0,
                langMap["ES"] ?: 0,
                langMap["MIX"] ?: 0
            )

            adapter.updateScores(scores)
        }
    }

    /* ------------------ Adapter ------------------ */
    class ScoreAdapter(private var scores: List<QuizScore>)
        : RecyclerView.Adapter<ScoreViewHolder>() {

        override fun onCreateViewHolder(p: android.view.ViewGroup, v: Int) =
            ScoreViewHolder(android.view.LayoutInflater.from(p.context)
                .inflate(R.layout.item_score, p, false))

        override fun onBindViewHolder(h: ScoreViewHolder, pos: Int) {
            val s = scores[pos]
            h.scoreText.text = "Score : ${s.score}/10"
            h.langText.text  = "Langue : ${s.lang}"
            h.dateText.text  = "Le : ${
                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    .format(Date(s.timestamp))
            }"
        }

        override fun getItemCount() = scores.size
        fun updateScores(newScores: List<QuizScore>) {
            scores = newScores; notifyDataSetChanged()
        }
    }

    class ScoreViewHolder(v: android.view.View) : RecyclerView.ViewHolder(v) {
        val scoreText: TextView = v.findViewById(R.id.scoreText)
        val langText : TextView = v.findViewById(R.id.langText)
        val dateText : TextView = v.findViewById(R.id.dateText)
    }
}
