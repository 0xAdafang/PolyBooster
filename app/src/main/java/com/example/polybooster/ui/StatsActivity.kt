package com.example.polybooster.ui

import android.os.Bundle
import android.widget.ProgressBar
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

    private lateinit var db: AppDatabase
    private lateinit var boosterManager: BoosterManager

    private lateinit var unlockedText: TextView
    private lateinit var totalText: TextView
    private lateinit var starText: TextView
    private lateinit var statParties: TextView
    private lateinit var statMoyenne: TextView
    private lateinit var statBest: TextView

    private lateinit var langEnBar: ProgressBar
    private lateinit var langEsBar: ProgressBar
    private lateinit var langMixBar: ProgressBar
    private lateinit var langEnCount: TextView
    private lateinit var langEsCount: TextView
    private lateinit var langMixCount: TextView

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ScoreAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.topAppBarStats)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        db = AppDatabase.getDatabase(this)
        boosterManager = BoosterManager(this, db)

        // Texte des cartes
        unlockedText = findViewById(R.id.unlockedTextView)
        totalText = findViewById(R.id.totalTextView)
        starText = findViewById(R.id.starTextView)
        statParties = findViewById(R.id.statParties)
        statMoyenne = findViewById(R.id.statMoyenne)
        statBest = findViewById(R.id.statBest)

        // Langues
        langEnBar = findViewById(R.id.langEnBar)
        langEsBar = findViewById(R.id.langEsBar)
        langMixBar = findViewById(R.id.langMixBar)
        langEnCount = findViewById(R.id.langEnCount)
        langEsCount = findViewById(R.id.langEsCount)
        langMixCount = findViewById(R.id.langMixCount)

        // RecyclerView
        recyclerView = findViewById(R.id.statsRecyclerView)
        adapter = ScoreAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadStats()
    }

    private fun loadStats() = lifecycleScope.launch(Dispatchers.IO) {
        val cards = db.cardDao().getAllCards()
        val unlockedCount = cards.count { it.unlocked }
        val totalCount = cards.size
        val starsAvailable = boosterManager.getStarCount()

        val scores = db.quizScoreDao().getAllScores()
        val average = scores.map { it.score }.average()
        val best = scores.maxByOrNull { it.score }?.score ?: "-"
        val count = scores.size
        val langMap = scores.groupingBy { it.lang.uppercase() }.eachCount()

        val en = langMap["EN"] ?: 0
        val es = langMap["ES"] ?: 0
        val mix = langMap["MIX"] ?: 0
        val max = maxOf(en, es, mix, 1)

        withContext(Dispatchers.Main) {
            // Carte infos
            unlockedText.text = getString(R.string.unlocked_cards, unlockedCount)
            totalText.text = getString(R.string.total_cards, totalCount)
            starText.text = getString(R.string.available_stars, starsAvailable)

            // Carte résumé
            statParties.text = "$count"
            statMoyenne.text = if (average.isFinite()) "%.1f".format(average) else "-"
            statBest.text = "$best/10"

            // Carte langues
            langEnBar.progress = en * 100 / max
            langEsBar.progress = es * 100 / max
            langMixBar.progress = mix * 100 / max

            langEnCount.text = en.toString()
            langEsCount.text = es.toString()
            langMixCount.text = mix.toString()

            // Adapter
            adapter.updateScores(scores)
        }
    }

    class ScoreAdapter(private var scores: List<QuizScore>) : RecyclerView.Adapter<ScoreViewHolder>() {
        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ScoreViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_score, parent, false)
            return ScoreViewHolder(view)
        }

        override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
            val s = scores[position]
            holder.scoreText.text = "Score : ${s.score}/10"
            holder.langText.text = "Langue : ${s.lang}"
            holder.dateText.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(s.timestamp))

            holder.langIcon.setImageResource(
                when (s.lang.uppercase()) {
                    "FR" -> R.drawable.ic_flag_fr
                    "EN" -> R.drawable.ic_flag_uk
                    "ES" -> R.drawable.ic_flag_es
                    else -> R.drawable.languages
                }
            )

            val cardView = holder.itemView as androidx.cardview.widget.CardView
            cardView.setCardBackgroundColor(
                when {
                    s.score >= 9 -> androidx.core.content.ContextCompat.getColor(holder.itemView.context, R.color.goodResult)
                    s.score >= 7 -> androidx.core.content.ContextCompat.getColor(holder.itemView.context, R.color.averageResult)
                    else -> androidx.core.content.ContextCompat.getColor(holder.itemView.context, R.color.badResult)
                }
            )
        }

        override fun getItemCount() = scores.size

        fun updateScores(newScores: List<QuizScore>) {
            scores = newScores
            notifyDataSetChanged()
        }
    }

    class ScoreViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val scoreText: TextView = view.findViewById(R.id.scoreText)
        val langText: TextView = view.findViewById(R.id.langText)
        val dateText: TextView = view.findViewById(R.id.dateText)
        val langIcon: android.widget.ImageView = view.findViewById(R.id.langIcon)
    }
}
