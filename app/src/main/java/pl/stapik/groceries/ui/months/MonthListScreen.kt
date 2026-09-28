package pl.stapik.groceries.ui.months

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.stapik.groceries.R
import pl.stapik.groceries.domain.model.MonthlyList
import pl.stapik.groceries.ui.format.MoneyFormatter
import pl.stapik.groceries.ui.format.formatUpdatedAt
import pl.stapik.groceries.ui.format.title
import pl.stapik.groceries.ui.groceries.GroceriesUiState
import pl.stapik.groceries.ui.theme.RetroColorScheme
import pl.stapik.groceries.ui.theme.retroPanel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthListScreen(
    success: GroceriesUiState.Success,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onMonthClick: (MonthlyList) -> Unit,
    scheme: RetroColorScheme,
    modifier: Modifier = Modifier,
) {
    val document = success.snapshot.document
    val formatter = remember(document.currencyCode) { MoneyFormatter(document.currency) }
    var yearFilter by rememberSaveable { mutableStateOf<Int?>(null) }

    val years = document.months.map { it.month.year }.distinct().sortedDescending()
    val months = document.monthsNewestFirst.filter { yearFilter == null || it.month.year == yearFilter }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (success.isStale) {
                item { StaleDataBanner(updatedAt = success.snapshot.updatedAt, scheme = scheme) }
            }

            item { YearDropdown(years, yearFilter, { yearFilter = it }, scheme) }

            if (months.isEmpty()) {
                item { Text(text = stringResource(R.string.months_no_months), color = scheme.textMuted) }
            }

            items(months, key = { it.month.key }) { month ->
                MonthCard(month = month, formatter = formatter, scheme = scheme, onClick = { onMonthClick(month) })
            }
        }
    }
}

@Composable
private fun MonthCard(
    month: MonthlyList,
    formatter: MoneyFormatter,
    scheme: RetroColorScheme,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .retroPanel(scheme)
            .clickable(onClick = onClick)
            .padding(12.dp),
    ) {
        Text(text = month.month.title(), color = scheme.textDark, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = stringResource(R.string.months_planned_short, formatter.format(month.plannedTotal)),
                color = scheme.textMuted,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = stringResource(R.string.months_real_short, formatter.format(month.realTotal)),
                color = scheme.textDark,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun StaleDataBanner(updatedAt: String, scheme: RetroColorScheme) {
    Text(
        text = stringResource(R.string.months_stale_banner, formatUpdatedAt(updatedAt)),
        color = scheme.textDark,
        modifier = Modifier
            .fillMaxWidth()
            .background(scheme.borderLight)
            .padding(8.dp),
    )
}

@Composable
private fun YearDropdown(
    years: List<Int>,
    selectedYear: Int?,
    onYearSelected: (Int?) -> Unit,
    scheme: RetroColorScheme,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { expanded = true }) {
            Text(selectedYear?.toString() ?: stringResource(R.string.months_filter_all_years), color = scheme.accent)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.months_filter_all_years)) },
                onClick = { onYearSelected(null); expanded = false },
            )
            years.forEach { year ->
                DropdownMenuItem(text = { Text(year.toString()) }, onClick = { onYearSelected(year); expanded = false })
            }
        }
    }
}
