package it.uninsubria.ricette

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
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

        val emailPattern = "^[A-Za-z][A-Za-z0-9]*@[A-Za-z0-9]+\\.[A-Za-z]+$"
        val phonePattern = "^[0-9]{10}$"

        if (numeroTelOrEmail.isEmpty() || (!numeroTelOrEmail.matches(emailPattern.toRegex()) && !numeroTelOrEmail.matches(phonePattern.toRegex()))) {
            binding.editTextR1.error = "Scrivi un numero di telefono o un email valida"
            isValid = false
        }
        if (nomeCognome.isEmpty() || nomeCognome.split(" ").size < 2) {
            binding.editTextR2.error = "Inserisci nome e cognome completi"
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
            Snackbar.make(binding.main, "Compila tutti i campi", Snackbar.LENGTH_SHORT).show()
            return
        }

        checkDuplicate(numeroTelOrEmail, nomeCognome, username, password)
    }

    private fun checkDuplicate(numeroTelOrEmail: String, nomeCognome: String, username: String, password: String) {
        val numeroTelQuery: Query = firebaseRef.orderByChild("numeroTelOrEmail").equalTo(numeroTelOrEmail)
        numeroTelQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Snackbar.make(binding.main, "Numero di telefono o email già associato a un altro account", Snackbar.LENGTH_SHORT).show()
                } else {
                    // Continua a verificare l'username solo se il numero di telefono o l'email non sono già registrati
                    checkUsername(username, numeroTelOrEmail, nomeCognome, password)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(binding.main, "Errore nella verifica del numero di telefono/email: ${error.message}", Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun checkUsername(username: String, numeroTelOrEmail: String, nomeCognome: String, password: String) {
        val usernameQuery: Query = firebaseRef.orderByChild("username").equalTo(username)
        usernameQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Snackbar.make(binding.main, "Username già associato a un altro account", Snackbar.LENGTH_SHORT).show()
                } else {
                    // Procedi con la registrazione se anche l'username non è duplicato
                    registerUser(numeroTelOrEmail, nomeCognome, username, password)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(binding.main, "Errore nella verifica dell'username: ${error.message}", Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun registerUser(numeroTelOrEmail: String, nomeCognome: String, username: String, password: String) {
        val utenteId = firebaseRef.push().key!!
        val utenti = UtentiRegistrati(utenteId, numeroTelOrEmail, nomeCognome, username, password)
        firebaseRef.child(utenteId).setValue(utenti)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Snackbar.make(binding.main, "Dati memorizzati con successo", Snackbar.LENGTH_SHORT).show()

                    val intent = Intent(this@RegistrazioneActivity2, SceltaActivity::class.java)
                    intent.putExtra("USERNAME", username) // Passa lo username a SceltaActivity
                    startActivity(intent)
                } else {
                    Snackbar.make(binding.main, "Errore nel salvataggio dei dati", Snackbar.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Snackbar.make(binding.main, "Errore: ${exception.message}", Snackbar.LENGTH_SHORT).show()
            }
    }
}
