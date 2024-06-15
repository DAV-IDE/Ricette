package it.uninsubria.ricette

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import it.uninsubria.ricette.databinding.ActivityRicettaBinding

class RicettaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRicettaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRicettaBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val ricetta = intent.getParcelableExtra<Ricette>("RICETTA")
//        if (ricetta != null) {
//            binding.textViewTitolo4.text = ricetta.nome
//            binding.textViewPDB.text = ricetta.procedimento
//            // Popola la UI con gli altri dettagli della ricetta
//        }
    }
}