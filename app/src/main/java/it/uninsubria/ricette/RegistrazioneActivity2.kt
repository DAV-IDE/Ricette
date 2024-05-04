package it.uninsubria.ricette

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistrazioneActivity2 : AppCompatActivity() {

    private lateinit var buttonRegistrazione: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrazione2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        buttonRegistrazione = findViewById(R.id.buttonR2)

        buttonRegistrazione.setOnClickListener {
            val intent = Intent (this@RegistrazioneActivity2, SceltaActivity :: class.java)
            startActivity(intent)
        }
    }
}