package it.uninsubria.ricette;

import android.os.Bundle;
import androidx.activity.enableEdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import it.uninsubria.ricette.databinding.ActivityRicetta2Binding;
import com.squareup.picasso.Picasso;

class RicettaActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityRicetta2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRicetta2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val ricetta = intent.getParcelableExtra<Ricette>("RICETTA")
        val username = intent.getStringExtra("USERNAME")

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

            // Display the procedure
            binding.textViewPDB2.text = ricetta.procedimento

            // Dynamically add ingredients, quantities, and units of measure
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

        // Handle the window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
