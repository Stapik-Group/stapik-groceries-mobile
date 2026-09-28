package pl.stapik.groceries.ui.weekly

import pl.stapik.groceries.domain.model.Currency
import pl.stapik.groceries.domain.model.MonthId
import pl.stapik.groceries.domain.model.WeeklyPlan

sealed interface WeeklyPlanUiState {
    data object NoData : WeeklyPlanUiState

    data class MonthEnded(val month: MonthId) : WeeklyPlanUiState

    data class Ready(
        val plan: WeeklyPlan,
        val checkedProductIds: Set<String>,
        val currency: Currency,
    ) : WeeklyPlanUiState
}
