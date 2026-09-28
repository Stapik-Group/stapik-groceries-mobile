package pl.stapik.groceries.data.config

data class ApiConfig(
    val serverUrl: String,
    val apiKey: String,
) {
    companion object {
        const val SLOT_KEY = "groceries.json"
    }
}
