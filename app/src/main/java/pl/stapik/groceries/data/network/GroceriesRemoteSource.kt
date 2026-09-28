package pl.stapik.groceries.data.network

import pl.stapik.groceries.data.config.ApiConfig

data class RawDocument(val content: String, val updatedAt: String)

interface GroceriesRemoteSource {
    suspend fun fetch(config: ApiConfig): RawDocument
}

class RetrofitGroceriesRemoteSource : GroceriesRemoteSource {
    override suspend fun fetch(config: ApiConfig): RawDocument {
        val response = NetworkModule.createApi(config.serverUrl)
            .getDocument(slotKey = ApiConfig.SLOT_KEY, apiKey = config.apiKey)
        return RawDocument(content = response.content, updatedAt = response.updatedAt)
    }
}
