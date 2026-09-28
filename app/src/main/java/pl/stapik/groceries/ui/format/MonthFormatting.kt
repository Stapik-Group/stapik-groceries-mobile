package pl.stapik.groceries.ui.format

import pl.stapik.groceries.domain.model.MonthId
import java.time.Instant
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

fun monthName(month: Int, style: TextStyle = TextStyle.FULL_STANDALONE): String =
    Month.of(month).getDisplayName(style, Locale.getDefault()).replaceFirstChar { it.uppercase() }

fun MonthId.title(): String = "${monthName(month)} $year"

fun MonthId.shortLabel(): String =
    "${monthName(month, TextStyle.SHORT)}\n${(year % 100).toString().padStart(2, '0')}"

fun formatUpdatedAt(iso: String): String = try {
    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        .withLocale(Locale.getDefault())
        .withZone(ZoneId.systemDefault())
        .format(Instant.parse(iso))
} catch (_: Exception) {
    iso
}
