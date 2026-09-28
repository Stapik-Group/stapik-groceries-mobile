package pl.stapik.groceries

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import pl.stapik.groceries.ui.connect.ConnectViewModel
import pl.stapik.groceries.ui.groceries.GroceriesViewModel
import pl.stapik.groceries.ui.root.AppRoot
import pl.stapik.groceries.ui.theme.ThemeViewModel
import pl.stapik.groceries.ui.weekly.WeeklyPlanViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val factory = (application as GroceriesApp).container.viewModelFactory
        val provider = ViewModelProvider(this, factory)
        val groceriesViewModel = provider[GroceriesViewModel::class.java]
        val connectViewModel = provider[ConnectViewModel::class.java]
        val themeViewModel = provider[ThemeViewModel::class.java]
        val weeklyPlanViewModel = provider[WeeklyPlanViewModel::class.java]

        setContent {
            AppRoot(
                groceriesViewModel = groceriesViewModel,
                connectViewModel = connectViewModel,
                themeViewModel = themeViewModel,
                weeklyPlanViewModel = weeklyPlanViewModel,
            )
        }
    }
}
