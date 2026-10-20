package pl.stapik.groceries

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import pl.stapik.groceries.ui.groceries.GroceriesViewModel
import pl.stapik.groceries.ui.root.AppRoot
import pl.stapik.groceries.ui.theme.ThemeViewModel
import pl.stapik.groceries.ui.weekly.WeeklyPlanViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as GroceriesApp).container
        val provider = ViewModelProvider(this, container.viewModelFactory)
        val groceriesViewModel = provider[GroceriesViewModel::class.java]
        val themeViewModel = provider[ThemeViewModel::class.java]
        val weeklyPlanViewModel = provider[WeeklyPlanViewModel::class.java]

        setContent {
            AppRoot(
                groceriesViewModel = groceriesViewModel,
                themeViewModel = themeViewModel,
                weeklyPlanViewModel = weeklyPlanViewModel,
                configStorage = container.configStorage,
                newConnectController = container::newConnectController,
            )
        }
    }
}
