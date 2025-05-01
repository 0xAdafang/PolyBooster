// BoosterActivity.kt
package com.example.polybooster.ui


import android.graphics.Color
import android.media.MediaPlayer
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.polybooster.R
import com.example.polybooster.booster.BoosterManager
import com.example.polybooster.data.database.AppDatabase
import com.example.polybooster.databinding.ActivityBoosterBinding


class BoosterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBoosterBinding
    private lateinit var boosterManager: BoosterManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBoosterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup de la Toolbar avec la flèche retour
        setSupportActionBar(binding.topAppBarBooster)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Accueil"

        binding.topAppBarBooster.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Retour quand on clique sur la flèche
        }


        val db = AppDatabase.getDatabase(this)
        boosterManager = BoosterManager(this, db)

        lifecycleScope.launch {
            boosterManager.initializeIfFirstLaunch()
            updateStars()
        }

        binding.buttonOpenBooster.setOnClickListener {
            lifecycleScope.launch {
                if (boosterManager.canOpenBooster()) {
                    val cards = boosterManager.openBooster()
                    updateStars()


                    if (cards.isNotEmpty()) {

                        // 🎉 Affiche le KonfettiView
                        binding.konfettiView.visibility = View.VISIBLE

                        // 🔊 Joue le son de confettis
                        val confettiSound = MediaPlayer.create(this@BoosterActivity, R.raw.confetti_sound)
                        confettiSound?.apply {
                            setOnCompletionListener { release() }
                            start()
                        }


                        // 🎉 Lancer les confettis
                        binding.konfettiView.start(
                            Party(
                                speed = 20f,
                                maxSpeed = 50f,
                                damping = 0.9f,
                                spread = 360,
                                colors = listOf(Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.CYAN),
                                emitter = Emitter(100, TimeUnit.MILLISECONDS).max(100),
                                position = Position.Relative(0.5, 0.3),
                                timeToLive = 2000L
                            )
                        )

                        delay(800)

                        BoosterRevealDialog(cards)
                            .show(supportFragmentManager, "BoosterReveal")
                    }

                } else {
                    Toast.makeText(
                        this@BoosterActivity,
                        "Pas assez d’étoiles (5 requises)",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }


    private fun updateStars() {
        val stars = boosterManager.getStarCount()
        binding.starsLabel.text = "Étoiles : $stars"
        binding.resultText.text =
            if (stars >= 1) "Booster disponible !" else "Gagnez des étoiles dans les quiz."

        // 🔄 Synchroniser la ProgressBar avec les étoiles
        binding.starProgressBar.progress = (stars.coerceAtMost(5)) * 20
    }

}


