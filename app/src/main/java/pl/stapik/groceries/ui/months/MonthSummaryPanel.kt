package pl.stapik.groceries.ui.months

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.stapik.groceries.R
import pl.stapik.groceries.domain.model.MonthlyList
import pl.stapik.groceries.ui.format.MoneyFormatter
import pl.stapik.groceries.ui.theme.RetroColorScheme
import pl.stapik.groceries.ui.theme.retroPanel

@Composable
fun MonthSummaryPanel(
    month: MonthlyList,
    formatter: MoneyFormatter,
    scheme: RetroColorScheme,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().retroPanel(scheme).padding(16.dp)) {
        SummaryRow(stringResource(R.string.summary_planned), formatter.format(month.plannedTotal), scheme)
        SummaryRow(stringResource(R.string.summary_real), formatter.format(month.realTotal), scheme)
        SummaryRow(stringResource(R.string.summary_savings), formatter.format(month.savingsTotal), scheme)
        SummaryRow(stringResource(R.string.summary_off_budget), formatter.format(month.offBudgetAmount), scheme)
        Spacer(modifier = Modifier.height(8.dp))
        SummaryRow(
            label = stringResource(R.string.summary_projected),
            value = formatter.format(month.projectedMonthlyCost),
            scheme = scheme,
            emphasize = true,
        )
    }
}

@Composable
fun SummaryRow(label: String, value: String, scheme: RetroColorScheme, emphasize: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = scheme.textMuted, style = MaterialTheme.typography.bodyMedium)
        Text(
            value,
            color = scheme.textDark,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Normal,
            style = if (emphasize) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
        )
    }
}
