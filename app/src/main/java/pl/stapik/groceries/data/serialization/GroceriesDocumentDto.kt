package pl.stapik.groceries.data.serialization

import kotlinx.serialization.Serializable

@Serializable
internal data class GroceriesDocumentDto(
    val formatVersion: Int = 1,
    val lastUpdate: Long = 0L,
    val currency: String = "PLN",
    val categories: List<CategoryDto> = emptyList(),
    val months: List<MonthDto> = emptyList(),
)

@Serializable
internal data class CategoryDto(
    val id: String,
    val name: String,
    val colorHex: String = "#CCCCCC",
)

@Serializable
internal data class MonthDto(
    val month: String,
    val offBudgetAmount: Double = 0.0,
    val products: List<ProductDto> = emptyList(),
)

@Serializable
internal data class ProductDto(
    val id: String,
    val name: String,
    val categoryId: String,
    val unitName: String,
    val plannedQuantity: Double,
    val plannedUnitPrice: Double,
    val purchasedQuantity: Double? = null,
    /** Legacy field the desktop app still reads when purchasedQuantity is absent. */
    val timesPurchased: Int = 0,
    val realAverageUnitPrice: Double = 0.0,
)
