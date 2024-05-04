package it.uninsubria.ricette

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SceltaActivity : AppCompatActivity() {

    private lateinit var buttonCerca : Button
    private lateinit var buttonCrea : Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scelta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        buttonCerca = findViewById(R.id.buttonSceltaCerca)
        buttonCrea = findViewById(R.id.buttonSceltaCrea)

        buttonCerca.setOnClickListener {
            val intent = Intent (this@SceltaActivity, CercaRicetteActivity :: class.java)
            startActivity(intent)
        }

        buttonCrea.setOnClickListener {
            val intent = Intent (this@SceltaActivity, CreaRicetteActivity :: class.java)
            startActivity(intent)
        }
    }
}