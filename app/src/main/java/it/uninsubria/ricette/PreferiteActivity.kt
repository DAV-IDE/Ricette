package it.uninsubria.ricette

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
                    container.removeAllViews()

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
                }
            })
        }
    }

    private fun fetchRecipeDetails(recipeId: String, container: LinearLayout) {
        val recipeReference = FirebaseDatabase.getInstance().getReference("ricette").child(recipeId)
        recipeReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    val cardView = createDeletedRecipeCardView(recipeId)
                    container.addView(cardView)
                    return
                }

                val nome = dataSnapshot.child("nome").getValue(String::class.java) ?: ""
                val ingredienti = dataSnapshot.child("ingredienti").getValue(object : GenericTypeIndicator<List<String>>() {}) ?: emptyList()
                val quantita = dataSnapshot.child("quantita").getValue(object : GenericTypeIndicator<List<String>>() {}) ?: emptyList()
                val unita = dataSnapshot.child("unita").getValue(object : GenericTypeIndicator<List<String>>() {}) ?: emptyList()
                val procedimento = dataSnapshot.child("procedimento").getValue(String::class.java) ?: ""
                val fotoUrl = dataSnapshot.child("fotoUrl").getValue(String::class.java) ?: ""
                val recipeUsername = dataSnapshot.child("username").getValue(String::class.java) ?: ""

                if (nome.isEmpty() && ingredienti.isEmpty() && quantita.isEmpty() && unita.isEmpty() && procedimento.isEmpty() && fotoUrl.isEmpty() && recipeUsername.isEmpty()) {
                    val cardView = createDeletedRecipeCardView(recipeId)
                    container.addView(cardView)
                } else {
                    val ricetta = Ricette(nome, ingredienti, quantita, unita, procedimento, fotoUrl, recipeUsername, recipeId)
                    val cardView = createCardView(ricetta)
                    container.addView(cardView)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

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
        cardView.tag = ricetta.recipeId

        cardView.apply {
            findViewById<TextView>(R.id.textViewNomeRicetta).text = ricetta.nome
            findViewById<TextView>(R.id.textViewNomeUtente).text = ricetta.username

            val imageView = findViewById<ImageView>(R.id.imageViewRicetta)
            if (ricetta.fotoUrl.isNotEmpty()) {
                Picasso.get()
                    .load(ricetta.fotoUrl)
                    .placeholder(R.drawable.image_placeholder)
                    .into(imageView)
            } else {
                imageView.setImageResource(R.drawable.image_placeholder)
            }

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

    private fun createDeletedRecipeCardView(recipeId: String): View {
        val cardView = LayoutInflater.from(this).inflate(R.layout.recipe_card_view_template, null, false) as CardView

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val margin = resources.getDimensionPixelSize(R.dimen.default_margin)
        layoutParams.setMargins(margin, margin, margin, margin)
        cardView.layoutParams = layoutParams

        cardView.apply {
            findViewById<TextView>(R.id.textViewNomeRicetta).text = "Questa ricetta è stata eliminata dal suo creatore"
            findViewById<TextView>(R.id.textViewNomeUtente).text = "Informazione non disponibile"
            findViewById<ImageView>(R.id.imageViewRicetta).apply {
                setImageResource(R.drawable.image_placeholder)
                visibility = View.VISIBLE
            }
            val buttonPreferito = findViewById<ImageButton>(R.id.buttonPreferito)
            buttonPreferito.visibility = View.VISIBLE
            buttonPreferito.isSelected = true
            buttonPreferito.setOnClickListener {
                toggleRecipeFavoriteStatus(null, buttonPreferito, cardView)
            }
        }
        cardView.tag = recipeId
        return cardView
    }

    private fun toggleRecipeFavoriteStatus(ricetta: Ricette?, button: ImageButton, cardView: CardView) {
        username?.let { usr ->
            val recipeId = ricetta?.recipeId ?: cardView.tag as String
            val databaseReference = FirebaseDatabase.getInstance().getReference("preferiti").child(usr).child(recipeId)
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        databaseReference.removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                button.isSelected = false
                                Snackbar.make(findViewById(R.id.main), "Ricetta rimossa dai preferiti", Snackbar.LENGTH_SHORT).show()
                                val container = findViewById<LinearLayout>(R.id.linear_layout_container2)
                                container.removeView(cardView)
                                if (container.childCount == 0) {
                                    val noFavoritesView = LayoutInflater.from(this@PreferiteActivity).inflate(R.layout.no_favorites_message, container, false)
                                    container.addView(noFavoritesView)
                                }
                            } else {
                                Snackbar.make(findViewById(R.id.main), "Errore nella rimozione della ricetta dai preferiti", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    } else {

                        val container = findViewById<LinearLayout>(R.id.linear_layout_container2)
                        container.removeView(cardView)
                        if (container.childCount == 0) {
                            val noFavoritesView = LayoutInflater.from(this@PreferiteActivity).inflate(R.layout.no_favorites_message, container, false)
                            container.addView(noFavoritesView)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
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
