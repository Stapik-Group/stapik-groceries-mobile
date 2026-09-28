package pl.stapik.groceries.ui.months

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.stapik.groceries.R
import pl.stapik.groceries.domain.model.Category
import pl.stapik.groceries.domain.model.Product
import pl.stapik.groceries.ui.format.MoneyFormatter
import pl.stapik.groceries.ui.format.QuantityFormatter
import pl.stapik.groceries.ui.format.swatchColor
import pl.stapik.groceries.ui.theme.RetroColorScheme
import pl.stapik.groceries.ui.theme.retroPanel

@Composable
fun ProductRow(
    product: Product,
    category: Category?,
    moneyFormatter: MoneyFormatter,
    quantityFormatter: QuantityFormatter,
    scheme: RetroColorScheme,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().retroPanel(scheme).padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(category.swatchColor(fallback = scheme.textMuted)),
            )
            Column(modifier = Modifier.weight(1f).padding(start = 10.dp)) {
                Text(product.name, color = scheme.textDark, fontWeight = FontWeight.Bold)
                Text(
                    text = category?.name ?: stringResource(R.string.category_none),
                    color = scheme.textMuted,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        Text(
            text = stringResource(
                R.string.table_plan_line,
                quantityFormatter.format(product.plannedQuantity),
                product.unitName,
                moneyFormatter.format(product.plannedUnitPrice),
                moneyFormatter.format(product.plannedTotal),
            ),
            color = scheme.textDark,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = stringResource(
                R.string.table_purchased_line,
                quantityFormatter.format(product.purchasedQuantity),
                product.unitName,
                moneyFormatter.format(product.realAverageUnitPrice),
                moneyFormatter.format(product.realTotal),
            ),
            color = scheme.textDark,
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = stringResource(R.string.table_savings_line, moneyFormatter.format(product.savings)),
            color = if (product.savings > 0.0) scheme.accent else scheme.textMuted,
            fontWeight = if (product.savings > 0.0) FontWeight.Bold else FontWeight.Normal,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
