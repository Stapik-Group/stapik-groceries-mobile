package pl.stapik.groceries.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import pl.stapik.groceries.data.cache.DataStoreGroceriesCacheStorage
import pl.stapik.groceries.data.config.ApiConfigStorage
import pl.stapik.groceries.data.config.ConnectionApiConfigStorage
import pl.stapik.groceries.data.local.DataStoreWeeklyPlanCheckStorage
import pl.stapik.groceries.data.local.WeeklyPlanCheckStorage
import pl.stapik.groceries.data.repository.GroceriesRepository
import pl.stapik.groceries.data.serialization.GroceriesDocumentParser
import pl.stapik.groceries.domain.planning.WeeklyPlanGenerator
import pl.stapik.groceries.ui.connect.ConnectViewModel
import pl.stapik.groceries.ui.groceries.GroceriesViewModel
import pl.stapik.groceries.ui.theme.DataStoreThemePreferenceStorage
import pl.stapik.groceries.ui.theme.ThemePreferenceStorage
import pl.stapik.groceries.ui.theme.ThemeViewModel
import pl.stapik.groceries.ui.weekly.WeeklyPlanViewModel

class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    private val configStorage: ApiConfigStorage = ConnectionApiConfigStorage(appContext)
    private val themeStorage: ThemePreferenceStorage = DataStoreThemePreferenceStorage(appContext)
    private val weeklyPlanChecks: WeeklyPlanCheckStorage = DataStoreWeeklyPlanCheckStorage(appContext)
    private val weeklyPlanGenerator = WeeklyPlanGenerator()
    private val repository = GroceriesRepository(
        cache = DataStoreGroceriesCacheStorage(appContext),
        parser = GroceriesDocumentParser(),
    )

    val viewModelFactory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T = when {
            modelClass.isAssignableFrom(GroceriesViewModel::class.java) ->
                GroceriesViewModel(repository, configStorage)
            modelClass.isAssignableFrom(ConnectViewModel::class.java) ->
                ConnectViewModel(repository, configStorage)
            modelClass.isAssignableFrom(ThemeViewModel::class.java) ->
                ThemeViewModel(themeStorage)
            modelClass.isAssignableFrom(WeeklyPlanViewModel::class.java) ->
                WeeklyPlanViewModel(weeklyPlanGenerator, weeklyPlanChecks)
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        } as T
    }
}
