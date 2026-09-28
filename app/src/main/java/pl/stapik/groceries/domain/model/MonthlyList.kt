package pl.stapik.groceries.domain.model

data class MonthlyList(
    val month: MonthId,
    val products: List<Product>,
    val offBudgetAmount: Double,
) {
    val plannedTotal: Double get() = products.sumOf { it.plannedTotal }
    val realTotal: Double get() = products.sumOf { it.realTotal }
    val savingsTotal: Double get() = products.sumOf { it.savings }
    val projectedMonthlyCost: Double get() = plannedTotal - savingsTotal + offBudgetAmount
}
