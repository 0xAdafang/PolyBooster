package com.example.polybooster.ui

import android.app.Dialog
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.polybooster.R
import com.example.polybooster.data.model.Card
import android.view.animation.AnimationUtils
import android.view.animation.Animation


class BoosterRevealDialog(
    private val cards: List<Card>
) : DialogFragment() {

    private var currentIndex = 0
    private lateinit var cardTitle: TextView
    private lateinit var fr: TextView
    private lateinit var en: TextView
    private lateinit var es: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_unlocked_card, null)

        cardTitle = view.findViewById(R.id.cardTitle)
        fr = view.findViewById(R.id.cardFr)
        en = view.findViewById(R.id.cardEn)
        es = view.findViewById(R.id.cardEs)

        updateView()

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .setCancelable(false)
            .setPositiveButton("Suivant", null)
            .create()

        val anim = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        cardTitle.startAnimation(anim)
        fr.startAnimation(anim)
        en.startAnimation(anim)
        es.startAnimation(anim)

        dialog.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                if (currentIndex < cards.lastIndex) {
                    // Son de flip de carte
                    val player = MediaPlayer.create(requireContext(), R.raw.card_flip)
                    player.setOnCompletionListener { it.release() }
                    player.start()

                    // Met à jour la carte
                    currentIndex++
                    updateView()
                    if (currentIndex == cards.lastIndex) {
                        button.text = "Terminer"
                    }

                } else {
                    // Dernière carte son de victoire
                    val finalPlayer = MediaPlayer.create(requireContext(), R.raw.card_last)
                    finalPlayer.setOnCompletionListener { it.release() }
                    finalPlayer.start()

                    dialog.dismiss()
                }
            }
        }

        return dialog
    }

    private fun updateView() {
        val card = cards[currentIndex]
        cardTitle.text = "Catégorie : ${card.category}"
        fr.text = "FR : ${card.fr}"
        en.text = "EN : ${card.en}"
        es.text = "ES : ${card.es}"
    }
}
