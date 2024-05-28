package it.uninsubria.ricette

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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
        val numeroTel = binding.editTextR1.text.toString()
        val nomeCognome = binding.editTextR2.text.toString()
        val username = binding.editTextR3.text.toString()
        val password = binding.editTextR4.text.toString()

        var isValid = true

        if (numeroTel.isEmpty()) {
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
            // Mostra un toast per notificare l'utente che ci sono errori
            Toast.makeText(applicationContext, "Compila tutti i campi", Toast.LENGTH_SHORT).show()
            return
        }

        val utenteId = firebaseRef.push().key!!
        val utenti = UtentiRegistrati(utenteId, numeroTel, nomeCognome, username, password)

        firebaseRef.child(utenteId).setValue(utenti)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
//                    runOnUiThread {
//                        Toast.makeText(applicationContext, "Dati memorizzati con successo", Toast.LENGTH_SHORT).show()
//                    }
                    val intent = Intent(this@RegistrazioneActivity2, SceltaActivity::class.java)
                    startActivity(intent)
               }
                //                else {
//                    runOnUiThread {
//                        Toast.makeText(applicationContext, "Errore nel salvataggio dei dati", Toast.LENGTH_SHORT).show()
//                    }
//                }
            }
//            .addOnFailureListener { exception ->
//                runOnUiThread {
//                    Toast.makeText(applicationContext, "Errore: ${exception.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
    }
}

