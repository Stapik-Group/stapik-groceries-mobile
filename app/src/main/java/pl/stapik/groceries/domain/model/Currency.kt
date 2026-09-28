package pl.stapik.groceries.domain.model

data class Currency(
    val code: String,
    val symbol: String,
    val symbolBeforeAmount: Boolean,
)

object CurrencyCatalog {
    private val all = listOf(
        Currency("PLN", "zł", symbolBeforeAmount = false),
        Currency("EUR", "€", symbolBeforeAmount = false),
        Currency("USD", "$", symbolBeforeAmount = true),
        Currency("GBP", "£", symbolBeforeAmount = true),
        Currency("CZK", "Kč", symbolBeforeAmount = false),
        Currency("CHF", "CHF", symbolBeforeAmount = false),
    )

    val default: Currency = all.first()

    fun findByCode(code: String): Currency = all.firstOrNull { it.code == code } ?: default
}
