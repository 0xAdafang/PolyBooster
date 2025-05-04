// BoosterActivity.kt
package com.example.polybooster.ui

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.polybooster.R
import com.example.polybooster.booster.BoosterManager
import com.example.polybooster.data.database.AppDatabase
import com.example.polybooster.databinding.ActivityBoosterBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

class BoosterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoosterBinding
    private lateinit var boosterManager: BoosterManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoosterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pour mettre la flèche de retour dans barre de navigation
        setSupportActionBar(binding.topAppBarBooster)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Accueil"
        }

        binding.topAppBarBooster.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Initialise la gestion d'un booster
        val db = AppDatabase.getDatabase(this)
        boosterManager = BoosterManager(this, db)

        // Initialise des boosters au 1er lancement et met à jour les étoiles
        lifecycleScope.launch {
            boosterManager.initializeIfFirstLaunch()
            updateStars()
        }

        // Permet de gérer le clic su le bouton "Ouvrir un booster"
        binding.buttonOpenBooster.setOnClickListener() {
            lifecycleScope.launch {

                if (boosterManager.isCollectionComplete()) {
                        Toast.makeText(
                        this@BoosterActivity,
                        "Tu as déjà débloqué toutes les cartes 🎉",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                if (boosterManager.canOpenBooster()) {
                    val cards = boosterManager.openBooster()
                    updateStars()

                    if (cards.isNotEmpty()) {
                        // 🎉 Affichage et son de célébration
                        binding.konfettiView.visibility = View.VISIBLE

                        MediaPlayer.create(this@BoosterActivity, R.raw.confetti_sound)?.apply {
                            setOnCompletionListener { release() }
                            start()
                        }

                        // 🎊 Animation de confettis
                        binding.konfettiView.start(
                            Party(
                                speed = 20f,
                                maxSpeed = 50f,
                                damping = 0.9f,
                                spread = 360,
                                colors = listOf(
                                    Color.YELLOW,
                                    Color.GREEN,
                                    Color.MAGENTA,
                                    Color.CYAN
                                ),
                                emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                                position = Position.Relative(0.5, 0.3),
                                timeToLive = 2000L
                            )
                        )

                        // ⏳ Légère pause avant d'afficher les cartes
                        delay(800)

                        BoosterRevealDialog(cards).show(supportFragmentManager, "BoosterReveal")
                    }
                } else {
                    Toast.makeText(
                        this@BoosterActivity,
                        "Pas assez d’étoiles (2 requises)",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Met à jour l'affichage du nombre d'étoiles et de la barre de progression
    private fun updateStars() {
        val stars = boosterManager.getStarCount()
        binding.starsLabel.text = "Étoiles : $stars"
        binding.resultText.text = if (stars >= 2) {
            "Booster disponible !"
        } else {
            "Gagnez des étoiles dans les quiz."
        }

        binding.starProgressBar.apply {
            max = 2
            progress = stars.coerceAtMost(2)
        }
    }
}



