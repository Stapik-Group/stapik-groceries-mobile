package pl.stapik.groceries.ui.format

import androidx.compose.ui.graphics.Color
import pl.stapik.groceries.domain.model.Category
import androidx.core.graphics.toColorInt

fun Category?.swatchColor(fallback: Color): Color {
    val hex = this?.colorHex ?: return fallback
    return try {
        Color(hex.toColorInt())
    } catch (_: IllegalArgumentException) {
        fallback
    }
}
