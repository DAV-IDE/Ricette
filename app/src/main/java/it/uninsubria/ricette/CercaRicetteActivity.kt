package it.uninsubria.ricette

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import android.view.View


class CercaRicetteActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var ingredients: ArrayList<String>
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cerca_ricette)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        listView = findViewById(R.id.multiple_list_view)
        searchView = findViewById(R.id.searchView)
        ingredients = arrayListOf(
            "aglio", "agnello", "albicocca", "alloro", "aloe", "ananas",
            "anatra", "aneto", "arachidi", "arancia", "asparagi",
            "avocado", "basilico", "biancospino", "broccoli", "burro",
            "cacao", "cannella", "cardamomo", "carciofi", "carne di manzo",
            "carote", "cavolfiore", "cavolo", "cavolo riccio", "cedro",
            "cetriolo", "chiodi di garofano", "cipolla", "coriandolo",
            "couscous", "cumino", "curcuma", "datteri", "dragoncello",
            "fagioli", "fagiolini", "farina", "fegato", "finocchio",
            "formaggio", "fragola", "frumento", "fungi", "gelato",
            "girasole", "granchio", "insalata", "kiwi", "lampone",
            "latte", "lattuga", "limone", "litchi", "maiale", "mais",
            "mandarino", "mango", "melanzana", "melone", "menta", "merluzzo",
            "miele", "mirtilli", "nocciola", "noce", "noce moscata", "oliva",
            "origano", "orzo", "pane", "panna", "papaya", "paprika", "pasta",
            "patata", "pepe", "pepe bianco", "pepe di Cayenna", "pepe nero",
            "pepe rosa", "peperoncino", "peperoncino in polvere", "peperoni",
            "pesca", "pesce spada", "petto di pollo", "piadina", "pistacchio",
            "polenta", "pollo", "pomodori", "prezzemolo", "prosciutto", "prugna",
            "quinoa", "radicchio", "riso", "rosmarino", "salmone", "sale", "salsiccia",
            "sedano", "semi di papavero", "sesamo", "spinaci", "tacchino",
            "timo", "tonno", "uva", "uva passa", "vaniglia", "verza", "vongole",
            "yogurt", "zafferano", "zenzero", "zucca", "zucchero", "zucchine"
        )

        val adapter: ArrayAdapter<String> = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, ingredients) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<android.widget.TextView>(android.R.id.text1)
                textView.setTextColor(Color.WHITE)
                return view
            }
        }

        listView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })

        changeSearchViewTextColor(searchView, Color.WHITE, Color.WHITE)

        changeSearchViewIconColor(searchView, Color.YELLOW)

        // listView.setOnItemClickListener{ }
    }

    private fun changeSearchViewTextColor(searchView: SearchView, textColor: Int, hintColor: Int) {
        val searchTextViewId = searchView.context.resources.getIdentifier("android:id/search_src_text", null, null)
        val searchText = searchView.findViewById<android.widget.TextView>(searchTextViewId)
        if (searchText != null) {
            searchText.setTextColor(textColor)
            searchText.setHintTextColor(hintColor)
        } else {
            Log.e("CercaRicetteActivity", "searchText is null")
        }
    }

    private fun changeSearchViewIconColor(searchView: SearchView, color: Int) {
        val searchMagIconId = searchView.context.resources.getIdentifier("android:id/search_mag_icon", null, null)
        val searchMagIcon = searchView.findViewById<android.widget.ImageView>(searchMagIconId)
        if (searchMagIcon != null) {
            searchMagIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        } else {
            Log.e("CercaRicetteActivity", "searchMagIcon is null")
        }
    }
    fun scelta(view: View) {
        val intent = Intent (this@CercaRicetteActivity, SceltaRicettaActivity :: class.java)
        startActivity(intent)
    }
}