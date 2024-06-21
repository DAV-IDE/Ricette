package it.uninsubria.ricette

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class SceltaRicettaActivity : AppCompatActivity() {

    private var username: String? = null
    private val tAG = "SceltaRicettaActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

                var foundRecipe = false
                for (recipeSnapshot in dataSnapshot.children) {
                    val nome = recipeSnapshot.child("nome").getValue(String::class.java) ?: ""
                    val ingredienti = recipeSnapshot.child("ingredienti").getValue(object : GenericTypeIndicator<List<String>>() {}) ?: emptyList()
                    val quantita = recipeSnapshot.child("quantita").getValue(object : GenericTypeIndicator<List<String>>() {}) ?: emptyList()
                    val unita = recipeSnapshot.child("unita").getValue(object : GenericTypeIndicator<List<String>>() {}) ?: emptyList()
                    val procedimento = recipeSnapshot.child("procedimento").getValue(String::class.java) ?: ""
                    val fotoUrl = recipeSnapshot.child("fotoUrl").getValue(String::class.java) ?: ""
                    val recipeUsername = recipeSnapshot.child("username").getValue(String::class.java) ?: ""
                    val recipeId = recipeSnapshot.key ?: ""

                    val ricetta = Ricette(nome, ingredienti, quantita, unita, procedimento, fotoUrl, recipeUsername, recipeId)
                    Log.d(tAG, "Ricetta: $ricetta")

                    if (ingredients.all { ricetta.ingredienti.contains(it) }) {
                        foundRecipe = true
                        Log.d(tAG, "Matching recipe found: ${ricetta.nome}")
                        val cardView = createCardView(ricetta)
                        container.addView(cardView)
                        Log.d(tAG, "CardView added for recipe: ${ricetta.nome}")
                    }
                }

                if (!foundRecipe) {
                    val noRecipeView = LayoutInflater.from(this@SceltaRicettaActivity).inflate(R.layout.no_recipes_message, container, false)
                    container.addView(noRecipeView)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(tAG, "Database error: ${databaseError.message}")
            }
        })
    }

    private fun createCardView(ricetta: Ricette): View {
        val cardView = LayoutInflater.from(this).inflate(R.layout.recipe_card_view_template, null, false) as CardView

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val margin = resources.getDimensionPixelSize(R.dimen.default_margin)
        layoutParams.setMargins(margin, margin, margin, margin)
        cardView.layoutParams = layoutParams

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

            // Aggiungi il pulsante preferiti e il relativo listener
            val buttonPreferito = findViewById<ImageButton>(R.id.buttonPreferito)
            checkIfRecipeIsFavorite(ricetta.recipeId, buttonPreferito)
            buttonPreferito.setOnClickListener {
                toggleRecipeFavoriteStatus(ricetta, buttonPreferito)
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

    private fun toggleRecipeFavoriteStatus(ricetta: Ricette, button: ImageButton) {
        username?.let {
            val databaseReference = FirebaseDatabase.getInstance().getReference("preferiti").child(it).child(ricetta.recipeId)
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Se la ricetta è già nei preferiti, rimuovila
                        databaseReference.removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                button.isSelected = false
                                Snackbar.make(findViewById(R.id.main), "Ricetta rimossa dai preferiti", Snackbar.LENGTH_SHORT).show()
                            } else {
                                Snackbar.make(findViewById(R.id.main), "Errore nella rimozione della ricetta dai preferiti", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // Se la ricetta non è nei preferiti, aggiungila
                        databaseReference.setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                button.isSelected = true
                                Snackbar.make(findViewById(R.id.main), "Ricetta aggiunta ai preferiti", Snackbar.LENGTH_SHORT).show()
                            } else {
                                Snackbar.make(findViewById(R.id.main), "Errore nell'aggiungere la ricetta ai preferiti", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(tAG, "Database error: ${databaseError.message}")
                }
            })
        }
    }

    private fun checkIfRecipeIsFavorite(recipeId: String, button: ImageButton) {
        username?.let {
            val databaseReference = FirebaseDatabase.getInstance().getReference("preferiti").child(it).child(recipeId)
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    button.isSelected = snapshot.exists()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(tAG, "Database error: ${databaseError.message}")
                }
            })
        }
    }
}
