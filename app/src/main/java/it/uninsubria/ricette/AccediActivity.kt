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
        val input = binding.editTextUserEmaiPhonel1.text.toString()
        val password = binding.editTextPassword1.text.toString()

        if (input.isEmpty()) {
            binding.editTextUserEmaiPhonel1.error = "Inserisci username, email o numero di telefono"
            return
        }
        if (password.isEmpty()) {
            binding.editTextPassword1.error = "Inserisci la password"
            return
        }

        // Cerca utente per username, email o numero di telefono
        val query: Query = firebaseRef.orderByChild("numeroTelOrEmail").equalTo(input)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    verifyUserAndPassword(snapshot, password)
                } else {
                    // Cerca per username se il primo campo non corrisponde a numeroTelOrEmail
                    firebaseRef.orderByChild("username").equalTo(input).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(usernameSnapshot: DataSnapshot) {
                            if (usernameSnapshot.exists()) {
                                verifyUserAndPassword(usernameSnapshot, password)
                            } else {
                                Snackbar.make(binding.main, "Nessun utente trovato con questi dati", Snackbar.LENGTH_LONG).show()
                            }
                        }

                        override fun onCancelled(dbError: DatabaseError) {
                            Toast.makeText(applicationContext, "Errore database: ${dbError.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(applicationContext, "Errore database: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun verifyUserAndPassword(snapshot: DataSnapshot, password: String) {
        for (userSnapshot in snapshot.children) {
            val user = userSnapshot.getValue(UtentiRegistrati::class.java)
            if (user != null && user.password == password) {
                Toast.makeText(applicationContext, "Accesso riuscito", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@AccediActivity, SceltaActivity::class.java)
                intent.putExtra("USER_ID", user.id)
                startActivity(intent)
                finish()
                return
            }
        }
        Snackbar.make(binding.main, "Password errata", Snackbar.LENGTH_LONG).show()
    }
}