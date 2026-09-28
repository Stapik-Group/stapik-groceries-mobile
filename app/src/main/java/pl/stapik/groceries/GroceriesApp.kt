package pl.stapik.groceries

import android.app.Application
import pl.stapik.groceries.di.AppContainer

class GroceriesApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
