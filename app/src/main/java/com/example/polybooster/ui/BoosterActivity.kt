// BoosterActivity.kt
package com.example.polybooster.ui

import androidx.lifecycle.lifecycleScope
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.polybooster.booster.BoosterManager
import com.example.polybooster.data.database.AppDatabase
import com.example.polybooster.databinding.ActivityBoosterBinding
import kotlinx.coroutines.launch

class BoosterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBoosterBinding
    private lateinit var boosterManager: BoosterManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoosterBinding.inflate(layoutInflater)
        setContentView(binding.root)


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
                        BoosterRevealDialog(cards)
                            .show(supportFragmentManager, "BoosterReveal")
                    }
                } else {
                    Toast.makeText(
                        this@BoosterActivity,
                        "Pas assez d’étoiles (10 requises)",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.buttonSpendStar.setOnClickListener {
            lifecycleScope.launch {
                val success = boosterManager.spendStar()
                updateStars()
                if (!success) {
                    Toast.makeText(
                        this@BoosterActivity,
                        "Aucune étoile à dépenser",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.buttonBack.setOnClickListener { finish() }
    }

    private fun updateStars() {
        val stars = boosterManager.getStarCount()
        binding.starsLabel.text = "Étoiles : $stars"
        binding.resultText.text =
            if (stars >= 10) "Booster disponible !" else "Gagnez des étoiles dans les quiz."
    }
}


