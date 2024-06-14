package it.uninsubria.ricette

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import it.uninsubria.ricette.databinding.ActivityAccediBinding

class AccediActivity : AppCompatActivity() {

    lateinit var binding: ActivityAccediBinding
    private lateinit var firebaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAccediBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseRef = FirebaseDatabase.getInstance().getReference("utenti")
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun accedi(view: View) {
        val username = binding.editTextUserEmaiPhonel1.text.toString()
        val password = binding.editTextPassword1.text.toString()

        var isValid = true

        if (username.isEmpty()) {
            binding.editTextUserEmaiPhonel1.error = "Scrivi il tuo username"
            isValid = false
        }
        if (password.isEmpty()) {
            binding.editTextPassword1.error = "Scrivi la tua password"
            isValid = false
        }
        if (!isValid) {
            Snackbar.make(binding.main, "Compila tutti i campi", Snackbar.LENGTH_SHORT).show()
            return
        }

        val query: Query = firebaseRef.orderByChild("username").equalTo(username)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.d("AccediActivity", "Username found")
                    for (userSnapshot in snapshot.children) {
                        val utente = userSnapshot.getValue(UtentiRegistrati::class.java)
                        if (utente != null) {
                            Log.d("AccediActivity", "Utente: ${utente.username}, Password: ${utente.password}")
                            if (utente.password == password) {
                                Toast.makeText(applicationContext, "Accesso riuscito", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@AccediActivity, SceltaActivity::class.java)
                                intent.putExtra("USER_ID", utente.id)
                                startActivity(intent)
                                finish()
                                return
                            }
                        }
                    }
                    Snackbar.make(binding.main, "Password errata", Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(binding.main, "Utente non registrato", Snackbar.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Errore: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
