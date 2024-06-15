package it.uninsubria.ricette

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import it.uninsubria.ricette.databinding.ActivityRicettaBinding

class RicettaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRicettaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRicettaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ricetta = intent.getParcelableExtra<Ricette>("RICETTA")
        val username = intent.getStringExtra("USERNAME")

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
        }
    }
}
