package com.example.polybooster.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polybooster.R
import com.example.polybooster.data.database.AppDatabase
import com.example.polybooster.data.model.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CollectionActivity : AppCompatActivity() {

    private lateinit var adapter: CollectionAdapter
    private lateinit var cardDao: com.example.polybooster.data.dao.CardDao

    /** clef → titre (et éventuel emoji) */
    private val categories = listOf(
        "aliments"   to "🥗  Aliments",
        "transport"  to "🚗  Transport",
        "politesse"  to "🙏  Politesse",
        "geographie" to "🌍  Géographie",
        "objet"      to "📦  Objets"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)

        // Toolbar avec retour
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.topAppBarCollection)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Accueil"

        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Dao pour accéder aux cartes
        cardDao = AppDatabase.getDatabase(this).cardDao()

        // Configuration du RecyclerView avec grille responsive
        adapter = CollectionAdapter()
        val span = 2

        val recycler = findViewById<RecyclerView>(R.id.collectionRecycler)
        recycler.layoutManager = GridLayoutManager(this, span).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int) =
                    if (adapter.isHeader(position)) span else 1
            }
        }
        recycler.adapter = adapter

        // Chargement initial
        loadData()
    }

    /** Charge les données depuis la base et les transmet à l'adapter*/
    private fun loadData() = lifecycleScope.launch {
        val sections = mutableListOf<Any>()

        withContext(Dispatchers.IO) {
            for ((key, title) in categories) {
                val cards = cardDao.getCardsByCategory(key)

                // 1) un en-tête de section avec le ratio débloqué/total
                sections.add("$title  (${cards.count { it.unlocked }}/${cards.size})")

                // 2) Ajoute les cartes triées par ordre alphabétique
                sections.addAll(cards.sortedBy { it.fr })
            }
        }

        adapter.submit(sections)
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem) =
        if (item.itemId == android.R.id.home) { finish(); true }
        else super.onOptionsItemSelected(item)
}

/* -------------------------------------------------------------------------- */
/*                                ADAPTER                                     */
/* -------------------------------------------------------------------------- */
private const val TYPE_HEADER = 0
private const val TYPE_CARD   = 1

class CollectionAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Any>()

    /* ---------- helpers ---------- */
    fun isHeader(pos: Int)        = items[pos] is String
    fun submit(list: List<Any>) { items.apply { clear(); addAll(list) }; notifyDataSetChanged() }

    /* ---------- adapter ---------- */
    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) =
        if (isHeader(position)) TYPE_HEADER else TYPE_CARD

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == TYPE_HEADER) {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_header, parent, false)
            HeaderVH(v)
        } else {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_card, parent, false)
            CardVH(v)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isHeader(position)) {
            (holder as HeaderVH).bind(items[position] as String)
        } else {
            (holder as CardVH).bind(items[position] as Card)
        }
    }

    /* ---------- view‑holders ---------- */
    private class HeaderVH(v: View) : RecyclerView.ViewHolder(v) {
        private val title: TextView = v.findViewById(R.id.headerTitle)
        fun bind(text: String) { title.text = text }
    }

    private class CardVH(v: View) : RecyclerView.ViewHolder(v) {
        private val fr: TextView = v.findViewById(R.id.cardFrText)
        private val en: TextView = v.findViewById(R.id.cardEnText)
        private val es: TextView = v.findViewById(R.id.cardEsText)
        private val icon: ImageView = v.findViewById(R.id.cardIcon)
        private val category: TextView = v.findViewById(R.id.cardPortfolio)


        fun bind(card: Card) {
            fr.text = card.fr
            en.text = card.en
            es.text = card.es

            // Chargement de l’icône
            val context = itemView.context
            val resId = context.resources.getIdentifier(card.iconName, "drawable", context.packageName)
            if (resId != 0) {
                icon.setImageResource(resId)
            } else {
                icon.setImageResource(R.drawable.ic_launcher_background)
            }

            // Opacité selon verrouillage
            itemView.alpha = if (card.unlocked) 1f else 0.3f

            // Affiche la catégorie avec emoji
            category.text = when (card.category) {
                "aliments"   -> "🥗 Aliments"
                "transport"  -> "🚗 Transport"
                "politesse"  -> "🙏 Politesse"
                "geographie" -> "🌍 Géographie"
                "objet"      -> "📦 Objets"
                else         -> "Catégorie : ${card.category.capitalize()}"
            }

        }
    }

}
