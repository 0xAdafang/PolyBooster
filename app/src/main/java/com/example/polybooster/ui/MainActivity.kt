package com.example.polybooster.ui
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.polybooster.R
import com.example.polybooster.data.database.AppDatabase
import com.example.polybooster.data.model.Card
import kotlinx.coroutines.*
import android.widget.Button
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getDatabase(this)

        recyclerView = findViewById(R.id.cardRecyclerView)
        adapter = CardAdapter(listOf())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val boosterButton: Button = findViewById(R.id.boosterButton)
        boosterButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, BoosterActivity::class.java))
        }

        val quizButton: Button = findViewById(R.id.quizButton)
        quizButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, QuizActivity::class.java))
        }

        val statsButton: Button = findViewById(R.id.statsButton)
        statsButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, StatsActivity::class.java))
        }

        loadCards()
    }

    private fun loadCards() {
        CoroutineScope(Dispatchers.IO).launch {
            val cards = db.cardDao().getAllCards()

            val baseCards = listOf(
                Card(front = "Pomme", back = "Apple", lang = "EN", portfolio = "aliments", unlocked = true),
                Card(front = "Eau", back = "Water", lang = "EN", portfolio = "aliments"),
                Card(front = "Chat", back = "Cat", lang = "EN", portfolio = "animaux"),
                Card(front = "Gracias", back = "Merci", lang = "ES", portfolio = "formules", unlocked = true),
                Card(front = "Hola", back = "Bonjour", lang = "ES", portfolio = "formules"),
                Card(front = "Camisa", back = "Chemise", lang = "ES", portfolio = "vêtements")
            )
            db.cardDao().insertAll(baseCards)

            val updatedCards = db.cardDao().getAllCards()

            withContext(Dispatchers.Main) {
                adapter.updateCards(updatedCards)
            }
        }
    }
}
