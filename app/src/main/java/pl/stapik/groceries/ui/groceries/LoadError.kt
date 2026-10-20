package pl.stapik.groceries.ui.groceries

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.serialization.SerializationException
import pl.stapik.common.i18n.Messages
import pl.stapik.common.sync.CloudError
import pl.stapik.common.sync.CloudException
import pl.stapik.common.sync.UnreadableKind
import pl.stapik.groceries.R
import pl.stapik.groceries.data.repository.GroceriesNotFoundException

sealed interface LoadError {
    data object NoNetwork : LoadError
    data object Unauthorized : LoadError
    data object NotFound : LoadError
    data object InvalidData : LoadError

    /** The cloud data cannot be read (encrypted, wrong passphrase, newer format); [messageKey] is a library message key. */
    data class Unreadable(val messageKey: String) : LoadError

    data class Unknown(val message: String) : LoadError
}

fun Throwable.toLoadError(): LoadError = when (this) {
    is GroceriesNotFoundException -> LoadError.NotFound
    is CloudException -> when (val cloud = error) {
        CloudError.Unauthorized, CloudError.Forbidden -> LoadError.Unauthorized
        is CloudError.Network -> LoadError.NoNetwork
        is CloudError.Unreadable -> LoadError.Unreadable(unreadableKey(cloud.kind))
        is CloudError.ServerError -> LoadError.Unknown("HTTP ${cloud.httpCode}")
        is CloudError.RateLimited -> LoadError.Unknown("HTTP 429")
        CloudError.Conflict -> LoadError.Unknown("Conflict")
    }
    is SerializationException, is IllegalArgumentException -> LoadError.InvalidData
    else -> LoadError.Unknown(message ?: this::class.simpleName ?: "Unknown error")
}

private fun unreadableKey(kind: UnreadableKind): String = when (kind) {
    UnreadableKind.PASSPHRASE_REQUIRED -> "sync.unreadable.passphrase_required"
    UnreadableKind.DECRYPTION_FAILED -> "sync.unreadable.wrong_passphrase"
    UnreadableKind.UNSUPPORTED_VERSION -> "sync.unreadable.newer_version"
    UnreadableKind.OTHER -> "sync.unreadable.other"
}

@Composable
fun LoadError.toDisplayMessage(): String = when (this) {
    LoadError.NoNetwork -> stringResource(R.string.error_no_network)
    LoadError.Unauthorized -> stringResource(R.string.error_unauthorized)
    LoadError.NotFound -> stringResource(R.string.error_not_found)
    LoadError.InvalidData -> stringResource(R.string.error_invalid_data)
    is LoadError.Unreadable -> Messages.text(LocalContext.current, messageKey)
    is LoadError.Unknown -> stringResource(R.string.error_unknown, message)
}
