package it.uninsubria.ricette

data class Ricette(
    val nome: String = "",
    val ingredienti: List<String> = emptyList(),
    val quantita: List<String> = emptyList(),
    val unita: List<String> = emptyList(),
    val procedimento: String = ""
)
