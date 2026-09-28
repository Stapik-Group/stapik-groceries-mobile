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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import pl.stapik.groceries.R
import pl.stapik.groceries.ui.about.AboutScreen
import pl.stapik.groceries.ui.connect.ConnectScreen
import pl.stapik.groceries.ui.connect.ConnectViewModel
import pl.stapik.groceries.ui.groceries.GroceriesUiState
import pl.stapik.groceries.ui.groceries.GroceriesViewModel
import pl.stapik.groceries.ui.months.MonthsScreen
import pl.stapik.groceries.ui.stats.StatsScreen
import pl.stapik.groceries.ui.theme.ThemeScreen
import pl.stapik.groceries.ui.theme.ThemeViewModel
import pl.stapik.groceries.ui.theme.colorsFor
import pl.stapik.groceries.ui.theme.toMaterialColorScheme
import pl.stapik.groceries.ui.weekly.WeeklyPlanScreen
import pl.stapik.groceries.ui.weekly.WeeklyPlanViewModel

@Composable
fun AppRoot(
    groceriesViewModel: GroceriesViewModel,
    connectViewModel: ConnectViewModel,
    themeViewModel: ThemeViewModel,
    weeklyPlanViewModel: WeeklyPlanViewModel,
) {
    val uiState by groceriesViewModel.uiState.collectAsState()
    val isRefreshing by groceriesViewModel.isRefreshing.collectAsState()
    val connectState by connectViewModel.uiState.collectAsState()
    val currentTheme by themeViewModel.theme.collectAsState()
    val weeklyPlanState by weeklyPlanViewModel.uiState.collectAsState()
    val scheme = colorsFor(currentTheme)

    var screen by rememberSaveable { mutableStateOf(AppScreen.MONTHS) }
    var menuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is GroceriesUiState.NotConnected) screen = AppScreen.CONNECT
    }

    LaunchedEffect(Unit) {
        connectViewModel.connected.collect {
            groceriesViewModel.refresh()
            screen = AppScreen.MONTHS
        }
    }

    val canGoBackToMonths = screen != AppScreen.MONTHS &&
        !(screen == AppScreen.CONNECT && uiState is GroceriesUiState.NotConnected)
    BackHandler(enabled = canGoBackToMonths) { screen = AppScreen.MONTHS }

    MaterialTheme(colorScheme = scheme.toMaterialColorScheme()) {
        Scaffold(
            floatingActionButton = {
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
                            text = { Text(stringResource(R.string.menu_connect)) },
                            onClick = { menuExpanded = false; screen = AppScreen.CONNECT },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.settings_theme_label)) },
                            onClick = { menuExpanded = false; screen = AppScreen.THEME },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.menu_about)) },
                            onClick = { menuExpanded = false; screen = AppScreen.ABOUT },
                        )
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

                    screen == AppScreen.CONNECT -> ConnectScreen(
                        state = connectState,
                        scheme = scheme,
                        onBack = { screen = AppScreen.MONTHS },
                        onSave = connectViewModel::connect,
                        modifier = Modifier.fillMaxSize(),
                    )

                    screen == AppScreen.THEME -> ThemeScreen(
                        currentTheme = currentTheme,
                        scheme = scheme,
                        onSelect = themeViewModel::select,
                        onBack = { screen = AppScreen.MONTHS },
                        modifier = Modifier.fillMaxSize(),
                    )

                    screen == AppScreen.ABOUT -> AboutScreen(
                        scheme = scheme,
                        onBack = { screen = AppScreen.MONTHS },
                        modifier = Modifier.fillMaxSize(),
                    )

                    else -> MonthsScreen(
                        state = uiState,
                        isRefreshing = isRefreshing,
                        onRefresh = { groceriesViewModel.refresh(isPullToRefresh = true) },
                        onRetry = { groceriesViewModel.refresh() },
                        onNavigateToConnect = { screen = AppScreen.CONNECT },
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
