package it.uninsubria.ricette

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.widget.ListView
import android.widget.SearchView


class CercaRicetteActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var ingredients : ArrayList<String>
    private lateinit var searchView : SearchView


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
        ingredients = arrayListOf("aglio", "agnello", "albicocca", "alloro", "aloe", "ananas",
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
                                    "yogurt", "zafferano", "zenzero", "zucca", "zucchero", "zucchine")
        val adapter : ArrayAdapter<String> = ArrayAdapter(this,android.R.layout.simple_list_item_1,android.R.id.text1,ingredients)
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
       // listView.setOnItemClickListener{ }

    }
}