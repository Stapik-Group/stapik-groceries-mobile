package pl.stapik.groceries.data.repository

import pl.stapik.common.cloud.CloudConfig
import pl.stapik.common.cloud.HttpCloudGateway
import pl.stapik.common.cloud.HttpTransport
import pl.stapik.common.cloud.OkHttpTransport
import pl.stapik.common.model.SlotKey
import pl.stapik.common.sync.CloudGateway
import pl.stapik.groceries.data.cache.GroceriesCacheStorage
import pl.stapik.groceries.data.cache.RawDocument
import pl.stapik.groceries.data.config.ApiConfig
import pl.stapik.groceries.data.config.ApiConfig.Companion.SLOT_KEY
import pl.stapik.groceries.data.serialization.GroceriesDocumentParser

/** The server answered, but it has no groceries document in its slot. */
class GroceriesNotFoundException : Exception("No groceries document on the server")

class GroceriesRepository(
    private val cache: GroceriesCacheStorage,
    private val parser: GroceriesDocumentParser,
    private val transport: HttpTransport = OkHttpTransport(),
) {
    // The gateway keeps the derived end-to-end key, which is slow to compute, so it is reused
    // for as long as the configuration stays the same.
    private var gateway: Pair<ApiConfig, CloudGateway>? = null

    @Synchronized
    private fun gatewayFor(config: ApiConfig): CloudGateway {
        gateway?.let { (known, ready) -> if (known == config) return ready }
        val created = HttpCloudGateway(CloudConfig(config.serverUrl, config.apiKey, config.passphrase), transport)
        gateway = config to created
        return created
    }

    suspend fun fetch(config: ApiConfig): GroceriesFetchOutcome {
        val fresh = runCatching {
            val remote = gatewayFor(config).read(SlotKey(SLOT_KEY)) ?: throw GroceriesNotFoundException()
            val raw = RawDocument(content = remote.content, updatedAt = remote.updatedAt)
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
