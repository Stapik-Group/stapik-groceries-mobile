package pl.stapik.groceries.data.cache

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

data class RawDocument(val content: String, val updatedAt: String)

interface GroceriesCacheStorage {
    suspend fun load(): RawDocument?
    suspend fun save(document: RawDocument)
}

private val Context.groceriesCacheDataStore by preferencesDataStore(name = "groceries_cache")

class DataStoreGroceriesCacheStorage(private val context: Context) : GroceriesCacheStorage {

    private object Keys {
        val CONTENT = stringPreferencesKey("content_json")
        val UPDATED_AT = stringPreferencesKey("updated_at")
    }

    override suspend fun load(): RawDocument? {
        val prefs = context.groceriesCacheDataStore.data.first()
        val content = prefs[Keys.CONTENT] ?: return null
        val updatedAt = prefs[Keys.UPDATED_AT] ?: return null
        return RawDocument(content, updatedAt)
    }

    override suspend fun save(document: RawDocument) {
        context.groceriesCacheDataStore.edit { prefs ->
            prefs[Keys.CONTENT] = document.content
            prefs[Keys.UPDATED_AT] = document.updatedAt
        }
    }
}
