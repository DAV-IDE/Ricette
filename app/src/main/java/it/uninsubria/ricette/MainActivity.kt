package it.uninsubria.ricette

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var buttonRegistrazione: Button
    private lateinit var buttonAccedi: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

            // ciao professore tutto bene?
            //bene
        }*/

        buttonRegistrazione = findViewById(R.id.buttonregistrati)
        buttonAccedi = findViewById(R.id.buttonaccedi)

        buttonRegistrazione.setOnClickListener {
            val intent = Intent (this@MainActivity, RegistrazioneActivity2 :: class.java)
            startActivity(intent)
        }

        buttonAccedi.setOnClickListener {
            val intent = Intent (this@MainActivity, AccediActivity :: class.java)
            startActivity(intent)
        }
    }
}