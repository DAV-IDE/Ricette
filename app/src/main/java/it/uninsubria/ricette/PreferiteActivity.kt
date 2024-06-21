package it.uninsubria.ricette

import android.app.Activity
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
import it.uninsubria.ricette.databinding.ActivityPreferiteBinding

class PreferiteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreferiteBinding
    private var username: String? = null
    private val tAG = "PreferiteActivity"
    private val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreferiteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        username = intent.getStringExtra("USERNAME")
        fetchFavoriteRecipes()
    }

    private fun fetchFavoriteRecipes() {
        username?.let {
            val databaseReference = FirebaseDatabase.getInstance().getReference("preferiti").child(it)
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val container = findViewById<LinearLayout>(R.id.linear_layout_container2)
                    container.removeAllViews()  // Clear existing views if needed
                    Log.d(tAG, "DataSnapshot children count: ${dataSnapshot.childrenCount}")

                    if (dataSnapshot.childrenCount.toInt() == 0) {
                        val noFavoritesView = LayoutInflater.from(this@PreferiteActivity).inflate(R.layout.no_favorites_message, container, false)
                        container.addView(noFavoritesView)
                        return
                    }

                    for (favoriteSnapshot in dataSnapshot.children) {
                        val recipeId = favoriteSnapshot.key ?: continue
                        fetchRecipeDetails(recipeId, container)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(tAG, "Database error: ${databaseError.message}")
                }
            })
        }
    }

    private fun fetchRecipeDetails(recipeId: String, container: LinearLayout) {
        val recipeReference = FirebaseDatabase.getInstance().getReference("ricette").child(recipeId)
        recipeReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val nome = dataSnapshot.child("nome").getValue(String::class.java) ?: ""
                val ingredienti = dataSnapshot.child("ingredienti").getValue(object : GenericTypeIndicator<List<String>>() {}) ?: emptyList()
                val quantita = dataSnapshot.child("quantita").getValue(object : GenericTypeIndicator<List<String>>() {}) ?: emptyList()
                val unita = dataSnapshot.child("unita").getValue(object : GenericTypeIndicator<List<String>>() {}) ?: emptyList()
                val procedimento = dataSnapshot.child("procedimento").getValue(String::class.java) ?: ""
                val fotoUrl = dataSnapshot.child("fotoUrl").getValue(String::class.java) ?: ""
                val recipeUsername = dataSnapshot.child("username").getValue(String::class.java) ?: ""

                val ricetta = Ricette(nome, ingredienti, quantita, unita, procedimento, fotoUrl, recipeUsername, recipeId)
                val cardView = createCardView(ricetta)
                container.addView(cardView)
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
        cardView.tag = ricetta.recipeId  // Imposta l'ID della ricetta come tag del cardView

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

            // Imposta la stella del buttonPreferiti come gialla
            val buttonPreferito = findViewById<ImageButton>(R.id.buttonPreferito)
            buttonPreferito.isSelected = true
            buttonPreferito.setOnClickListener {
                toggleRecipeFavoriteStatus(ricetta, buttonPreferito, cardView)
            }

            setOnClickListener {
                val intent = Intent(this@PreferiteActivity, RicettaActivity::class.java)
                intent.putExtra("RICETTA", ricetta)
                intent.putExtra("USERNAME", username)
                startActivityForResult(intent, REQUEST_CODE)
            }
        }
        return cardView
    }

    private fun toggleRecipeFavoriteStatus(ricetta: Ricette, button: ImageButton, cardView: CardView) {
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
                                // Rimuovi la cardView dal container
                                val container = findViewById<LinearLayout>(R.id.linear_layout_container2)
                                container.removeView(cardView)
                                // Se non ci sono più ricette preferite, mostra il messaggio
                                if (container.childCount == 0) {
                                    val noFavoritesView = LayoutInflater.from(this@PreferiteActivity).inflate(R.layout.no_favorites_message, container, false)
                                    container.addView(noFavoritesView)
                                }
                            } else {
                                Snackbar.make(findViewById(R.id.main), "Errore nella rimozione della ricetta dai preferiti", Snackbar.LENGTH_SHORT).show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val recipeId = data?.getStringExtra("RECIPE_ID")
            val isFavorite = data?.getBooleanExtra("IS_FAVORITE", false) ?: false
            val container = findViewById<LinearLayout>(R.id.linear_layout_container2)
            for (i in 0 until container.childCount) {
                val cardView = container.getChildAt(i) as CardView
                val buttonPreferito = cardView.findViewById<ImageButton>(R.id.buttonPreferito)
                if (cardView.tag == recipeId) {
                    buttonPreferito.isSelected = isFavorite
                    if (!isFavorite) {
                        container.removeView(cardView)
                    }
                }
            }
            if (container.childCount == 0) {
                val noFavoritesView = LayoutInflater.from(this@PreferiteActivity).inflate(R.layout.no_favorites_message, container, false)
                container.addView(noFavoritesView)
            }
        }
    }
}
