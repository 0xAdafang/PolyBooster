package com.example.polybooster.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.polybooster.R
import com.example.polybooster.data.model.Card

class CardAdapter(private var cards: List<Card>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val frText: TextView = view.findViewById(R.id.cardFrText)
        val enText: TextView = view.findViewById(R.id.cardEnText)
        val esText: TextView = view.findViewById(R.id.cardEsText)
        val portfolioText: TextView = view.findViewById(R.id.cardPortfolio)
        val icon: ImageView = view.findViewById(R.id.cardIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        holder.frText.text = "FR : ${card.fr}"
        holder.enText.text = "EN : ${card.en}"
        holder.esText.text = "ES : ${card.es}"
        holder.portfolioText.text = "Catégorie : ${card.portfolio}"

        val context = holder.itemView.context
        val resId = context.resources.getIdentifier(card.iconName, "drawable", context.packageName)
        if (resId != 0) {
            holder.icon.setImageResource(resId)
        } else {
            holder.icon.setImageResource(R.drawable.ic_launcher_background)
        }

    }

    override fun getItemCount() = cards.size

    fun updateCards(newCards: List<Card>) {
        cards = newCards
        notifyDataSetChanged()
    }
}
