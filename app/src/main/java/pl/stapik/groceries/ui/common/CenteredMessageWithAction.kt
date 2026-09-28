package pl.stapik.groceries.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.stapik.groceries.ui.theme.RetroButton
import pl.stapik.groceries.ui.theme.RetroColorScheme

@Composable
fun CenteredMessageWithAction(
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    scheme: RetroColorScheme,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, color = scheme.textDark)
            Spacer(modifier = Modifier.height(12.dp))
            RetroButton(text = actionLabel, onClick = onAction, scheme = scheme)
        }
    }
}
