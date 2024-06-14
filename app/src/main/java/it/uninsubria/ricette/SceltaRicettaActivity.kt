package it.uninsubria.ricette

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.*
import it.uninsubria.ricette.databinding.ActivitySceltaRicettaBinding

class SceltaRicettaActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySceltaRicettaBinding
    private lateinit var database: DatabaseReference
    private lateinit var selectedIngredients: ArrayList<String>
    private val filteredRecipes = mutableListOf<Ricette>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySceltaRicettaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        selectedIngredients = intent.getStringArrayListExtra("SELECTED_INGREDIENTS") ?: arrayListOf()
        database = FirebaseDatabase.getInstance().reference

        fetchFilteredRecipes()
    }

    private fun fetchFilteredRecipes() {
        database.child("ricette").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ricettaSnapshot in snapshot.children) {
                    val ricetta = ricettaSnapshot.getValue(Ricette::class.java)
                    if (ricetta != null && ricetta.ingredients.containsAll(selectedIngredients)) {
                        filteredRecipes.add(ricetta)
                    }
                }
                showRecipes()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SceltaRicettaActivity", "Errore nel recupero delle ricette: ${error.message}")
            }
        })
    }

    private fun showRecipes() {
        // Popola la UI con le ricette filtrate (CardView)
        for (ricetta in filteredRecipes) {
            val cardView = CardView(this).apply {
                setOnClickListener {
                    val intent = Intent(this@SceltaRicettaActivity, RicettaActivity::class.java)
                    intent.putExtra("RICETTA", ricetta)
                    startActivity(intent)
                }
                // Personalizza la CardView per visualizzare le informazioni della ricetta
            }
            binding.container.addView(cardView)
        }
    }
}
