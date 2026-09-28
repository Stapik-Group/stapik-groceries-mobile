package pl.stapik.groceries.ui.groceries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.stapik.groceries.data.config.ApiConfigStorage
import pl.stapik.groceries.data.config.ApiSchemaGuard
import pl.stapik.groceries.data.repository.GroceriesFetchOutcome
import pl.stapik.groceries.data.repository.GroceriesRepository

class GroceriesViewModel(
    private val repository: GroceriesRepository,
    private val configStorage: ApiConfigStorage,
    private val schemaGuard: ApiSchemaGuard,
) : ViewModel() {

    private val _uiState = MutableStateFlow<GroceriesUiState>(GroceriesUiState.Loading)
    val uiState: StateFlow<GroceriesUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        refresh()
    }

    fun refresh(isPullToRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isPullToRefresh) {
                _isRefreshing.value = true
            } else {
                _uiState.value = GroceriesUiState.Loading
            }

            try {
                schemaGuard.ensureCurrentSchema()
                val config = configStorage.load()
                if (config == null) {
                    _uiState.value = GroceriesUiState.NotConnected
                    return@launch
                }
                _uiState.value = repository.fetch(config).toUiState()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = GroceriesUiState.Error(e.toLoadError())
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}

private fun GroceriesFetchOutcome.toUiState(): GroceriesUiState = when (this) {
    is GroceriesFetchOutcome.Fresh -> GroceriesUiState.Success(snapshot, isStale = false)
    is GroceriesFetchOutcome.Cached -> GroceriesUiState.Success(snapshot, isStale = true)
    is GroceriesFetchOutcome.Failure -> GroceriesUiState.Error(cause.toLoadError())
}
