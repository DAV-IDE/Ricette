package it.uninsubria.ricette

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import it.uninsubria.ricette.databinding.ActivityRegistrazione2Binding

class RegistrazioneActivity2 : AppCompatActivity() {

    lateinit var binding: ActivityRegistrazione2Binding
    private lateinit var firebaseRef : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegistrazione2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseRef = FirebaseDatabase.getInstance().getReference("utenti")
        //setContentView(R.layout.activity_registrazione2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun registrazione(view: View) {
        val intent = Intent (this@RegistrazioneActivity2, SceltaActivity :: class.java)
        startActivity(intent)
        saveData()
    }

    private fun saveData() {
        val email= binding.editTextR1.text.toString()
        val nomeCognome = binding.editTextR2.text.toString()
        val username = binding.editTextR3.text.toString()
        val password = binding.editTextR4.text.toString()

        if(email.isEmpty()) binding.editTextR1.error = "Scrivi la tua email"
        if(nomeCognome.isEmpty()) binding.editTextR1.error = "Scrivi il tuo nome"
        if(username.isEmpty()) binding.editTextR1.error = "Scrivi il tuo username"
        if(password.isEmpty()) binding.editTextR1.error = "Scrivi la tua password"

        val utenteId = firebaseRef.push().key!!
        val utenti = UtentiRegistrati(utenteId, email, nomeCognome, username, password)

        firebaseRef.child(utenteId).setValue(utenti)


    }

}