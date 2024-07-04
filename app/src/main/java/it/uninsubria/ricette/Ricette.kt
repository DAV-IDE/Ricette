package it.uninsubria.ricette

import android.os.Parcel
import android.os.Parcelable

data class Ricette(
    var nome: String = "",
    var ingredienti: List<String> = emptyList(),
    var quantita: List<String> = emptyList(),
    var unita: List<String> = emptyList(),
    var procedimento: String = "",
    var fotoUrl: String = "",
    var username: String = "",
    var recipeId: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList(),
        parcel.createStringArrayList() ?: emptyList(),
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "" // Legge il recipeId dal Parcel
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nome)
        parcel.writeStringList(ingredienti)
        parcel.writeStringList(quantita)
        parcel.writeStringList(unita)
        parcel.writeString(procedimento)
        parcel.writeString(fotoUrl)
        parcel.writeString(username)
        parcel.writeString(recipeId) // Scrive il recipeId nel Parcel
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Ricette> {
        override fun createFromParcel(parcel: Parcel): Ricette {
            return Ricette(parcel)
        }

        override fun newArray(size: Int): Array<Ricette?> {
            return arrayOfNulls(size)
        }
    }
}
