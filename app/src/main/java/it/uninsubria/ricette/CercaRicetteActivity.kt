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
        ingredients = arrayListOf("Acqua", "Aceto", "Farina")
        val adapter : ArrayAdapter<String> = ArrayAdapter(this,android.R.layout.simple_list_item_1, ingredients)
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