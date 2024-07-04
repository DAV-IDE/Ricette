package it.uninsubria.ricette

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import it.uninsubria.ricette.databinding.ActivityRicetta2Binding

class RicettaActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityRicetta2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRicetta2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val ricetta = intent.getParcelableExtra<Ricette>("RICETTA")
//        val username = intent.getStringExtra("USERNAME")
        val recipeId = intent.getStringExtra("RECIPE_ID")

        if (ricetta != null) {
            binding.textViewTitolo8.text = ricetta.nome

            if (ricetta.fotoUrl.isNotEmpty()) {
                Picasso.get()
                    .load(ricetta.fotoUrl)
                    .placeholder(R.drawable.image_placeholder)
                    .into(binding.imageViewFromDB2)
            } else {
                binding.imageViewFromDB2.setImageResource(R.drawable.image_placeholder)
            }


            binding.textViewPDB2.text = ricetta.procedimento


            val ingredientsContainer = findViewById<LinearLayout>(R.id.ingredients_container2)
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
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonElimina2.setOnClickListener {
            showDeleteConfirmationDialog(recipeId)
        }
    }

    private fun showDeleteConfirmationDialog(recipeId: String?) {
        if (recipeId == null) return

        AlertDialog.Builder(this).apply {
            setTitle("Conferma Eliminazione")
            setMessage("Sei sicuro di volere eliminare definitivamente la ricetta?")
            setPositiveButton("Sì") { _, _ ->
                deleteRecipeFromFirebase(recipeId)
            }
            setNegativeButton("No", null)
            create()
            show()
        }
    }

    private fun deleteRecipeFromFirebase(recipeId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("ricette").child(recipeId)

        databaseReference.removeValue().addOnSuccessListener {
            Toast.makeText(this, "Ricetta eliminata", Toast.LENGTH_SHORT).show()
            finishActivityWithDelay()
        }.addOnFailureListener {
            Toast.makeText(this, "Errore durante l'eliminazione", Toast.LENGTH_SHORT).show()
        }
    }

    private fun finishActivityWithDelay() {
        android.os.Handler(mainLooper).postDelayed({
            finish()
        }, 3000)
    }
}
