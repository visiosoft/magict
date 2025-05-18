package upworksolutions.themagictricks.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Trick(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val videoUrl: String = "",
    val thumbnailUrl: String = "",
    val duration: Int = 0,
    val categories: List<String> = emptyList(),
    val isPro: Boolean = false,
    val isFeatured: Boolean = false,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false
) : Parcelable {
    
    /**
     * Converts the Trick object to a Map for Firestore upload
     * @return Map containing all fields except id (which is handled by Firestore)
     */
    fun toMap(): Map<String, Any?> = mapOf(
        "title" to title,
        "description" to description,
        "videoUrl" to videoUrl,
        "thumbnailUrl" to thumbnailUrl,
        "duration" to duration,
        "categories" to categories,
        "isPro" to isPro,
        "isFeatured" to isFeatured,
        "isLiked" to isLiked,
        "isSaved" to isSaved
    )

    /**
     * Formats duration in seconds to MM:SS format
     * @return Formatted duration string
     */
    fun getFormattedDuration(): String {
        val minutes = duration / 60
        val seconds = duration % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    companion object {
        /**
         * Creates a Trick object from a Firestore document
         * @param id Document ID
         * @param data Document data
         * @return Trick object
         */
        fun fromFirestore(id: String, data: Map<String, Any?>): Trick {
            return Trick(
                id = id,
                title = data["title"] as? String ?: "",
                description = data["description"] as? String ?: "",
                videoUrl = data["videoUrl"] as? String ?: "",
                thumbnailUrl = data["thumbnailUrl"] as? String ?: "",
                duration = (data["duration"] as? Number)?.toInt() ?: 0,
                categories = (data["categories"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                isPro = data["isPro"] as? Boolean ?: false,
                isFeatured = data["isFeatured"] as? Boolean ?: false,
                isLiked = data["isLiked"] as? Boolean ?: false,
                isSaved = data["isSaved"] as? Boolean ?: false
            )
        }
    }
} 