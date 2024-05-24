package it.uninsubria.ricette

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class MainActivity : AppCompatActivity() {
    private lateinit var buttonRegistrazione: Button
    private lateinit var buttonAccedi: Button
    private lateinit var buttonProva: Button
    private lateinit var firebaseRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }*/

        buttonRegistrazione = findViewById(R.id.buttonMainR)
        buttonAccedi = findViewById(R.id.buttonMainA)
        buttonProva = findViewById(R.id.buttonProva)

        buttonRegistrazione.setOnClickListener {
            val intent = Intent (this@MainActivity, RegistrazioneActivity2 :: class.java)
            startActivity(intent)
        }

        buttonAccedi.setOnClickListener {
            val intent = Intent (this@MainActivity, AccediActivity :: class.java)
            startActivity(intent)
        }
    }

    fun prova(view : View){

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")

       myRef.setValue("Hello, World!")


    }

}