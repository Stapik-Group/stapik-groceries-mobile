package pl.stapik.groceries.ui.weekly

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import pl.stapik.groceries.R
import pl.stapik.groceries.domain.model.WeeklyPlanItem
import pl.stapik.groceries.ui.common.ScreenHeader
import pl.stapik.groceries.ui.format.MoneyFormatter
import pl.stapik.groceries.ui.format.QuantityFormatter
import pl.stapik.groceries.ui.format.title
import pl.stapik.groceries.ui.theme.RetroColorScheme

@Composable
fun WeeklyPlanScreen(
    state: WeeklyPlanUiState,
    scheme: RetroColorScheme,
    onBack: () -> Unit,
    onSetChecked: (productId: String, checked: Boolean) -> Unit,
    onClearChecks: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().background(scheme.windowBackground)) {
        ScreenHeader(title = stringResource(R.string.weekly_plan_title), scheme = scheme, onBack = onBack)

        when (state) {
            WeeklyPlanUiState.NoData -> Text(
                text = stringResource(R.string.weekly_no_month),
                color = scheme.textMuted,
                modifier = Modifier.padding(16.dp),
            )

            is WeeklyPlanUiState.MonthEnded -> Text(
                text = stringResource(R.string.weekly_month_ended, state.month.title()),
                color = scheme.textMuted,
                modifier = Modifier.padding(16.dp),
            )

            is WeeklyPlanUiState.Ready -> WeeklyPlanContent(state, scheme, onSetChecked, onClearChecks)
        }
    }
}

@Composable
private fun WeeklyPlanContent(
    state: WeeklyPlanUiState.Ready,
    scheme: RetroColorScheme,
    onSetChecked: (String, Boolean) -> Unit,
    onClearChecks: () -> Unit,
) {
    val plan = state.plan
    val moneyFormatter = remember(state.currency) { MoneyFormatter(state.currency) }
    val quantityFormatter = remember { QuantityFormatter() }
    val groups = remember(plan) { plan.items.groupBy { it.categoryName } }
    val checkedCount = plan.items.count { it.productId in state.checkedProductIds }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item {
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    text = "${plan.month.title()} · " +
                        stringResource(R.string.weekly_week_format, plan.currentWeek, plan.totalWeeks),
                    color = scheme.textDark,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.weekly_local_hint),
                    color = scheme.textMuted,
                    style = MaterialTheme.typography.bodySmall,
                )
                if (plan.items.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.weekly_progress, checkedCount, plan.items.size),
                            color = scheme.textDark,
                            modifier = Modifier.weight(1f),
                        )
                        if (checkedCount > 0) {
                            TextButton(onClick = onClearChecks) {
                                Text(stringResource(R.string.weekly_uncheck_all), color = scheme.accent)
                            }
                        }
                    }
                }
            }
        }

        if (plan.items.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.weekly_empty_title),
                    color = scheme.textDark,
                    fontWeight = FontWeight.Bold,
                )
                Text(text = stringResource(R.string.weekly_empty_body), color = scheme.textMuted)
            }
        }

        groups.forEach { (categoryName, itemsInCategory) ->
            item(key = "header-${categoryName.orEmpty()}") {
                Text(
                    text = categoryName ?: stringResource(R.string.weekly_no_category),
                    color = scheme.textDark,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
            items(itemsInCategory, key = { it.productId }) { item ->
                WeeklyPlanRow(
                    item = item,
                    checked = item.productId in state.checkedProductIds,
                    moneyFormatter = moneyFormatter,
                    quantityFormatter = quantityFormatter,
                    scheme = scheme,
                    onCheckedChange = { onSetChecked(item.productId, it) },
                )
            }
        }
    }
}

@Composable
private fun WeeklyPlanRow(
    item: WeeklyPlanItem,
    checked: Boolean,
    moneyFormatter: MoneyFormatter,
    quantityFormatter: QuantityFormatter,
    scheme: RetroColorScheme,
    onCheckedChange: (Boolean) -> Unit,
) {
    val quantity = if (item.isWholeUnits) item.quantity.toLong().toString() else quantityFormatter.format(item.quantity)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = null)
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = item.productName,
                color = if (checked) scheme.textMuted else scheme.textDark,
                textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None,
            )
            Text(
                text = stringResource(
                    R.string.weekly_item_details,
                    quantity,
                    item.unitName,
                    moneyFormatter.format(item.plannedUnitPrice),
                ),
                color = scheme.textMuted,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
