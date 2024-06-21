package it.uninsubria.ricette

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class MieRicetteActivity : AppCompatActivity() {

    private var username: String? = null
    private val TAG = "MieRicetteActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mie_ricette)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        username = intent.getStringExtra("USERNAME")
        Log.d(TAG, "Username received: $username")
        if (username == null) {
            Log.e(TAG, "No username provided, exiting activity.")
            finish()  // Close activity if no username is provided.
            return
        }

        fetchUserRecipes()
    }

    private fun fetchUserRecipes() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("ricette")
        val query = databaseReference.orderByChild("username").equalTo(username)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val container = findViewById<LinearLayout>(R.id.linear_layout_container2)
                container.removeAllViews() // Clear existing views

                if (snapshot.childrenCount.toInt() == 0) {
                    val noRecipesView = LayoutInflater.from(this@MieRicetteActivity).inflate(R.layout.no_yours_message, container, false)
                    container.addView(noRecipesView)
                } else {
                    for (recipeSnapshot in snapshot.children) {
                        val ricetta = recipeSnapshot.getValue(Ricette::class.java)
                        ricetta?.let {
                            Log.d(TAG, "Creating card for recipe: ${it.nome}")
                            val cardView = createCardView(it, recipeSnapshot.key!!)
                            container.addView(cardView)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database error: ${error.message}")
            }
        })
    }

    private fun createCardView(ricetta: Ricette, recipeId: String): View {
        val cardView = LayoutInflater.from(this).inflate(R.layout.recipe_card_view_template2, null, false) as CardView
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val margin = resources.getDimensionPixelSize(R.dimen.default_margin)
        layoutParams.setMargins(margin, margin, margin, margin)
        cardView.layoutParams = layoutParams

        cardView.apply {
            findViewById<TextView>(R.id.textViewNomeRicetta2).text = ricetta.nome
            findViewById<ImageView>(R.id.imageViewRicetta2).let { imageView ->
                if (ricetta.fotoUrl.isNotEmpty()) {
                    Picasso.get().load(ricetta.fotoUrl).placeholder(R.drawable.image_placeholder).into(imageView)
                } else {
                    imageView.setImageResource(R.drawable.image_placeholder)
                }
            }
            findViewById<ImageButton>(R.id.buttonElimina).setOnClickListener {
                showDeleteConfirmationDialog(recipeId, cardView)
            }
            setOnClickListener {
                val intent = Intent(this@MieRicetteActivity, RicettaActivity2::class.java).apply {
                    putExtra("RICETTA", ricetta)
                    putExtra("USERNAME", username)
                    putExtra("RECIPE_ID", recipeId)
                }
                startActivity(intent)
            }
        }
        return cardView
    }

    private fun showDeleteConfirmationDialog(recipeId: String, cardView: CardView) {
        AlertDialog.Builder(this).apply {
            setTitle("Conferma Eliminazione")
            setMessage("Sei sicuro di volere eliminare definitivamente la ricetta?")
            setPositiveButton("Sì") { _, _ ->
                deleteRecipeFromFirebase(recipeId, cardView)
            }
            setNegativeButton("No", null)
            create()
            show()
        }
    }

    private fun deleteRecipeFromFirebase(recipeId: String, cardView: CardView) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("ricette").child(recipeId)

        databaseReference.removeValue().addOnSuccessListener {
            Toast.makeText(this, "Ricetta eliminata", Toast.LENGTH_SHORT).show()
            removeCardView(cardView)
        }.addOnFailureListener {
            Toast.makeText(this, "Errore durante l'eliminazione", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeCardView(cardView: CardView) {
        val container = findViewById<LinearLayout>(R.id.linear_layout_container2)
        container.removeView(cardView)

        // Check if there are no more recipes
        if (container.childCount == 0) {
            val noRecipesView = LayoutInflater.from(this).inflate(R.layout.no_yours_message, container, false)
            container.addView(noRecipesView)
        }
    }
}
