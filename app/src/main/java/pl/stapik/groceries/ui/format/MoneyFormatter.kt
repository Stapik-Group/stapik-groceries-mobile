package pl.stapik.groceries.ui.format

import pl.stapik.groceries.domain.model.Currency
import java.util.Locale

object DecimalSeparator {
    fun forDefaultLocale(): Char = if (Locale.getDefault().language == "en") '.' else ','
}

class MoneyFormatter(
    private val currency: Currency,
    private val decimalSeparator: Char = DecimalSeparator.forDefaultLocale(),
) {
    fun format(amount: Double): String {
        val number = String.format(Locale.ROOT, "%.2f", amount).replace('.', decimalSeparator)
        return if (currency.symbolBeforeAmount) "${currency.symbol}$number" else "$number ${currency.symbol}"
    }
}
