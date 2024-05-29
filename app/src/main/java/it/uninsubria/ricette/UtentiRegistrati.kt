package it.uninsubria.ricette

data class UtentiRegistrati(
    val id : String? ,
    val numeroTelOrEmail : String? ,
    val nome : String? ,
    val username : String? ,
    val password : String? ,
) {
    constructor() : this("", "", "", "", "")
}
