package com.example.polybooster.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polybooster.R
import com.example.polybooster.data.database.AppDatabase
import com.example.polybooster.data.model.QuizScore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class StatsActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ScoreAdapter
    private lateinit var globalStats: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        db = AppDatabase.getDatabase(this)
        recyclerView = findViewById(R.id.statsRecyclerView)
        globalStats = findViewById(R.id.globalStats)
        adapter = ScoreAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadScores()
    }

    private fun loadScores() {
        CoroutineScope(Dispatchers.IO).launch {
            val scores = db.quizScoreDao().getAllScores()
            val moyenne = scores.map { it.score }.average().takeIf { it.isFinite() }?.let { "%.2f".format(it) } ?: "-"
            val max = scores.maxByOrNull { it.score }?.score?.toString() ?: "-"
            val count = scores.size
            val langMap = scores.groupingBy { it.lang }.eachCount()
            val en = langMap["EN"] ?: 0
            val es = langMap["ES"] ?: 0
            val mix = langMap["MIX"] ?: 0

            val text = listOf(
                getString(R.string.stat_total, count),
                getString(R.string.stat_average, moyenne),
                getString(R.string.stat_best, max),
                getString(R.string.stat_lang, en, es, mix)
            ).joinToString("\n")

            withContext(Dispatchers.Main) {
                globalStats.text = text
                adapter.updateScores(scores)
            }
        }
    }

    class ScoreAdapter(private var scores: List<QuizScore>) : RecyclerView.Adapter<ScoreViewHolder>() {

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ScoreViewHolder {
            val view = android.view.LayoutInflater.from(parent.context).inflate(R.layout.item_score, parent, false)
            return ScoreViewHolder(view)
        }

        override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
            val score = scores[position]
            holder.scoreText.text = "Score : ${score.score}/10"
            holder.langText.text = "Langue : ${score.lang}"
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            holder.dateText.text = "Le : ${sdf.format(Date(score.timestamp))}"
        }

        override fun getItemCount() = scores.size

        fun updateScores(newScores: List<QuizScore>) {
            scores = newScores
            notifyDataSetChanged()
        }
    }

    class ScoreViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val scoreText: android.widget.TextView = view.findViewById(R.id.scoreText)
        val langText: android.widget.TextView = view.findViewById(R.id.langText)
        val dateText: android.widget.TextView = view.findViewById(R.id.dateText)
    }
}
