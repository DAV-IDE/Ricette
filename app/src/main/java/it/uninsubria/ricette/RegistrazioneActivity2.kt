package it.uninsubria.ricette

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.*
import it.uninsubria.ricette.databinding.ActivityRegistrazione2Binding

class RegistrazioneActivity2 : AppCompatActivity() {

    lateinit var binding: ActivityRegistrazione2Binding
    private lateinit var firebaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegistrazione2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseRef = FirebaseDatabase.getInstance().getReference("utenti")
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun registrazione(view: View) {
        saveData()
    }

    private fun saveData() {
        val numeroTelOrEmail = binding.editTextR1.text.toString()
        val nomeCognome = binding.editTextR2.text.toString()
        val username = binding.editTextR3.text.toString()
        val password = binding.editTextR4.text.toString()

        var isValid = true

        if (numeroTelOrEmail.isEmpty()) {
            binding.editTextR1.error = "Scrivi il tuo numero"
            isValid = false
        }
        if (nomeCognome.isEmpty()) {
            binding.editTextR2.error = "Scrivi il tuo nome"
            isValid = false
        }
        if (username.isEmpty()) {
            binding.editTextR3.error = "Scrivi il tuo username"
            isValid = false
        }
        if (password.isEmpty()) {
            binding.editTextR4.error = "Scrivi la tua password"
            isValid = false
        }

        if (!isValid) {
            Toast.makeText(applicationContext, "Compila tutti i campi", Toast.LENGTH_SHORT).show()
            return
        }

        checkDuplicate(numeroTelOrEmail, nomeCognome, username, password)
    }

    private fun checkDuplicate(numeroTelOrEmail: String, nomeCognome: String, username: String, password: String) {
        val numeroTelQuery: Query = firebaseRef.orderByChild("numeroTelOrEmail").equalTo(numeroTelOrEmail)
        numeroTelQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(applicationContext, "Numero di telefono già registrato", Toast.LENGTH_SHORT).show()
                } else {
                    val usernameQuery: Query = firebaseRef.orderByChild("username").equalTo(username)
                    usernameQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                Toast.makeText(applicationContext, "Username già registrato", Toast.LENGTH_SHORT).show()
                            } else {
                                val utenteId = firebaseRef.push().key!!
                                val utenti = UtentiRegistrati(utenteId, numeroTelOrEmail, nomeCognome, username, password)
                                firebaseRef.child(utenteId).setValue(utenti)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(applicationContext, "Dati memorizzati con successo", Toast.LENGTH_SHORT).show()
                                            val intent = Intent(this@RegistrazioneActivity2, SceltaActivity::class.java)
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(applicationContext, "Errore nel salvataggio dei dati", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(applicationContext, "Errore: ${exception.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(applicationContext, "Errore nella verifica: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Errore nella verifica: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}