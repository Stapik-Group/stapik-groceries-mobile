package pl.stapik.groceries.domain.model

data class WeeklyPlanItem(
    val productId: String,
    val productName: String,
    val categoryName: String?,
    val quantity: Double,
    val unitName: String,
    val isWholeUnits: Boolean,
    val plannedUnitPrice: Double,
)

data class WeeklyPlan(
    val month: MonthId,
    val currentWeek: Int,
    val totalWeeks: Int,
    val items: List<WeeklyPlanItem>,
) {
    val id: String get() = "${month.key}-w$currentWeek"
}
