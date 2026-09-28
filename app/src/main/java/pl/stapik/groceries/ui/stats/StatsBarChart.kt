package pl.stapik.groceries.ui.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.stapik.groceries.ui.format.MoneyFormatter
import pl.stapik.groceries.ui.theme.RetroColorScheme

data class StatsChartPoint(
    val label: String,
    val planned: Double,
    val real: Double,
    val savings: Double,
)

internal val PlannedColor = Color(0xFF4D73BF)
internal val RealColor = Color(0xFFCC5933)
internal val SavingsColor = Color(0xFF4DA659)

@Composable
fun StatsBarChart(
    points: List<StatsChartPoint>,
    formatter: MoneyFormatter,
    scheme: RetroColorScheme,
    modifier: Modifier = Modifier,
) {
    val measurer = rememberTextMeasurer()
    val labelStyle = TextStyle(color = scheme.textMuted, fontSize = 9.sp, textAlign = TextAlign.Center)

    Canvas(modifier = modifier.fillMaxWidth().height(260.dp)) {
        if (points.isEmpty()) return@Canvas

        val left = 64.dp.toPx()
        val right = 8.dp.toPx()
        val top = 8.dp.toPx()
        val bottom = 34.dp.toPx()
        val plotWidth = size.width - left - right
        val plotHeight = size.height - top - bottom
        val plotBottom = top + plotHeight

        val maxValue = points
            .maxOf { maxOf(it.planned, it.real, it.savings) }
            .takeIf { it > 0.0 } ?: 1.0

        for (i in 0..2) {
            val fraction = i / 2f
            val y = plotBottom - plotHeight * fraction
            drawLine(
                color = scheme.borderDark.copy(alpha = 0.35f),
                start = Offset(left, y),
                end = Offset(size.width - right, y),
                strokeWidth = 1.dp.toPx(),
            )
            val label = measurer.measure(text = formatter.format(maxValue * fraction), style = labelStyle)
            drawText(
                textLayoutResult = label,
                topLeft = Offset(left - label.size.width - 4.dp.toPx(), y - label.size.height / 2f),
            )
        }

        val groupWidth = plotWidth / points.size
        val barGap = 2.dp.toPx()
        val barWidth = minOf(groupWidth / 3.6f, 18.dp.toPx())
        val groupContentWidth = barWidth * 3 + barGap * 2

        points.forEachIndexed { index, point ->
            val groupLeft = left + groupWidth * index
            var barX = groupLeft + (groupWidth - groupContentWidth) / 2f

            val bars = listOf(point.planned to PlannedColor, point.real to RealColor, point.savings to SavingsColor)
            for ((value, color) in bars) {
                val barHeight = (plotHeight * (value / maxValue)).toFloat().coerceAtLeast(0f)
                drawRect(
                    color = color,
                    topLeft = Offset(barX, plotBottom - barHeight),
                    size = Size(barWidth, barHeight),
                )
                barX += barWidth + barGap
            }

            val label = measurer.measure(text = point.label, style = labelStyle)
            drawText(
                textLayoutResult = label,
                topLeft = Offset(groupLeft + groupWidth / 2f - label.size.width / 2f, plotBottom + 4.dp.toPx()),
            )
        }
    }
}
