package pl.stapik.groceries.data.serialization

import kotlinx.serialization.json.Json
import pl.stapik.groceries.domain.model.GroceriesDocument

class GroceriesDocumentParser {
    private val json = Json { ignoreUnknownKeys = true }

    fun parse(content: String): GroceriesDocument {
        if (content.isBlank()) return GroceriesDocument.EMPTY
        return json.decodeFromString<GroceriesDocumentDto>(content).toDomain()
    }
}
