package pl.stapik.groceries.ui.months

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.stapik.groceries.R
import pl.stapik.groceries.domain.model.GroceriesDocument
import pl.stapik.groceries.domain.model.MonthlyList
import pl.stapik.groceries.ui.common.ScreenHeader
import pl.stapik.groceries.ui.theme.RetroButton
import pl.stapik.groceries.ui.format.MoneyFormatter
import pl.stapik.groceries.ui.format.QuantityFormatter
import pl.stapik.groceries.ui.format.title
import pl.stapik.groceries.ui.theme.RetroColorScheme

@Composable
fun MonthDetailScreen(
    month: MonthlyList,
    document: GroceriesDocument,
    scheme: RetroColorScheme,
    onBack: () -> Unit,
    onWeeklyPlan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val moneyFormatter = remember(document.currencyCode) { MoneyFormatter(document.currency) }
    val quantityFormatter = remember { QuantityFormatter() }

    Column(modifier = modifier.fillMaxSize().background(scheme.windowBackground)) {
        ScreenHeader(title = month.month.title(), scheme = scheme, onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item { MonthSummaryPanel(month = month, formatter = moneyFormatter, scheme = scheme) }

            item {
                RetroButton(
                    text = stringResource(R.string.menu_weekly_plan),
                    onClick = onWeeklyPlan,
                    scheme = scheme,
                )
            }

            if (month.products.isEmpty()) {
                item { Text(text = stringResource(R.string.detail_no_products), color = scheme.textMuted) }
            }

            items(month.products, key = { it.id }) { product ->
                ProductRow(
                    product = product,
                    category = document.categoriesById[product.categoryId],
                    moneyFormatter = moneyFormatter,
                    quantityFormatter = quantityFormatter,
                    scheme = scheme,
                )
            }
        }
    }
}
