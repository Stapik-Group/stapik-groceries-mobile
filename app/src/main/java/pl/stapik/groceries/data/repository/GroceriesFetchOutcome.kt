package pl.stapik.groceries.data.repository

import pl.stapik.groceries.domain.model.GroceriesDocument

data class GroceriesSnapshot(val document: GroceriesDocument, val updatedAt: String)

sealed interface GroceriesFetchOutcome {
    data class Fresh(val snapshot: GroceriesSnapshot) : GroceriesFetchOutcome
    data class Cached(val snapshot: GroceriesSnapshot) : GroceriesFetchOutcome
    data class Failure(val cause: Throwable) : GroceriesFetchOutcome
}
