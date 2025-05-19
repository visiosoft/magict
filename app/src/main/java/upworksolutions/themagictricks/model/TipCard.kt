package upworksolutions.themagictricks.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TipCard(
    val id: String,
    val title: String,
    val description: String,
    val backgroundImageUrl: String,
    val difficulty: String,
    val secretTip: String,
    val isFavorite: Boolean = false
) : Parcelable 