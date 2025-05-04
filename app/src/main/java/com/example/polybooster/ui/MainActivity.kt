package com.example.polybooster.ui

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    private var backgroundMusic: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getDatabase(this)
        boosterManager = BoosterManager(this, db)

        // Booster button
        findViewById<Button>(R.id.boosterButton).apply {
            val drawable = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_booster)
            drawable?.setBounds(0, 0, 70, 70)
            setCompoundDrawables(drawable, null, null, null)
            compoundDrawablePadding = 12

            setOnClickListener {
                startActivity(Intent(this@MainActivity, BoosterActivity::class.java))
            }
        }

        // Quiz button
        findViewById<Button>(R.id.quizButton).apply {
            val drawable = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_quiz)
            drawable?.setBounds(0, 0, 70, 70)
            setCompoundDrawables(drawable, null, null, null)
            compoundDrawablePadding = 12

            setOnClickListener {
                startActivity(Intent(this@MainActivity, QuizActivity::class.java))
            }
        }

        // Stats button
        findViewById<Button>(R.id.statsButton).apply {
            val drawable = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_stats)
            drawable?.setBounds(0, 0, 70, 70)
            setCompoundDrawables(drawable, null, null, null)
            compoundDrawablePadding = 12

            setOnClickListener {
                startActivity(Intent(this@MainActivity, StatsActivity::class.java))
            }
        }

        // MediaPlayer Pour la musique
        backgroundMusic = MediaPlayer.create(this, R.raw.background_relax)
        backgroundMusic?.isLooping = true // 🔁 Boucle infinie
        backgroundMusic?.setVolume(0.5f, 0.5f) // Volume doux
        backgroundMusic?.start()

        // Collection button
        findViewById<Button>(R.id.collectionButton).apply {
            val drawable = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_bookmark)
            drawable?.setBounds(0, 0, 70, 70)
            setCompoundDrawables(drawable, null, null, null)
            compoundDrawablePadding = 12

            setOnClickListener {
                startActivity(Intent(this@MainActivity, CollectionActivity::class.java))
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            val count = db.cardDao().getAllCards().size
            if (count == 0) {
                seedExtendedCards()
                withContext(Dispatchers.Main) {
                }
            }
        }
    }

    override fun finish() {
        super.onPause()
        backgroundMusic?.pause()
    }

    override fun onResume() {
        super.onResume()
        backgroundMusic?.start()

    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundMusic?.release()
        backgroundMusic =  null
    }

    private suspend fun seedExtendedCards() {
        val cards = listOf(
            // 🥗 Aliments
            Card(fr = "pomme", en = "apple", es = "manzana", category = "aliments", iconName = "apple"),
            Card(fr = "pain", en = "bread", es = "pan", category = "aliments", iconName = "bread"),
            Card(fr = "lait", en = "milk", es = "leche", category = "aliments", iconName = "milk"),
            Card(fr = "fromage", en = "cheese", es = "queso", category = "aliments", iconName = "cheese"),
            Card(fr = "eau", en = "water", es = "agua", category = "aliments", iconName = "water"),
            Card(fr = "riz", en = "rice", es = "arroz", category = "aliments", iconName = "rice"),
            Card(fr = "poisson", en = "fish", es = "pescado", category = "aliments", iconName = "fish"),
            Card(fr = "viande", en = "meat", es = "carne", category = "aliments", iconName = "meat"),
            Card(fr = "sel", en = "salt", es = "sal", category = "aliments", iconName = "salt"),
            Card(fr = "sucre", en = "sugar", es = "azúcar", category = "aliments", iconName = "sugar"),

            // 🚗 Transport
            Card(fr = "voiture", en = "car", es = "coche", category = "transport", iconName = "car"),
            Card(fr = "train", en = "train", es = "tren", category = "transport", iconName = "train"),
            Card(fr = "bus", en = "bus", es = "autobús", category = "transport", iconName = "bus"),
            Card(fr = "vélo", en = "bike", es = "bicicleta", category = "transport", iconName = "bike"),
            Card(fr = "moto", en = "motorbike", es = "motocicleta", category = "transport", iconName = "motorbike"),
            Card(fr = "avion", en = "plane", es = "avión", category = "transport", iconName = "plane"),
            Card(fr = "bateau", en = "boat", es = "barco", category = "transport", iconName = "boat"),
            Card(fr = "camion", en = "truck", es = "camión", category = "transport", iconName = "truck"),
            Card(fr = "métro", en = "subway", es = "metro", category = "transport", iconName = "subway"),
            Card(fr = "taxi", en = "taxi", es = "taxi", category = "transport", iconName = "taxi"),

            // 🙏 Politesse
            Card(fr = "bonjour", en = "hello", es = "hola", category = "politesse", iconName = "hello"),
            Card(fr = "merci", en = "thank you", es = "gracias", category = "politesse", iconName = "thanks_you"),
            Card(fr = "au revoir", en = "goodbye", es = "adiós", category = "politesse", iconName = "goodbye"),
            Card(fr = "s'il vous plaît", en = "please", es = "por favor", category = "politesse", iconName = "please"),
            Card(fr = "excusez-moi", en = "excuse me", es = "perdón", category = "politesse", iconName = "excuse_me"),
            Card(fr = "bienvenue", en = "welcome", es = "bienvenido", category = "politesse", iconName = "welcome"),
            Card(fr = "désolé", en = "sorry", es = "lo siento", category = "politesse", iconName = "sorry"),
            Card(fr = "bonne nuit", en = "good night", es = "buenas noches", category = "politesse", iconName = "good_night"),
            Card(fr = "bonsoir", en = "good evening", es = "buenas tardes", category = "politesse", iconName = "good_evening"),
            Card(fr = "bonne journée", en = "have a nice day", es = "que tengas un buen día", category = "politesse", iconName = "nice_day"),

            // 🌍 Géographie
            Card(fr = "France", en = "France", es = "Francia", category = "geographie", iconName = "ic_flag_fr"),
            Card(fr = "Espagne", en = "Spain", es = "España", category = "geographie", iconName = "ic_flag_es"),
            Card(fr = "Allemagne", en = "Germany", es = "Alemania", category = "geographie", iconName = "germany"),
            Card(fr = "Italie", en = "Italy", es = "Italia", category = "geographie", iconName = "italy"),
            Card(fr = "Canada", en = "Canada", es = "Canadá", category = "geographie", iconName = "canada"),
            Card(fr = "Brésil", en = "Brazil", es = "Brasil", category = "geographie", iconName = "brazil"),
            Card(fr = "Chine", en = "China", es = "China", category = "geographie", iconName = "china"),
            Card(fr = "Japon", en = "Japan", es = "Japón", category = "geographie", iconName = "japan"),
            Card(fr = "Russie", en = "Russia", es = "Rusia", category = "geographie", iconName = "russia"),
            Card(fr = "Maroc", en = "Morocco", es = "Marruecos", category = "geographie", iconName = "morocco"),

            // 📦 Objets
            Card(fr = "chaise", en = "chair", es = "silla", category = "objet", iconName = "chair"),
            Card(fr = "table", en = "table", es = "mesa", category = "objet", iconName = "table"),
            Card(fr = "stylo", en = "pen", es = "bolígrafo", category = "objet", iconName = "pen"),
            Card(fr = "téléphone", en = "phone", es = "teléfono", category = "objet", iconName = "phone"),
            Card(fr = "ordinateur", en = "computer", es = "ordenador", category = "objet", iconName = "computer"),
            Card(fr = "lampe", en = "lamp", es = "lámpara", category = "objet", iconName = "lamp"),
            Card(fr = "livre", en = "book", es = "libro", category = "objet", iconName = "book"),
            Card(fr = "clavier", en = "keyboard", es = "teclado", category = "objet", iconName = "keyboard"),
            Card(fr = "porte", en = "door", es = "puerta", category = "objet", iconName = "door"),
            Card(fr = "fenêtre", en = "window", es = "ventana", category = "objet", iconName = "window")
        )
        db.cardDao().insertAll(cards)
    }

}
