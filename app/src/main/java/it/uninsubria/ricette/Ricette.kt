
package it.uninsubria.ricette

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ricette(
    val id: String= "",
    val nome: String = "",
    val ingredients: List<String> = listOf(),
    val quantities: List<String> = listOf(),
    val units: List<String> = listOf(),
    val procedimento: String = "",
    val fotoUrl: String = ""
) : Parcelable
