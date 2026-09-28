package pl.stapik.groceries.domain.planning

import pl.stapik.groceries.domain.model.Category
import pl.stapik.groceries.domain.model.GroceriesDocument
import pl.stapik.groceries.domain.model.MonthId
import pl.stapik.groceries.domain.model.MonthlyList
import pl.stapik.groceries.domain.model.Product
import pl.stapik.groceries.domain.model.WeeklyPlan
import pl.stapik.groceries.domain.model.WeeklyPlanItem
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.round

sealed interface WeeklyPlanOutcome {
    data class Generated(val plan: WeeklyPlan) : WeeklyPlanOutcome
    data class MonthEnded(val month: MonthId) : WeeklyPlanOutcome
}

class WeeklyPlanGenerator {
    fun generate(document: GroceriesDocument, monthId: MonthId, today: LocalDate): WeeklyPlanOutcome? {
        val month = document.months.firstOrNull { it.month == monthId } ?: return null

        val currentCalendarMonth = MonthId(today.year, today.monthValue)
        if (monthId < currentCalendarMonth) return WeeklyPlanOutcome.MonthEnded(monthId)

        val referenceDate =
            if (monthId == currentCalendarMonth) today else LocalDate.of(monthId.year, monthId.month, 1)

        return WeeklyPlanOutcome.Generated(generate(month, document.categories, referenceDate))
    }

    private fun generate(month: MonthlyList, categories: List<Category>, referenceDate: LocalDate): WeeklyPlan {
        val currentWeek = weekNumberInMonth(referenceDate.year, referenceDate.monthValue, referenceDate.dayOfMonth)
        val totalWeeks = weekNumberInMonth(referenceDate.year, referenceDate.monthValue, referenceDate.lengthOfMonth())
        val weeksRemaining = maxOf(1, totalWeeks - currentWeek + 1)

        val categoriesById = categories.associateBy { it.id }
        val items = month.products.mapNotNull { product ->
            toItem(product, categoriesById[product.categoryId]?.name, weeksRemaining)
        }

        return WeeklyPlan(month.month, currentWeek, totalWeeks, items)
    }

    private fun toItem(product: Product, categoryName: String?, weeksRemaining: Int): WeeklyPlanItem? {
        val remaining = product.plannedQuantity - product.purchasedQuantity
        if (remaining <= EPSILON) return null

        val rounded = round(remaining)
        val isWholeUnits = abs(remaining - rounded) < EPSILON

        val quantity = if (isWholeUnits) {
            val weeks = weeksRemaining.toLong()
            ((rounded.toLong() + weeks - 1) / weeks).toDouble()
        } else {
            remaining / weeksRemaining
        }
        if (quantity <= EPSILON) return null

        return WeeklyPlanItem(
            productId = product.id,
            productName = product.name,
            categoryName = categoryName,
            quantity = quantity,
            unitName = product.unitName,
            isWholeUnits = isWholeUnits,
            plannedUnitPrice = product.plannedUnitPrice,
        )
    }

    private fun weekNumberInMonth(year: Int, month: Int, day: Int): Int {
        val firstIsoWeekday = LocalDate.of(year, month, 1).dayOfWeek.value
        return (day + firstIsoWeekday - 2) / 7 + 1
    }

    private companion object {
        const val EPSILON = 1e-6
    }
}
