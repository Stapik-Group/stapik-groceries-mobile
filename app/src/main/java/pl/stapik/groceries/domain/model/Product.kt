package pl.stapik.groceries.domain.model

data class Product(
    val id: String,
    val name: String,
    val categoryId: String,
    val unitName: String,
    val plannedQuantity: Double,
    val plannedUnitPrice: Double,
    val purchasedQuantity: Double,
    val realAverageUnitPrice: Double,
) {
    val plannedTotal: Double get() = plannedQuantity * plannedUnitPrice
    val realTotal: Double get() = realAverageUnitPrice * purchasedQuantity
    val metPlannedQuantity: Boolean get() = purchasedQuantity >= plannedQuantity

    val savings: Double get() = if (metPlannedQuantity) plannedTotal - realTotal else 0.0
}
