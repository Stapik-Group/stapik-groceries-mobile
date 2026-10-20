package pl.stapik.groceries.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.stapik.common.theme.ThemeId

class ThemeViewModel(private val storage: ThemePreferenceStorage) : ViewModel() {

    private val _theme = MutableStateFlow(ThemeId.CLASSIC)
    val theme: StateFlow<ThemeId> = _theme.asStateFlow()

    init {
        viewModelScope.launch { _theme.value = storage.load() }
    }

    fun select(theme: ThemeId) {
        _theme.value = theme
        viewModelScope.launch { storage.save(theme) }
    }
}
