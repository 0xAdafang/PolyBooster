package com.example.polybooster.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.polybooster.R
import com.example.polybooster.booster.BoosterManager
import com.example.polybooster.data.database.AppDatabase
import com.example.polybooster.data.model.Card
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var boosterManager: BoosterManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getDatabase(this)
        boosterManager = BoosterManager(this, db)

        findViewById<Button>(R.id.boosterButton).setOnClickListener {
            startActivity(Intent(this, BoosterActivity::class.java))
        }

        findViewById<Button>(R.id.quizButton).setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
        }

        findViewById<Button>(R.id.statsButton).setOnClickListener {
            startActivity(Intent(this, StatsActivity::class.java))
        }

        CoroutineScope(Dispatchers.IO).launch {
            val count = db.cardDao().getAllCards().size
            if (count == 0) {
                seedExtendedCards()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "10 cartes offertes !", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private suspend fun seedExtendedCards() {
        val cards = listOf(
            // 🥗 Aliments
            Card(fr = "pomme", en = "apple", es = "manzana", category = "aliments"),
            Card(fr = "pain", en = "bread", es = "pan", category = "aliments"),
            Card(fr = "lait", en = "milk", es = "leche", category = "aliments"),
            Card(fr = "fromage", en = "cheese", es = "queso", category = "aliments"),
            Card(fr = "eau", en = "water", es = "agua", category = "aliments"),
            Card(fr = "riz", en = "rice", es = "arroz", category = "aliments"),
            Card(fr = "poisson", en = "fish", es = "pescado", category = "aliments"),
            Card(fr = "viande", en = "meat", es = "carne", category = "aliments"),
            Card(fr = "sel", en = "salt", es = "sal", category = "aliments"),
            Card(fr = "sucre", en = "sugar", es = "azúcar", category = "aliments"),

            // 🚗 Transport
            Card(fr = "voiture", en = "car", es = "coche", category = "transport"),
            Card(fr = "train", en = "train", es = "tren", category = "transport"),
            Card(fr = "bus", en = "bus", es = "autobús", category = "transport"),
            Card(fr = "vélo", en = "bike", es = "bicicleta", category = "transport"),
            Card(fr = "moto", en = "motorbike", es = "motocicleta", category = "transport"),
            Card(fr = "avion", en = "plane", es = "avión", category = "transport"),
            Card(fr = "bateau", en = "boat", es = "barco", category = "transport"),
            Card(fr = "camion", en = "truck", es = "camión", category = "transport"),
            Card(fr = "métro", en = "subway", es = "metro", category = "transport"),
            Card(fr = "taxi", en = "taxi", es = "taxi", category = "transport"),

            // 🙏 Politesse
            Card(fr = "bonjour", en = "hello", es = "hola", category = "politesse"),
            Card(fr = "merci", en = "thank you", es = "gracias", category = "politesse"),
            Card(fr = "au revoir", en = "goodbye", es = "adiós", category = "politesse"),
            Card(fr = "s'il vous plaît", en = "please", es = "por favor", category = "politesse"),
            Card(fr = "excusez-moi", en = "excuse me", es = "perdón", category = "politesse"),
            Card(fr = "bienvenue", en = "welcome", es = "bienvenido", category = "politesse"),
            Card(fr = "désolé", en = "sorry", es = "lo siento", category = "politesse"),
            Card(fr = "bonne nuit", en = "good night", es = "buenas noches", category = "politesse"),
            Card(fr = "bonsoir", en = "good evening", es = "buenas tardes", category = "politesse"),
            Card(fr = "bonne journée", en = "have a nice day", es = "que tengas un buen día", category = "politesse"),

            // 🌍 Géographie
            Card(fr = "France", en = "France", es = "Francia", category = "geographie"),
            Card(fr = "Espagne", en = "Spain", es = "España", category = "geographie"),
            Card(fr = "Allemagne", en = "Germany", es = "Alemania", category = "geographie"),
            Card(fr = "Italie", en = "Italy", es = "Italia", category = "geographie"),
            Card(fr = "Canada", en = "Canada", es = "Canadá", category = "geographie"),
            Card(fr = "Brésil", en = "Brazil", es = "Brasil", category = "geographie"),
            Card(fr = "Chine", en = "China", es = "China", category = "geographie"),
            Card(fr = "Japon", en = "Japan", es = "Japón", category = "geographie"),
            Card(fr = "Russie", en = "Russia", es = "Rusia", category = "geographie"),
            Card(fr = "Maroc", en = "Morocco", es = "Marruecos", category = "geographie"),

            // 📦 Objets
            Card(fr = "chaise", en = "chair", es = "silla", category = "objet"),
            Card(fr = "table", en = "table", es = "mesa", category = "objet"),
            Card(fr = "stylo", en = "pen", es = "bolígrafo", category = "objet"),
            Card(fr = "téléphone", en = "phone", es = "teléfono", category = "objet"),
            Card(fr = "ordinateur", en = "computer", es = "ordenador", category = "objet"),
            Card(fr = "lampe", en = "lamp", es = "lámpara", category = "objet"),
            Card(fr = "livre", en = "book", es = "libro", category = "objet"),
            Card(fr = "clavier", en = "keyboard", es = "teclado", category = "objet"),
            Card(fr = "porte", en = "door", es = "puerta", category = "objet"),
            Card(fr = "fenêtre", en = "window", es = "ventana", category = "objet")
        )
        db.cardDao().insertAll(cards)
    }
}
