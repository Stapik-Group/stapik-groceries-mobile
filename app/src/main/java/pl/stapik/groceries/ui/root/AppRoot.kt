package pl.stapik.groceries.ui.root

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import pl.stapik.common.cloud.CloudConfig
import pl.stapik.common.i18n.R as CommonR
import pl.stapik.common.presentation.ConnectController
import pl.stapik.common.ui.shell.AppDefinition
import pl.stapik.common.ui.shell.SettingsFlow
import pl.stapik.common.ui.theme.StapikTheme
import pl.stapik.groceries.R
import pl.stapik.groceries.data.config.ApiConfig
import pl.stapik.groceries.data.config.ApiConfigStorage
import pl.stapik.groceries.ui.groceries.GroceriesUiState
import pl.stapik.groceries.ui.groceries.GroceriesViewModel
import pl.stapik.groceries.ui.months.MonthsScreen
import pl.stapik.groceries.ui.stats.StatsScreen
import pl.stapik.groceries.ui.theme.ThemeViewModel
import pl.stapik.groceries.ui.theme.colorsFor
import pl.stapik.groceries.ui.weekly.WeeklyPlanScreen
import pl.stapik.groceries.ui.weekly.WeeklyPlanViewModel
import pl.stapik.groceries.util.AppInfo

private val appDefinition = AppDefinition(
    appId = "groceries",
    name = "Stapik Groceries",
    versionName = AppInfo.VERSION_NAME,
    author = "Sebastian Smoliński",
    websiteUrl = "https://github.com/Stapik-Group/stapik-groceries-mobile",
)

/**
 * @param newConnectController builds the controller of the connect form, prefilled with the saved
 *   connection; called each time the settings are opened.
 */
@Composable
fun AppRoot(
    groceriesViewModel: GroceriesViewModel,
    themeViewModel: ThemeViewModel,
    weeklyPlanViewModel: WeeklyPlanViewModel,
    configStorage: ApiConfigStorage,
    newConnectController: (CloudConfig?) -> ConnectController,
) {
    val uiState by groceriesViewModel.uiState.collectAsState()
    val isRefreshing by groceriesViewModel.isRefreshing.collectAsState()
    val currentTheme by themeViewModel.theme.collectAsState()
    val weeklyPlanState by weeklyPlanViewModel.uiState.collectAsState()
    val scheme = colorsFor(currentTheme)
    val scope = rememberCoroutineScope()

    var screen by rememberSaveable { mutableStateOf(AppScreen.MONTHS) }
    var menuExpanded by remember { mutableStateOf(false) }
    var savedConfig by remember { mutableStateOf<ApiConfig?>(null) }

    LaunchedEffect(Unit) {
        savedConfig = configStorage.load()
    }

    // The settings have their own back handling.
    BackHandler(enabled = screen != AppScreen.MONTHS && screen != AppScreen.SETTINGS) { screen = AppScreen.MONTHS }

    StapikTheme(currentTheme) {
        Scaffold(
            floatingActionButton = {
                if (screen != AppScreen.SETTINGS) {
                    Box {
                        FloatingActionButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings_content_description))
                        }

                        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.menu_statistics)) },
                                enabled = uiState is GroceriesUiState.Success,
                                onClick = { menuExpanded = false; screen = AppScreen.STATS },
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(CommonR.string.menu_settings)) },
                                onClick = { menuExpanded = false; screen = AppScreen.SETTINGS },
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                val success = uiState as? GroceriesUiState.Success

                when {
                    screen == AppScreen.STATS && success != null -> StatsScreen(
                        document = success.snapshot.document,
                        scheme = scheme,
                        onBack = { screen = AppScreen.MONTHS },
                        modifier = Modifier.fillMaxSize(),
                    )

                    screen == AppScreen.WEEKLY -> WeeklyPlanScreen(
                        state = weeklyPlanState,
                        scheme = scheme,
                        onBack = { screen = AppScreen.MONTHS },
                        onSetChecked = weeklyPlanViewModel::setChecked,
                        onClearChecks = weeklyPlanViewModel::clearChecks,
                        modifier = Modifier.fillMaxSize(),
                    )

                    screen == AppScreen.SETTINGS -> {
                        val connectController = remember {
                            newConnectController(savedConfig?.let { CloudConfig(it.serverUrl, it.apiKey, it.passphrase) })
                        }
                        SettingsFlow(
                            app = appDefinition,
                            themeId = currentTheme,
                            onThemeChange = themeViewModel::select,
                            connectController = connectController,
                            connected = savedConfig != null,
                            onConnected = {
                                scope.launch {
                                    savedConfig = configStorage.load()
                                    groceriesViewModel.refresh()
                                }
                            },
                            onDisconnected = {
                                savedConfig = null
                                groceriesViewModel.refresh()
                            },
                            onClose = { screen = AppScreen.MONTHS },
                            modifier = Modifier.fillMaxSize(),
                        )
                    }

                    else -> MonthsScreen(
                        state = uiState,
                        isRefreshing = isRefreshing,
                        onRefresh = { groceriesViewModel.refresh(isPullToRefresh = true) },
                        onRetry = { groceriesViewModel.refresh() },
                        onNavigateToConnect = { screen = AppScreen.SETTINGS },
                        onWeeklyPlan = { monthId ->
                            success?.let { weeklyPlanViewModel.open(it.snapshot.document, monthId) }
                            screen = AppScreen.WEEKLY
                        },
                        scheme = scheme,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}
