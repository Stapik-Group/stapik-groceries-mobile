package pl.stapik.groceries.ui.weekly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.stapik.groceries.data.local.WeeklyPlanCheckStorage
import pl.stapik.groceries.domain.model.GroceriesDocument
import pl.stapik.groceries.domain.model.MonthId
import pl.stapik.groceries.domain.planning.WeeklyPlanGenerator
import pl.stapik.groceries.domain.planning.WeeklyPlanOutcome
import java.time.LocalDate

class WeeklyPlanViewModel(
    private val generator: WeeklyPlanGenerator,
    private val checkStorage: WeeklyPlanCheckStorage,
    private val today: () -> LocalDate = { LocalDate.now() },
) : ViewModel() {

    private data class Input(val document: GroceriesDocument, val monthId: MonthId, val today: LocalDate)

    private val input = MutableStateFlow<Input?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<WeeklyPlanUiState> = input
        .flatMapLatest { current -> if (current == null) flowOf(WeeklyPlanUiState.NoData) else stateFor(current) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WeeklyPlanUiState.NoData)

    fun open(document: GroceriesDocument, monthId: MonthId) {
        input.value = Input(document, monthId, today())
    }

    fun setChecked(productId: String, checked: Boolean) {
        val planId = currentPlanId() ?: return
        viewModelScope.launch { checkStorage.setChecked(planId, productId, checked) }
    }

    fun clearChecks() {
        val planId = currentPlanId() ?: return
        viewModelScope.launch { checkStorage.clear(planId) }
    }

    private fun currentPlanId(): String? = (uiState.value as? WeeklyPlanUiState.Ready)?.plan?.id

    private fun stateFor(current: Input): Flow<WeeklyPlanUiState> {
        val outcome = generator.generate(current.document, current.monthId, current.today)
            ?: return flowOf(WeeklyPlanUiState.NoData)

        return when (outcome) {
            is WeeklyPlanOutcome.MonthEnded -> flowOf(WeeklyPlanUiState.MonthEnded(outcome.month))
            is WeeklyPlanOutcome.Generated -> checkStorage.observeChecked(outcome.plan.id).map { checked ->
                WeeklyPlanUiState.Ready(outcome.plan, checked, current.document.currency)
            }
        }
    }
}
