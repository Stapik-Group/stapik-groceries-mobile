package pl.stapik.groceries.ui.groceries

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import kotlinx.serialization.SerializationException
import pl.stapik.groceries.R
import retrofit2.HttpException
import java.io.IOException

sealed interface LoadError {
    data object NoNetwork : LoadError
    data object Unauthorized : LoadError
    data object NotFound : LoadError
    data object CannotVerify : LoadError
    data object InvalidData : LoadError
    data class Unknown(val message: String) : LoadError
}

fun Throwable.toLoadError(): LoadError = when (this) {
    is IOException -> LoadError.NoNetwork
    is HttpException -> when (code()) {
        401, 403 -> LoadError.Unauthorized
        404 -> LoadError.NotFound
        else -> LoadError.Unknown(message())
    }
    is SerializationException, is IllegalArgumentException -> LoadError.InvalidData
    else -> LoadError.Unknown(message ?: this::class.simpleName ?: "Unknown error")
}

@Composable
fun LoadError.toDisplayMessage(): String = when (this) {
    LoadError.NoNetwork -> stringResource(R.string.error_no_network)
    LoadError.Unauthorized -> stringResource(R.string.error_unauthorized)
    LoadError.NotFound -> stringResource(R.string.error_not_found)
    LoadError.CannotVerify -> stringResource(R.string.error_cannot_verify)
    LoadError.InvalidData -> stringResource(R.string.error_invalid_data)
    is LoadError.Unknown -> stringResource(R.string.error_unknown, message)
}
