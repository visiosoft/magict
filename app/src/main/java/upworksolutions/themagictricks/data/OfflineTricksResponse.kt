package upworksolutions.themagictricks.data

data class OfflineTricksResponse(val magic_tricks: List<OfflineTrick>)

data class OfflineTrick(
    val id: Int,
    val title: String,
    val thumbnail: String,
    val subtitle: String?,
    val description: String,
    val items_needed: List<String>,
    val steps: List<String>,
    val how_it_works: String,
    val difficulty: String,
    val category: String
) 