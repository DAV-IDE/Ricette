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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import java.text.SimpleDateFormat
import java.util.*

class CreaRicetteActivity : AppCompatActivity() {
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    companion object {
        private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crea_ricette)

        // Enable full screen content display under the system bars
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var inputStream = context.contentResolver.openInputStream(selectedImage)
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream?.close()

        val scaleFactor = Math.max(options.outWidth / 1024, options.outHeight / 1024)

        options.inJustDecodeBounds = false
        options.inSampleSize = scaleFactor

        inputStream = context.contentResolver.openInputStream(selectedImage)
        val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
        inputStream?.close()
        return bitmap
    }
}


