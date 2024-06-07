package it.uninsubria.ricette

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.WindowManager
import android.widget.AdapterView

class CreaRicetteActivity : AppCompatActivity() {
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    companion object {
        private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crea_ricette)

        setupFullscreen()
        setupSpinner()
        setupImagePicker()
    }

    private fun setupFullscreen() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val bitmap = decodeUriToBitmap(this, it)
                findViewById<ImageView>(R.id.uploadedImage).setImageBitmap(bitmap)
            }
        }

        findViewById<Button>(R.id.buttonCaricaImmagine).setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
            } else {
                imagePickerLauncher.launch("image/*")
            }
        }
    }

    private fun decodeUriToBitmap(context: Context, selectedImage: Uri): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        context.contentResolver.openInputStream(selectedImage).use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
        }

        val scaleFactor = Math.max(options.outWidth / 1024, options.outHeight / 1024)
        options.apply {
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }

        return context.contentResolver.openInputStream(selectedImage).use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
        }
    }

    private fun setupSpinner() {
        val spinner: Spinner = findViewById(R.id.spinnerIngredients)
        // Crea un ArrayAdapter usando l'array di stringhe dal file di risorse
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.ingredients_array,
            android.R.layout.simple_spinner_dropdown_item
        )
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position == 0) {
                    Toast.makeText(applicationContext, "Per favore seleziona un ingrediente valido", Toast.LENGTH_LONG).show()
                } else {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    Toast.makeText(applicationContext, "Selezionato: $selectedItem", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Opzionale: cosa fare quando non viene selezionato nulla
            }
        }
    }
}


