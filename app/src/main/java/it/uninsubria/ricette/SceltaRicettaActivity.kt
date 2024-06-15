package it.uninsubria.ricette

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.squareup.picasso.Picasso
import com.google.firebase.database.*

class SceltaRicettaActivity : AppCompatActivity() {

    private var username: String? = null
    private val tAG = "SceltaRicettaActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scelta_ricetta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        username = intent.getStringExtra("USERNAME")

        val selectedIngredients = intent.getStringArrayListExtra("SELECTED_INGREDIENTS") ?: return
        Log.d(tAG, "Selected ingredients: $selectedIngredients")
        fetchRecipes(selectedIngredients)
    }

    private fun fetchRecipes(ingredients: ArrayList<String>) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("ricette")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val container = findViewById<LinearLayout>(R.id.linear_layout_container)
                container.removeAllViews()  // Clear existing views if needed
                Log.d(tAG, "DataSnapshot children count: ${dataSnapshot.childrenCount}")
                for (snapshot in dataSnapshot.children) {
                    val ricetta = snapshot.getValue(Ricette::class.java)
                    if (ricetta != null) {
                        Log.d(tAG, "Ricetta: $ricetta")
                        if (ingredients.all { ricetta.ingredienti.contains(it) }) {
                            Log.d(tAG, "Matching recipe found: ${ricetta.nome}")
                            val cardView = createCardView(ricetta)
                            container.addView(cardView)
                            Log.d(tAG, "CardView added for recipe: ${ricetta.nome}")
                        }
                    } else {
                        Log.d(tAG, "Ricetta is null")
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(tAG, "Database error: ${databaseError.message}")
            }
        })
    }

    private fun createCardView(ricetta: Ricette): View {
        val cardView = LayoutInflater.from(this).inflate(R.layout.recipe_card_view_template, null, false) as CardView
        cardView.apply {
            findViewById<TextView>(R.id.textViewNomeRicetta).text = ricetta.nome
            findViewById<TextView>(R.id.textViewNomeUtente).text = ricetta.username
            val imageView = findViewById<ImageView>(R.id.imageViewRicetta)
            if (ricetta.fotoUrl.isNotEmpty()) {
                Picasso.get()
                    .load(ricetta.fotoUrl)
                    .placeholder(R.drawable.image_placeholder) // Immagine predefinita mentre l'immagine viene caricata
                    .into(imageView)
            } else {
                imageView.setImageResource(R.drawable.image_placeholder)  // Immagine predefinita se non c'è fotoUrl
            }
            setOnClickListener {
                val intent = Intent(this@SceltaRicettaActivity, RicettaActivity::class.java)
                intent.putExtra("RICETTA", ricetta)
                intent.putExtra("USERNAME", username)
                startActivity(intent)
            }
        }
        return cardView
    }
}
