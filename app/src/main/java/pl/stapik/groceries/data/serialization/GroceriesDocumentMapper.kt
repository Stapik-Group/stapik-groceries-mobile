package pl.stapik.groceries.data.serialization

import pl.stapik.groceries.domain.model.Category
import pl.stapik.groceries.domain.model.GroceriesDocument
import pl.stapik.groceries.domain.model.MonthId
import pl.stapik.groceries.domain.model.MonthlyList
import pl.stapik.groceries.domain.model.Product

internal fun GroceriesDocumentDto.toDomain(): GroceriesDocument = GroceriesDocument(
    categories = categories.map { it.toDomain() },
    months = months.map { it.toDomain() }.sortedBy { it.month },
    currencyCode = currency,
    lastUpdateEpochMs = lastUpdate,
)

private fun CategoryDto.toDomain() = Category(id = id, name = name, colorHex = colorHex)

private fun MonthDto.toDomain() = MonthlyList(
    month = MonthId.fromKey(month),
    products = products.map { it.toDomain() },
    offBudgetAmount = offBudgetAmount,
)

private fun ProductDto.toDomain() = Product(
    id = id,
    name = name,
    categoryId = categoryId,
    unitName = unitName,
    plannedQuantity = plannedQuantity,
    plannedUnitPrice = plannedUnitPrice,
    purchasedQuantity = purchasedQuantity ?: timesPurchased.toDouble(),
    realAverageUnitPrice = realAverageUnitPrice,
)
