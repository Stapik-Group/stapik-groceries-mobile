package pl.stapik.groceries.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.stapik.groceries.R
import pl.stapik.groceries.domain.model.GroceriesDocument
import pl.stapik.groceries.domain.model.MonthlyList
import pl.stapik.groceries.ui.common.ScreenHeader
import pl.stapik.groceries.ui.format.MoneyFormatter
import pl.stapik.groceries.ui.format.shortLabel
import pl.stapik.groceries.ui.format.title
import pl.stapik.groceries.ui.months.SummaryRow
import pl.stapik.groceries.ui.theme.RetroColorScheme
import pl.stapik.groceries.ui.theme.retroPanel

private enum class StatsView { LIST, CHART }

private const val CHART_MONTHS = 12

@Composable
fun StatsScreen(
    document: GroceriesDocument,
    scheme: RetroColorScheme,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var view by rememberSaveable { mutableStateOf(StatsView.LIST) }
    val formatter = remember(document.currencyCode) { MoneyFormatter(document.currency) }

    Column(modifier = modifier.fillMaxSize().background(scheme.windowBackground)) {
        ScreenHeader(title = stringResource(R.string.stats_title), scheme = scheme, onBack = onBack)

        Row(modifier = Modifier.padding(horizontal = 8.dp)) {
            ViewTab(StatsView.LIST, view, R.string.stats_view_list, scheme) { view = it }
            ViewTab(StatsView.CHART, view, R.string.stats_view_chart, scheme) { view = it }
        }

        if (document.months.isEmpty()) {
            Text(
                text = stringResource(R.string.stats_no_data),
                color = scheme.textMuted,
                modifier = Modifier.padding(16.dp),
            )
        } else when (view) {
            StatsView.LIST -> StatsList(document.monthsNewestFirst, formatter, scheme)
            StatsView.CHART -> StatsChartSection(document.months.takeLast(CHART_MONTHS), formatter, scheme)
        }
    }
}

@Composable
private fun StatsList(months: List<MonthlyList>, formatter: MoneyFormatter, scheme: RetroColorScheme) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(months, key = { it.month.key }) { month ->
            Column(modifier = Modifier.fillMaxWidth().retroPanel(scheme).padding(12.dp)) {
                Text(
                    text = month.month.title(),
                    color = scheme.textDark,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
                SummaryRow(stringResource(R.string.summary_planned), formatter.format(month.plannedTotal), scheme)
                SummaryRow(stringResource(R.string.summary_real), formatter.format(month.realTotal), scheme)
                SummaryRow(stringResource(R.string.summary_savings), formatter.format(month.savingsTotal), scheme)
                SummaryRow(stringResource(R.string.summary_off_budget), formatter.format(month.offBudgetAmount), scheme)
                SummaryRow(stringResource(R.string.stats_projected), formatter.format(month.projectedMonthlyCost), scheme)
            }
        }
    }
}

@Composable
private fun StatsChartSection(months: List<MonthlyList>, formatter: MoneyFormatter, scheme: RetroColorScheme) {
    val points = remember(months) {
        months.map { StatsChartPoint(it.month.shortLabel(), it.plannedTotal, it.realTotal, it.savingsTotal) }
    }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Column(modifier = Modifier.fillMaxWidth().retroPanel(scheme).padding(8.dp)) {
            StatsBarChart(points = points, formatter = formatter, scheme = scheme)
        }
        Row(
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LegendItem(PlannedColor, stringResource(R.string.summary_planned), scheme)
            LegendItem(RealColor, stringResource(R.string.summary_real), scheme)
            LegendItem(SavingsColor, stringResource(R.string.summary_savings), scheme)
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String, scheme: RetroColorScheme) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(modifier = Modifier.size(10.dp).background(color))
        Text(label, color = scheme.textDark)
    }
}

@Composable
private fun ViewTab(
    value: StatsView,
    selected: StatsView,
    labelRes: Int,
    scheme: RetroColorScheme,
    onSelect: (StatsView) -> Unit,
) {
    TextButton(onClick = { onSelect(value) }) {
        Text(
            stringResource(labelRes),
            color = if (value == selected) scheme.accent else scheme.textMuted,
            fontWeight = if (value == selected) FontWeight.Bold else FontWeight.Normal,
        )
    }
}
