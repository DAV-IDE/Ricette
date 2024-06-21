package it.uninsubria.ricette

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import it.uninsubria.ricette.databinding.ActivityRicettaBinding

class RicettaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRicettaBinding
    private var username: String? = null
    private lateinit var ricetta: Ricette
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRicettaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ricetta = intent.getParcelableExtra("RICETTA")!!
        username = intent.getStringExtra("USERNAME")

        if (ricetta != null) {
            binding.textViewTitolo4.text = ricetta.nome
            binding.textViewSottoTitolo4.text = ricetta.username

            if (ricetta.fotoUrl.isNotEmpty()) {
                Picasso.get()
                    .load(ricetta.fotoUrl)
                    .placeholder(R.drawable.image_placeholder)
                    .into(binding.imageViewFromDB)
            } else {
                binding.imageViewFromDB.setImageResource(R.drawable.image_placeholder)
            }

            // Visualizza il procedimento
            binding.textViewPDB.text = ricetta.procedimento

            // Aggiungi dinamicamente gli ingredienti, quantità e unità di misura
            val ingredientsContainer = findViewById<LinearLayout>(R.id.ingredients_container)
            for (i in ricetta.ingredienti.indices) {
                val ingredientView = LayoutInflater.from(this).inflate(R.layout.ingredient_item, ingredientsContainer, false)
                val ingredientText = ingredientView.findViewById<TextView>(R.id.textViewIngredientsDB)
                val quantityText = ingredientView.findViewById<TextView>(R.id.textViewQuantityDB)
                val unitText = ingredientView.findViewById<TextView>(R.id.textViewUnitMisuraDB)

                ingredientText.text = ricetta.ingredienti[i]
                quantityText.text = ricetta.quantita.getOrNull(i) ?: ""
                unitText.text = ricetta.unita.getOrNull(i) ?: ""

                ingredientsContainer.addView(ingredientView)
            }

            // Aggiungi il listener per il pulsante preferiti
            val buttonPreferito2 = binding.buttonPreferito2
            checkIfRecipeIsFavorite(ricetta.recipeId, buttonPreferito2)
            buttonPreferito2.setOnClickListener {
                toggleRecipeFavoriteStatus(ricetta, buttonPreferito2)
            }
        }
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
                                isFavorite = false
                                Snackbar.make(binding.root, "Ricetta rimossa dai preferiti", Snackbar.LENGTH_SHORT).show()
                                setResultData()
                            } else {
                                Snackbar.make(binding.root, "Errore nella rimozione della ricetta dai preferiti", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // Se la ricetta non è nei preferiti, aggiungila
                        databaseReference.setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                button.isSelected = true
                                isFavorite = true
                                Snackbar.make(binding.root, "Ricetta aggiunta ai preferiti", Snackbar.LENGTH_SHORT).show()
                                setResultData()
                            } else {
                                Snackbar.make(binding.root, "Errore nell'aggiungere la ricetta ai preferiti", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    private fun checkIfRecipeIsFavorite(recipeId: String, button: ImageButton) {
        username?.let {
            val databaseReference = FirebaseDatabase.getInstance().getReference("preferiti").child(it).child(recipeId)
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    isFavorite = snapshot.exists()
                    button.isSelected = isFavorite
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    private fun setResultData() {
        val resultIntent = Intent()
        resultIntent.putExtra("RECIPE_ID", ricetta.recipeId)
        resultIntent.putExtra("IS_FAVORITE", isFavorite)
        setResult(Activity.RESULT_OK, resultIntent)
    }
}
