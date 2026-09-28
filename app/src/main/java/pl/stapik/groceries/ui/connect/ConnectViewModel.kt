package pl.stapik.groceries.ui.connect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.stapik.groceries.data.config.ApiConfig
import pl.stapik.groceries.data.config.ApiConfigStorage
import pl.stapik.groceries.data.repository.GroceriesFetchOutcome
import pl.stapik.groceries.data.repository.GroceriesRepository
import pl.stapik.groceries.ui.groceries.LoadError
import pl.stapik.groceries.ui.groceries.toLoadError

sealed interface ConnectStatus {
    data object Idle : ConnectStatus
    data object Testing : ConnectStatus
    data class Failed(val error: LoadError) : ConnectStatus
}

data class ConnectUiState(
    val initialConfig: ApiConfig? = null,
    val isLoaded: Boolean = false,
    val status: ConnectStatus = ConnectStatus.Idle,
)

class ConnectViewModel(
    private val repository: GroceriesRepository,
    private val configStorage: ApiConfigStorage,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConnectUiState())
    val uiState: StateFlow<ConnectUiState> = _uiState.asStateFlow()

    private val _connected = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    val connected: SharedFlow<Unit> = _connected.asSharedFlow()

    init {
        viewModelScope.launch {
            val config = try {
                configStorage.load()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                null
            }
            _uiState.update { it.copy(initialConfig = config, isLoaded = true) }
        }
    }

    fun connect(config: ApiConfig) {
        if (_uiState.value.status is ConnectStatus.Testing) return

        viewModelScope.launch {
            _uiState.update { it.copy(status = ConnectStatus.Testing) }

            when (val outcome = repository.fetch(config)) {
                is GroceriesFetchOutcome.Fresh -> {
                    configStorage.save(config)
                    _uiState.update { it.copy(initialConfig = config, status = ConnectStatus.Idle) }
                    _connected.tryEmit(Unit)
                }
                is GroceriesFetchOutcome.Cached ->
                    _uiState.update { it.copy(status = ConnectStatus.Failed(LoadError.CannotVerify)) }
                is GroceriesFetchOutcome.Failure ->
                    _uiState.update { it.copy(status = ConnectStatus.Failed(outcome.cause.toLoadError())) }
            }
        }
    }
}
