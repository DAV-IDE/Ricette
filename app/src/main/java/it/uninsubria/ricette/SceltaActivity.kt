package it.uninsubria.ricette

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SceltaActivity : AppCompatActivity() {

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scelta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Recupera l'ID utente dall'intento
        userId = intent.getStringExtra("USER_ID")
    }

    fun sceltaProfilo(view: View) {
        val intent = Intent(this@SceltaActivity, ProfiloUtenteActivity::class.java)
        startActivity(intent)
    }

    fun cerca(view: View) {
        val intent = Intent(this@SceltaActivity, CercaRicetteActivity::class.java)
        startActivity(intent)
    }

    fun crea(view: View) {
        val intent = Intent(this@SceltaActivity, CreaRicetteActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }
}
