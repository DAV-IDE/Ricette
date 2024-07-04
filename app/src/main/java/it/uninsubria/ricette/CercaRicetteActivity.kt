package it.uninsubria.ricette

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import it.uninsubria.ricette.databinding.ActivityCercaRicetteBinding

class CercaRicetteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCercaRicetteBinding
    private lateinit var ingredients: ArrayList<String>
    private lateinit var filteredIngredients: ArrayList<String>
    private val maxSelectableIngredients = 5
    private val selectedIngredients = mutableSetOf<String>()
    private var username: String? = null
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCercaRicetteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        username = intent.getStringExtra("USERNAME")

        ingredients = arrayListOf(
            "Acqua", "Aglio", "Agnello", "Albicocca", "Alloro", "Aloe", "Ananas",
            "Anatra", "Aneto", "Arachidi", "Arancia", "Asparagi", "Avocado", "Basilico",
            "Biancospino", "Broccoli", "Burro", "Cacao", "Cacao Amaro", "Cannella",
            "Cardamomo", "Carciofi", "Carne Di Manzo", "Carote", "Cavolfiore", "Cavolo",
            "Cavolo Riccio", "Cedro", "Cetriolo", "Chiodi Di Garofano", "Cioccolato Fondente",
            "Cipolla", "Coriandolo", "Couscous", "Cumino", "Curcuma", "Datteri",
            "Dragoncello", "Fagioli", "Fagiolini", "Farina", "Farina 00", "Farina Integrale",
            "Fegato", "Finocchio", "Formaggio", "Fragola", "Frumento", "Fungi", "Gelato",
            "Girasole", "Granchio", "Insalata", "Kiwi", "Lampone", "Latte", "Lattuga",
            "Limone", "Litchi", "Lievito Per Dolci", "Maiale", "Mais", "Mandarino", "Mango",
            "Melanzana", "Melone", "Menta", "Merluzzo", "Miele", "Mirtilli", "Nocciola",
            "Noce", "Noce Moscata", "Olio", "Oliva", "Origano", "Orzo", "Pane", "Panna",
            "Papaya", "Paprika", "Pasta", "Patata", "Pepe", "Pepe Bianco", "Pepe Di Cayenna",
            "Pepe Nero", "Pepe Rosa", "Peperoncino", "Peperoncino In Polvere", "Peperoni",
            "Pesca", "Pesce Spada", "Petto Di Pollo", "Piadina", "Pistacchio", "Polenta",
            "Pollo", "Pomodori", "Prezzemolo", "Prosciutto", "Prugna", "Quinoa", "Radicchio",
            "Riso", "Rosmarino", "Salmone", "Sale", "Salsiccia", "Sedano", "Semi Di Papavero",
            "Sesamo", "Spinaci", "Tacchino", "Timo", "Tonno", "Uova", "Uva", "Uva Passa",
            "Vaniglia", "Verza", "Vongole", "Yogurt", "Zafferano", "Zenzero", "Zucca",
            "Zucchero", "Zucchine"
        )

        filteredIngredients = ArrayList(ingredients)

        adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, android.R.id.text1, filteredIngredients) {
            override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<android.widget.TextView>(android.R.id.text1)
                textView.setTextColor(Color.WHITE)

                // Setta il check solo se l'ingrediente è selezionato
                val isChecked = selectedIngredients.contains(filteredIngredients[position])
                binding.multipleListView.setItemChecked(position, isChecked)

                return view
            }
        }

        binding.multipleListView.adapter = adapter
        binding.multipleListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        binding.multipleListView.setOnItemClickListener { parent, view, position, id ->
            val ingredient = filteredIngredients[position]
            if (selectedIngredients.contains(ingredient)) {
                selectedIngredients.remove(ingredient)
            } else {
                if (selectedIngredients.size < maxSelectableIngredients) {
                    selectedIngredients.add(ingredient)
                } else {
                    binding.multipleListView.setItemChecked(position, false)
                    Log.e("CercaRicetteActivity", "Puoi selezionare un massimo di $maxSelectableIngredients ingredienti")
                }
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterIngredients(newText)
                return false
            }
        })

        changeSearchViewTextColor(binding.searchView, Color.WHITE, Color.WHITE)
        changeSearchViewIconColor(binding.searchView, Color.WHITE)
    }

    override fun onResume() {
        super.onResume()
        // Reset delle selezioni
        selectedIngredients.clear()
        for (i in 0 until binding.multipleListView.count) {
            binding.multipleListView.setItemChecked(i, false)
        }
        adapter.notifyDataSetChanged()
    }

    private fun filterIngredients(query: String) {
        filteredIngredients.clear()
        if (query.isEmpty()) {
            filteredIngredients.addAll(ingredients)
        } else {
            filteredIngredients.addAll(ingredients.filter { it.contains(query, ignoreCase = true) })
        }
        adapter.notifyDataSetChanged()
    }

    private fun changeSearchViewTextColor(searchView: SearchView, textColor: Int, hintColor: Int) {
        val searchTextViewId = searchView.context.resources.getIdentifier("android:id/search_src_text", null, null)
        val searchText = searchView.findViewById<android.widget.TextView>(searchTextViewId)
        if (searchText != null) {
            searchText.setTextColor(textColor)
            searchText.setHintTextColor(hintColor)
        } else {
            Log.e("CercaRicetteActivity", "searchText è null")
        }
    }

    private fun changeSearchViewIconColor(searchView: SearchView, color: Int) {
        val iconIds = listOf(
            "android:id/search_button", // Lente di ingrandimento
            "android:id/search_close_btn", // Bottone di chiusura
            "android:id/search_voice_btn"  // Icona del microfono
        )

        for (iconId in iconIds) {
            val iconViewId = searchView.context.resources.getIdentifier(iconId, null, null)
            val iconView = searchView.findViewById<ImageView>(iconViewId)
            if (iconView != null) {
                iconView.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            } else {
                Log.e("CercaRicetteActivity", "$iconId è null")
            }
        }
    }

    fun scelta(view: View) {
        if (selectedIngredients.isEmpty()) {
            Toast.makeText(this, "Seleziona almeno un ingrediente", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(this@CercaRicetteActivity, SceltaRicettaActivity::class.java).apply {
                putExtra("USERNAME", username)
                putStringArrayListExtra("SELECTED_INGREDIENTS", ArrayList(selectedIngredients))
            }
            startActivity(intent)
        }
    }
}
