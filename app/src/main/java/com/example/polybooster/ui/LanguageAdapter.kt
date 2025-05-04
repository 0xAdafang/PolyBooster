package com.example.polybooster.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.polybooster.R

/**
 * Adapter personnalisé pour afficher une liste de langues avec icône dans un Spinner.
 */
class LanguageAdapter(
    context: Context,
    private val languages: List<SpinnerLanguageItem>
) : ArrayAdapter<SpinnerLanguageItem>(context, 0, languages) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Vue affichée dans le Spinner fermé
        return createCustomView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Vue affichée dans la liste déroulante
        return createCustomView(position, convertView, parent)
    }

    /**
     * Crée une vue personnalisée pour un élément du Spinner.
     */
    private fun createCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_language_spinner, parent, false)

        val icon = view.findViewById<ImageView>(R.id.langIcon)
        val name = view.findViewById<TextView>(R.id.langName)
        val item = getItem(position)

        item?.let {
            icon.setImageResource(it.iconResId)
            name.text = it.name
        }

        return view
    }
}


