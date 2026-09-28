package pl.stapik.groceries.data.repository

import pl.stapik.groceries.data.cache.GroceriesCacheStorage
import pl.stapik.groceries.data.config.ApiConfig
import pl.stapik.groceries.data.network.GroceriesRemoteSource
import pl.stapik.groceries.data.network.RawDocument
import pl.stapik.groceries.data.serialization.GroceriesDocumentParser

class GroceriesRepository(
    private val remote: GroceriesRemoteSource,
    private val cache: GroceriesCacheStorage,
    private val parser: GroceriesDocumentParser,
) {
    suspend fun fetch(config: ApiConfig): GroceriesFetchOutcome {
        val fresh = runCatching {
            val raw = remote.fetch(config)
            GroceriesSnapshot(parser.parse(raw.content), raw.updatedAt) to raw
        }

        fresh.onSuccess { (_, raw) -> runCatching { cache.save(raw) } }

        return fresh.fold(
            onSuccess = { (snapshot, _) -> GroceriesFetchOutcome.Fresh(snapshot) },
            onFailure = { error -> loadCached() ?: GroceriesFetchOutcome.Failure(error) },
        )
    }

    private suspend fun loadCached(): GroceriesFetchOutcome.Cached? {
        val raw: RawDocument = runCatching { cache.load() }.getOrNull() ?: return null
        val document = runCatching { parser.parse(raw.content) }.getOrNull() ?: return null
        return GroceriesFetchOutcome.Cached(GroceriesSnapshot(document, raw.updatedAt))
    }
}
