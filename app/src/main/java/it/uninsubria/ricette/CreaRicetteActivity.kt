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

class CreaRicetteActivity : AppCompatActivity() {
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private lateinit var layout: ConstraintLayout
    private var lastComponentId: Int = R.id.labelQuantity

    companion object {
        private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crea_ricette)

        layout=findViewById(R.id.main)

        setupSpinnerIngredients()
        setupSpinnerUnit()
        setupImagePicker()

        lastComponentId = R.id.spinnerUnitMisura

        val addButton: Button = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            addNewComponents()
        }
    }

    private fun setupImagePicker() {
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
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

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.units_array,
            android.R.layout.simple_spinner_dropdown_item
        )
        spinner.adapter = adapter
    }

    private fun addNewComponents() {
        val newSpinnerIngredients = createSpinner(R.array.ingredients_array)
        layout.addView(newSpinnerIngredients)

        val newEditTextQuantity = createEditText()
        layout.addView(newEditTextQuantity)

        val newSpinnerUnits = createSpinner(R.array.units_array)
        layout.addView(newSpinnerUnits)

        applyConstraints(newSpinnerIngredients, newEditTextQuantity, newSpinnerUnits)
    }

    private fun createSpinner(arrayResId: Int): Spinner {
        return Spinner(this).apply {
            id = View.generateViewId()
            layoutParams =
                ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT)
            setPadding(0, 16, 0, 16)
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

    private fun createEditText(): EditText {
        return EditText(this).apply {
            id = View.generateViewId()
            layoutParams =
                ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT)
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            background =
                ContextCompat.getDrawable(this@CreaRicetteActivity, R.drawable.ingredient_border)
            setTextColor(ContextCompat.getColor(this@CreaRicetteActivity, R.color.black))
            setPadding(0, 16, 0, 16)
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
            ConstraintSet.TOP,
            lastComponentId,
            ConstraintSet.BOTTOM,
            44  // Margine verticale tra i gruppi
        )
        constraintSet.constrainWidth(spinnerIngredients.id, 200)
        constraintSet.constrainHeight(spinnerIngredients.id, 40)

        // Connessione dell'EditText della quantità
        constraintSet.connect(
            editTextQuantity.id,
            ConstraintSet.START,
            spinnerIngredients.id,
            ConstraintSet.END,
            8  // Margine orizzontale tra gli elementi
        )
        constraintSet.connect(
            editTextQuantity.id,
            ConstraintSet.TOP,
            spinnerIngredients.id,
            ConstraintSet.TOP
        )
        constraintSet.constrainWidth(editTextQuantity.id, 100)
        constraintSet.constrainHeight(editTextQuantity.id, 40)

        // Connessione dello Spinner delle unità di misura
        constraintSet.connect(
            spinnerUnits.id,
            ConstraintSet.START,
            editTextQuantity.id,
            ConstraintSet.END,
            8  // Margine orizzontale tra gli elementi
        )
        constraintSet.connect(
            spinnerUnits.id,
            ConstraintSet.TOP,
            editTextQuantity.id,
            ConstraintSet.TOP
        )
        constraintSet.connect(
            spinnerUnits.id,
            ConstraintSet.END,
            ConstraintSet.PARENT_ID,
            ConstraintSet.END
        )
        constraintSet.constrainWidth(spinnerUnits.id, 60)
        constraintSet.constrainHeight(spinnerUnits.id, 40)

        // Applica i vincoli ai nuovi componenti
        constraintSet.applyTo(layout)

        // Aggiorna l'ultimo componente aggiunto per posizionare correttamente i successivi
        lastComponentId = spinnerUnits.id

        // Aggiorna i vincoli per textViewProcedimento e editTextProcedimento
        updateProcedimentoConstraints()
    }

    private fun updateProcedimentoConstraints() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(layout)

        // Aggiorna il vincolo di textViewProcedimento per ancorarlo sotto l'ultimo componente inserito
        constraintSet.connect(
            R.id.textViewProcedimento,
            ConstraintSet.TOP,
            lastComponentId,
            ConstraintSet.BOTTOM,
            20  // Margine di 20dp dall'ultimo componente
        )

        // Aggiorna il vincolo di editTextProcedimento per ancorarlo sotto textViewProcedimento
        constraintSet.connect(
            R.id.editTextProcedimento,
            ConstraintSet.TOP,
            R.id.textViewProcedimento,
            ConstraintSet.BOTTOM,
            8  // Margine di 8dp da textViewProcedimento
        )

        constraintSet.applyTo(layout)
    }


}


