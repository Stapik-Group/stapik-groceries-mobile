package pl.stapik.groceries.ui.groceries

import pl.stapik.groceries.data.repository.GroceriesSnapshot

sealed interface GroceriesUiState {
    data object Loading : GroceriesUiState
    data object NotConnected : GroceriesUiState
    data class Success(val snapshot: GroceriesSnapshot, val isStale: Boolean) : GroceriesUiState
    data class Error(val error: LoadError) : GroceriesUiState
}
