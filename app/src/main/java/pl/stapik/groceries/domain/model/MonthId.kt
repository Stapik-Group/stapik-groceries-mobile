package pl.stapik.groceries.domain.model

import java.util.Locale

data class MonthId(val year: Int, val month: Int) : Comparable<MonthId> {
    init {
        require(month in 1..12) { "Invalid month: $month" }
    }

    val key: String get() = String.format(Locale.ROOT, "%04d-%02d", year, month)

    override fun compareTo(other: MonthId): Int =
        compareValuesBy(this, other, MonthId::year, MonthId::month)

    companion object {
        fun fromKey(key: String): MonthId {
            require(key.length == 7 && key[4] == '-') { "Invalid month key: $key" }
            return MonthId(year = key.substring(0, 4).toInt(), month = key.substring(5, 7).toInt())
        }
    }
}
