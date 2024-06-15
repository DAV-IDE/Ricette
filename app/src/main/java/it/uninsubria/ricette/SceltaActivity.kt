package it.uninsubria.ricette

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SceltaActivity : AppCompatActivity() {

    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scelta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Recupera lo username dall'intento
        username = intent.getStringExtra("USERNAME")
    }

    fun sceltaProfilo(view: View) {
        val intent = Intent(this@SceltaActivity, ProfiloUtenteActivity::class.java)
        intent.putExtra("USERNAME", username)
        startActivity(intent)
    }

    fun cerca(view: View) {
        val intent = Intent(this@SceltaActivity, CercaRicetteActivity::class.java)
        intent.putExtra("USERNAME", username)
        startActivity(intent)
    }

    fun crea(view: View) {
        val intent = Intent(this@SceltaActivity, CreaRicetteActivity::class.java)
        intent.putExtra("USERNAME", username)
        startActivity(intent)
    }
}
