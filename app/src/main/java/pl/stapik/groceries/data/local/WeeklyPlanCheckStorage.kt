package pl.stapik.groceries.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

interface WeeklyPlanCheckStorage {
    fun observeChecked(planId: String): Flow<Set<String>>
    suspend fun setChecked(planId: String, productId: String, checked: Boolean)
    suspend fun clear(planId: String)
}

private val Context.weeklyPlanChecksDataStore by preferencesDataStore(name = "weekly_plan_checks")

class DataStoreWeeklyPlanCheckStorage(private val context: Context) : WeeklyPlanCheckStorage {

    private fun keyFor(planId: String) = stringSetPreferencesKey("checked_$planId")

    override fun observeChecked(planId: String): Flow<Set<String>> =
        context.weeklyPlanChecksDataStore.data
            .map { prefs -> prefs[keyFor(planId)] ?: emptySet() }
            .distinctUntilChanged()

    override suspend fun setChecked(planId: String, productId: String, checked: Boolean) {
        val key = keyFor(planId)
        context.weeklyPlanChecksDataStore.edit { prefs ->
            val current = prefs[key] ?: emptySet()
            prefs[key] = if (checked) current + productId else current - productId
        }
    }

    override suspend fun clear(planId: String) {
        context.weeklyPlanChecksDataStore.edit { prefs -> prefs.remove(keyFor(planId)) }
    }
}
