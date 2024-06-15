package it.uninsubria.ricette

import android.os.Parcel
import android.os.Parcelable

data class Ricette(
    val nome: String = "",
    val ingredienti: List<String> = emptyList(),
    val quantita: List<String> = emptyList(),
    val unita: List<String> = emptyList(),
    val procedimento: String = "",
    val fotoUrl: String = "",
    val username: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList(),
        parcel.createStringArrayList() ?: emptyList(),
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nome)
        parcel.writeStringList(ingredienti)
        parcel.writeStringList(quantita)
        parcel.writeStringList(unita)
        parcel.writeString(procedimento)
        parcel.writeString(fotoUrl)
        parcel.writeString(username)
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
