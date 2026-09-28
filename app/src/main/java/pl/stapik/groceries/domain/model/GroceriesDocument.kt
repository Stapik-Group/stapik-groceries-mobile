package pl.stapik.groceries.domain.model

data class GroceriesDocument(
    val categories: List<Category>,
    val months: List<MonthlyList>,
    val currencyCode: String,
    val lastUpdateEpochMs: Long,
) {
    val currency: Currency get() = CurrencyCatalog.findByCode(currencyCode)
    val monthsNewestFirst: List<MonthlyList> get() = months.sortedByDescending { it.month }
    val categoriesById: Map<String, Category> by lazy { categories.associateBy { it.id } }

    companion object {
        val EMPTY = GroceriesDocument(emptyList(), emptyList(), CurrencyCatalog.default.code, 0L)
    }
}
