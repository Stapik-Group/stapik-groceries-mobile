package pl.stapik.groceries.ui.format

import java.math.BigDecimal
import java.math.RoundingMode

class QuantityFormatter(
    private val decimalSeparator: Char = DecimalSeparator.forDefaultLocale(),
) {
    fun format(value: Double): String =
        BigDecimal(value)
            .setScale(3, RoundingMode.HALF_UP)
            .stripTrailingZeros()
            .toPlainString()
            .replace('.', decimalSeparator)
}
