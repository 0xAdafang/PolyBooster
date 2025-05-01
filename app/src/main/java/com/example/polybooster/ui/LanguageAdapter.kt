package com.example.polybooster.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.polybooster.R

class LanguageAdapter(context: Context, private val languages: List<SpinnerLanguageItem>)
    : ArrayAdapter<SpinnerLanguageItem>(context, 0, languages) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createCustomView(position, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createCustomView(position, parent)
    }

    private fun createCustomView(position: Int, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_language_spinner, parent, false)

        val langIcon = view.findViewById<ImageView>(R.id.langIcon)
        val langName = view.findViewById<TextView>(R.id.langName)

        val item = getItem(position)
        if (item != null) {
            langIcon.setImageResource(item.iconResId)
            langName.text = item.name
        }
        return view
    }
}

