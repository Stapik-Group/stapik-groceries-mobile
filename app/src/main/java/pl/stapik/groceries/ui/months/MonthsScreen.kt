package pl.stapik.groceries.ui.months

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import pl.stapik.groceries.R
import pl.stapik.groceries.domain.model.MonthId
import pl.stapik.groceries.domain.model.MonthlyList
import pl.stapik.groceries.ui.common.CenteredMessageWithAction
import pl.stapik.groceries.ui.groceries.GroceriesUiState
import pl.stapik.groceries.ui.groceries.toDisplayMessage
import pl.stapik.groceries.ui.theme.RetroColorScheme

@Composable
fun MonthsScreen(
    state: GroceriesUiState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onNavigateToConnect: () -> Unit,
    onWeeklyPlan: (MonthId) -> Unit,
    scheme: RetroColorScheme,
    modifier: Modifier = Modifier,
) {
    var selectedMonthKey by rememberSaveable { mutableStateOf<String?>(null) }

    val selectedMonth: MonthlyList? = remember(selectedMonthKey, state) {
        val key = selectedMonthKey ?: return@remember null
        (state as? GroceriesUiState.Success)?.snapshot?.document?.months?.firstOrNull { it.month.key == key }
    }

    BackHandler(enabled = selectedMonth != null) { selectedMonthKey = null }

    if (selectedMonth != null && state is GroceriesUiState.Success) {
        MonthDetailScreen(
            month = selectedMonth,
            document = state.snapshot.document,
            scheme = scheme,
            onBack = { selectedMonthKey = null },
            onWeeklyPlan = { onWeeklyPlan(selectedMonth.month) },
            modifier = modifier,
        )
        return
    }

    when (state) {
        GroceriesUiState.Loading -> Box(modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        GroceriesUiState.NotConnected -> CenteredMessageWithAction(
            message = stringResource(R.string.months_not_connected),
            actionLabel = stringResource(R.string.menu_connect),
            onAction = onNavigateToConnect,
            scheme = scheme,
            modifier = modifier,
        )

        is GroceriesUiState.Error -> CenteredMessageWithAction(
            message = state.error.toDisplayMessage(),
            actionLabel = stringResource(R.string.retry),
            onAction = onRetry,
            scheme = scheme,
            modifier = modifier,
        )

        is GroceriesUiState.Success -> MonthListScreen(
            success = state,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            onMonthClick = { selectedMonthKey = it.month.key },
            scheme = scheme,
            modifier = modifier,
        )
    }
}
