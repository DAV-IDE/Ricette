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
import android.text.InputType
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
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CreaRicetteActivity : AppCompatActivity() {
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private lateinit var layout: ConstraintLayout
    private var lastComponentId: Int = R.id.labelQuantity
    private var userId: String? = null
    private var selectedImageUri: Uri? = null
    private lateinit var storageReference: StorageReference

    companion object {
        private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crea_ricette)

        layout = findViewById(R.id.main)
        userId = intent.getStringExtra("USER_ID")
        storageReference = FirebaseStorage.getInstance().reference

        setupSpinnerIngredients()
        setupSpinnerUnit()
        setupImagePicker()

        lastComponentId = R.id.spinnerUnitMisura

        val addButton: Button = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            addNewComponents()
        }

        val saveButton: Button = findViewById(R.id.buttonCondividi)
        saveButton.setOnClickListener {
            saveRicetta()
        }
    }

    fun registraRicetta(view: View) {
        saveRicetta()
    }

    private fun saveRicetta() {
        val nome = findViewById<EditText>(R.id.editTextNomeRicetta).text.toString()
        val procedimento = findViewById<EditText>(R.id.editTextProcedimento).text.toString()

        val ingredients = mutableListOf<String>()
        val quantities = mutableListOf<String>()
        val units = mutableListOf<String>()

        for (i in 0 until layout.childCount) {
            val view = layout.getChildAt(i)
            if (view is Spinner && view.adapter != null && view.adapter.count > 0) {
                when (view.tag) {
                    "ingredient" -> ingredients.add(view.selectedItem.toString())
                    "unit" -> units.add(view.selectedItem.toString())
                }
            } else if (view is EditText && view.inputType == (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)) {
                quantities.add(view.text.toString())
            }
        }

        if (selectedImageUri != null) {
            val photoRef = storageReference.child("images/${selectedImageUri?.lastPathSegment}")
            photoRef.putFile(selectedImageUri!!)
                .addOnSuccessListener {
                    photoRef.downloadUrl.addOnSuccessListener { uri ->
                        val ricetta = Ricette(nome, ingredients, quantities, units, procedimento, uri.toString())
                        saveRicettaToDatabase(ricetta)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Errore nel caricamento della foto", Toast.LENGTH_SHORT).show()
                }
        } else {
            val ricetta = Ricette(nome, ingredients, quantities, units, procedimento)
            saveRicettaToDatabase(ricetta)
        }
    }

    private fun saveRicettaToDatabase(ricetta: Ricette) {
        val database = FirebaseDatabase.getInstance().reference
        if (userId != null) {
            val ricettaId = database.child("ricette").child(userId!!).push().key
            if (ricettaId != null) {
                database.child("ricette").child(userId!!).child(ricettaId).setValue(ricetta)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Ricetta salvata con successo", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Errore nel salvataggio della ricetta", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(this, "Utente non autenticato", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupImagePicker() {
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    selectedImageUri = it
                    val bitmap = decodeUriToBitmap(this, it)
                    findViewById<ImageView>(R.id.uploadedImage).setImageBitmap(bitmap)
                }
            }

        findViewById<Button>(R.id.buttonCaricaImmagine).setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                )
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

    private fun setupSpinnerIngredients() {
        val spinner: Spinner = findViewById(R.id.spinnerIngredients)
        spinner.tag = "ingredient"

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.ingredients_array,
            android.R.layout.simple_spinner_dropdown_item
        )
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    Toast.makeText(
                        applicationContext,
                        "Per favore seleziona un ingrediente valido",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    Toast.makeText(
                        applicationContext,
                        "Selezionato: $selectedItem",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Opzionale: cosa fare quando non viene selezionato nulla
            }
        }
    }

    private fun setupSpinnerUnit() {
        val spinner: Spinner = findViewById(R.id.spinnerUnitMisura)
        spinner.tag = "unit"

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.units_array,
            android.R.layout.simple_spinner_dropdown_item
        )
        spinner.adapter = adapter
    }

    fun Int.dpToPx(): Int {
        val density = this@CreaRicetteActivity.resources.displayMetrics.density
        return (this * density).toInt()
    }

    private fun addNewComponents() {
        val newSpinnerIngredients = createSpinner(R.array.ingredients_array, 200)
        newSpinnerIngredients.tag = "ingredient"
        layout.addView(newSpinnerIngredients)

        val newEditTextQuantity = createEditText(100)
        layout.addView(newEditTextQuantity)

        val newSpinnerUnits = createSpinner(R.array.units_array, 60)
        newSpinnerUnits.tag = "unit"
        layout.addView(newSpinnerUnits)

        applyConstraints(newSpinnerIngredients, newEditTextQuantity, newSpinnerUnits)
    }

    private fun createSpinner(arrayResId: Int, width: Int): Spinner {
        return Spinner(this).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(width.dpToPx(), 40.dpToPx())
            adapter = ArrayAdapter.createFromResource(
                this@CreaRicetteActivity,
                arrayResId,
                android.R.layout.simple_spinner_dropdown_item
            )
            background = ContextCompat.getDrawable(
                this@CreaRicetteActivity,
                R.drawable.background_spinner_with_icon
            )
        }
    }

    private fun createEditText(width: Int): EditText {
        return EditText(this).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(width.dpToPx(), 40.dpToPx())
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            background = ContextCompat.getDrawable(this@CreaRicetteActivity, R.drawable.ingredient_border)
            setTextColor(ContextCompat.getColor(this@CreaRicetteActivity, R.color.black))
        }
    }

    private fun applyConstraints(
        spinnerIngredients: Spinner,
        editTextQuantity: EditText,
        spinnerUnits: Spinner
    ) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(layout)

        // Connessione dello Spinner degli ingredienti
        constraintSet.connect(
            spinnerIngredients.id,
            ConstraintSet.START,
            ConstraintSet.PARENT_ID,
            ConstraintSet.START
        )
        constraintSet.connect(
            spinnerIngredients.id,
            ConstraintSet.END,
            editTextQuantity.id,
            ConstraintSet.START
        )
        constraintSet.connect(
            spinnerIngredients.id,
            ConstraintSet.TOP,
            lastComponentId,
            ConstraintSet.BOTTOM,
            44  // Margine verticale tra i gruppi
        )

        // Connessione dell'EditText della quantità
        constraintSet.connect(
            editTextQuantity.id,
            ConstraintSet.START,
            spinnerIngredients.id,
            ConstraintSet.END
        )
        constraintSet.connect(
            editTextQuantity.id,
            ConstraintSet.END,
            spinnerUnits.id,
            ConstraintSet.START
        )
        constraintSet.connect(
            editTextQuantity.id,
            ConstraintSet.TOP,
            spinnerIngredients.id,
            ConstraintSet.TOP
        )

        // Connessione dello Spinner delle unità di misura
        constraintSet.connect(
            spinnerUnits.id,
            ConstraintSet.START,
            editTextQuantity.id,
            ConstraintSet.END
        )
        constraintSet.connect(
            spinnerUnits.id,
            ConstraintSet.END,
            ConstraintSet.PARENT_ID,
            ConstraintSet.END
        )
        constraintSet.connect(
            spinnerUnits.id,
            ConstraintSet.TOP,
            editTextQuantity.id,
            ConstraintSet.TOP
        )

        // Aggiorna i vincoli per addButton per spostarlo sotto l'ultimo spinner unit
        constraintSet.connect(
            R.id.addButton,
            ConstraintSet.TOP,
            spinnerUnits.id,
            ConstraintSet.BOTTOM,
            20  // Margine di 20dp dall'ultimo componente
        )

        // Aggiorna il vincolo di textViewProcedimento per ancorarlo sotto addButton
        constraintSet.connect(
            R.id.textViewProcedimento,
            ConstraintSet.TOP,
            R.id.addButton,
            ConstraintSet.BOTTOM,
            20  // Margine di 20dp da addButton
        )

        // Aggiorna il vincolo di editTextProcedimento per ancorarlo sotto textViewProcedimento
        constraintSet.connect(
            R.id.editTextProcedimento,
            ConstraintSet.TOP,
            R.id.textViewProcedimento,
            ConstraintSet.BOTTOM,
            8  // Margine di 8dp da textViewProcedimento
        )

        // Applica i vincoli ai nuovi componenti
        constraintSet.applyTo(layout)

        // Aggiorna l'ultimo componente aggiunto per posizionare correttamente i successivi
        lastComponentId = spinnerUnits.id
    }
}
